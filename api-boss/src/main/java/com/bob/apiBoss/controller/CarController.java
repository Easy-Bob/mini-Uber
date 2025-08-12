package com.bob.apiBoss.controller;

import com.bob.apiBoss.remote.DriverUserCarClient;
import com.bob.internalcommon.constant.dto.Car;
import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@RestController
public class CarController {
    @Autowired
    private DriverUserCarClient client;

    @PostMapping("/car")
    public ResponseResult addCar(@RequestBody Car car) {
        return client.addCar(car);
    }
}
