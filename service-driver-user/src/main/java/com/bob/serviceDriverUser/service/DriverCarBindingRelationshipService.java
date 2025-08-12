package com.bob.serviceDriverUser.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.internalcommon.constant.constant.CommonStatusEnum;
import com.bob.internalcommon.constant.constant.DriverCarConstants;
import com.bob.internalcommon.constant.dto.DriverCarBindingRelationship;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.serviceDriverUser.mapper.DriverCarBindingRelationshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@Service
public class DriverCarBindingRelationshipService {

    @Autowired
    private DriverCarBindingRelationshipMapper mapper;

    public ResponseResult bind(@RequestBody DriverCarBindingRelationship o){

        // 司机绑定查询
        QueryWrapper<DriverCarBindingRelationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id", o.getDriverId());
        queryWrapper.eq("bind_state", DriverCarConstants.DRIVER_CAR_BIND);

        Integer ifDriverBind = mapper.selectCount(queryWrapper);
        if(ifDriverBind.intValue() > 0){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_BIND_EXISTS.getCode(), CommonStatusEnum.DRIVER_BIND_EXISTS.getValue());
        }

        // 车辆绑定查询
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("car_id", o.getCarId());
        queryWrapper.eq("bind_state", DriverCarConstants.DRIVER_CAR_BIND);

        Integer ifCarBind = mapper.selectCount(queryWrapper);
        if(ifCarBind.intValue() > 0){
            return ResponseResult.fail(CommonStatusEnum.CAR_BIND_EXISTS.getCode(), CommonStatusEnum.CAR_BIND_EXISTS.getValue());
        }

        // 司机-车辆绑定查询
        Map<String, Object> map = new HashMap<>();
        map.put("driver_id", o.getDriverId());
        map.put("car_id", o.getCarId());

        List<DriverCarBindingRelationship> res = mapper.selectByMap(map);
        if(res == null || res.isEmpty()){
            // 司机 车辆空闲，绑定操作
            LocalDateTime now = LocalDateTime.now();
            o.setBindingTime(now);
            o.setBindState(DriverCarConstants.DRIVER_CAR_BIND);
            mapper.insert(o);
            return ResponseResult.success("");
        }else{
            o = res.get(0);
            if(o.getBindState() == DriverCarConstants.DRIVER_CAR_UNBIND){
                o.setBindState(DriverCarConstants.DRIVER_CAR_BIND);
                mapper.updateById(o);
                return ResponseResult.success("");
            }else{
                return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_EXISTS.getCode(), CommonStatusEnum.DRIVER_CAR_BIND_EXISTS.getValue());
            }
        }
    }

    public ResponseResult unbind(@RequestBody DriverCarBindingRelationship o){
        // 车辆解绑 查询
        Map<String, Object> map = new HashMap<>();
        map.put("driver_id", o.getDriverId());
        map.put("car_id", o.getCarId());
        map.put("bind_state", DriverCarConstants.DRIVER_CAR_BIND);

        List<DriverCarBindingRelationship> res = mapper.selectByMap(map);
        if(res == null || res.isEmpty()){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_UNBIND_EXISTS.getCode(), CommonStatusEnum.DRIVER_CAR_UNBIND_EXISTS.getValue());
        }else{
            o = res.get(0);
            LocalDateTime now = LocalDateTime.now();
            o.setUnBindingTime(now);
            o.setBindState(DriverCarConstants.DRIVER_CAR_UNBIND);
            mapper.updateById(o);
            return ResponseResult.success("");
        }

    }

}
