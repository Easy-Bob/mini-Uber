package com.bob.internalcommon.constant.request;

import lombok.Data;

/**
 * Created by Sun on 2025/8/10.
 * Description:
 */
@Data
public class ForecastPriceDTO {
    private String depLongitude;
    private String depLatitude;
    private String destLongitude;
    private String destLatitude;
}
