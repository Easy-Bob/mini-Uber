package com.bob.internalcommon.constant;

import lombok.Getter;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
public enum CommonStatusEnum {
    SUCCESS(1, "success"),
    FAIL(0, "fail")
    ;
    @Getter
    private int code;
    @Getter
    private String value;

    CommonStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
