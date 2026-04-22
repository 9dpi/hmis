-- ============================================================
-- V3: Tạo bảng patients (Hồ sơ bệnh nhân)
-- ============================================================

CREATE TABLE IF NOT EXISTS patients (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    code             VARCHAR(50) NOT NULL,          -- Mã bệnh nhân (MBN2024001)
    full_name        VARCHAR(255) NOT NULL,
    dob              DATE,                           -- Ngày sinh
    gender           VARCHAR(10),                    -- MALE | FEMALE | OTHER
    phone            VARCHAR(20),
    email            VARCHAR(100),
    address          TEXT,
    identity_number  VARCHAR(20),                    -- CCCD / CMND
    insurance_number VARCHAR(20),                    -- Số BHYT
    insurance_expiry DATE,                           -- Ngày hết hạn BHYT
    blood_type       VARCHAR(5),                     -- A, B, AB, O + Rh
    allergies        TEXT,                           -- Tiền sử dị ứng
    chronic_diseases TEXT,                           -- Bệnh mãn tính
    emergency_contact_name  VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(tenant_id, code)
);

CREATE INDEX idx_patients_tenant_id        ON patients(tenant_id);
CREATE INDEX idx_patients_full_name        ON patients(full_name);
CREATE INDEX idx_patients_identity_number  ON patients(identity_number);
CREATE INDEX idx_patients_insurance_number ON patients(insurance_number);
CREATE INDEX idx_patients_phone            ON patients(phone);
