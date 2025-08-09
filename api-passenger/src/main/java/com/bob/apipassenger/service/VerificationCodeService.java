package com.bob.apipassenger.service;

import com.bob.apipassenger.remote.ServicePassengerUserClient;
import com.bob.apipassenger.remote.ServiceVerificationClient;
import com.bob.internalcommon.constant.CommonStatusEnum;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.VerificationCodeDTO;
import com.bob.internalcommon.constant.response.NumberCodeResponse;
import com.bob.internalcommon.constant.response.TokenResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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

    // 生成验证码服务
    @Autowired
    private ServiceVerificationClient serviceVerificationClient;

    //校验验证码服务
    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;

    private String verificationCodePrefix = "passenger-verification-code-";

    // 连接redis
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成验证码
     * @param passengerPhone 手机号
     * @return
     */
    public ResponseResult generateCode(String passengerPhone){
        // 调用验证码服务，获取验证码
        System.out.println("调用验证码服务，获取验证码");

        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationClient.getNumberCode(6);
        int numberCode = numberCodeResponse.getData().getNumberCode();

        System.out.println("Remote number Code: " + numberCode);

//        存储Redis
        System.out.println("存入Redis");
        //key value expiration-time
        String key = generateKeyByPhone(passengerPhone);
        // value = numberCode
        stringRedisTemplate.opsForValue().set(key, String.valueOf(numberCode), 2, TimeUnit.MINUTES);

        // 通过短信服务商，发送验证码到手机上
        // 如阿里短信服务，腾讯短信通
        return ResponseResult.success();
    }

    /**
     * 根据手机号生成key
     * @param passengerPhone
     * @return
     */
    private String generateKeyByPhone(String passengerPhone){
        return verificationCodePrefix + passengerPhone;
    }

    /**
     * 校验验证码
     * @param passengerPhone 手机号
     * @param verificationCode 验证码
     * @return
     */
    public ResponseResult checkCode(String passengerPhone, String verificationCode){
        // 根据手机号，去redis读取验证码
        String key = generateKeyByPhone(passengerPhone);
        String codeRedis = stringRedisTemplate.opsForValue().get(key);
        System.out.println("redis中的value:" + codeRedis);
        // 校验验证码
        if(StringUtils.isBlank(codeRedis)){
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }else if(!verificationCode.trim().equals(codeRedis)){
            return ResponseResult.fail(CommonStatusEnum.VERIFICATION_CODE_ERROR.getCode(), CommonStatusEnum.VERIFICATION_CODE_ERROR.getValue());
        }

        // 验证码校验成功，进行下一步：用户注册或登入
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
        verificationCodeDTO.setPassengerPhone(passengerPhone);

        // 调用微服务，进行用户注册/登入，存储数据库
        servicePassengerUserClient.loginOrRegister(verificationCodeDTO);

        System.out.println("颁发令牌");

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken("token value");
        return ResponseResult.success(tokenResponse);
    }
}
