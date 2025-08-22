package com.bob.serviceorder.remote;

import com.bob.internalcommon.constant.dto.PriceRule;
import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by Sun on 8/19/2025.
 * Description:
 */
@FeignClient("service-price")
public interface ServicePriceClient {

    @GetMapping("/price-rule/if-exists")
    public ResponseResult<Boolean> ifPriceRuleExists(@RequestBody PriceRule priceRule);
}
