-- ============================================================
-- V2: Tạo bảng users (Bác sĩ, Y tá, Admin...)
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    username        VARCHAR(100) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(255) NOT NULL,
    email           VARCHAR(100),
    phone           VARCHAR(20),
    role            VARCHAR(30) NOT NULL DEFAULT 'STAFF',
    -- ADMIN | DOCTOR | NURSE | PHARMACIST | STAFF | READONLY
    department      VARCHAR(100),
    license_number  VARCHAR(100),               -- Số chứng chỉ hành nghề
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at   TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(tenant_id, username)
);

CREATE INDEX idx_users_tenant_id   ON users(tenant_id);
CREATE INDEX idx_users_role        ON users(role);

-- Seed: Admin mặc định (password: Admin@123 - bcrypt)
INSERT INTO users (tenant_id, username, password_hash, full_name, email, role)
SELECT
    t.id,
    'admin',
    '$2a$12$TlMd/TOcJHQ4RWkf9WdcMuFPTvvAiFAfQMKQFEY1RyV2JxcBN02Fy',
    'Quản trị viên',
    'admin@hmis.local',
    'ADMIN'
FROM tenants t WHERE t.code = 'TRAM_YTE_MAU'
ON CONFLICT DO NOTHING;
