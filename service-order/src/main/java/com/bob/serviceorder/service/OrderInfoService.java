package com.bob.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.internalcommon.constant.constant.CommonStatusEnum;
import com.bob.internalcommon.constant.constant.OrderInfoConstants;
import com.bob.internalcommon.constant.dto.OrderInfo;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.OrderRequest;
import com.bob.internalcommon.constant.util.RedisPrefixUtils;
import com.bob.serviceorder.mapper.OrderInfoMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sun on 8/17/2025.
 * Description:
 */
@Service
public class OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ResponseResult add(OrderRequest orderRequest){
        OrderInfo orderInfo = null;
        if(isOrderPending(orderRequest.getPassengerId()) > 0){
            return ResponseResult.fail(CommonStatusEnum.ORDER_PENDING.getCode(), CommonStatusEnum.ORDER_PENDING.getValue());
        }
        // add blacklist for constant requests
        String deviceOrder = orderRequest.getDeviceCode();
        String deviceOrderKey = RedisPrefixUtils.generateBlackDeviceKey(deviceOrder);

        Boolean hasKey = stringRedisTemplate.hasKey(deviceOrderKey);
        System.out.println(hasKey);
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
        return ResponseResult.success("");
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
