package com.bob.apipassenger.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.bob.internalcommon.constant.dto.ResponseResult;
import com.bob.internalcommon.constant.dto.TokenResult;
import com.bob.internalcommon.constant.util.JwtUtils;
import com.bob.internalcommon.constant.util.RedisPrefixUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

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

        TokenResult tokenResult = null;
        try{
            tokenResult = JwtUtils.parseToken(token);
        }catch (SignatureVerificationException e){
            resultString = "token sign error";
            result = false;
        }catch (TokenExpiredException e){
            resultString = "token time out";
            result = false;
        }catch (AlgorithmMismatchException e){
            resultString = "token AlgorithmMismatchException";
            result = false;
        }catch (Exception e){
            resultString = "token invalid";
            result = false;
        }

        if(tokenResult != null){
            // 从Redis中取出token
            String phone = tokenResult.getPassengerPhone();
            String identity = tokenResult.getIdentity();

            String tokenKey = RedisPrefixUtils.generateTokenKey(phone, identity);
            // 比较接收到的的token是否存在Redis中
            String tokenRedis = stringRedisTemplate.opsForValue().get(tokenKey);
            if(StringUtils.isBlank(tokenRedis)){
                resultString = "token invalid";
                result = false;
            }else{
                if(!token.trim().equals(tokenRedis.trim())){
                    resultString = "token invalid";
                    result = false;
                }
            }
        }else{
            resultString = "token invalid";
            result = false;
        }

        return result;
    }
}
