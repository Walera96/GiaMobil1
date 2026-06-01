-- Миграция V31: Система заданий (Assignments) для преподавателей и студентов
-- Создаётся: типы заданий, статусы сдачи, таблицы assignments и assignment_submissions

-- Типы заданий
CREATE TYPE assignment_type AS ENUM (
    'VKR', 'COURSEWORK', 'LAB', 'PRACTICE', 'EXAM', 'HOMEWORK'
);

-- Статусы сдачи
CREATE TYPE submission_status AS ENUM (
    'DRAFT', 'SUBMITTED', 'REVIEWING', 'REVIEWED', 'RETURNED'
);

-- Таблица групп (если ещё не существует)
CREATE TABLE IF NOT EXISTS groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50),
    department_id UUID,
    curator_id UUID REFERENCES app_user(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Таблица заданий (преподаватель создаёт)
CREATE TABLE assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    assignment_type assignment_type NOT NULL,
    
    -- Кто создал
    created_by UUID NOT NULL REFERENCES app_user(id),
    
    -- Назначение: группе или индивидуально
    target_group_id UUID REFERENCES groups(id) ON DELETE SET NULL,
    target_student_ids UUID[],
    
    -- Сроки
    deadline TIMESTAMP WITH TIME ZONE,
    allow_late_submission BOOLEAN DEFAULT FALSE,
    
    -- Оценка (гибкая настройка)
    max_score INTEGER DEFAULT 100,
    scoring_config JSONB DEFAULT '{
        "type": "default",
        "criteria": [],
        "auto_check": false,
        "allow_partial_score": true
    }',
    
    -- Файлы задания
    attached_files JSONB DEFAULT '[]',
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Таблица сдач (студент загружает решение)
CREATE TABLE assignment_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    assignment_id UUID NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    
    -- Файлы решения
    solution_files JSONB NOT NULL DEFAULT '[]',
    
    -- Комментарий студента
    student_comment TEXT,
    
    -- Статус и оценка
    status submission_status DEFAULT 'DRAFT',
    submitted_at TIMESTAMP WITH TIME ZONE,
    
    -- Гибкая оценка (JSONB)
    score JSONB,
    total_score NUMERIC(5,2),
    
    -- Обратная связь от преподавателя
    teacher_feedback TEXT,
    teacher_comment TEXT,
    reviewed_by UUID REFERENCES app_user(id) ON DELETE SET NULL,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    
    -- Версионность
    version INTEGER DEFAULT 1,
    previous_version_id UUID REFERENCES assignment_submissions(id) ON DELETE SET NULL,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Один студент может иметь несколько версий сдачи по одному заданию
    UNIQUE(assignment_id, student_id, version)
);

-- Индексы для производительности
CREATE INDEX idx_assignments_created_by ON assignments(created_by);
CREATE INDEX idx_assignments_target_group ON assignments(target_group_id);
CREATE INDEX idx_assignments_deadline ON assignments(deadline);
CREATE INDEX idx_assignments_type ON assignments(assignment_type);
CREATE INDEX idx_assignments_created_at ON assignments(created_at DESC);

CREATE INDEX idx_submissions_assignment ON assignment_submissions(assignment_id);
CREATE INDEX idx_submissions_student ON assignment_submissions(student_id);
CREATE INDEX idx_submissions_status ON assignment_submissions(status);
CREATE INDEX idx_submissions_reviewed_by ON assignment_submissions(reviewed_by);
CREATE INDEX idx_submissions_submitted_at ON assignment_submissions(submitted_at DESC);

-- Триггер для обновления updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_assignments_updated_at
    BEFORE UPDATE ON assignments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_assignment_submissions_updated_at
    BEFORE UPDATE ON assignment_submissions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Комментарии
COMMENT ON TABLE assignments IS 'Задания, создаваемые преподавателями';
COMMENT ON TABLE assignment_submissions IS 'Сдачи заданий студентами';
COMMENT ON COLUMN assignments.scoring_config IS 'Гибкая конфигурация оценки (критерии, веса, тип оценки)';
COMMENT ON COLUMN assignment_submissions.score IS 'Детальная оценка по критериям (JSONB)';
