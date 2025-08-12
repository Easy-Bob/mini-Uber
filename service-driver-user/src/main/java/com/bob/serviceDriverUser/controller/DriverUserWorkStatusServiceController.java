package com.bob.serviceDriverUser.controller;

import com.bob.internalcommon.constant.dto.DriverUserWorkStatus;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.serviceDriverUser.service.DriverUserWorkStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@RestController
public class DriverUserWorkStatusServiceController {

    @Autowired
    private DriverUserWorkStatusService driverUserWorkStatusService;

    @PostMapping("/driver-user-work-status")
    public ResponseResult changeWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus){
        Long driverId = driverUserWorkStatus.getDriverId();
        Integer workStatus = driverUserWorkStatus.getWorkStatus();
        return driverUserWorkStatusService.changeWorkStatus(driverId, workStatus);
    }
}
