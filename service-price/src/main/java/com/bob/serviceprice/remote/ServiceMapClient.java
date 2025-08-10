package com.bob.serviceprice.remote;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.ForecastPriceDTO;
import com.bob.internalcommon.constant.response.DirectionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Sun on 2025/8/10.
 * Description:
 */
@FeignClient("service-map")
public interface ServiceMapClient {

    @RequestMapping(method = RequestMethod.GET, value = "/direction/driving")
    public ResponseResult<DirectionResponse> direction(@RequestBody ForecastPriceDTO forecastPriceDTO);
}
