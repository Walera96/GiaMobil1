-- Обновление V32: семестры и накопительная система для заданий

-- Добавляем поле semester в assignments
ALTER TABLE assignments ADD COLUMN IF NOT EXISTS semester INTEGER DEFAULT 1;

-- Добавляем поле previous_semester_score для накопительной системы
ALTER TABLE assignment_submissions ADD COLUMN IF NOT EXISTS previous_semester_score NUMERIC(5,2);

-- Таблица для истории пересдач (если allowRetake = true)
CREATE TABLE IF NOT EXISTS assignment_retakes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    original_submission_id UUID NOT NULL REFERENCES assignment_submissions(id),
    new_submission_id UUID NOT NULL REFERENCES assignment_submissions(id),
    retake_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Индекс для быстрого поиска пересдач по студенту
CREATE INDEX IF NOT EXISTS idx_assignment_retakes_original ON assignment_retakes(original_submission_id);
CREATE INDEX IF NOT EXISTS idx_assignment_retakes_new ON assignment_retakes(new_submission_id);

-- Индекс для поиска заданий по семестру
CREATE INDEX IF NOT EXISTS idx_assignments_semester ON assignments(semester);

-- Комментарии
COMMENT ON COLUMN assignments.semester IS 'Семестр (1, 2)';
COMMENT ON COLUMN assignment_submissions.previous_semester_score IS 'Баллы с предыдущего семестра (накопительная система)';
