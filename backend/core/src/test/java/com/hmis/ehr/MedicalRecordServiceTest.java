package com.hmis.ehr;

import com.hmis.ehr.dto.MedicalRecordDTO;
import com.hmis.ehr.dto.PatientDTO;
import com.hmis.ehr.dto.PrescriptionDTO;
import com.hmis.ehr.dto.VitalSignsDTO;
import com.hmis.ehr.model.MedicalRecord;
import com.hmis.ehr.model.Patient;
import com.hmis.ehr.repository.PrescriptionRepository;
import com.hmis.ehr.repository.VitalSignsRepository;
import com.hmis.ehr.service.MedicalRecordService;
import com.hmis.ehr.service.PatientService;
import com.hmis.tenant.context.TenantContext;
import com.hmis.tenant.model.Tenant;
import com.hmis.tenant.repository.TenantRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Service-level tests cho MedicalRecordService.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MedicalRecordServiceTest {

    @Autowired private MedicalRecordService medicalRecordService;
    @Autowired private PatientService patientService;
    @Autowired private PrescriptionRepository prescriptionRepository;
    @Autowired private VitalSignsRepository vitalSignsRepository;

    @Autowired(required = false)
    private TenantRepository tenantRepository;

    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private UUID patientId;
    private UUID medicalRecordId;

    @BeforeAll
    void setup() {
        if (tenantRepository != null) {
            try {
                tenantRepository.save(Tenant.builder()
                        .id(TENANT_ID).code("MR_TEST").name("MR Test").isActive(true).build());
            } catch (Exception ignored) {}
        }

        // Create patient for tests
        TenantContext.setCurrentTenantId(TENANT_ID);
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setFullName("Lê Văn D");
        patientDTO.setGender("MALE");
        patientDTO.setPhone("0987654321");
        Patient patient = patientService.createPatient(patientDTO);
        patientId = patient.getId();
        TenantContext.clear();
    }

    @BeforeEach
    void setTenant() {
        TenantContext.setCurrentTenantId(TENANT_ID);
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Tạo hồ sơ bệnh án kèm đơn thuốc và sinh tồn")
    void createMedicalRecordWithPrescriptionsAndVitalSigns() {
        // Prescription
        PrescriptionDTO rxDTO = new PrescriptionDTO();
        rxDTO.setDrugName("Amoxicillin 500mg");
        rxDTO.setDosage("500mg");
        rxDTO.setUnit("viên");
        rxDTO.setFrequency("3 lần/ngày");
        rxDTO.setDurationDays(7);
        rxDTO.setQuantity(21);
        rxDTO.setMorningDose(BigDecimal.ONE);
        rxDTO.setNoonDose(BigDecimal.ONE);
        rxDTO.setEveningDose(BigDecimal.ONE);

        // Vital signs
        VitalSignsDTO vsDTO = new VitalSignsDTO();
        vsDTO.setPatientId(patientId.toString());
        vsDTO.setTemperature(new BigDecimal("38.2"));
        vsDTO.setBloodPressureSystolic(130);
        vsDTO.setBloodPressureDiastolic(85);
        vsDTO.setHeartRate(88);
        vsDTO.setSpo2(97);
        vsDTO.setMeasuredAt(LocalDateTime.of(2026, 4, 22, 8, 30));

        // Medical record
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setPatientId(patientId.toString());
        dto.setVisitDate(LocalDateTime.of(2026, 4, 22, 9, 0));
        dto.setVisitReason("Sốt cao, ho khan");
        dto.setSymptoms("Sốt 38.2°C, ho khan 3 ngày");
        dto.setClinicalDiagnosis("Viêm phế quản cấp");
        dto.setIcd10Code("J20.9");
        dto.setTreatmentPlan("Kháng sinh + hạ sốt");
        dto.setPrescriptions(List.of(rxDTO));
        dto.setVitalSigns(List.of(vsDTO));

        MedicalRecord record = medicalRecordService.createMedicalRecord(dto);

        assertNotNull(record.getId());
        assertNotNull(record.getRecordNumber());
        assertTrue(record.getRecordNumber().startsWith("HS"));
        assertEquals("Viêm phế quản cấp", record.getClinicalDiagnosis());
        assertEquals(TENANT_ID, record.getTenantId());

        medicalRecordId = record.getId();

        // Verify prescriptions saved
        var prescriptions = prescriptionRepository.findByMedicalRecordIdOrderByCreatedAtAsc(medicalRecordId);
        assertEquals(1, prescriptions.size());
        assertEquals("Amoxicillin 500mg", prescriptions.get(0).getDrugName());

        // Verify vital signs saved
        var vitalSigns = vitalSignsRepository.findByMedicalRecordIdOrderByMeasuredAtAsc(medicalRecordId);
        assertEquals(1, vitalSigns.size());
        assertEquals(88, vitalSigns.get(0).getHeartRate());
    }

    @Test
    @Order(2)
    @DisplayName("Lấy hồ sơ bệnh án theo ID")
    void getById() {
        MedicalRecord record = medicalRecordService.getById(medicalRecordId);
        assertEquals("J20.9", record.getIcd10Code());
    }

    @Test
    @Order(3)
    @DisplayName("Lịch sử khám bệnh của bệnh nhân")
    void getByPatientId() {
        var records = medicalRecordService.getByPatientId(patientId);
        assertFalse(records.isEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("Cập nhật hồ sơ bệnh án")
    void updateMedicalRecord() {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setPatientId(patientId.toString());
        dto.setVisitDate(LocalDateTime.of(2026, 4, 22, 9, 0));
        dto.setVisitReason("Sốt cao, ho khan (cập nhật)");
        dto.setNotes("Tái khám sau 5 ngày");

        MedicalRecord updated = medicalRecordService.updateMedicalRecord(medicalRecordId, dto);
        assertEquals("Tái khám sau 5 ngày", updated.getNotes());
    }

    @Test
    @DisplayName("Lấy hồ sơ không tồn tại → Exception")
    void getByIdNotFound() {
        assertThrows(Exception.class, () ->
                medicalRecordService.getById(UUID.randomUUID()));
    }
}
