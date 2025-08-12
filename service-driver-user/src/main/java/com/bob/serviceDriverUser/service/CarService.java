package com.bob.serviceDriverUser.service;

import com.bob.internalcommon.constant.dto.Car;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.serviceDriverUser.mapper.CarMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@Repository
public class CarService {

    @Autowired
    private CarMapper carMapper;

    @PostMapping("/car")
    public ResponseResult addCar(@RequestBody Car car){
        LocalDateTime now = LocalDateTime.now();
        car.setGmtCreate(now);
        car.setGmtModified(now);

        carMapper.insert(car);
        return ResponseResult.success("");
    }
}
