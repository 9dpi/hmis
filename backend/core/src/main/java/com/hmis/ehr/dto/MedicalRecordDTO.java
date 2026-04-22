package com.hmis.ehr.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO nhận / trả dữ liệu hồ sơ bệnh án điện tử.
 */
@Data
public class MedicalRecordDTO {

    private String id;
    private String recordNumber;

    @NotNull(message = "Mã bệnh nhân không được trống")
    private String patientId;

    @NotNull(message = "Ngày khám không được trống")
    private LocalDateTime visitDate;

    private String visitReason;
    private String symptoms;
    private String clinicalDiagnosis;
    private String icd10Code;
    private String subclinicalResults;
    private String treatmentPlan;
    private LocalDate followUpDate;
    private String notes;
    private String doctorId;
    private String department;
    private String status;

    // Đơn thuốc kèm theo (tùy chọn khi tạo)
    @Valid
    private List<PrescriptionDTO> prescriptions;

    // Dấu sinh tồn (tùy chọn khi tạo)
    @Valid
    private List<VitalSignsDTO> vitalSigns;

    // Trả về
    private PatientDTO patient;
    private String createdAt;
    private String updatedAt;
}
