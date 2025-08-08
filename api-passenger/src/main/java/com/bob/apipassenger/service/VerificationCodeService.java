package com.bob.apipassenger.service;

import com.bob.apipassenger.remote.ServiceVerificationClient;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.NumberCodeResponse;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
@Service
public class VerificationCodeService {

    @Autowired
    private ServiceVerificationClient serviceVerificationClient;

    public String generateCode(String passengerPhone){
        // 调用验证码服务，获取验证码
        System.out.println("调用验证码服务，获取验证码");

        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        System.out.println("Remote number Code: " + numberCode);

//        存入Redis
        System.out.println("存入Redis");

        JSONObject result = new JSONObject();
        result.put("code", 1);
        result.put("message", "success");
        return result.toString();
    }
}
