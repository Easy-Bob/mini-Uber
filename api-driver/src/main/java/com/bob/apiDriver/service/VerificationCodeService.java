package com.bob.apiDriver.service;

import com.bob.apiDriver.remote.ServiceDriverUserClient;
import com.bob.apiDriver.remote.ServiceVerificationcodeClient;
import com.bob.internalcommon.constant.constant.CommonStatusEnum;
import com.bob.internalcommon.constant.constant.DriverCarConstants;
import com.bob.internalcommon.constant.constant.IdentityConstants;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.response.DriverUserExistsResponse;
import com.bob.internalcommon.constant.response.NumberCodeResponse;
import com.bob.internalcommon.constant.util.RedisPrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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
}
