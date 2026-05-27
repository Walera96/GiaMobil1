-- ============================================================
-- V29: Fix password hashes, remove duplicates, add missing data
-- ============================================================

-- 1. Fix password hashes: generate new BCrypt $2a$ hashes for all users
UPDATE app_user SET password = '$2a$12$z8ZEsuwLickknt8Xa5w2seODNYUcq6xDGyazuzlXsAhEnTlHzKILq' WHERE username = 'admin';
UPDATE app_user SET password = '$2a$12$qM/9ZPjjX6dI9o7nI4QSiek5G61JsUhUGE4fVnWELChq5YMJqxPe6' WHERE username = 'methodist';
UPDATE app_user SET password = '$2a$12$I2tT6ELdJkiSTk6/LVbE8.HRg.Huny5AK8Kz5ptCvmfi.8UXmmrN2' WHERE username = 'secretary';
UPDATE app_user SET password = '$2a$12$V3QmFL3gCMegNExUwknPg.D7igxP/yNLHlzbzI4nNMXWIBnG492UG' WHERE username = 'chairman';
UPDATE app_user SET password = '$2a$12$U2VTZ4gj/f4Hh1lmnH.OSOnBvhKsdkp.oVJiV3xpRH/bNaMxErWNm' WHERE username = 'member1';
UPDATE app_user SET password = '$2a$12$np.JyQRrXFda0F4/R7TDfun9z6HpiEOI0pumQh0.YJScbShYGZCsG' WHERE username = 'member2';
UPDATE app_user SET password = '$2a$12$O7UI4a7PL2ckRCZTeoGAmejwX0qh2oBpTCeq7N4xeUks6Fh63Nrie' WHERE username = 'member3';
UPDATE app_user SET password = '$2a$12$s7PnqX8hRrC3xuMqhT3n0epfZjYmhgduJhP.I/dvNvC6C2hfCsNUu' WHERE username = 'member4';
UPDATE app_user SET password = '$2a$12$6hov1okKnqMdgixjJOk05.q5SKdlLIbOZUKZkc0ZO121tzZN.5hxa' WHERE username = 'member5';
UPDATE app_user SET password = '$2a$12$5yl9yWlRm7bC7T6iD/iUv.xqAiFBjXwy9jUFcMF1ywxL4Hgatd1iG' WHERE username = 'student1';
UPDATE app_user SET password = '$2a$12$5yl9yWlRm7bC7T6iD/iUv.xqAiFBjXwy9jUFcMF1ywxL4Hgatd1iG' WHERE username = 'student2';
UPDATE app_user SET password = '$2a$12$5yl9yWlRm7bC7T6iD/iUv.xqAiFBjXwy9jUFcMF1ywxL4Hgatd1iG' WHERE username = 'student3';
UPDATE app_user SET password = '$2a$12$5yl9yWlRm7bC7T6iD/iUv.xqAiFBjXwy9jUFcMF1ywxL4Hgatd1iG' WHERE username = 'student4';
UPDATE app_user SET password = '$2a$12$5yl9yWlRm7bC7T6iD/iUv.xqAiFBjXwy9jUFcMF1ywxL4Hgatd1iG' WHERE username = 'student5';
UPDATE app_user SET password = '$2a$12$5yl9yWlRm7bC7T6iD/iUv.xqAiFBjXwy9jUFcMF1ywxL4Hgatd1iG' WHERE username = 'student6';
UPDATE app_user SET password = '$2a$12$5yl9yWlRm7bC7T6iD/iUv.xqAiFBjXwy9jUFcMF1ywxL4Hgatd1iG' WHERE username = 'student7';

-- 2. Add unique constraint to prevent duplicate students in same group
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'student_unique_name_group'
    ) THEN
        ALTER TABLE student ADD CONSTRAINT student_unique_name_group
            UNIQUE (last_name, first_name, middle_name, group_id);
    END IF;
END $$;

-- 3. Add notifications for all users (if not exists)
INSERT INTO notification (id, user_id, title, message, type, is_read, created_at)
SELECT 
    gen_random_uuid(),
    u.id,
    'Добро пожаловать в систему ГИА',
    'Ваш аккаунт активирован. Теперь вы можете пользоваться всеми функциями системы.',
    'INFO',
    false,
    NOW()
FROM app_user u
WHERE NOT EXISTS (
    SELECT 1 FROM notification n WHERE n.user_id = u.id AND n.title = 'Добро пожаловать в систему ГИА'
);

INSERT INTO notification (id, user_id, title, message, type, is_read, created_at)
SELECT 
    gen_random_uuid(),
    u.id,
    'Ближайшее заседание ГЭК',
    'Проверьте расписание защит в разделе "Заседания".',
    'WARNING',
    false,
    NOW()
FROM app_user u
WHERE u.role IN ('CHAIRMAN', 'SECRETARY', 'GEK_MEMBER')
  AND NOT EXISTS (
    SELECT 1 FROM notification n WHERE n.user_id = u.id AND n.title = 'Ближайшее заседание ГЭК'
);

-- 4. Add more teachers
INSERT INTO teacher (id, last_name, first_name, middle_name, department, position, degree, email, created_at, updated_at)
SELECT * FROM (VALUES
    (gen_random_uuid(), 'Алексеев', 'Михаил', 'Сергеевич', 'Кафедра информационных систем', 'Профессор', 'д.т.н.', 'alekseev@spbutu.ru', NOW(), NOW()),
    (gen_random_uuid(), 'Борисова', 'Ольга', 'Дмитриевна', 'Кафедра прикладной информатики', 'Доцент', 'к.э.н.', 'borisova@spbutu.ru', NOW(), NOW()),
    (gen_random_uuid(), 'Григорьев', 'Павел', 'Александрович', 'Кафедра информационных систем', 'Старший преподаватель', '', 'grigoriev@spbutu.ru', NOW(), NOW()),
    (gen_random_uuid(), 'Дмитриева', 'Наталья', 'Викторовна', 'Кафедра экономики и управления', 'Доцент', 'к.э.н.', 'dmitrieva@spbutu.ru', NOW(), NOW()),
    (gen_random_uuid(), 'Егоров', 'Андрей', 'Петрович', 'Кафедра прикладной информатики', 'Профессор', 'д.ф.-м.н.', 'egorov@spbutu.ru', NOW(), NOW()),
    (gen_random_uuid(), 'Захарова', 'Екатерина', 'Михайловна', 'Кафедра информационных систем', 'Доцент', 'к.т.н.', 'zakharova@spbutu.ru', NOW(), NOW()),
    (gen_random_uuid(), 'Козлов', 'Игорь', 'Владимирович', 'Кафедра прикладной информатики', 'Старший преподаватель', '', 'kozlov@spbutu.ru', NOW(), NOW()),
    (gen_random_uuid(), 'Лебедева', 'Анна', 'Сергеевна', 'Кафедра экономики и управления', 'Доцент', 'к.э.н.', 'lebedeva@spbutu.ru', NOW(), NOW())
) AS v(id, last_name, first_name, middle_name, department, position, degree, email, created_at, updated_at)
WHERE NOT EXISTS (
    SELECT 1 FROM teacher t 
    WHERE t.last_name = v.last_name AND t.first_name = v.first_name AND t.middle_name = v.middle_name
);

-- 5. Add study_plan_discipline links
INSERT INTO study_plan_discipline (id, study_plan_id, discipline_id, semester, course, hours, credits, control_type, is_mandatory, created_at, updated_at)
SELECT * FROM (VALUES
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '1a057129-774a-4a9d-914a-febd4963a1a8'::uuid, 1, 1, 144, 4, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, 'edd62374-de6c-4cb5-845a-7106ad330852'::uuid, 2, 1, 108, 3, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, 'a87e6561-c995-432e-b3c1-4a3ad45115ab'::uuid, 3, 2, 108, 3, 'CREDIT', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '0bc53747-5c84-4610-be39-ce50067fcc5a'::uuid, 4, 2, 72, 2, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '0be77342-48ec-4f28-a0e8-3ecb0d230c10'::uuid, 5, 3, 144, 4, 'CREDIT', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '412d2f19-44e0-44c4-a5ba-826a999a090b'::uuid, 3, 2, 108, 3, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '8e442593-850e-4c15-af8c-44d283d42c70'::uuid, 1, 1, 144, 4, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '02616fbe-bf0f-4f50-a4db-ec5cd55bba22'::uuid, 2, 1, 108, 3, 'CREDIT', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '4b1f3fbb-923f-48dd-a469-b8cabf359c32'::uuid, 2, 1, 144, 4, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '846453b6-181c-40ed-948c-7258e976ce43'::uuid, 1, 1, 72, 2, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '8505356f-0943-473b-81a5-d59edd518106'::uuid, 6, 4, 72, 2, 'CREDIT', true, NOW(), NOW()),
    (gen_random_uuid(), 'a0000001-0000-0000-0000-000000000001'::uuid, '08b65b29-6740-4ea2-a821-5c697487537a'::uuid, 5, 3, 108, 3, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), '88af5508-8a0a-4ed7-8dd0-b8a87835244b'::uuid, 'edd62374-de6c-4cb5-845a-7106ad330852'::uuid, 1, 1, 108, 3, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), '88af5508-8a0a-4ed7-8dd0-b8a87835244b'::uuid, 'a87e6561-c995-432e-b3c1-4a3ad45115ab'::uuid, 2, 1, 108, 3, 'CREDIT', true, NOW(), NOW()),
    (gen_random_uuid(), '88af5508-8a0a-4ed7-8dd0-b8a87835244b'::uuid, '4b1f3fbb-923f-48dd-a469-b8cabf359c32'::uuid, 1, 1, 144, 4, 'EXAM', true, NOW(), NOW()),
    (gen_random_uuid(), '88af5508-8a0a-4ed7-8dd0-b8a87835244b'::uuid, '8505356f-0943-473b-81a5-d59edd518106'::uuid, 4, 2, 72, 2, 'CREDIT', true, NOW(), NOW()),
    (gen_random_uuid(), '88af5508-8a0a-4ed7-8dd0-b8a87835244b'::uuid, '08b65b29-6740-4ea2-a821-5c697487537a'::uuid, 3, 2, 108, 3, 'EXAM', true, NOW(), NOW())
) AS v(id, study_plan_id, discipline_id, semester, course, hours, credits, control_type, is_mandatory, created_at, updated_at)
WHERE NOT EXISTS (
    SELECT 1 FROM study_plan_discipline spd 
    WHERE spd.study_plan_id = v.study_plan_id AND spd.discipline_id = v.discipline_id
);

-- 6. Add grades for students without grades (using existing disciplines)
INSERT INTO grade (id, student_id, subject_name, score, semester, discipline_id, current_control, attendance, activity, exam_score, total_score, ects_grade, five_point_grade, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    s.id,
    d.name,
    (random() * 40 + 50)::int,
    '2025/2026-1',
    d.id,
    (random() * 50 + 10)::int,
    (random() * 8 + 2)::int,
    (random() * 15 + 5)::int,
    (random() * 20 + 5)::int,
    (random() * 40 + 50)::int,
    CASE 
        WHEN random() > 0.9 THEN 'A'
        WHEN random() > 0.7 THEN 'B'
        WHEN random() > 0.5 THEN 'C'
        WHEN random() > 0.3 THEN 'D'
        ELSE 'E'
    END,
    CASE 
        WHEN random() > 0.9 THEN 5
        WHEN random() > 0.6 THEN 4
        WHEN random() > 0.3 THEN 3
        ELSE 2
    END,
    NOW(),
    NOW()
FROM student s
CROSS JOIN (SELECT id, name FROM discipline ORDER BY id LIMIT 5) d
WHERE NOT EXISTS (SELECT 1 FROM grade g WHERE g.student_id = s.id);
