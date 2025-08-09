package com.bob.internalcommon.constant.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

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

    private static final String JWT_KEY = "passengerPhone";
    // 生成token
    public static String generateToken(String passengerPhone){
        Map<String, String> map = new HashMap<>();
        map.put(JWT_KEY, passengerPhone);
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
    public static String parseToken(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        Claim claim = verify.getClaim(JWT_KEY);
        return claim.toString();
    }


    public static void main(String[] args) {
        String s = generateToken("12234239839");
        System.out.println(s);
        System.out.println(parseToken(s));
    }
}
