-- ============================================================
-- V4: Tạo bảng medical_records (Hồ sơ bệnh án điện tử - EHR)
-- ============================================================

CREATE TABLE IF NOT EXISTS medical_records (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id            UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    patient_id           UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    record_number        VARCHAR(50) NOT NULL,         -- Mã hồ sơ (theo quy định BYT)

    -- Thông tin lâm sàng
    visit_date           TIMESTAMP NOT NULL,
    visit_reason         TEXT,                          -- Lý do khám
    symptoms             TEXT,                          -- Triệu chứng
    clinical_diagnosis   TEXT,                          -- Chẩn đoán lâm sàng
    icd10_code           VARCHAR(10),                   -- Mã ICD-10
    subclinical_results  TEXT,                          -- Kết quả cận lâm sàng

    -- Điều trị
    treatment_plan       TEXT,                          -- Phác đồ điều trị
    follow_up_date       DATE,                          -- Ngày tái khám
    notes                TEXT,                          -- Ghi chú

    -- Bác sĩ & khoa
    doctor_id            UUID REFERENCES users(id),
    department           VARCHAR(100),

    -- Metadata
    status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    -- ACTIVE | COMPLETED | CANCELLED
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(tenant_id, record_number)
);

CREATE INDEX idx_medical_records_tenant_id   ON medical_records(tenant_id);
CREATE INDEX idx_medical_records_patient_id  ON medical_records(patient_id);
CREATE INDEX idx_medical_records_visit_date  ON medical_records(visit_date);
CREATE INDEX idx_medical_records_doctor_id   ON medical_records(doctor_id);
CREATE INDEX idx_medical_records_icd10_code  ON medical_records(icd10_code);
