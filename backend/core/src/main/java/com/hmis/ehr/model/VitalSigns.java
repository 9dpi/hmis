package com.hmis.ehr.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vital_signs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VitalSigns {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "medical_record_id")
    private UUID medicalRecordId;

    // ── Chỉ số sinh tồn ────────────────────────────────────────

    @Column(name = "temperature", precision = 4, scale = 1)
    private BigDecimal temperature;        // °C

    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic; // mmHg

    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic; // mmHg

    @Column(name = "heart_rate")
    private Integer heartRate;             // lần/phút

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;       // lần/phút

    @Column(name = "spo2")
    private Integer spo2;                  // %

    @Column(name = "weight", precision = 5, scale = 2)
    private BigDecimal weight;             // kg

    @Column(name = "height", precision = 5, scale = 1)
    private BigDecimal height;             // cm

    @Column(name = "bmi", precision = 4, scale = 2)
    private BigDecimal bmi;                // tính toán tự động

    @Column(name = "blood_glucose", precision = 5, scale = 1)
    private BigDecimal bloodGlucose;       // mmol/L

    // ── Nguồn đo ─────────────────────────────────────────────

    @Column(name = "source", length = 20)
    private String source = "MANUAL";     // MANUAL | IOT | DEVICE

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "device_type", length = 100)
    private String deviceType;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "recorded_by_id")
    private UUID recordedById;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
