package com.hmis.ehr.service;

import com.hmis.common.PageResponse;
import com.hmis.ehr.dto.PatientDTO;
import com.hmis.ehr.model.Patient;
import com.hmis.ehr.repository.PatientRepository;
import com.hmis.tenant.context.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    // ── CREATE ────────────────────────────────────────────────

    @Transactional
    public Patient createPatient(PatientDTO dto) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        String code = generatePatientCode(tenantId);

        Patient patient = Patient.builder()
                .tenantId(tenantId)
                .code(code)
                .fullName(dto.getFullName())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .identityNumber(dto.getIdentityNumber())
                .insuranceNumber(dto.getInsuranceNumber())
                .insuranceExpiry(dto.getInsuranceExpiry())
                .bloodType(dto.getBloodType())
                .allergies(dto.getAllergies())
                .chronicDiseases(dto.getChronicDiseases())
                .emergencyContactName(dto.getEmergencyContactName())
                .emergencyContactPhone(dto.getEmergencyContactPhone())
                .status("ACTIVE")
                .build();

        Patient saved = patientRepository.save(patient);
        log.info("Created patient {} [{}] for tenant {}", saved.getFullName(), saved.getCode(), tenantId);
        return saved;
    }

    // ── READ ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<Patient> getAllPatients(int page, int size) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        var pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        return PageResponse.from(patientRepository.findByTenantId(tenantId, pageable));
    }

    @Transactional(readOnly = true)
    public Patient getPatientById(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bệnh nhân: " + id));
    }

    @Transactional(readOnly = true)
    public List<Patient> searchPatients(String keyword) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        return patientRepository.searchByKeyword(tenantId, keyword);
    }

    // ── UPDATE ────────────────────────────────────────────────

    @Transactional
    public Patient updatePatient(UUID id, PatientDTO dto) {
        Patient patient = getPatientById(id);

        patient.setFullName(dto.getFullName());
        patient.setDob(dto.getDob());
        patient.setGender(dto.getGender());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress());
        patient.setIdentityNumber(dto.getIdentityNumber());
        patient.setInsuranceNumber(dto.getInsuranceNumber());
        patient.setInsuranceExpiry(dto.getInsuranceExpiry());
        patient.setBloodType(dto.getBloodType());
        patient.setAllergies(dto.getAllergies());
        patient.setChronicDiseases(dto.getChronicDiseases());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());

        return patientRepository.save(patient);
    }

    // ── DELETE (soft) ─────────────────────────────────────────

    @Transactional
    public void deletePatient(UUID id) {
        Patient patient = getPatientById(id);
        patient.setStatus("INACTIVE");
        patientRepository.save(patient);
        log.info("Soft-deleted patient {} [{}]", patient.getFullName(), patient.getCode());
    }

    // ── HELPERS ───────────────────────────────────────────────

    private String generatePatientCode(UUID tenantId) {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = patientRepository.countByTenantIdAndStatus(tenantId, "ACTIVE") + 1;
        String code = "BN" + year + String.format("%05d", count);
        // Đảm bảo không trùng
        while (patientRepository.existsByTenantIdAndCode(tenantId, code)) {
            count++;
            code = "BN" + year + String.format("%05d", count);
        }
        return code;
    }
}
