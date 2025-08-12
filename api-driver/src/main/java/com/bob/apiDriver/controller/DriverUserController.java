package com.bob.apiDriver.controller;

import com.bob.apiDriver.service.DriverUserService;
import com.bob.internalcommon.constant.dto.DriverUser;
import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@RestController
public class DriverUserController {
    @Autowired
    private DriverUserService driverUserService;

    @PutMapping("/driver-user")
    public ResponseResult updateDriverUser(@RequestBody DriverUser driverUser){
        return driverUserService.updateDriverUser(driverUser);
    }

}
