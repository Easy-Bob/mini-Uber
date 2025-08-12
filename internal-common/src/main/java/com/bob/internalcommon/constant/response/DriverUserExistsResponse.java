package com.bob.internalcommon.constant.response;

import lombok.Data;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@Data
public class DriverUserExistsResponse {
    private String driverPhone;
    private int ifExists;
}
