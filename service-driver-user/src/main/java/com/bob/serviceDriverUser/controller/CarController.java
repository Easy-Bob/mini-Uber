package com.bob.serviceDriverUser.controller;


import com.bob.internalcommon.constant.dto.Car;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.serviceDriverUser.remote.ServiceMapClient;
import com.bob.serviceDriverUser.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bob Sun
 * @since 2025-08-12
 */
@RestController
public class CarController {

    @Autowired
    private CarService carService;

    @PostMapping("/car")
    public ResponseResult addCar(@RequestBody Car car){
        return carService.addCar(car);
    }

    @GetMapping("/car")
    public ResponseResult<Car> getCarById(Long carId){
        return carService.getCarById(carId);
    }


}
