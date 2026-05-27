-- ============================================================
-- Миграция V9__student_thesis_vote_comment.sql
-- Поля для файла ВКР, комментариев голосования, общего среднего балла
-- ============================================================

-- Файл ВКР студента
ALTER TABLE student ADD COLUMN IF NOT EXISTS thesis_file_path VARCHAR(500);
ALTER TABLE student ADD COLUMN IF NOT EXISTS thesis_file_name VARCHAR(300);

-- Комментарий к голосу
ALTER TABLE vote ADD COLUMN IF NOT EXISTS comment VARCHAR(1000);

-- Общий средний балл по всем членам ГЭК
ALTER TABLE agenda_item ADD COLUMN IF NOT EXISTS overall_average_score NUMERIC(4,2);
