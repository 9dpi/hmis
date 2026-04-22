package com.hmis.ehr.controller;

import com.hmis.common.ApiResponse;
import com.hmis.common.PageResponse;
import com.hmis.ehr.dto.PatientDTO;
import com.hmis.ehr.model.Patient;
import com.hmis.ehr.service.PatientService;
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
@RequestMapping("/api/v1/ehr/patients")
@RequiredArgsConstructor
@Tag(name = "EHR - Quản lý Bệnh nhân", description = "Tạo, tìm kiếm, cập nhật hồ sơ bệnh nhân")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @Operation(summary = "Tạo bệnh nhân mới")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF')")
    public ResponseEntity<ApiResponse<Patient>> createPatient(
            @Valid @RequestBody PatientDTO dto) {
        Patient created = patientService.createPatient(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Tạo bệnh nhân thành công", created));
    }

    @GetMapping
    @Operation(summary = "Danh sách bệnh nhân (phân trang)")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF','READONLY')")
    public ResponseEntity<ApiResponse<PageResponse<Patient>>> getAllPatients(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.getAllPatients(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết bệnh nhân")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF','READONLY')")
    public ResponseEntity<ApiResponse<Patient>> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.getPatientById(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm bệnh nhân theo tên / SĐT / mã")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF','READONLY')")
    public ResponseEntity<ApiResponse<List<Patient>>> searchPatients(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.searchPatients(keyword)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin bệnh nhân")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','STAFF')")
    public ResponseEntity<ApiResponse<Patient>> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật thành công",
                patientService.updatePatient(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa bệnh nhân (soft delete)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.ok("Đã xóa bệnh nhân", null));
    }
}
