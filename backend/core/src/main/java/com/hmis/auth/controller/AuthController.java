package com.hmis.auth.controller;

import com.hmis.auth.dto.LoginRequest;
import com.hmis.auth.dto.LoginResponse;
import com.hmis.auth.jwt.JwtUtil;
import com.hmis.auth.model.User;
import com.hmis.auth.repository.UserRepository;
import com.hmis.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Đăng nhập / Xác thực JWT")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository        userRepository;
    private final JwtUtil               jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập – nhận JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        User user = (User) auth.getPrincipal();

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getTenantId(),
                user.getRole().name());

        // Cập nhật last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .tenantId(user.getTenantId().toString())
                .build();

        log.info("User {} (role={}) logged in successfully", user.getUsername(), user.getRole());
        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công", response));
    }
}
