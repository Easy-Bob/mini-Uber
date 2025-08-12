package com.bob.internalcommon.constant.request;

import lombok.Data;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
@Data
public class VerificationCodeDTO {

    private String phone;
    private String passengerPhone;
    private String driverPhone;

    private String verificationCode;

}
