package com.bob.apiDriver.service;

import com.bob.apiDriver.remote.ServiceDriverUserClient;
import com.bob.apiDriver.remote.ServiceVerificationcodeClient;
import com.bob.internalcommon.constant.constant.CommonStatusEnum;
import com.bob.internalcommon.constant.constant.DriverCarConstants;
import com.bob.internalcommon.constant.constant.IdentityConstants;
import com.bob.internalcommon.constant.constant.TokenConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.request.VerificationCodeDTO;
import com.bob.internalcommon.constant.response.DriverUserExistsResponse;
import com.bob.internalcommon.constant.response.NumberCodeResponse;
import com.bob.internalcommon.constant.response.TokenResponse;
import com.bob.internalcommon.constant.util.JwtUtils;
import com.bob.internalcommon.constant.util.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.TimeUnit;

import static com.bob.internalcommon.constant.constant.IdentityConstants.DRIVER_IDENTITY;
import static com.bob.internalcommon.constant.constant.IdentityConstants.PASSENGER_IDENTITY;
import static com.bob.internalcommon.constant.util.RedisPrefixUtils.generateKeyByPhone;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@Service
@Slf4j
public class VerificationCodeService {

    @Autowired
    private ServiceDriverUserClient client;

    @Autowired
    private ServiceVerificationcodeClient serviceVerificationcodeClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ResponseResult checkAndSendVerificationCode(String driverPhone){
        // 查询service-driver-user, 判断用户是否存在
        ResponseResult<DriverUserExistsResponse> result = client.checkDriver(driverPhone);
        DriverUserExistsResponse data = result.getData();
        int ifExists = data.getIfExists();
        if(ifExists == DriverCarConstants.DRIVER_NOT_EXISTS){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_NOT_EXIST.getCode(), CommonStatusEnum.DRIVER_NOT_EXIST.getValue());
        }
        log.info("driver判读成功");
        // 获取验证码
        ResponseResult<NumberCodeResponse> numberCodeResult = serviceVerificationcodeClient.getNumberCode(6);
        NumberCodeResponse numberCodeResponse = numberCodeResult.getData();
        int numberCode = numberCodeResponse.getNumberCode();
        log.info("验证码 " + numberCode);

        // 调用第三方发送验证码

        // 存入redis
        String key = RedisPrefixUtils.generateKeyByPhone(IdentityConstants.DRIVER_IDENTITY, driverPhone);
        stringRedisTemplate.opsForValue().set(key, ""+numberCode, 2, TimeUnit.MINUTES);

        return ResponseResult.success("");
    }


    public ResponseResult checkCode(String driverPhone, String verificationCode) {
        // 根据手机号，去redis读取验证码
        String key = generateKeyByPhone(DRIVER_IDENTITY, driverPhone);
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
        verificationCodeDTO.setPhone(driverPhone);

        // 颁发双令牌
        String accessToken = JwtUtils.generateToken(driverPhone, IdentityConstants.DRIVER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);

        String refreshToken = JwtUtils.generateToken(driverPhone, IdentityConstants.DRIVER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);


        //  将token存储到redis中
        String accessTokenKey = RedisPrefixUtils.generateTokenKey(driverPhone, IdentityConstants.DRIVER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 30, TimeUnit.DAYS);

        String refreshTokenKey = RedisPrefixUtils.generateTokenKey(driverPhone, IdentityConstants.DRIVER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 31, TimeUnit.DAYS);

        // 响应
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);
        return ResponseResult.success(tokenResponse);


    }
}
