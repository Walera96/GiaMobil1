-- ============================================================
-- Миграция V32__assignments.sql
-- Система заданий для преподавателей и студентов
-- ============================================================

-- Типы заданий
CREATE TYPE assignment_type AS ENUM ('VKR', 'COURSEWORK', 'LAB', 'PRACTICE', 'EXAM', 'HOMEWORK');

-- Статусы сдачи
CREATE TYPE submission_status AS ENUM ('DRAFT', 'SUBMITTED', 'REVIEWING', 'REVIEWED', 'RETURNED');

-- Таблица групп (для заданий)
CREATE TABLE IF NOT EXISTS groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50),
    department_id UUID,
    curator_id UUID REFERENCES app_user(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Таблица заданий
CREATE TABLE assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(500) NOT NULL,
    description TEXT,
    assignment_type assignment_type NOT NULL,
    created_by UUID NOT NULL REFERENCES app_user(id),
    target_group_id UUID REFERENCES groups(id),
    target_student_ids UUID[],
    deadline TIMESTAMP WITH TIME ZONE,
    allow_late_submission BOOLEAN NOT NULL DEFAULT false,
    max_score INTEGER,
    scoring_config JSONB,
    attached_files JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Таблица сдач заданий
CREATE TABLE assignment_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id UUID NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES app_user(id),
    solution_files JSONB,
    student_comment TEXT,
    status submission_status NOT NULL DEFAULT 'DRAFT',
    submitted_at TIMESTAMP WITH TIME ZONE,
    score JSONB,
    total_score INTEGER,
    teacher_feedback TEXT,
    teacher_comment TEXT,
    reviewed_by UUID REFERENCES app_user(id),
    reviewed_at TIMESTAMP WITH TIME ZONE,
    version INTEGER NOT NULL DEFAULT 1,
    previous_version_id UUID REFERENCES assignment_submissions(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Индексы
CREATE INDEX idx_assignments_created_by ON assignments(created_by);
CREATE INDEX idx_assignments_target_group ON assignments(target_group_id);
CREATE INDEX idx_assignments_type ON assignments(assignment_type);
CREATE INDEX idx_assignments_deadline ON assignments(deadline);

CREATE INDEX idx_submissions_assignment ON assignment_submissions(assignment_id);
CREATE INDEX idx_submissions_student ON assignment_submissions(student_id);
CREATE INDEX idx_submissions_status ON assignment_submissions(status);
CREATE INDEX idx_submissions_version ON assignment_submissions(assignment_id, student_id, version);
