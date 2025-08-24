package com.bob.internalcommon.constant.util;

public class SsePrefixUtils {
    public static final String SEPARATOR = "$";

    public static String generateSseKey(Long userId, String identity){
        return userId + SEPARATOR +identity;
    }
}
