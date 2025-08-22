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
import com.bob.internalcommon.constant.response.OrderDriverResponse;
import com.bob.internalcommon.constant.response.TerminalResponse;
import com.bob.internalcommon.constant.util.RedisPrefixUtils;
import com.bob.serviceorder.mapper.OrderInfoMapper;
import com.bob.serviceorder.remote.ServiceDriverUserClient;
import com.bob.serviceorder.remote.ServiceMapClient;
import com.bob.serviceorder.remote.ServicePriceClient;
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
        dispatchRealTimeOrder(orderInfo);
        return ResponseResult.success("");
    }

    /**
     * order dispatch logic
     * 2km - 4km - 6km (then back to 2km and loop)
     * @param orderInfo
     */
    public int dispatchRealTimeOrder(OrderInfo orderInfo) {
        int result = 1;
        // 2km
        String depLongitude = orderInfo.getDepLongitude();
        String depLatitude = orderInfo.getDepLatitude();
        // search list
        String center = depLatitude + "," + depLongitude;
        List<Integer> radiusList = new ArrayList<>();
        radiusList.add(2000);
        radiusList.add(4000);
        radiusList.add(5000);

        ResponseResult<List<TerminalResponse>> listResponseResult = null;

        for (int i = 0; i < radiusList.size(); i++) {
            Integer radius = radiusList.get(i);
            listResponseResult = serviceMapClient.terminalAroundSearch(center, radius);

            // get the terminal
            log.info("Search a car in the range scope of - " + radius);

            // resolve the terminal
//            JSONArray result = JSONArray.fromObject(listResponseResult.getData());
            List<TerminalResponse> data = listResponseResult.getData();

            for (int j = 0; j < data.size(); j++) {
                TerminalResponse terminalResponse = data.get(j);
                Long carId = terminalResponse.getCarId();

                String longitude = terminalResponse.getLongitude();
                String latitude = terminalResponse.getLatitude();

                // 查询是否有对于的可派单司机
                ResponseResult<OrderDriverResponse> availableDriver = serviceDriverUserClient.getAvailableDriver(carId);
                if (availableDriver.getCode() == CommonStatusEnum.NO_DRIVER_NEARBY.getCode()) {
                    log.info("没有车辆ID：" + carId + ",对于的司机");
                    continue;
                } else {
                    log.info("车辆ID：" + carId + "找到了正在出车的司机");

                    // check the car info based on the terminal
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

                    String lockKey = String.valueOf(driverId).intern();
                    RLock lock = redissonClient.getLock(lockKey);
                    lock.lock();


// 判断司机 是否有进行中的订单
                    if (isOrderPending(driverId) > 0){
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


                    // 通知乘客
                    JSONObject passengerContent = new  JSONObject();
                    passengerContent.put("orderId",orderInfo.getId());
                    passengerContent.put("driverId",orderInfo.getDriverId());
                    passengerContent.put("driverPhone",orderInfo.getDriverPhone());
                    passengerContent.put("vehicleNo",orderInfo.getVehicleNo());
                    // 车辆信息，调用车辆服务
                    ResponseResult<Car> carById = null;
//                    carById = serviceDriverUserClient.getCarById(carId);
                    Car carRemote = carById.getData();

                    passengerContent.put("brand", carRemote.getBrand());
                    passengerContent.put("model",carRemote.getModel());
                    passengerContent.put("vehicleColor",carRemote.getVehicleColor());

                    passengerContent.put("receiveOrderCarLongitude",orderInfo.getReceiveOrderCarLongitude());
                    passengerContent.put("receiveOrderCarLatitude",orderInfo.getReceiveOrderCarLatitude());

                    result = 1;
                    lock.unlock();

                }
            }
        }
        return result;
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
}
