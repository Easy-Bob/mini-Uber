package com.bob.apipassenger.remote;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.NumberCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
@FeignClient("service-verificationcode")
public interface ServiceVerificationClient {
    @RequestMapping(method = RequestMethod.GET, value = "/numberCode/{size")
    ResponseResult<NumberCodeResponse> getNumberCode(@PathVariable("size") int size);

}
