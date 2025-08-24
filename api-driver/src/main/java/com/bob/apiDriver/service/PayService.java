package com.bob.apiDriver.service;

import com.bob.apiDriver.remote.ServiceSseCommClient;
import com.bob.internalcommon.constant.constant.IdentityConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayService {

    @Autowired
    ServiceSseCommClient serviceSseCommClient;

    public ResponseResult pushPayInfo(Long orderId, String price, Long passengerId) {
        JSONObject message = new JSONObject();
        message.put("price", price);
        serviceSseCommClient.push(passengerId, IdentityConstants.PASSENGER_IDENTITY, message.toString());

        return ResponseResult.success();

    }
}
