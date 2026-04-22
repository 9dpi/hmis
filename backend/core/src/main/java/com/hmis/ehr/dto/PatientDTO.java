package com.hmis.ehr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO nhận / trả dữ liệu bệnh nhân qua REST API.
 */
@Data
public class PatientDTO {

    // Chỉ dùng khi trả về (response), bỏ qua khi nhận (request)
    private String id;

    @NotBlank(message = "Họ tên không được trống")
    private String fullName;

    private LocalDate dob;

    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Giới tính phải là MALE, FEMALE hoặc OTHER")
    private String gender;

    private String phone;
    private String email;
    private String address;
    private String identityNumber;
    private String insuranceNumber;
    private LocalDate insuranceExpiry;
    private String bloodType;
    private String allergies;
    private String chronicDiseases;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String status;

    // Trả về (không nhận)
    private String code;
    private String createdAt;
    private String updatedAt;
}
