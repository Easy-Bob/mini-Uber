package com.bob.apipassenger.service;

import com.bob.apipassenger.remote.ServicePassengerUserClient;
import com.bob.apipassenger.remote.ServiceVerificationClient;
import com.bob.internalcommon.constant.constant.CommonStatusEnum;
import com.bob.internalcommon.constant.constant.IdentityConstants;
import com.bob.internalcommon.constant.constant.TokenConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.VerificationCodeDTO;
import com.bob.internalcommon.constant.response.NumberCodeResponse;
import com.bob.internalcommon.constant.response.TokenResponse;
import com.bob.internalcommon.constant.util.JwtUtils;
import com.bob.internalcommon.constant.util.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.bob.internalcommon.constant.constant.IdentityConstants.PASSENGER_IDENTITY;
import static com.bob.internalcommon.constant.util.RedisPrefixUtils.generateKeyByPhone;

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
        String key = RedisPrefixUtils.generateKeyByPhone(passengerPhone);
        // value = numberCode
        stringRedisTemplate.opsForValue().set(key, String.valueOf(numberCode), 2, TimeUnit.MINUTES);

        // 通过短信服务商，发送验证码到手机上
        // 如阿里短信服务，腾讯短信通
        return ResponseResult.success();
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

        // 颁发双令牌
        String accessToken = JwtUtils.generateToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);

        String refreshToken = JwtUtils.generateToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);


        //  将token存储到redis中
        String accessTokenKey = RedisPrefixUtils.generateTokenKey(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 30, TimeUnit.DAYS);

        String refreshTokenKey = RedisPrefixUtils.generateTokenKey(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 31, TimeUnit.DAYS);

        // 响应
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success(tokenResponse);
    }
}
