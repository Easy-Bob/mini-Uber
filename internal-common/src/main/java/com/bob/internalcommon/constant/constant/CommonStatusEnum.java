package com.bob.internalcommon.constant.constant;

import lombok.Getter;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
public enum CommonStatusEnum {
    // 验证码错误提示: 1000 - 1099
    VERIFICATION_CODE_ERROR(1099,"验证码不正确"),

    /**
     * Token类提示 ： 1100 - 1199
     */
    TOKEN_ERROR(1199, "token错误"),

    /**
     * 用户提示：1200 - 1299
     */
    USER_NOT_EXISTS(1200, "当前用户不存在"),

    /**
     * 计算规则不存在
     */
    PRICE_RULE_EMPTY(1300, "计价规则不存在"),

    /**
     * 地图信息
     * 1400 - 1499
     */
    MAP_DISTRICT_ERROR(1400, "请求地图错误"),

    /**
     * 司机和车辆关系
     * 1500 - 1599
     */
    DRIVER_NOT_EXIST(1501, "司机不存在"),
    DRIVER_CAR_BIND_EXISTS (1502, "司机和车辆已绑定，请勿重复操作"),
    DRIVER_CAR_UNBIND_EXISTS (1503, "司机和车辆已解绑，请勿重复操作"),
    DRIVER_BIND_EXISTS (1504, "司机以被绑定，请勿重复操作"),
    CAR_BIND_EXISTS (1505, "车辆以被绑定，请勿重复操作"),

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
