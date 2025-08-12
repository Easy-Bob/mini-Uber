package com.bob.serviceDriverUser.service;

import com.bob.internalcommon.constant.dto.DriverUserWorkStatus;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.serviceDriverUser.mapper.DriverUserWorkStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@Service
public class DriverUserWorkStatusService {

    @Autowired
    DriverUserWorkStatusMapper mapper;

    public ResponseResult changeWorkStatus(Long driverId, Integer workStatus){
        Map<String, Object> map = new HashMap<>();
        map.put("driver_id", driverId);
        List<DriverUserWorkStatus> driverUserWorkStatuses = mapper.selectByMap(map);
        DriverUserWorkStatus driverUserWorkStatus = driverUserWorkStatuses.get(0);
        driverUserWorkStatus.setWorkStatus(workStatus);

        mapper.updateById(driverUserWorkStatus);

        return ResponseResult.success("");
    }
}
