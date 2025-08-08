package com.bob.apipassenger.controller;

import com.bob.apipassenger.request.VerificationCodeDTO;
import com.bob.apipassenger.service.VerificationCodeService;
import com.bob.internalcommon.constant.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
@RestController
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    @GetMapping("/verification-code")
    public ResponseResult verificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO){
//        接收手机号
        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        System.out.println("接收到的手机号：" + passengerPhone);

        return verificationCodeService.generateCode(passengerPhone);
    }

    @PostMapping("/verification-code-check")
    public ResponseResult checkVerificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO){
        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        String verificationCode = verificationCodeDTO.getVerificationCode();
        System.out.println("手机号：" + passengerPhone + ", 验证码：" + verificationCode);
        return verificationCodeService.checkCode(passengerPhone, verificationCode);
    }
}
