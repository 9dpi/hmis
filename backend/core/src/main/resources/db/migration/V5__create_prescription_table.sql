-- ============================================================
-- V5: Tạo bảng prescriptions (Đơn thuốc)
-- ============================================================

CREATE TABLE IF NOT EXISTS prescriptions (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    medical_record_id UUID NOT NULL REFERENCES medical_records(id) ON DELETE CASCADE,

    -- Thông tin thuốc
    drug_name         VARCHAR(255) NOT NULL,
    drug_code         VARCHAR(50),               -- Mã thuốc (theo danh mục BYT)
    active_ingredient VARCHAR(255),              -- Hoạt chất
    dosage            VARCHAR(100),              -- Liều lượng (VD: 500mg)
    unit              VARCHAR(50),               -- Đơn vị (viên, ống, chai, gói)
    frequency         VARCHAR(100),              -- Tần suất (3 lần/ngày)
    route             VARCHAR(50),               -- Đường dùng (uống, tiêm, bôi)
    duration_days     INT,                       -- Số ngày dùng
    quantity          INT,                       -- Số lượng cấp
    morning_dose      DECIMAL(5,2),              -- Sáng
    noon_dose         DECIMAL(5,2),              -- Trưa
    evening_dose      DECIMAL(5,2),              -- Tối
    instructions      TEXT,                      -- Hướng dẫn sử dụng

    status            VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    prescribed_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prescriptions_tenant_id          ON prescriptions(tenant_id);
CREATE INDEX idx_prescriptions_medical_record_id  ON prescriptions(medical_record_id);
CREATE INDEX idx_prescriptions_drug_code          ON prescriptions(drug_code);
