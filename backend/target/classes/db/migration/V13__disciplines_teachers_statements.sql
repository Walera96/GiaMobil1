-- ============================================================
-- Миграция V13: Дисциплины, преподаватели, ведомости, расширение grade
-- ============================================================

-- 1. Таблица дисциплин
CREATE TABLE IF NOT EXISTS discipline (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) UNIQUE,
    name VARCHAR(200) NOT NULL,
    hours INTEGER,
    ects_credits INTEGER,
    course INTEGER CHECK (course BETWEEN 1 AND 6),
    semester VARCHAR(20),
    control_type VARCHAR(20) CHECK (control_type IN ('EXAM','TEST')),
    direction_id UUID REFERENCES direction(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_discipline_direction ON discipline(direction_id);

-- 2. Таблица преподавателей
CREATE TABLE IF NOT EXISTS teacher (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    department VARCHAR(200),
    position VARCHAR(100),
    degree VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_teacher_department ON teacher(department);

-- 3. Расширение таблицы grade
ALTER TABLE grade
    ADD COLUMN IF NOT EXISTS discipline_id UUID REFERENCES discipline(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS current_control INTEGER CHECK (current_control BETWEEN 0 AND 70),
    ADD COLUMN IF NOT EXISTS attendance INTEGER CHECK (attendance BETWEEN 0 AND 10),
    ADD COLUMN IF NOT EXISTS activity INTEGER CHECK (activity BETWEEN 0 AND 20),
    ADD COLUMN IF NOT EXISTS exam_score INTEGER CHECK (exam_score BETWEEN 0 AND 30),
    ADD COLUMN IF NOT EXISTS total_score INTEGER,
    ADD COLUMN IF NOT EXISTS ects_grade VARCHAR(2),
    ADD COLUMN IF NOT EXISTS five_point_grade INTEGER CHECK (five_point_grade BETWEEN 2 AND 5);

CREATE INDEX IF NOT EXISTS idx_grade_discipline ON grade(discipline_id);

-- 4. Таблица ведомостей
CREATE TABLE IF NOT EXISTS statement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    statement_number VARCHAR(50),
    academic_year VARCHAR(20),
    semester VARCHAR(20),
    group_id UUID NOT NULL REFERENCES study_group(id) ON DELETE RESTRICT,
    discipline_id UUID REFERENCES discipline(id) ON DELETE SET NULL,
    teacher_id UUID REFERENCES teacher(id) ON DELETE SET NULL,
    status VARCHAR(20) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','PENDING','APPROVED','PRINTED')),
    created_by UUID REFERENCES app_user(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_statement_group ON statement(group_id);
CREATE INDEX IF NOT EXISTS idx_statement_status ON statement(status);

-- 5. Таблица записей ведомости
CREATE TABLE IF NOT EXISTS statement_record (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    statement_id UUID NOT NULL REFERENCES statement(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES student(id) ON DELETE RESTRICT,
    current_control INTEGER CHECK (current_control BETWEEN 0 AND 70),
    attendance INTEGER CHECK (attendance BETWEEN 0 AND 10),
    activity INTEGER CHECK (activity BETWEEN 0 AND 20),
    exam_score INTEGER CHECK (exam_score BETWEEN 0 AND 30),
    total_score INTEGER,
    ects_grade VARCHAR(2),
    five_point_grade INTEGER CHECK (five_point_grade BETWEEN 2 AND 5),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(statement_id, student_id)
);

CREATE INDEX IF NOT EXISTS idx_statement_record_statement ON statement_record(statement_id);
