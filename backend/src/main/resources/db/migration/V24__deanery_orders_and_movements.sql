-- ============================================================
-- Миграция V24: Приказы деканата и движение контингента
-- ============================================================

CREATE TABLE deanery_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(100) NOT NULL UNIQUE,
    order_date DATE NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    title VARCHAR(500) NOT NULL,
    content TEXT,
    file_path VARCHAR(500),
    created_by UUID REFERENCES app_user(id),
    approved_by UUID REFERENCES app_user(id),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deanery_order_type ON deanery_order(type);
CREATE INDEX idx_deanery_order_status ON deanery_order(status);
CREATE INDEX idx_deanery_order_date ON deanery_order(order_date);

CREATE TABLE contingent_movement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES student(id),
    movement_type VARCHAR(30) NOT NULL,
    movement_date DATE NOT NULL,
    reason VARCHAR(500),
    order_id UUID REFERENCES deanery_order(id),
    semester VARCHAR(20),
    academic_year VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contingent_movement_student ON contingent_movement(student_id);
CREATE INDEX idx_contingent_movement_type ON contingent_movement(movement_type);
CREATE INDEX idx_contingent_movement_order ON contingent_movement(order_id);

-- Демо-данные
INSERT INTO deanery_order (id, order_number, order_date, type, status, title, content) VALUES
('d0000001-0000-0000-0000-000000000001', '01-ГИА/2026', '2026-05-20', 'GEK_APPOINTMENT', 'SIGNED', 'О назначении ГЭК для проведения ГИА', 'Назначить государственную экзаменационную комиссию...'),
('d0000001-0000-0000-0000-000000000002', '02-ГИА/2026', '2026-05-25', 'ADMISSION_APPROVAL', 'APPROVED', 'О допуске обучающихся к ГИА', 'Допустить к государственной итоговой аттестации...'),
('d0000001-0000-0000-0000-000000000003', '03-ГИА/2026', '2026-06-01', 'RESULTS', 'DRAFT', 'О результатах ГИА', 'Присудить квалификацию бакалавр...');

INSERT INTO contingent_movement (id, student_id, movement_type, movement_date, reason, order_id, academic_year) VALUES
('a0000001-0000-0000-0000-000000000001', 'f10b0fb3-e1f5-46df-8c46-90ae8afc699d', 'ADMITTED_GIA', '2026-05-25', 'Нет задолженностей, БРС >= 60', 'd0000001-0000-0000-0000-000000000002', '2025/2026'),
('a0000001-0000-0000-0000-000000000002', '1bc8096e-43ea-4d84-9ea4-205e6c0c3207', 'ADMITTED_GIA', '2026-05-25', 'Нет задолженностей, БРС >= 60', 'd0000001-0000-0000-0000-000000000002', '2025/2026');
