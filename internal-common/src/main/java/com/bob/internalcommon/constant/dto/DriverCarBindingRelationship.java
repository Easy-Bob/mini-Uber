package com.bob.internalcommon.constant.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Driver-Car Binding Relationship DTO
 */
@Data
public class DriverCarBindingRelationship {

    /**
     * Primary key ID
     */
    private Long id;

    /**
     * Driver ID
     */
    private Long driverId;

    /**
     * Car ID
     */
    private Long carId;

    /**
     * Binding state
     * 0: unbound, 1: bound
     */
    private Integer bindState;

    /**
     * Binding time
     */
    private LocalDateTime bindingTime;

    /**
     * Unbinding time
     */
    private LocalDateTime unBindingTime;
}