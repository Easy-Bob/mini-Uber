package com.bob.apiDriver.remote;

import com.bob.internalcommon.constant.dto.DriverUser;
import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@FeignClient("service-driver-user")
public interface DriverUserClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/user")
    ResponseResult updateDriverUser(DriverUser driverUser);
}
