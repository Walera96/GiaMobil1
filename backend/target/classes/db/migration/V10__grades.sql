-- ============================================================
-- Миграция V10__grades.sql
-- Таблица оценок студентов по предметам + демо-данные
-- ============================================================

CREATE TABLE IF NOT EXISTS grade (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES student(id),
    subject_name VARCHAR(200) NOT NULL,
    score INTEGER NOT NULL,
    semester VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_grade_student_id ON grade(student_id);

-- Демо-оценки для студентов группы ОУИТб-ПИ01-22-4
INSERT INTO grade (id, student_id, subject_name, score, semester)
SELECT
    gen_random_uuid(),
    s.id,
    subj.name,
    floor(random() * 3 + 3)::int, -- случайная оценка 3-5
    '2025-2'
FROM student s
CROSS JOIN (VALUES
    ('Математический анализ'),
    ('Программирование на Java'),
    ('Базы данных'),
    ('Веб-технологии'),
    ('Экономика предприятия'),
    ('Управление проектами'),
    ('Информационная безопасность'),
    ('Теория вероятностей')
) AS subj(name)
WHERE s.group_id = (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4')
ON CONFLICT DO NOTHING;
