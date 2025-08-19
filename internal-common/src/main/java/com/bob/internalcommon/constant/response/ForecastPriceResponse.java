package com.bob.internalcommon.constant.response;

import lombok.Data;

/**
 * Created by Sun on 2025/8/10.
 * Description:
 */
@Data
public class ForecastPriceResponse {
    private Double price;
    private String cityCode;
    private String vehicleType;
    private String fareType;
    private Integer fareVersion;
}
