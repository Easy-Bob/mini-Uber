package com.bob.internalcommon.constant.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bob.internalcommon.constant.constant.IdentityConstant;
import com.bob.internalcommon.constant.dto.TokenResult;

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

    private static final String JWT_KEY_PHONE = "passengerPhone";

    private static final String JWT_KEY_IDENTITY = "identity";
    // 生成token
    public static String generateToken(String passengerPhone, String identity){
        Map<String, String> map = new HashMap<>();
        map.put(JWT_KEY_PHONE, passengerPhone);
        map.put(JWT_KEY_IDENTITY, identity);

        // token过期时间
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, 1);
//        Date date = calendar.getTime();

        JWTCreator.Builder builder = JWT.create();
        // 整合map
        map.forEach(
            (k, v) -> {
                builder.withClaim(k, v);
            }
        );
        // 整合过期时间
//        builder.withExpiresAt(date);

        // 生成 token
        String sign = builder.sign(Algorithm.HMAC256(SIGN));

        return sign;
    }

    // 解析token
    public static TokenResult parseToken(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        String phone = verify.getClaim(JWT_KEY_PHONE).toString();
        String identity = verify.getClaim(JWT_KEY_IDENTITY).toString();

        TokenResult tokenResult = new TokenResult();
        tokenResult.setPassengerPhone(phone);
        tokenResult.setIdentity(identity);
        return tokenResult;
    }


    public static void main(String[] args) {
        String s = generateToken("12234239839", IdentityConstant.PASSENGER_IDENTITY);
        System.out.println(s);
        System.out.println("解析---");
        TokenResult tokenResult = parseToken(s);
        System.out.println(tokenResult.getPassengerPhone());
        System.out.println(tokenResult.getIdentity());
    }
}
