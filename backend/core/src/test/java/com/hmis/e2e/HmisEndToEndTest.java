package com.hmis.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmis.auth.model.User;
import com.hmis.auth.model.enums.UserRole;
import com.hmis.auth.repository.UserRepository;
import com.hmis.tenant.model.Tenant;
import com.hmis.tenant.repository.TenantRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End Integration Test cho toàn bộ HMIS Backend.
 * 
 * Flow: Login → Create Patient → Create Medical Record (with Prescriptions + VitalSigns)
 *       → Query all data → Update → Delete
 * 
 * Sử dụng H2 in-memory, không cần Docker.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HmisEndToEndTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private TenantRepository tenantRepository;

    private static final UUID TEST_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    
    // State shared across ordered tests
    private String jwtToken;
    private String patientId;
    private String medicalRecordId;

    @BeforeAll
    void setupTestData() {
        // Create test tenant if TenantRepository exists
        if (tenantRepository != null) {
            try {
                Tenant tenant = Tenant.builder()
                        .id(TEST_TENANT_ID)
                        .code("TEST_TENANT")
                        .name("Test Tenant")
                        .isActive(true)
                        .build();
                tenantRepository.save(tenant);
            } catch (Exception e) {
                // Tenant table may not exist in H2 create-drop, ignore
            }
        }

        // Create test admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .tenantId(TEST_TENANT_ID)
                    .username("admin")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .fullName("System Admin")
                    .email("admin@hmis.local")
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // 1. AUTH TESTS
    // ═══════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @DisplayName("1.1 Login thành công → nhận JWT token")
    void login_success() throws Exception {
        String loginJson = """
            { "username": "admin", "password": "Admin@123" }
            """;

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andReturn();

        // Extract token for subsequent tests
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        jwtToken = json.get("data").get("token").asText();
    }

    @Test
    @Order(2)
    @DisplayName("1.2 Login thất bại → sai mật khẩu")
    void login_wrongPassword() throws Exception {
        String loginJson = """
            { "username": "admin", "password": "wrong" }
            """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().is(401).or(status().is(500).or(status().is(403))));
    }

    @Test
    @Order(3)
    @DisplayName("1.3 Truy cập API bảo vệ mà không có token → 401/403")
    void accessProtectedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/patients"))
                .andExpect(status().is4xxClientError());
    }

    // ═══════════════════════════════════════════════════════════
    // 2. PATIENT CRUD TESTS
    // ═══════════════════════════════════════════════════════════

    @Test
    @Order(10)
    @DisplayName("2.1 Tạo bệnh nhân mới")
    void createPatient() throws Exception {
        String patientJson = """
            {
                "fullName": "Nguyễn Văn A",
                "dob": "1990-05-15",
                "gender": "MALE",
                "phone": "0901234567",
                "email": "nguyenvana@test.com",
                "address": "123 Đường Lê Lợi, Q.1, TP.HCM",
                "identityNumber": "079090012345",
                "insuranceNumber": "HS4060123456789",
                "bloodType": "O+",
                "allergies": "Penicillin",
                "chronicDiseases": "Cao huyết áp",
                "emergencyContactName": "Nguyễn Thị B",
                "emergencyContactPhone": "0909876543"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/v1/ehr/patients")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Nguyễn Văn A"))
                .andExpect(jsonPath("$.data.code").isNotEmpty())
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        patientId = json.get("data").get("id").asText();
    }

    @Test
    @Order(11)
    @DisplayName("2.2 Lấy chi tiết bệnh nhân theo ID")
    void getPatientById() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/patients/" + patientId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Nguyễn Văn A"))
                .andExpect(jsonPath("$.data.phone").value("0901234567"));
    }

    @Test
    @Order(12)
    @DisplayName("2.3 Danh sách bệnh nhân (phân trang)")
    void listPatients() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/patients")
                        .param("page", "0").param("size", "10")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(13)
    @DisplayName("2.4 Tìm kiếm bệnh nhân theo tên")
    void searchPatients() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/patients/search")
                        .param("keyword", "Nguyễn")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(14)
    @DisplayName("2.5 Cập nhật thông tin bệnh nhân")
    void updatePatient() throws Exception {
        String updateJson = """
            {
                "fullName": "Nguyễn Văn A (Cập nhật)",
                "dob": "1990-05-15",
                "gender": "MALE",
                "phone": "0901234999",
                "address": "456 Đường Nguyễn Huệ, Q.1, TP.HCM"
            }
            """;

        mockMvc.perform(put("/api/v1/ehr/patients/" + patientId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName").value("Nguyễn Văn A (Cập nhật)"))
                .andExpect(jsonPath("$.data.phone").value("0901234999"));
    }

    // ═══════════════════════════════════════════════════════════
    // 3. MEDICAL RECORD + PRESCRIPTION + VITAL SIGNS
    // ═══════════════════════════════════════════════════════════

    @Test
    @Order(20)
    @DisplayName("3.1 Tạo hồ sơ bệnh án (kèm đơn thuốc + sinh tồn)")
    void createMedicalRecord() throws Exception {
        String recordJson = String.format("""
            {
                "patientId": "%s",
                "visitDate": "2026-04-22T08:30:00",
                "visitReason": "Đau đầu, sổ mũi",
                "symptoms": "Đau đầu nhẹ, chảy nước mũi 2 ngày",
                "clinicalDiagnosis": "Viêm mũi họng cấp",
                "icd10Code": "J06.9",
                "treatmentPlan": "Nghỉ ngơi, uống nhiều nước",
                "department": "Khoa Nội",
                "prescriptions": [
                    {
                        "drugName": "Paracetamol 500mg",
                        "dosage": "500mg",
                        "unit": "viên",
                        "frequency": "3 lần/ngày",
                        "durationDays": 5,
                        "quantity": 15,
                        "morningDose": 1,
                        "noonDose": 1,
                        "eveningDose": 1,
                        "instructions": "Uống sau ăn"
                    },
                    {
                        "drugName": "Cetirizine 10mg",
                        "dosage": "10mg",
                        "unit": "viên",
                        "frequency": "1 lần/ngày",
                        "durationDays": 7,
                        "quantity": 7,
                        "eveningDose": 1,
                        "instructions": "Uống trước khi ngủ"
                    }
                ],
                "vitalSigns": [
                    {
                        "patientId": "%s",
                        "temperature": 37.5,
                        "bloodPressureSystolic": 120,
                        "bloodPressureDiastolic": 80,
                        "heartRate": 78,
                        "spo2": 98,
                        "measuredAt": "2026-04-22T08:25:00"
                    }
                ]
            }
            """, patientId, patientId);

        MvcResult result = mockMvc.perform(post("/api/v1/ehr/medical-records")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recordJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.recordNumber").isNotEmpty())
                .andExpect(jsonPath("$.data.clinicalDiagnosis").value("Viêm mũi họng cấp"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        medicalRecordId = json.get("data").get("id").asText();
    }

    @Test
    @Order(21)
    @DisplayName("3.2 Lấy chi tiết hồ sơ bệnh án")
    void getMedicalRecordById() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/medical-records/" + medicalRecordId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.icd10Code").value("J06.9"));
    }

    @Test
    @Order(22)
    @DisplayName("3.3 Lịch sử khám bệnh theo bệnh nhân")
    void getMedicalRecordsByPatient() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/medical-records/patient/" + patientId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(23)
    @DisplayName("3.4 Cập nhật hồ sơ bệnh án")
    void updateMedicalRecord() throws Exception {
        String updateJson = String.format("""
            {
                "patientId": "%s",
                "visitDate": "2026-04-22T08:30:00",
                "visitReason": "Đau đầu, sổ mũi (cập nhật)",
                "symptoms": "Đau đầu nhẹ, chảy nước mũi 2 ngày, sốt nhẹ",
                "clinicalDiagnosis": "Viêm mũi họng cấp (theo dõi)",
                "icd10Code": "J06.9",
                "treatmentPlan": "Nghỉ ngơi, uống nhiều nước, tái khám sau 3 ngày",
                "notes": "Bệnh nhân cần theo dõi thêm"
            }
            """, patientId);

        mockMvc.perform(put("/api/v1/ehr/medical-records/" + medicalRecordId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.notes").value("Bệnh nhân cần theo dõi thêm"));
    }

    // ═══════════════════════════════════════════════════════════
    // 4. PRESCRIPTION QUERIES
    // ═══════════════════════════════════════════════════════════

    @Test
    @Order(30)
    @DisplayName("4.1 Lấy đơn thuốc theo hồ sơ bệnh án")
    void getPrescriptionsByMedicalRecord() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/prescriptions/medical-record/" + medicalRecordId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].drugName").isNotEmpty());
    }

    // ═══════════════════════════════════════════════════════════
    // 5. VITAL SIGNS
    // ═══════════════════════════════════════════════════════════

    @Test
    @Order(40)
    @DisplayName("5.1 Ghi nhận dấu hiệu sinh tồn mới (standalone)")
    void recordVitalSigns() throws Exception {
        String vsJson = String.format("""
            {
                "patientId": "%s",
                "medicalRecordId": "%s",
                "temperature": 36.8,
                "bloodPressureSystolic": 118,
                "bloodPressureDiastolic": 75,
                "heartRate": 72,
                "spo2": 99,
                "weight": 65.5,
                "height": 170.0,
                "measuredAt": "2026-04-22T14:00:00",
                "source": "MANUAL"
            }
            """, patientId, medicalRecordId);

        mockMvc.perform(post("/api/v1/ehr/vital-signs")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.heartRate").value(72));
    }

    @Test
    @Order(41)
    @DisplayName("5.2 Lịch sử sinh tồn theo bệnh nhân")
    void getVitalSignsByPatient() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/vital-signs/patient/" + patientId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @Order(42)
    @DisplayName("5.3 Sinh tồn theo hồ sơ bệnh án")
    void getVitalSignsByMedicalRecord() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/vital-signs/medical-record/" + medicalRecordId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    // ═══════════════════════════════════════════════════════════
    // 6. VALIDATION & ERROR HANDLING
    // ═══════════════════════════════════════════════════════════

    @Test
    @Order(50)
    @DisplayName("6.1 Tạo bệnh nhân thiếu fullName → 400")
    void createPatientMissingName() throws Exception {
        String invalidJson = """
            { "phone": "0901234567", "gender": "MALE" }
            """;

        mockMvc.perform(post("/api/v1/ehr/patients")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(51)
    @DisplayName("6.2 Lấy bệnh nhân không tồn tại → 404")
    void getPatientNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/ehr/patients/" + UUID.randomUUID())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    // ═══════════════════════════════════════════════════════════
    // 7. SOFT DELETE
    // ═══════════════════════════════════════════════════════════

    @Test
    @Order(90)
    @DisplayName("7.1 Soft delete bệnh nhân")
    void softDeletePatient() throws Exception {
        mockMvc.perform(delete("/api/v1/ehr/patients/" + patientId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ═══════════════════════════════════════════════════════════
    // 8. ACTUATOR HEALTH CHECK
    // ═══════════════════════════════════════════════════════════

    @Test
    @Order(99)
    @DisplayName("8.1 Health check endpoint")
    void healthCheck() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}
