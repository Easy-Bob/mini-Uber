package com.bob.servicemap.controller;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.ForecastPriceDTO;
import com.bob.servicemap.service.DirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/10.
 * Description:
 */
@RestController
@RequestMapping("/direction")
public class DirectionController {

    @Autowired
    DirectionService directionService;

    @GetMapping("/driving")
    public ResponseResult driving(@RequestBody ForecastPriceDTO forecastPriceDTO){

        String depLongitude =  forecastPriceDTO.getDepLongitude();
        String depLatitude = forecastPriceDTO.getDepLatitude();
        String destLongitude = forecastPriceDTO.getDestLongitude();
        String destLatitude = forecastPriceDTO.getDestLatitude();
        return directionService.driving(depLongitude,depLatitude,destLongitude,destLatitude);
    }
}
