package com.bob.internalcommon.constant.util;

/**
 * Created by Sun on 2025/8/9.
 * Description:
 */
public class RedisPrefixUtils {

    private static String verificationCodePrefix = "-verification-code-";
    private static String tokenPrefix = "token-";
    private static String blackDeviceCodePrefix = "black-";

    /**
     * 根据手机号生成key
     * @param identity
     * @param phone
     * @return
     */
    public static String generateKeyByPhone(String identity, String phone){
        return identity + verificationCodePrefix + phone;
    }

    /**
     * 生成token关键字
     * @param phone
     * @param identity
     * @return
     */
    public static String generateTokenKey(String phone, String identity, String tokenType){
        return tokenPrefix + phone + "-" + identity + "-" + tokenType;
    }

    public static String generateBlackDeviceKey(String deviceCode){
        return blackDeviceCodePrefix + deviceCode;
    }
}
