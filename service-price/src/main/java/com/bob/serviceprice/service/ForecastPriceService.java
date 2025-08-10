package com.bob.serviceprice.service;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.ForecastPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 2025/8/10.
 * Description:
 */
@Service
@Slf4j
public class ForecastPriceService {

    public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude){
        log.info("出发地经度" + depLongitude);
        log.info("出发地纬度" + depLatitude);
        log.info("目的地经度" + destLongitude);
        log.info("目的地纬度" + destLatitude);

        log.info("调用地图服务，计算价格");


        log.info("根据距离，时长，计算价格");

        ForecastPriceResponse forecastPriceResponse = new ForecastPriceResponse();
        forecastPriceResponse.setPrice(12.22);
        return ResponseResult.success(forecastPriceResponse);
    }
}
