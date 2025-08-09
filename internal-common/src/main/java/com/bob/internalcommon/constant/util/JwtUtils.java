package com.bob.internalcommon.constant.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bob.internalcommon.constant.constant.IdentityConstants;
import com.bob.internalcommon.constant.constant.TokenConstants;
import com.bob.internalcommon.constant.dto.TokenResult;

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

    private static final String JWT_TOKEN_TYPE = "tokenType";

    // 生成token
    public static String generateToken(String passengerPhone, String identity, String tokenType){
        Map<String, String> map = new HashMap<>();
        map.put(JWT_KEY_PHONE, passengerPhone);
        map.put(JWT_KEY_IDENTITY, identity);
        map.put(JWT_TOKEN_TYPE, tokenType);

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
        String phone = verify.getClaim(JWT_KEY_PHONE).asString();
        String identity = verify.getClaim(JWT_KEY_IDENTITY).asString();

        TokenResult tokenResult = new TokenResult();
        tokenResult.setPassengerPhone(phone);
        tokenResult.setIdentity(identity);
        return tokenResult;
    }


    public static void main(String[] args) {
        String s = generateToken("12234239839", IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        System.out.println(s);
        System.out.println("解析---");
        TokenResult tokenResult = parseToken(s);
        System.out.println(tokenResult.getPassengerPhone());
        System.out.println(tokenResult.getIdentity());
    }
}
