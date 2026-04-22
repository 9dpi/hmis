-- ============================================================
-- V1: Tạo bảng tenants (Multi-tenant)
-- Mỗi tenant là 1 cơ sở y tế (Trạm Y tế, Phòng khám...)
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS tenants (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(50)  NOT NULL UNIQUE,    -- Mã cơ sở (VD: TRAM_YTE_Q1)
    name        VARCHAR(255) NOT NULL,            -- Tên cơ sở y tế
    address     TEXT,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    license_number VARCHAR(100),                  -- Số giấy phép hoạt động
    is_active   BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed: Tenant mặc định
INSERT INTO tenants (code, name, address, phone, email)
VALUES (
    'TRAM_YTE_MAU',
    'Trạm Y tế Mẫu',
    '123 Đường ABC, Phường XYZ, TP. HCM',
    '028-1234-5678',
    'tramyte@hmis.local'
) ON CONFLICT (code) DO NOTHING;
