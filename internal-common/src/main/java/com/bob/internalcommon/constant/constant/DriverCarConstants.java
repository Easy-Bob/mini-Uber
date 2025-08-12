package com.bob.internalcommon.constant.constant;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
public class DriverCarConstants {
    public static final int DRIVER_CAR_BIND = 1;
    public static final int DRIVER_CAR_UNBIND = 2;
    public static final int DRIVER_STATE_VALID = 0;
    public static final int DRIVER_STATE_INVALID = 1;

    /**
     * 司机状态，存在: 1 ; 不存在: 0
     */
    public static int DRIVER_EXISTS = 1;
    public static int DRIVER_NOT_EXISTS = 0;

    /**
     * 司机工作状态 收车：0；出车：1，暂停：2
     */
    public static int DRIVER_WORK_STATUS_STOP = 0;
    public static int DRIVER_WORK_STATUS_START = 1;
    public static int DRIVER_WORK_STATUS_SUSPEND = 2;

}
