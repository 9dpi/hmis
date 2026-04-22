package com.hmis.ehr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho dấu hiệu sinh tồn.
 */
@Data
public class PrescriptionDTO {

    private String id;
    private String medicalRecordId;

    @NotBlank(message = "Tên thuốc không được trống")
    private String drugName;

    private String drugCode;
    private String activeIngredient;
    private String dosage;
    private String unit;
    private String frequency;
    private String route;
    private Integer durationDays;
    private Integer quantity;
    private BigDecimal morningDose;
    private BigDecimal noonDose;
    private BigDecimal eveningDose;
    private String instructions;
    private String status;
    private String prescribedAt;
}
