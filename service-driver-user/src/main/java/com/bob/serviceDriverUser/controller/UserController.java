package com.bob.serviceDriverUser.controller;

import com.bob.internalcommon.constant.constant.DriverCarConstants;
import com.bob.internalcommon.constant.dto.DriverUser;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.DriverUserExistsResponse;
import com.bob.internalcommon.constant.response.OrderDriverResponse;
import com.bob.serviceDriverUser.service.DriverUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@RestController
public class UserController {

    @Autowired
    private DriverUserService driverUserService;

    @PostMapping("/user")
    public ResponseResult addUser(@RequestBody DriverUser driverUser){
        return driverUserService.addDriverUser(driverUser);
    }

    @PutMapping("/user")
    public ResponseResult updateUser(@RequestBody DriverUser driverUser){
        return driverUserService.updateDriverUser(driverUser);
    }

    /**
     * 查询司机
     * @param driverPhone
     * @return
     */
    @GetMapping("/check-driver/{driverPhone}")
    public ResponseResult getUser(@PathVariable String driverPhone){
        ResponseResult<DriverUser> driverUserByPhone = driverUserService.getDriverUser(driverPhone);
        DriverUser driverUserDB = driverUserByPhone.getData();
        int ifExists = DriverCarConstants.DRIVER_EXISTS;
        if(driverUserDB == null){
            ifExists = DriverCarConstants.DRIVER_NOT_EXISTS;
        }

        DriverUserExistsResponse response = new DriverUserExistsResponse();
        response.setDriverPhone(driverPhone);
        response.setIfExists(ifExists);

        return ResponseResult.success(response);
    }

    /**
     * 根据车辆Id查询订单需要的司机信息
     * @param carId
     * @return
     */
    @GetMapping("/get-available-driver/{carId}")
    public ResponseResult<OrderDriverResponse> getAvailableDriver(@PathVariable("carId") Long carId){
        return driverUserService.getAvailableDriver(carId);
    }
}
