package com.bob.internalcommon.constant.request;

import lombok.Data;

/**
 * Created by Sun on 2025/8/8.
 * Description:
 */
@Data
public class VerificationCodeDTO {

    private String passengerPhone;

    private String verificationCode;

}
