package com.bob.serviceorder.remote;

import com.bob.internalcommon.constant.dto.PriceRule;
import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Sun on 8/19/2025.
 * Description:
 */
@FeignClient("service-price")
public interface ServicePriceClient {

    @GetMapping("/price-rule/if-exists")
    public ResponseResult<Boolean> ifPriceRuleExists(@RequestBody PriceRule priceRule);

    @RequestMapping(method = RequestMethod.POST, value = "/calculate-price")
    public ResponseResult<Double> calculatePrice(@RequestParam Integer distance , @RequestParam Integer duration, @RequestParam String cityCode, @RequestParam String vehicleType);
}
