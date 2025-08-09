package com.bob.apipassenger.service;

import com.bob.apipassenger.remote.ServicePassengerUserClient;
import com.bob.internalcommon.constant.dto.PassengerUser;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.dto.TokenResult;
import com.bob.internalcommon.constant.request.VerificationCodeDTO;
import com.bob.internalcommon.constant.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 2025/8/9.
 * Description:
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    ServicePassengerUserClient servicePassengerUserClient;

    public ResponseResult getUserByAccessToken(String accessToken){
        log.info("accessToken " + accessToken);
        // 解析accessToken，拿到手机号
        TokenResult tokenResult = JwtUtils.checkToken(accessToken);
        String phone = tokenResult.getPassengerPhone();
        log.info("phone" + phone);

        // 根据手机号查询
        ResponseResult<PassengerUser> userByPhone = servicePassengerUserClient.getUserByPhone(phone);

        return ResponseResult.success(userByPhone.getData());
    }
}
