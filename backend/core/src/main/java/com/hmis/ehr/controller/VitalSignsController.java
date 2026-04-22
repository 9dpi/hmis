package com.hmis.ehr.controller;

import com.hmis.common.ApiResponse;
import com.hmis.ehr.dto.VitalSignsDTO;
import com.hmis.ehr.model.VitalSigns;
import com.hmis.ehr.repository.VitalSignsRepository;
import com.hmis.tenant.context.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ehr/vital-signs")
@RequiredArgsConstructor
@Tag(name = "EHR - Dấu hiệu Sinh tồn", description = "Ghi nhận và truy vấn chỉ số sinh tồn (IoT ready)")
@SecurityRequirement(name = "bearerAuth")
public class VitalSignsController {

    private final VitalSignsRepository vitalSignsRepository;

    @PostMapping
    @Operation(summary = "Ghi nhận dấu hiệu sinh tồn")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE')")
    public ResponseEntity<ApiResponse<VitalSigns>> recordVitalSigns(
            @Valid @RequestBody VitalSignsDTO dto) {

        VitalSigns vs = VitalSigns.builder()
                .tenantId(TenantContext.getCurrentTenantId())
                .patientId(UUID.fromString(dto.getPatientId()))
                .medicalRecordId(dto.getMedicalRecordId() != null
                        ? UUID.fromString(dto.getMedicalRecordId()) : null)
                .temperature(dto.getTemperature())
                .bloodPressureSystolic(dto.getBloodPressureSystolic())
                .bloodPressureDiastolic(dto.getBloodPressureDiastolic())
                .heartRate(dto.getHeartRate())
                .respiratoryRate(dto.getRespiratoryRate())
                .spo2(dto.getSpo2())
                .weight(dto.getWeight())
                .height(dto.getHeight())
                .bmi(dto.getBmi())
                .bloodGlucose(dto.getBloodGlucose())
                .source(dto.getSource() != null ? dto.getSource() : "MANUAL")
                .deviceId(dto.getDeviceId())
                .deviceType(dto.getDeviceType())
                .measuredAt(dto.getMeasuredAt())
                .recordedById(dto.getRecordedById() != null
                        ? UUID.fromString(dto.getRecordedById()) : null)
                .build();

        VitalSigns saved = vitalSignsRepository.save(vs);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Ghi nhận sinh tồn thành công", saved));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Lịch sử sinh tồn của bệnh nhân")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','READONLY')")
    public ResponseEntity<ApiResponse<List<VitalSigns>>> getByPatient(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(ApiResponse.ok(
                vitalSignsRepository.findByPatientIdOrderByMeasuredAtDesc(patientId)));
    }

    @GetMapping("/medical-record/{recordId}")
    @Operation(summary = "Sinh tồn trong lần khám")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','READONLY')")
    public ResponseEntity<ApiResponse<List<VitalSigns>>> getByMedicalRecord(
            @PathVariable UUID recordId) {
        return ResponseEntity.ok(ApiResponse.ok(
                vitalSignsRepository.findByMedicalRecordIdOrderByMeasuredAtAsc(recordId)));
    }
}
