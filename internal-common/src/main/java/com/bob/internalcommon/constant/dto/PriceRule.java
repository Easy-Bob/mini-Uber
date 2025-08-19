package com.bob.internalcommon.constant.dto;

import lombok.Data;

import java.util.Objects;

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
    private Integer fareVersion;
    private String fareType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceRule)) return false;
        PriceRule priceRule = (PriceRule) o;
        return Objects.equals(getStartFare(), priceRule.getStartFare()) && Objects.equals(getStartMile(), priceRule.getStartMile()) && Objects.equals(getUnitPricePerMile(), priceRule.getUnitPricePerMile()) && Objects.equals(getUnitPricePerMinute(), priceRule.getUnitPricePerMinute());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartFare(), getStartMile(), getUnitPricePerMile(), getUnitPricePerMinute());
    }
}
