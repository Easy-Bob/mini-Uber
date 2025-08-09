package com.bob.internalcommon.constant.util;

/**
 * Created by Sun on 2025/8/9.
 * Description:
 */
public class RedisPrefixUtils {

    private static String verificationCodePrefix = "passenger-verification-code-";
    private static String tokenPrefix = "token-";

    /**
     * 根据手机号生成key
     * @param passengerPhone
     * @return
     */
    public static String generateKeyByPhone(String passengerPhone){
        return verificationCodePrefix + passengerPhone;
    }

    /**
     * 生成token关键字
     * @param passengerPhone
     * @param identity
     * @return
     */
    public static String generateTokenKey(String passengerPhone, String identity, String tokenType){
        return tokenPrefix + passengerPhone + "-" + identity + "-" + tokenType;
    }
}
