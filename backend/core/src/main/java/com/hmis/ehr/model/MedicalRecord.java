package com.hmis.ehr.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "record_number", nullable = false, length = 50)
    private String recordNumber;

    // ── Thông tin lâm sàng ─────────────────────────────────────

    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "visit_reason", columnDefinition = "TEXT")
    private String visitReason;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "clinical_diagnosis", columnDefinition = "TEXT")
    private String clinicalDiagnosis;

    @Column(name = "icd10_code", length = 10)
    private String icd10Code;

    @Column(name = "subclinical_results", columnDefinition = "TEXT")
    private String subclinicalResults;

    // ── Điều trị ─────────────────────────────────────────────────

    @Column(name = "treatment_plan", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ── Bác sĩ & khoa ────────────────────────────────────────────

    @Column(name = "doctor_id")
    private UUID doctorId;

    @Column(name = "department", length = 100)
    private String department;

    // ── Liên kết đơn thuốc & sinh tồn ───────────────────────────

    @OneToMany(mappedBy = "medicalRecordId", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<Prescription> prescriptions;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_record_id")
    private List<VitalSigns> vitalSigns;

    // ── Metadata ─────────────────────────────────────────────────

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
