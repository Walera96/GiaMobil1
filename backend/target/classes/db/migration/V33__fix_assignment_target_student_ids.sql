-- Изменяем тип target_student_ids с uuid[] на jsonb для совместимости с JPA entity
ALTER TABLE assignments ALTER COLUMN target_student_ids TYPE jsonb USING to_jsonb(target_student_ids);
