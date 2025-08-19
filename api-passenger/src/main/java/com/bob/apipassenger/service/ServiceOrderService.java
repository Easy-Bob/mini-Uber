package com.bob.apipassenger.service;

import com.bob.apipassenger.remote.ServiceOrderClient;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 8/17/2025.
 * Description:
 */
@Service
public class ServiceOrderService {

    @Autowired
    private ServiceOrderClient serviceOrderClient;

    public ResponseResult add(OrderRequest orderRequest){
        return serviceOrderClient.add(orderRequest);
    }
}
