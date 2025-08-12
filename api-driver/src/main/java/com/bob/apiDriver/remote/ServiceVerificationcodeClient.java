package com.bob.apiDriver.remote;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.NumberCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@FeignClient("service-verificationcode")
public interface ServiceVerificationcodeClient {


    @RequestMapping(method = RequestMethod.GET, value = "/numberCode/{size}")
    public ResponseResult<NumberCodeResponse> getNumberCode(@PathVariable("size") int size);
}
