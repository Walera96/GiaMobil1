-- ============================================================
-- Миграция V26: Расширенный профиль студента
-- ============================================================

ALTER TABLE student ADD COLUMN IF NOT EXISTS phone VARCHAR(50);
ALTER TABLE student ADD COLUMN IF NOT EXISTS about_me TEXT;
ALTER TABLE student ADD COLUMN IF NOT EXISTS photo_url VARCHAR(500);

-- Индекс для поиска по телефону (опционально)
CREATE INDEX IF NOT EXISTS idx_student_phone ON student(phone);
