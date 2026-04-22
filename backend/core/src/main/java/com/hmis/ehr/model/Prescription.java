package com.hmis.ehr.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "medical_record_id", nullable = false)
    private UUID medicalRecordId;

    @Column(name = "drug_name", nullable = false)
    private String drugName;

    @Column(name = "drug_code", length = 50)
    private String drugCode;

    @Column(name = "active_ingredient")
    private String activeIngredient;

    @Column(name = "dosage", length = 100)
    private String dosage;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "frequency", length = 100)
    private String frequency;

    @Column(name = "route", length = 50)
    private String route;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "morning_dose", precision = 5, scale = 2)
    private BigDecimal morningDose;

    @Column(name = "noon_dose", precision = 5, scale = 2)
    private BigDecimal noonDose;

    @Column(name = "evening_dose", precision = 5, scale = 2)
    private BigDecimal eveningDose;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "prescribed_at", nullable = false)
    private LocalDateTime prescribedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
