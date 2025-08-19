package com.bob.internalcommon.constant.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * Created by Sun on 2025/8/16.
 * Description:
 */
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequest {

    private Long passengerId;
    private String passengerPhone;

    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;
    private String departure;
    private String depLongitude;
    private String depLatitude;
    private String destination;
    private String destLongitude;
    private String destLatitude;
    private Integer encrypt;
    private String fareType;
    private Integer fareVersion;

    private String deviceCode;

}

