package com.bob.serviceDriverUser.remote;

import com.bob.internalcommon.constant.dto.DriverUser;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.TerminalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@FeignClient("service-map")
public interface ServiceMapClient {

    @RequestMapping(method = RequestMethod.POST, value = "/terminal/add")
    public ResponseResult<TerminalResponse> addTerminal(@RequestParam String name);
}
