package com.hmis.ehr.controller;

import com.hmis.common.ApiResponse;
import com.hmis.ehr.dto.MedicalRecordDTO;
import com.hmis.ehr.model.MedicalRecord;
import com.hmis.ehr.service.MedicalRecordService;
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
@RequestMapping("/api/v1/ehr/medical-records")
@RequiredArgsConstructor
@Tag(name = "EHR - Hồ sơ Bệnh án", description = "Quản lý hồ sơ bệnh án điện tử (EHR)")
@SecurityRequirement(name = "bearerAuth")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping
    @Operation(summary = "Tạo hồ sơ bệnh án mới")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecord>> createMedicalRecord(
            @Valid @RequestBody MedicalRecordDTO dto) {
        MedicalRecord created = medicalRecordService.createMedicalRecord(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo hồ sơ bệnh án thành công", created));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Lịch sử khám bệnh của bệnh nhân")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF','READONLY')")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getByPatientId(
            @PathVariable UUID patientId) {
        return ResponseEntity.ok(
                ApiResponse.ok(medicalRecordService.getByPatientId(patientId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết hồ sơ bệnh án")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF','READONLY')")
    public ResponseEntity<ApiResponse<MedicalRecord>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(medicalRecordService.getById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật hồ sơ bệnh án")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecord>> updateMedicalRecord(
            @PathVariable UUID id,
            @Valid @RequestBody MedicalRecordDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thành công",
                medicalRecordService.updateMedicalRecord(id, dto)));
    }
}
