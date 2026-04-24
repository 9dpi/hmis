package com.hmis.auth;

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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit-level integration tests cho AuthController.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private TenantRepository tenantRepository;

    private static final UUID TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000099");

    @BeforeAll
    void setup() {
        if (tenantRepository != null) {
            try {
                tenantRepository.save(Tenant.builder()
                        .id(TENANT_ID).code("AUTH_TEST").name("Auth Test").isActive(true).build());
            } catch (Exception ignored) {}
        }
        if (userRepository.findByUsername("authtest").isEmpty()) {
            userRepository.save(User.builder()
                    .tenantId(TENANT_ID)
                    .username("authtest")
                    .passwordHash(passwordEncoder.encode("Test@123"))
                    .fullName("Auth Test User")
                    .role(UserRole.DOCTOR)
                    .isActive(true)
                    .build());
        }
    }

    @Test
    @DisplayName("Login thành công với user DOCTOR")
    void loginSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username": "authtest", "password": "Test@123"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("DOCTOR"));
    }

    @Test
    @DisplayName("Login thất bại – username không tồn tại")
    void loginBadUsername() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"username": "nonexistent", "password": "Test@123"}
                            """))
                .andExpect(status().is4xxClientError().or(status().is5xxServerError()));
    }

    @Test
    @DisplayName("Login thất bại – thiếu field username")
    void loginMissingFields() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"password": "Test@123"}
                            """))
                .andExpect(status().isBadRequest());
    }
}
