-- ============================================================
-- V6: Tạo bảng vital_signs (Dấu hiệu sinh tồn - IoT ready)
-- ============================================================

CREATE TABLE IF NOT EXISTS vital_signs (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id                UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    patient_id               UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    medical_record_id        UUID REFERENCES medical_records(id),

    -- Các chỉ số sinh tồn cơ bản
    temperature              DECIMAL(4,1),              -- Nhiệt độ (°C)
    blood_pressure_systolic  INT,                       -- Huyết áp tâm thu (mmHg)
    blood_pressure_diastolic INT,                       -- Huyết áp tâm trương (mmHg)
    heart_rate               INT,                       -- Nhịp tim (lần/phút)
    respiratory_rate         INT,                       -- Nhịp thở (lần/phút)
    spo2                     INT,                       -- SpO2 độ bão hòa oxy (%)
    weight                   DECIMAL(5,2),              -- Cân nặng (kg)
    height                   DECIMAL(5,1),              -- Chiều cao (cm)
    bmi                      DECIMAL(4,2),              -- BMI (tính toán tự động)
    blood_glucose            DECIMAL(5,1),              -- Đường huyết (mmol/L)

    -- Nguồn đo (thủ công hoặc IoT)
    source                   VARCHAR(20) DEFAULT 'MANUAL',   -- MANUAL | IOT | DEVICE
    device_id                VARCHAR(100),
    device_type              VARCHAR(100),
    measured_at              TIMESTAMP NOT NULL,
    recorded_by_id           UUID REFERENCES users(id),

    created_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vital_signs_tenant_id         ON vital_signs(tenant_id);
CREATE INDEX idx_vital_signs_patient_id        ON vital_signs(patient_id);
CREATE INDEX idx_vital_signs_medical_record_id ON vital_signs(medical_record_id);
CREATE INDEX idx_vital_signs_measured_at       ON vital_signs(measured_at);
