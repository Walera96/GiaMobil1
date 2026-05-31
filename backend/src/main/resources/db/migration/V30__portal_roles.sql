-- Миграция: множественные роли и порталы
-- Создано: 2026-05-28

-- 1. Создать таблицу для множественных ролей
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- 2. Перенести существующие роли (одна роль → одна запись)
INSERT INTO user_roles (user_id, role)
SELECT id, role::text FROM app_user
ON CONFLICT DO NOTHING;

-- 3. Добавить новые колонки
ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS department_id UUID,
    ADD COLUMN IF NOT EXISTS study_group_id UUID,
    ADD COLUMN IF NOT EXISTS primary_portal VARCHAR(30);

-- 4. Переименовать legacy роли в новые
UPDATE user_roles SET role = 'GEK_SECRETARY' WHERE role = 'SECRETARY';
UPDATE user_roles SET role = 'GEK_CHAIRMAN'   WHERE role = 'CHAIRMAN';
UPDATE user_roles SET role = 'SYSTEM_ADMIN'   WHERE role = 'ADMIN';

-- 5. Установить primary_portal на основе ролей
UPDATE app_user SET primary_portal = 'admin'
WHERE id IN (SELECT user_id FROM user_roles WHERE role IN ('SYSTEM_ADMIN', 'UNIVERSITY_ADMIN'));

UPDATE app_user SET primary_portal = 'deanery'
WHERE id IN (SELECT user_id FROM user_roles WHERE role IN ('DEAN', 'DEAN_SECRETARY'))
  AND primary_portal IS NULL;

UPDATE app_user SET primary_portal = 'gek'
WHERE id IN (SELECT user_id FROM user_roles WHERE role IN ('GEK_SECRETARY', 'GEK_CHAIRMAN', 'GEK_MEMBER'))
  AND primary_portal IS NULL;

UPDATE app_user SET primary_portal = 'department'
WHERE id IN (SELECT user_id FROM user_roles WHERE role IN ('DEPARTMENT_HEAD', 'DEPARTMENT_SECRETARY', 'SUPERVISOR'))
  AND primary_portal IS NULL;

UPDATE app_user SET primary_portal = 'methodist'
WHERE id IN (SELECT user_id FROM user_roles WHERE role = 'METHODIST')
  AND primary_portal IS NULL;

UPDATE app_user SET primary_portal = 'student'
WHERE id IN (SELECT user_id FROM user_roles WHERE role = 'STUDENT')
  AND primary_portal IS NULL;

-- Fallback
UPDATE app_user SET primary_portal = 'student' WHERE primary_portal IS NULL;
