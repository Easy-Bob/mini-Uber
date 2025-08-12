package com.bob.apiDriver.service;

import com.bob.apiDriver.remote.ServiceDriverUserClient;
import com.bob.internalcommon.constant.dto.DriverUser;
import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@Service
public class DriverUserService {
    @Autowired
    private ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult updateDriverUser(DriverUser driverUser){
        return serviceDriverUserClient.updateDriverUser(driverUser);
    }
}
