package com.bob.apiDriver.interceptor;

import com.bob.internalcommon.constant.constant.TokenConstants;
import com.bob.internalcommon.constant.dto.TokenResult;
import com.bob.internalcommon.constant.util.JwtUtils;
import com.bob.internalcommon.constant.util.RedisPrefixUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Sun on 2025/8/9.
 * Description:
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

        boolean result = true;
        String resultString = "";
        String token = request.getHeader("Authorization");

        TokenResult tokenResult = JwtUtils.checkToken(token);

        if(tokenResult != null){
            // 从Redis中取出token
            String phone = tokenResult.getPassengerPhone();
            String identity = tokenResult.getIdentity();

            String tokenKey = RedisPrefixUtils.generateTokenKey(phone, identity, TokenConstants.ACCESS_TOKEN_TYPE);
            // 比较接收到的的token是否存在Redis中
            String tokenRedis = stringRedisTemplate.opsForValue().get(tokenKey);
            if((StringUtils.isBlank(tokenRedis)) || (!token.trim().equals(tokenRedis.trim()))){
                resultString = "token invalid";
                result = false;
            }
        }else{
            resultString = "token invalid";
            result = false;
        }

        return result;
    }
}
