package com.bob.apiBoss.service;

import com.bob.apiBoss.remote.DriverUserCarClient;
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
    private DriverUserCarClient driverUserClient;

    public ResponseResult addDriverUser(DriverUser driverUser){
        return driverUserClient.addDriverUser(driverUser);
    }

    public ResponseResult updateDriverUser(DriverUser driverUser){
        return driverUserClient.updateDriverUser(driverUser);
    }
}
