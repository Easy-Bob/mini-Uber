package com.bob.serviceDriverUser.controller;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.serviceDriverUser.mapper.DriverUserMapper;
import com.bob.serviceDriverUser.service.DriverUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/11.
 * Description:
 */
@RestController
public class TestController {

    @Autowired
    private DriverUserService driverUserService;

    @GetMapping("/test")
    public String test(){
        return "service-driver-user";
    }

    @GetMapping("/test-db")
    public ResponseResult testDb(){
        return driverUserService.testGetDriverUser();
    }

    @Autowired
    DriverUserMapper driverUserMapper;

    // test mapper.xml validity
    @GetMapping("/test-xml")
    public int testXml(String cityCode){
        int i = driverUserMapper.selectDriverUserCountByCityCode(cityCode);
        System.out.println(i);
        return i;
    }
}
