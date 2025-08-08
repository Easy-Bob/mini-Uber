package com.bob.apipassenger.service;

import com.bob.apipassenger.remote.ServiceVerificationClient;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.NumberCodeResponse;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
@Service
public class VerificationCodeService {

    @Autowired
    private ServiceVerificationClient serviceVerificationClient;

    private String verificationCodePrefix = "passenger-verification-code-";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String generateCode(String passengerPhone){
        // 调用验证码服务，获取验证码
        System.out.println("调用验证码服务，获取验证码");

        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        System.out.println("Remote number Code: " + numberCode);

//        存入Redis
        System.out.println("存入Redis");
        //key value expiration-time
        String key = verificationCodePrefix + passengerPhone;
        // value = numberCode
        stringRedisTemplate.opsForValue().set(key, String.valueOf(numberCode), 2, TimeUnit.MINUTES);

        JSONObject result = new JSONObject();
        result.put("code", 1);
        result.put("message", "success");
        return result.toString();
    }
}
