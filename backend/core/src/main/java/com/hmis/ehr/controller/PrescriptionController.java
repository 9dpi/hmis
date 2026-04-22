package com.hmis.ehr.controller;

import com.hmis.common.ApiResponse;
import com.hmis.ehr.dto.PrescriptionDTO;
import com.hmis.ehr.model.Prescription;
import com.hmis.ehr.repository.PrescriptionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ehr/prescriptions")
@RequiredArgsConstructor
@Tag(name = "EHR - Đơn thuốc", description = "Quản lý đơn thuốc theo hồ sơ bệnh án")
@SecurityRequirement(name = "bearerAuth")
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;

    @GetMapping("/medical-record/{recordId}")
    @Operation(summary = "Danh sách đơn thuốc theo hồ sơ bệnh án")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','NURSE','PHARMACIST','READONLY')")
    public ResponseEntity<ApiResponse<List<Prescription>>> getByMedicalRecord(
            @PathVariable UUID recordId) {
        List<Prescription> list =
                prescriptionRepository.findByMedicalRecordIdOrderByCreatedAtAsc(recordId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa đơn thuốc")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ApiResponse<Void>> deletePrescription(@PathVariable UUID id) {
        prescriptionRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Đã xóa đơn thuốc", null));
    }
}
