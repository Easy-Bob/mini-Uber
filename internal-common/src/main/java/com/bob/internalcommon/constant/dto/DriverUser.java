package com.bob.internalcommon.constant.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by Sun on 2025/8/11.
 * Description:
 */
@Data
public class DriverUser {
    private Integer id;
    private String address;
    private String driverName;
    private String driverPhone;
    private Integer driverGender;
    private LocalDate driverBirthday;
    private String driverNation;
    private String driverContactAddress;
    private String licenseId;
    private LocalDate getDriverLicenseDate;
    private LocalDate driverLicenseOn;
    private LocalDate driverLicenseOff;
    private Integer taxiDriver;
    private String networkCarIssueOrganization;
    private String certificateNo;
    private LocalDate networkCarIssueDate;
    private LocalDate getNetworkCarProofDate;
    private LocalDate networkCarProofOn;
    private LocalDate networkCarProofOff;
    private LocalDate registerDate;
    private Integer commercialType;
    private String contractCompany;
    private LocalDate contractOn;
    private LocalDate contractOff;
    private Integer state;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
