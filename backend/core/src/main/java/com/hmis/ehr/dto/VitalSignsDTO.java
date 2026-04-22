package com.hmis.ehr.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho dấu hiệu sinh tồn (Vital Signs).
 */
@Data
public class VitalSignsDTO {

    private String id;
    private String patientId;
    private String medicalRecordId;

    private BigDecimal temperature;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Integer spo2;
    private BigDecimal weight;
    private BigDecimal height;
    private BigDecimal bmi;
    private BigDecimal bloodGlucose;

    private String source;       // MANUAL | IOT | DEVICE
    private String deviceId;
    private String deviceType;

    @NotNull(message = "Thời điểm đo không được trống")
    private LocalDateTime measuredAt;

    private String recordedById;
    private String createdAt;
}
