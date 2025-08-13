package com.bob.servicemap.service;

import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.servicemap.remote.ServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 2025/8/13.
 * Description:
 */
@Service
public class ServiceFromMapService {

    @Autowired
    private ServiceClient serviceClient;

    public ResponseResult add(String name){
        return serviceClient.add(name);
    }
}
