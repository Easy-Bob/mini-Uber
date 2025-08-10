package com.bob.internalcommon.constant.dto;

import lombok.Data;

/**
 * Created by Sun on 2025/8/10.
 * Description:
 */
@Data
public class PriceRule {
    private String cityCode;
    private String vehicleType;
    private Double startFare;
    private Integer startMile;
    private Double unitPricePerMile;
    private Double unitPricePerMinute;
}
