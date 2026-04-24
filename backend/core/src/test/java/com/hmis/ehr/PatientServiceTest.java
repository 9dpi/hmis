package com.hmis.ehr;

import com.hmis.auth.model.User;
import com.hmis.auth.model.enums.UserRole;
import com.hmis.auth.repository.UserRepository;
import com.hmis.ehr.dto.PatientDTO;
import com.hmis.ehr.model.Patient;
import com.hmis.ehr.service.PatientService;
import com.hmis.tenant.context.TenantContext;
import com.hmis.tenant.model.Tenant;
import com.hmis.tenant.repository.TenantRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Service-level tests cho PatientService.
 * Test trực tiếp service layer mà không qua HTTP.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatientServiceTest {

    @Autowired private PatientService patientService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private TenantRepository tenantRepository;

    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private UUID savedPatientId;

    @BeforeAll
    void setup() {
        if (tenantRepository != null) {
            try {
                tenantRepository.save(Tenant.builder()
                        .id(TENANT_ID).code("PS_TEST").name("PS Test").isActive(true).build());
            } catch (Exception ignored) {}
        }
    }

    @BeforeEach
    void setTenantContext() {
        TenantContext.setCurrentTenantId(TENANT_ID);
    }

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Tạo bệnh nhân thành công")
    void createPatient() {
        PatientDTO dto = new PatientDTO();
        dto.setFullName("Trần Thị C");
        dto.setDob(LocalDate.of(1985, 3, 20));
        dto.setGender("FEMALE");
        dto.setPhone("0912345678");
        dto.setInsuranceNumber("HS9876543210");
        dto.setBloodType("A+");

        Patient patient = patientService.createPatient(dto);

        assertNotNull(patient.getId());
        assertNotNull(patient.getCode());
        assertTrue(patient.getCode().startsWith("BN"));
        assertEquals("Trần Thị C", patient.getFullName());
        assertEquals("ACTIVE", patient.getStatus());
        assertEquals(TENANT_ID, patient.getTenantId());

        savedPatientId = patient.getId();
    }

    @Test
    @Order(2)
    @DisplayName("Lấy bệnh nhân theo ID")
    void getPatientById() {
        Patient patient = patientService.getPatientById(savedPatientId);
        assertEquals("Trần Thị C", patient.getFullName());
    }

    @Test
    @Order(3)
    @DisplayName("Tìm kiếm bệnh nhân theo keyword")
    void searchPatients() {
        var results = patientService.searchPatients("Trần");
        assertFalse(results.isEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("Cập nhật bệnh nhân")
    void updatePatient() {
        PatientDTO dto = new PatientDTO();
        dto.setFullName("Trần Thị C (Cập nhật)");
        dto.setPhone("0912345999");
        dto.setGender("FEMALE");

        Patient updated = patientService.updatePatient(savedPatientId, dto);
        assertEquals("Trần Thị C (Cập nhật)", updated.getFullName());
        assertEquals("0912345999", updated.getPhone());
    }

    @Test
    @Order(5)
    @DisplayName("Phân trang danh sách bệnh nhân")
    void listPatients() {
        var page = patientService.getAllPatients(0, 10);
        assertTrue(page.getTotalElements() >= 1);
        assertFalse(page.getContent().isEmpty());
    }

    @Test
    @Order(6)
    @DisplayName("Soft delete bệnh nhân")
    void softDeletePatient() {
        patientService.deletePatient(savedPatientId);
        Patient deleted = patientService.getPatientById(savedPatientId);
        assertEquals("INACTIVE", deleted.getStatus());
    }

    @Test
    @DisplayName("Lấy bệnh nhân không tồn tại → Exception")
    void getPatientNotFound() {
        assertThrows(Exception.class, () ->
                patientService.getPatientById(UUID.randomUUID()));
    }
}
