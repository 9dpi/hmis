package com.hmis.common.init;

import com.hmis.auth.model.enums.UserRole;
import com.hmis.auth.model.User;
import com.hmis.auth.repository.UserRepository;
import com.hmis.ehr.model.Patient;
import com.hmis.ehr.repository.PatientRepository;
import com.hmis.ehr.model.MedicalRecord;
import com.hmis.ehr.repository.MedicalRecordRepository;
import com.hmis.ehr.model.Prescription;
import com.hmis.ehr.repository.PrescriptionRepository;
import com.hmis.tenant.model.Tenant;
import com.hmis.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (tenantRepository.count() > 0) {
            return; // Đã có dữ liệu thì bỏ qua
        }

        log.info("Starting data initialization for local environment...");

        // 1. Tạo Tenant mặc định
        Tenant tenant = Tenant.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .code("TRAM_YTE_MAU")
                .name("Trạm Y tế Mẫu SCP")
                .isActive(true)
                .build();
        tenantRepository.save(tenant);

        // 2. Tạo User Admin
        User admin = User.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("admin123"))
                .fullName("System Admin")
                .role(UserRole.ADMIN)
                .tenantId(tenant.getId())
                .isActive(true)
                .build();
        userRepository.save(admin);

        // 3. Tạo bệnh nhân
        Patient p1 = createPatient(tenant.getId(), "Nguyễn Văn An", "BN001", "0901234567", LocalDate.of(1990, 5, 15), "MALE");
        Patient p2 = createPatient(tenant.getId(), "Trần Thị Bình", "BN002", "0988665544", LocalDate.of(1995, 10, 20), "FEMALE");
        Patient p3 = createPatient(tenant.getId(), "Lê Hoàng Long", "BN003", "0912112233", LocalDate.of(1980, 1, 1), "MALE");
        Patient p4 = createPatient(tenant.getId(), "Phạm Thu Hương", "BN004", "0933445566", LocalDate.of(2001, 8, 8), "FEMALE");
        Patient p5 = createPatient(tenant.getId(), "Bùi Tuấn Anh", "BN005", "0977112233", LocalDate.of(1965, 3, 12), "MALE");

        // 4. Tạo bệnh án cho p1 (Nguyễn Văn An) - Bệnh Hô Hấp
        MedicalRecord mr1 = createMedicalRecord(tenant.getId(), p1.getId(), "HS_2026_001", 
            "Sốt cao, ho nhiều", "Sốt 39 độ, ho có đờm, rát họng", "Viêm họng cấp", "J06.9", 
            "Uống nhiều nước, cách ly nhẹ", "Khoa Nội", LocalDateTime.now().minusDays(5));
        
        createPrescription(tenant.getId(), mr1.getId(), "Paracetamol 500mg", "Viên", 10, "Sáng 1, Tối 1 sau ăn", new BigDecimal("1.0"), new BigDecimal("0.0"), new BigDecimal("1.0"));
        createPrescription(tenant.getId(), mr1.getId(), "Amoxicillin 500mg", "Viên", 15, "Sáng 1, Trưa 1, Tối 1", new BigDecimal("1.0"), new BigDecimal("1.0"), new BigDecimal("1.0"));

        // 5. Tạo bệnh án cho p2 (Trần Thị Bình) - Tiêu hóa
        MedicalRecord mr2 = createMedicalRecord(tenant.getId(), p2.getId(), "HS_2026_002", 
            "Đau bụng từng cơn", "Đau thượng vị, ợ chua, buồn nôn", "Viêm dạ dày tá tràng", "K29.7", 
            "Ăn nhẹ, tránh đồ cay nóng", "Khoa Tiêu Hóa", LocalDateTime.now().minusDays(2));
            
        createPrescription(tenant.getId(), mr2.getId(), "Omeprazole 20mg", "Viên", 14, "Sáng 1 viên trước ăn 30p", new BigDecimal("1.0"), new BigDecimal("0.0"), new BigDecimal("0.0"));

        // 6. Tạo bệnh án cho p5 (Bùi Tuấn Anh) - Tim mạch / Tiểu đường (Người lớn tuổi)
        MedicalRecord mr3 = createMedicalRecord(tenant.getId(), p5.getId(), "HS_2026_003", 
            "Tái khám định kỳ", "Huyết áp ổn định, đường huyết hơi cao", "Tăng huyết áp vô căn / Tiểu đường tuýp 2", "I10", 
            "Duy trì thuốc, tập thể dục nhẹ nhàng", "Khoa Nội Tiết", LocalDateTime.now().minusHours(2));

        createPrescription(tenant.getId(), mr3.getId(), "Amlodipine 5mg", "Viên", 30, "Sáng 1 viên", new BigDecimal("1.0"), new BigDecimal("0.0"), new BigDecimal("0.0"));
        createPrescription(tenant.getId(), mr3.getId(), "Metformin 850mg", "Viên", 60, "Sáng 1, Tối 1 trong bữa ăn", new BigDecimal("1.0"), new BigDecimal("0.0"), new BigDecimal("1.0"));

        log.info("Data initialization completed successfully with Mock Patients and Medical Records!");
        log.info("Default Login -> Username: admin, Password: admin123");
    }

    private Patient createPatient(UUID tenantId, String name, String code, String phone, LocalDate dob, String gender) {
        Patient p = Patient.builder()
                .tenantId(tenantId).fullName(name).code(code).phone(phone)
                .dob(dob).gender(gender).bloodType("O").status("ACTIVE").build();
        return patientRepository.save(p);
    }

    private MedicalRecord createMedicalRecord(UUID tenantId, UUID patientId, String recordNumber, 
                                              String visitReason, String symptoms, String diagnosis, String icd10, 
                                              String treatmentPlan, String department, LocalDateTime visitDate) {
        MedicalRecord mr = MedicalRecord.builder()
                .tenantId(tenantId)
                .patientId(patientId)
                .recordNumber(recordNumber)
                .visitDate(visitDate)
                .visitReason(visitReason)
                .symptoms(symptoms)
                .clinicalDiagnosis(diagnosis)
                .icd10Code(icd10)
                .treatmentPlan(treatmentPlan)
                .department(department)
                .status("ACTIVE")
                .build();
        return medicalRecordRepository.save(mr);
    }

    private void createPrescription(UUID tenantId, UUID mrId, String drugName, String unit, Integer qty, 
                                    String instructions, BigDecimal morning, BigDecimal noon, BigDecimal evening) {
        Prescription rx = Prescription.builder()
                .tenantId(tenantId)
                .medicalRecordId(mrId)
                .drugName(drugName)
                .unit(unit)
                .quantity(qty)
                .instructions(instructions)
                .morningDose(morning)
                .noonDose(noon)
                .eveningDose(evening)
                .prescribedAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();
        prescriptionRepository.save(rx);
    }
}
