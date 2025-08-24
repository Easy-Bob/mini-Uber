package com.bob.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.internalcommon.constant.constant.CommonStatusEnum;
import com.bob.internalcommon.constant.constant.IdentityConstants;
import com.bob.internalcommon.constant.constant.OrderInfoConstants;
import com.bob.internalcommon.constant.dto.Car;
import com.bob.internalcommon.constant.dto.OrderInfo;
import com.bob.internalcommon.constant.dto.PriceRule;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.OrderRequest;
import com.bob.internalcommon.constant.request.PushRequest;
import com.bob.internalcommon.constant.response.OrderDriverResponse;
import com.bob.internalcommon.constant.response.TerminalResponse;
import com.bob.internalcommon.constant.response.TrsearchResponse;
import com.bob.internalcommon.constant.util.RedisPrefixUtils;
import com.bob.serviceorder.mapper.OrderInfoMapper;
import com.bob.serviceorder.remote.ServiceDriverUserClient;
import com.bob.serviceorder.remote.ServiceMapClient;
import com.bob.serviceorder.remote.ServicePriceClient;
import com.bob.serviceorder.remote.ServiceSseCommClient;
import com.mysql.cj.xdevapi.JsonArray;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sun on 8/17/2025.
 * Description:
 */
@Service
@Slf4j
public class OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private ServicePriceClient servicePriceClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ServiceSseCommClient serviceSseCommClient;

    public ResponseResult add(OrderRequest orderRequest){
        OrderInfo orderInfo = null;
        // available driver exists?
        ResponseResult<Boolean> result = serviceDriverUserClient.isAvailableDriver(orderRequest.getAddress());
        if(!result.getData()){
            return ResponseResult.fail(CommonStatusEnum.CITY_DRIVER_EMPTY.getCode(),CommonStatusEnum.CITY_DRIVER_EMPTY.getValue());
        }

        // able to order for passengers?
        if(isOrderPending(orderRequest.getPassengerId()) > 0){
            return ResponseResult.fail(CommonStatusEnum.ORDER_PENDING.getCode(), CommonStatusEnum.ORDER_PENDING.getValue());
        }

        // add blacklist for constant requests
        String deviceOrder = orderRequest.getDeviceCode();
        String deviceOrderKey = RedisPrefixUtils.generateBlackDeviceKey(deviceOrder);

        Boolean hasKey = stringRedisTemplate.hasKey(deviceOrderKey);
        if(hasKey){
            String value = stringRedisTemplate.opsForValue().get(deviceOrderKey);
            int v = Integer.parseInt(value);
            System.out.println(v);
            if(v > 2){
                // same device orders too much
                return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACKED.getCode(), CommonStatusEnum.DEVICE_IS_BLACKED.getValue());
            }else{
                stringRedisTemplate.opsForValue().increment(deviceOrderKey);
            }
        }else{
            stringRedisTemplate.opsForValue().setIfAbsent(deviceOrderKey,"1", 1L, TimeUnit.HOURS);
        }

        // check if service is available for given cityCode
        if(!hasPriceRule(orderRequest)){
            return ResponseResult.fail("NO SERVICE FOR THE CITY");
        }

        try {
            orderInfo = new OrderInfo();
            BeanUtils.copyProperties(orderInfo, orderRequest);
            orderInfo.setOrderStatus(OrderInfoConstants.ORDER_START);
            LocalDateTime now = LocalDateTime.now();
            orderInfo.setGmtCreate(now);
            orderInfo.setGmtModified(now);
        } catch (Exception e) {
            return ResponseResult.fail("insert Fail");
        }
        orderInfoMapper.insert(orderInfo);
                // 定时任务的处理
        for (int i = 0;i < 6; i++){
            // 派单 dispatchRealTimeOrder
            int res = dispatchRealTimeOrder(orderInfo);
            if (res == 1){
                break;
            }
            if (i == 5){
                // 订单无效
                orderInfo.setOrderStatus(OrderInfoConstants.ORDER_INVALID);
                orderInfoMapper.updateById(orderInfo);
            }else {
                // 等待20s
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return ResponseResult.success("");
    }

    /**
     * order dispatch logic
     * 2km - 4km - 6km (then back to 2km and loop)
     * @param orderInfo
     */


    public int dispatchRealTimeOrder(OrderInfo orderInfo){
        int result = 0;

        //2km
        String depLatitude = orderInfo.getDepLatitude();
        String depLongitude = orderInfo.getDepLongitude();

        String center = depLatitude+","+depLongitude;

        List<Integer> radiusList = new ArrayList<>();
        radiusList.add(2000);
        radiusList.add(4000);
        radiusList.add(5000);
        // 搜索结果
        ResponseResult<List<TerminalResponse>> listResponseResult = null;
        // goto是为了测试。
        radius:
        for (int i=0;i<radiusList.size();i++){
            Integer radius = radiusList.get(i);
            listResponseResult = serviceMapClient.terminalAroundSearch(center,radius);

            log.info("在半径为"+radius+"的范围内，寻找车辆,结果："+ JSONArray.fromObject(listResponseResult.getData()).toString());

            // 解析终端
            List<TerminalResponse> data = listResponseResult.getData();

            // 为了测试是否从地图上获取到司机
//            List<TerminalResponse> data = new ArrayList<>();
            for (int j = 0;j < data.size();j++){
                TerminalResponse terminalResponse = data.get(j);
                Long carId = terminalResponse.getCarId();

                String longitude = terminalResponse.getLongitude();
                String latitude = terminalResponse.getLatitude();

                // 查询是否有对于的可派单司机
                ResponseResult<OrderDriverResponse> availableDriver = serviceDriverUserClient.getAvailableDriver(carId);
                if(availableDriver.getCode() == CommonStatusEnum.NO_DRIVER_NEARBY.getCode()){
                    log.info("没有车辆ID："+carId+",对于的司机");
                    continue;
                }else {
                    log.info("车辆ID："+carId+"找到了正在出车的司机");

                    OrderDriverResponse orderDriverResponse = availableDriver.getData();
                    Long driverId = orderDriverResponse.getDriverId();
                    String driverPhone = orderDriverResponse.getDriverPhone();
                    String licenseId = orderDriverResponse.getLicenseId();
                    String vehicleNo = orderDriverResponse.getVehicleNo();
                    String vehicleTypeFromCar = orderDriverResponse.getVehicleType();

                    // 判断车辆的车型是否符合？
                    String vehicleType = orderInfo.getVehicleType();
                    if (!vehicleType.trim().equals(vehicleTypeFromCar.trim())){
                        System.out.println("车型不符合");
                        continue ;
                    }


                    String lockKey = (driverId+"").intern();
                    RLock lock = redissonClient.getLock(lockKey);
                    lock.lock();

                    // 判断司机 是否有进行中的订单
                    if (isDriverOrderGoingon(driverId) > 0){
                        lock.unlock();
                        continue ;
                    }
                    // 订单直接匹配司机
                    // 查询当前车辆信息
                    QueryWrapper<Car> carQueryWrapper = new QueryWrapper<>();
                    carQueryWrapper.eq("id",carId);


                    // 设置订单中和司机车辆相关的信息
                    orderInfo.setDriverId(driverId);
                    orderInfo.setDriverPhone(driverPhone);
                    orderInfo.setCarId(carId);
                    // 从地图中来
                    orderInfo.setReceiveOrderCarLongitude(longitude);
                    orderInfo.setReceiveOrderCarLatitude(latitude);

                    orderInfo.setReceiveOrderTime(LocalDateTime.now());
                    orderInfo.setLicenseId(licenseId);
                    orderInfo.setVehicleNo(vehicleNo);
                    orderInfo.setOrderStatus(OrderInfoConstants.DRIVER_RECEIVE_ORDER);

                    orderInfoMapper.updateById(orderInfo);

                    // 通知司机
                    JSONObject driverContent = new  JSONObject();

                    driverContent.put("orderId",orderInfo.getId());
                    driverContent.put("passengerId",orderInfo.getPassengerId());
                    driverContent.put("passengerPhone",orderInfo.getPassengerPhone());
                    driverContent.put("departure",orderInfo.getDeparture());
                    driverContent.put("depLongitude",orderInfo.getDepLongitude());
                    driverContent.put("depLatitude",orderInfo.getDepLatitude());

                    driverContent.put("destination",orderInfo.getDestination());
                    driverContent.put("destLongitude",orderInfo.getDestLongitude());
                    driverContent.put("destLatitude",orderInfo.getDestLatitude());

                    PushRequest pushRequest = new PushRequest();
                    pushRequest.setUserId(driverId);
                    pushRequest.setIdentity(IdentityConstants.DRIVER_IDENTITY);
                    pushRequest.setContent(driverContent.toString());

                    serviceSseCommClient.push(pushRequest);

                    // 通知乘客
                    JSONObject passengerContent = new  JSONObject();
                    passengerContent.put("orderId",orderInfo.getId());
                    passengerContent.put("driverId",orderInfo.getDriverId());
                    passengerContent.put("driverPhone",orderInfo.getDriverPhone());
                    passengerContent.put("vehicleNo",orderInfo.getVehicleNo());
                    // 车辆信息，调用车辆服务
                    ResponseResult<Car> carById = serviceDriverUserClient.getCarById(carId);
                    Car carRemote = carById.getData();

                    passengerContent.put("brand", carRemote.getBrand());
                    passengerContent.put("model",carRemote.getModel());
                    passengerContent.put("vehicleColor",carRemote.getVehicleColor());

                    passengerContent.put("receiveOrderCarLongitude",orderInfo.getReceiveOrderCarLongitude());
                    passengerContent.put("receiveOrderCarLatitude",orderInfo.getReceiveOrderCarLatitude());

                    PushRequest pushRequest1 = new PushRequest();
                    pushRequest1.setUserId(orderInfo.getPassengerId());
                    pushRequest1.setIdentity(IdentityConstants.PASSENGER_IDENTITY);
                    pushRequest1.setContent(passengerContent.toString());

                    serviceSseCommClient.push(pushRequest1);
                    result = 1;
                    lock.unlock();

                    // 退出，不在进行 司机的查找.如果派单成功，则退出循环
                    break radius;
                }

            }

        }

        return  result;
    }

    private boolean hasPriceRule(OrderRequest orderRequest){
        String fareType = orderRequest.getFareType();
        int index = fareType.indexOf("$");
        String cityCode = fareType.substring(0, index);
        String vehicleType = fareType.substring(index + 1);

        PriceRule priceRule = new PriceRule();
        priceRule.setCityCode(cityCode);
        priceRule.setVehicleType(vehicleType);

        ResponseResult<Boolean> result = servicePriceClient.ifPriceRuleExists(priceRule);
        return result.getData();

    }

    private int isOrderPending(Long passengerId){
        // check the status of the ongoing order
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("passenger_id",passengerId);
        queryWrapper.and(wrapper -> wrapper
                .eq("order_status",OrderInfoConstants.ORDER_START)
                .or().eq("order_status",OrderInfoConstants.DRIVER_RECEIVE_ORDER)
                .or().eq("order_status",OrderInfoConstants.DRIVER_TO_PICK_UP_PASSENGER)
                .or().eq("order_status",OrderInfoConstants.DRIVER_ARRIVED_DEPARTURE)
                .or().eq("order_status",OrderInfoConstants.PICK_UP_PASSENGER)
                .or().eq("order_status",OrderInfoConstants.PASSENGER_GET_OFF)
                .or().eq("order_status",OrderInfoConstants.TO_START_PAY));

        int validOrderNum = orderInfoMapper.selectCount(queryWrapper);
        return validOrderNum;
    }

    /**
     * 判断是否有 业务中的订单
     * @param driverId
     * @return
     */
    private int isDriverOrderGoingon(Long driverId){
        // 判断有正在进行的订单不允许下单
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id",driverId);
        queryWrapper.and(wrapper->wrapper
                .eq("order_status",OrderInfoConstants.DRIVER_RECEIVE_ORDER)
                .or().eq("order_status",OrderInfoConstants.DRIVER_TO_PICK_UP_PASSENGER)
                .or().eq("order_status",OrderInfoConstants.DRIVER_ARRIVED_DEPARTURE)
                .or().eq("order_status",OrderInfoConstants.PICK_UP_PASSENGER)

        );


        Integer validOrderNumber = orderInfoMapper.selectCount(queryWrapper);
        log.info("司机Id："+driverId+",正在进行的订单的数量："+validOrderNumber);

        return validOrderNumber;

    }

    // 接到乘客，简单的数据表修改
    public ResponseResult toPickUpPassenger(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();
        LocalDateTime toPickUpPassengerTime = orderRequest.getToPickUpPassengerTime();
        String toPickUpPassengerLongitude = orderRequest.getToPickUpPassengerLongitude();
        String toPickUpPassengerLatitude = orderRequest.getToPickUpPassengerLatitude();
        String toPickUpPassengerAddress = orderRequest.getToPickUpPassengerAddress();
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        orderInfo.setToPickUpPassengerAddress(toPickUpPassengerAddress);
        orderInfo.setToPickUpPassengerLatitude(toPickUpPassengerLatitude);
        orderInfo.setToPickUpPassengerLongitude(toPickUpPassengerLongitude);
        orderInfo.setToPickUpPassengerTime(LocalDateTime.now());
        orderInfo.setOrderStatus(OrderInfoConstants.DRIVER_TO_PICK_UP_PASSENGER);

        orderInfoMapper.updateById(orderInfo);

        return ResponseResult.success();

    }

    // 到达出发地，简单的数据表修改
    public ResponseResult arrivedDeparture(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",orderId);

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        orderInfo.setOrderStatus(OrderInfoConstants.DRIVER_ARRIVED_DEPARTURE);

        orderInfo.setDriverArrivedDepartureTime(LocalDateTime.now());
        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    // 接到乘客，简单的数据表修改
    public ResponseResult pickUpPassenger(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        orderInfo.setPickUpPassengerLongitude(orderRequest.getPickUpPassengerLongitude());
        orderInfo.setPickUpPassengerLatitude(orderRequest.getPickUpPassengerLatitude());
        orderInfo.setPickUpPassengerTime(LocalDateTime.now());
        orderInfo.setOrderStatus(OrderInfoConstants.PICK_UP_PASSENGER);

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    // 乘客下车，数据表修改，计算价格
    public ResponseResult passengerGetoff(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);

        orderInfo.setPassengerGetoffTime(LocalDateTime.now());
        orderInfo.setPassengerGetoffLongitude(orderRequest.getPassengerGetoffLongitude());
        orderInfo.setPassengerGetoffLatitude(orderRequest.getPassengerGetoffLatitude());

        orderInfo.setOrderStatus(OrderInfoConstants.PASSENGER_GET_OFF);
        // 订单行驶的路程和时间,调用 service-map
        ResponseResult<Car> carById = serviceDriverUserClient.getCarById(orderInfo.getCarId());
        Long starttime = orderInfo.getPickUpPassengerTime().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long endtime = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        System.out.println("开始时间："+starttime);
        System.out.println("结束时间："+endtime);
        // 不要跨天
        ResponseResult<TrsearchResponse> trsearch = serviceMapClient.trsearch(carById.getData().getTid(), starttime,endtime);
        TrsearchResponse data = trsearch.getData();
        Long driveMile = data.getDriveMile();
        Long driveTime = data.getDriveTime();

        orderInfo.setDriveMile(driveMile);
        orderInfo.setDriveTime(driveTime);

        // 获取价格
        String address = orderInfo.getAddress();
        String vehicleType = orderInfo.getVehicleType();
        ResponseResult<Double> doubleResponseResult = servicePriceClient.calculatePrice(driveMile.intValue(), driveTime.intValue(), address, vehicleType);
        Double price = doubleResponseResult.getData();
        orderInfo.setPrice(price);

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    /**
     * 支付
     * @param orderRequest
     * @return
     */
    public ResponseResult pay(OrderRequest orderRequest){

        Long orderId = orderRequest.getOrderId();
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);

        orderInfo.setOrderStatus(OrderInfoConstants.SUCCESS_PAY);
        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    /**
     * 订单取消
     * @param orderId 订单Id
     * @param identity  身份：1：乘客，2：司机
     * @return
     */
    public ResponseResult cancel(Long orderId, String identity){
        // 查询订单当前状态
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        Integer orderStatus = orderInfo.getOrderStatus();

        LocalDateTime cancelTime = LocalDateTime.now();
        Integer cancelOperator = null;
        Integer cancelTypeCode = null;

        // 正常取消
        int cancelType = 1;

        // 更新订单的取消状态
        // 如果是乘客取消
        if (identity.trim().equals(IdentityConstants.PASSENGER_IDENTITY)){
            switch (orderStatus){
                // 订单开始
                case OrderInfoConstants.ORDER_START:
                    cancelTypeCode = OrderInfoConstants.CANCEL_PASSENGER_BEFORE;
                    break;
                // 司机接到订单
                case OrderInfoConstants.DRIVER_RECEIVE_ORDER:
                    LocalDateTime receiveOrderTime = orderInfo.getReceiveOrderTime();
                    long between = ChronoUnit.MINUTES.between(receiveOrderTime, cancelTime);
                    if (between > 1){
                        cancelTypeCode = OrderInfoConstants.CANCEL_PASSENGER_ILLEGAL;
                    }else {
                        cancelTypeCode = OrderInfoConstants.CANCEL_PASSENGER_BEFORE;
                    }
                    break;
                // 司机去接乘客
                case OrderInfoConstants.DRIVER_TO_PICK_UP_PASSENGER:
                    // 司机到达乘客起点
                case OrderInfoConstants.DRIVER_ARRIVED_DEPARTURE:
                    cancelTypeCode = OrderInfoConstants.CANCEL_PASSENGER_ILLEGAL;
                    break;
                default:
                    log.info("乘客取消失败");
                    cancelType = 0;
                    break;
            }
        }

        // 如果是司机取消
        if (identity.trim().equals(IdentityConstants.DRIVER_IDENTITY)){
            switch (orderStatus){
                // 订单开始
                // 司机接到乘客
                case OrderInfoConstants.DRIVER_RECEIVE_ORDER:
                case OrderInfoConstants.DRIVER_TO_PICK_UP_PASSENGER:
                case OrderInfoConstants.DRIVER_ARRIVED_DEPARTURE:
                    LocalDateTime receiveOrderTime = orderInfo.getReceiveOrderTime();
                    long between = ChronoUnit.MINUTES.between(receiveOrderTime, cancelTime);
                    if (between > 1){
                        cancelTypeCode = OrderInfoConstants.CANCEL_DRIVER_ILLEGAL;
                    }else {
                        cancelTypeCode = OrderInfoConstants.CANCEL_DRIVER_BEFORE;
                    }
                    break;

                default:
                    log.info("司机取消失败");
                    cancelType = 0;
                    break;
            }
        }


        if (cancelType == 0){
            return ResponseResult.fail(CommonStatusEnum.ORDER_CANCEL_ERROR.getCode(),CommonStatusEnum.ORDER_CANCEL_ERROR.getValue());
        }

        orderInfo.setCancelTypeCode(cancelTypeCode);
        orderInfo.setCancelTime(cancelTime);
        orderInfo.setCancelOperator(Integer.parseInt(identity));
        orderInfo.setOrderStatus(OrderInfoConstants.ORDER_CANCEL);

        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();
    }

    public ResponseResult pushPayInfo(OrderRequest orderRequest) {

        Long orderId = orderRequest.getOrderId();

        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        orderInfo.setOrderStatus(OrderInfoConstants.TO_START_PAY);
        orderInfoMapper.updateById(orderInfo);
        return ResponseResult.success();

    }

    public ResponseResult<OrderInfo> detail(Long orderId){
        OrderInfo orderInfo =  orderInfoMapper.selectById(orderId);
        return ResponseResult.success(orderInfo);
    }


    public ResponseResult<OrderInfo> current(String phone, String identity){
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();

        if (identity.equals(IdentityConstants.DRIVER_IDENTITY)){
            queryWrapper.eq("driver_phone",phone);

            queryWrapper.and(wrapper->wrapper
                    .eq("order_status",OrderInfoConstants.DRIVER_RECEIVE_ORDER)
                    .or().eq("order_status",OrderInfoConstants.DRIVER_TO_PICK_UP_PASSENGER)
                    .or().eq("order_status",OrderInfoConstants.DRIVER_ARRIVED_DEPARTURE)
                    .or().eq("order_status",OrderInfoConstants.PICK_UP_PASSENGER)

            );
        }
        if (identity.equals(IdentityConstants.PASSENGER_IDENTITY)){
            queryWrapper.eq("passenger_phone",phone);
            queryWrapper.and(wrapper->wrapper.eq("order_status",OrderInfoConstants.ORDER_START)
                    .or().eq("order_status",OrderInfoConstants.DRIVER_RECEIVE_ORDER)
                    .or().eq("order_status",OrderInfoConstants.DRIVER_TO_PICK_UP_PASSENGER)
                    .or().eq("order_status",OrderInfoConstants.DRIVER_ARRIVED_DEPARTURE)
                    .or().eq("order_status",OrderInfoConstants.PICK_UP_PASSENGER)
                    .or().eq("order_status",OrderInfoConstants.PASSENGER_GET_OFF)
                    .or().eq("order_status",OrderInfoConstants.TO_START_PAY)
            );
        }

        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        return ResponseResult.success(orderInfo);
    }
}
