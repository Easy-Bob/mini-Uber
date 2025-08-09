package com.bob.internalcommon.constant.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by Sun on 2025/8/9.
 * Description:
 */
@Data
public class PassengerUser {
    private Long id;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private String passengerPhone;
    private String passengerName;
    private byte passengerGender;
    private byte state;
    private String profilePhoto;

}
