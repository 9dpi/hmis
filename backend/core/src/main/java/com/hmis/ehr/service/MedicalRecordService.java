package com.hmis.ehr.service;

import com.hmis.ehr.dto.MedicalRecordDTO;
import com.hmis.ehr.dto.PrescriptionDTO;
import com.hmis.ehr.dto.VitalSignsDTO;
import com.hmis.ehr.model.MedicalRecord;
import com.hmis.ehr.model.Prescription;
import com.hmis.ehr.model.VitalSigns;
import com.hmis.ehr.repository.MedicalRecordRepository;
import com.hmis.ehr.repository.PrescriptionRepository;
import com.hmis.ehr.repository.VitalSignsRepository;
import com.hmis.tenant.context.TenantContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository  prescriptionRepository;
    private final VitalSignsRepository    vitalSignsRepository;

    // ── CREATE ────────────────────────────────────────────────

    @Transactional
    public MedicalRecord createMedicalRecord(MedicalRecordDTO dto) {
        UUID tenantId = TenantContext.getCurrentTenantId();
        String recordNumber = generateRecordNumber(tenantId);

        MedicalRecord record = MedicalRecord.builder()
                .tenantId(tenantId)
                .patientId(UUID.fromString(dto.getPatientId()))
                .recordNumber(recordNumber)
                .visitDate(dto.getVisitDate())
                .visitReason(dto.getVisitReason())
                .symptoms(dto.getSymptoms())
                .clinicalDiagnosis(dto.getClinicalDiagnosis())
                .icd10Code(dto.getIcd10Code())
                .subclinicalResults(dto.getSubclinicalResults())
                .treatmentPlan(dto.getTreatmentPlan())
                .followUpDate(dto.getFollowUpDate())
                .notes(dto.getNotes())
                .doctorId(dto.getDoctorId() != null ? UUID.fromString(dto.getDoctorId()) : null)
                .department(dto.getDepartment())
                .status("ACTIVE")
                .build();

        MedicalRecord saved = medicalRecordRepository.save(record);

        // Lưu đơn thuốc kèm theo (nếu có)
        if (!CollectionUtils.isEmpty(dto.getPrescriptions())) {
            savePrescriptions(tenantId, saved.getId(), dto.getPrescriptions());
        }

        // Lưu dấu sinh tồn kèm theo (nếu có)
        if (!CollectionUtils.isEmpty(dto.getVitalSigns())) {
            saveVitalSigns(tenantId,
                    UUID.fromString(dto.getPatientId()),
                    saved.getId(),
                    dto.getVitalSigns());
        }

        log.info("Created medical record {} for patient {} (tenant {})",
                saved.getRecordNumber(), saved.getPatientId(), tenantId);
        return saved;
    }

    // ── READ ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MedicalRecord> getByPatientId(UUID patientId) {
        return medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId);
    }

    @Transactional(readOnly = true)
    public MedicalRecord getById(UUID id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hồ sơ bệnh án: " + id));
    }

    // ── UPDATE ────────────────────────────────────────────────

    @Transactional
    public MedicalRecord updateMedicalRecord(UUID id, MedicalRecordDTO dto) {
        MedicalRecord record = getById(id);

        record.setVisitReason(dto.getVisitReason());
        record.setSymptoms(dto.getSymptoms());
        record.setClinicalDiagnosis(dto.getClinicalDiagnosis());
        record.setIcd10Code(dto.getIcd10Code());
        record.setSubclinicalResults(dto.getSubclinicalResults());
        record.setTreatmentPlan(dto.getTreatmentPlan());
        record.setFollowUpDate(dto.getFollowUpDate());
        record.setNotes(dto.getNotes());
        record.setDepartment(dto.getDepartment());

        if (dto.getStatus() != null) {
            record.setStatus(dto.getStatus());
        }

        return medicalRecordRepository.save(record);
    }

    // ── HELPERS ───────────────────────────────────────────────

    private void savePrescriptions(UUID tenantId, UUID recordId, List<PrescriptionDTO> dtos) {
        dtos.forEach(dto -> {
            Prescription p = Prescription.builder()
                    .tenantId(tenantId)
                    .medicalRecordId(recordId)
                    .drugName(dto.getDrugName())
                    .drugCode(dto.getDrugCode())
                    .activeIngredient(dto.getActiveIngredient())
                    .dosage(dto.getDosage())
                    .unit(dto.getUnit())
                    .frequency(dto.getFrequency())
                    .route(dto.getRoute())
                    .durationDays(dto.getDurationDays())
                    .quantity(dto.getQuantity())
                    .morningDose(dto.getMorningDose())
                    .noonDose(dto.getNoonDose())
                    .eveningDose(dto.getEveningDose())
                    .instructions(dto.getInstructions())
                    .prescribedAt(LocalDateTime.now())
                    .status("ACTIVE")
                    .build();
            prescriptionRepository.save(p);
        });
    }

    private void saveVitalSigns(UUID tenantId, UUID patientId,
                                 UUID recordId, List<VitalSignsDTO> dtos) {
        dtos.forEach(dto -> {
            VitalSigns vs = VitalSigns.builder()
                    .tenantId(tenantId)
                    .patientId(patientId)
                    .medicalRecordId(recordId)
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
                    .measuredAt(dto.getMeasuredAt())
                    .build();
            vitalSignsRepository.save(vs);
        });
    }

    private String generateRecordNumber(UUID tenantId) {
        String ym = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
        long count = medicalRecordRepository.countByTenantIdAndStatus(tenantId, "ACTIVE") + 1;
        String number = "HS" + ym + String.format("%05d", count);
        while (medicalRecordRepository.existsByTenantIdAndRecordNumber(tenantId, number)) {
            count++;
            number = "HS" + ym + String.format("%05d", count);
        }
        return number;
    }
}
