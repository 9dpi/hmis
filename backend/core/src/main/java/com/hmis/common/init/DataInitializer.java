package com.hmis.common.init;

import com.hmis.auth.model.enums.UserRole;
import com.hmis.auth.model.User;
import com.hmis.auth.repository.UserRepository;
import com.hmis.ehr.model.Patient;
import com.hmis.ehr.repository.PatientRepository;
import com.hmis.tenant.model.Tenant;
import com.hmis.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (tenantRepository.count() > 0) {
            return;
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

        // 3. Tạo một vài bệnh nhân mẫu
        createPatient(tenant.getId(), "Nguyễn Văn An", "BN001", "0901234567", LocalDate.of(1990, 5, 15), "MALE");
        createPatient(tenant.getId(), "Trần Thị Bình", "BN002", "0988665544", LocalDate.of(1995, 10, 20), "FEMALE");
        createPatient(tenant.getId(), "Lê Hoàng Long", "BN003", "0912112233", LocalDate.of(1980, 1, 1), "MALE");

        log.info("Data initialization completed successfully!");
        log.info("Default Login -> Username: admin, Password: admin123");
    }

    private void createPatient(UUID tenantId, String name, String code, String phone, LocalDate dob, String gender) {
        Patient p = Patient.builder()
                .tenantId(tenantId)
                .fullName(name)
                .code(code)
                .phone(phone)
                .dob(dob)
                .gender(gender)
                .bloodType("O")
                .status("ACTIVE")
                .build();
        patientRepository.save(p);
    }
}
