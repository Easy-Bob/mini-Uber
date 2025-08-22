package com.bob.serviceDriverUser.service;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.serviceDriverUser.mapper.DriverUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 8/19/2025.
 * Description:
 */
@Service
public class CityDriverUserService {
    @Autowired
    private DriverUserMapper driverUserMapper;

    public ResponseResult<Boolean> isAvailableDriver(String cityCode){
        int count = driverUserMapper.selectDriverUserCountByCityCode(cityCode);
        return count > 0 ? ResponseResult.success(true) : ResponseResult.success(false);
    }

}
