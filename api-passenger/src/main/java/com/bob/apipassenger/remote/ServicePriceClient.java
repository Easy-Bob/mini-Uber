package com.bob.apipassenger.remote;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Created by Sun on 2025/8/10.
 * Description:
 */
@FeignClient("service-price")
public interface ServicePriceClient {
}
