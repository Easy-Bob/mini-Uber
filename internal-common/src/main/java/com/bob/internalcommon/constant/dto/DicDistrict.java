package com.bob.internalcommon.constant.dto;

import lombok.Data;

/**
 * Created by Sun on 2025/8/11.
 * Description:
 */
@Data
public class DicDistrict {
    private String addressCode;
    private String addressName;
    private String parentAddressCode;
    private Integer level;
}
