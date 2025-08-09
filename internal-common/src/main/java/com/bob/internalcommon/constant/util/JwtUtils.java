package com.bob.internalcommon.constant.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sun on 2025/8/9.
 * Description:
 */
public class JwtUtils {

    // 盐
    private static final String SIGN = "BOB123SZSFROMRice56";

    // 生成token
    public static String generateToken(Map<String, String> map){
        // token过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Date date = calendar.getTime();

        JWTCreator.Builder builder = JWT.create();
        // 整合map
        map.forEach(
            (k, v) -> {
                builder.withClaim(k, v);
            }
        );
        // 整合过期时间
        builder.withExpiresAt(date);

        // 生成 token
        String sign = builder.sign(Algorithm.HMAC256(SIGN));

        return sign;
    }

    // 解析token


    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", "1");

        String s = generateToken(map);
        System.out.println("生成的token: " + s);
    }
}
