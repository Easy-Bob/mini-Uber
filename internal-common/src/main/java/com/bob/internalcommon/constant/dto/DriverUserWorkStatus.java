package com.bob.internalcommon.constant.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by Sun on 2025/8/12.
 * Description:
 */
@Data
public class DriverUserWorkStatus {
    private Long id;
    private Long driverId;
    private Integer workStatus;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
