-- ============================================================
-- Миграция V25: Учебные планы
-- ============================================================

CREATE TABLE study_plan (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    direction_id UUID NOT NULL REFERENCES direction(id),
    profile VARCHAR(200),
    academic_year VARCHAR(20),
    form_of_study VARCHAR(20),
    qualification VARCHAR(50),
    total_hours INTEGER,
    total_credits INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_study_plan_direction ON study_plan(direction_id);
CREATE INDEX idx_study_plan_status ON study_plan(status);

CREATE TABLE study_plan_discipline (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    study_plan_id UUID NOT NULL REFERENCES study_plan(id) ON DELETE CASCADE,
    discipline_id UUID NOT NULL REFERENCES discipline(id),
    semester INTEGER NOT NULL,
    course INTEGER NOT NULL,
    hours INTEGER NOT NULL,
    credits INTEGER NOT NULL,
    control_type VARCHAR(20),
    is_mandatory BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_spd_study_plan ON study_plan_discipline(study_plan_id);
CREATE INDEX idx_spd_discipline ON study_plan_discipline(discipline_id);

-- Демо-данные
INSERT INTO study_plan (id, name, direction_id, profile, academic_year, form_of_study, qualification, total_hours, total_credits, status) VALUES
('a0000001-0000-0000-0000-000000000001', 'UP 09.03.01 Informatika 2022', '8522eabe-9f07-468b-bf34-f55e4db50ba7', 'Programmnaya inzheneriya', '2022/2023', 'OCHNAYA', 'BAKALAVR', 8000, 240, 'ACTIVE');

-- Получим ID первой дисциплины для демо (если есть)
-- INSERT INTO study_plan_discipline (id, study_plan_id, discipline_id, semester, course, hours, credits, control_type, is_mandatory)
-- SELECT gen_random_uuid(), 's0000001-0000-0000-0000-000000000001', id, 1, 1, 72, 2, 'EXAM', TRUE FROM discipline LIMIT 1;
