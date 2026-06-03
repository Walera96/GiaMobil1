-- Миграция: создание демо-данных для модуля assignments
-- Связывает существующих студентов и группы с заданиями

-- 1. Создаём демо-преподавателя (SUPERVISOR), если нет
INSERT INTO app_user (id, username, password, full_name, email, role, created_at, updated_at)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
    'teacher_demo',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjXAgLqkzgZ/mM5Vd3Q3Q3Q3Q3Q3Q3Q', -- заглушка, нужен реальный bcrypt
    'Иванов Иван Иванович',
    'teacher@spbutu.ru',
    'ADMIN',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_roles (user_id, role)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'SUPERVISOR')
ON CONFLICT DO NOTHING;

-- 2. Создаём демо-группу, если нет
INSERT INTO groups (id, name, code, created_at, updated_at)
VALUES (
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1',
    'ИС-401',
    'IS-401',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- 3. Создаём демо-задания
DO $$
DECLARE
    v_group_id UUID := 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1';
    v_teacher_id UUID := 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1';
BEGIN
    INSERT INTO assignments (
        id, title, description, assignment_type, created_by,
        target_group_id, target_student_ids, deadline,
        allow_late_submission, max_score, scoring_config,
        attached_files, created_at, updated_at
    ) VALUES (
        gen_random_uuid(),
        'Лабораторная работа №1: Моделирование ВКР',
        'Разработать модель информационной системы для дипломного проекта. Приложить UML-диаграммы и описание предметной области.',
        'LAB',
        v_teacher_id,
        v_group_id,
        '[]'::jsonb,
        NOW() + INTERVAL '7 days',
        true,
        100,
        '{"type": "weighted", "criteria": [{"name": "UML-диаграммы", "weight": 30, "maxPoints": 30}, {"name": "Описание предметной области", "weight": 40, "maxPoints": 40}, {"name": "Оформление", "weight": 30, "maxPoints": 30}], "maxTotalScore": 100, "passingScore": 60}'::jsonb,
        '[{"fileName": "lab1_template.docx", "fileUrl": "https://example.com/lab1.docx", "fileSize": 24576}]'::jsonb,
        NOW(),
        NOW()
    );

    INSERT INTO assignments (
        id, title, description, assignment_type, created_by,
        target_group_id, target_student_ids, deadline,
        allow_late_submission, max_score, scoring_config,
        attached_files, created_at, updated_at
    ) VALUES (
        gen_random_uuid(),
        'Курсовая работа: Проектирование БД',
        'Спроектировать реляционную базу данных для выбранной предметной области. ER-диаграмма, нормализация, SQL-скрипты.',
        'COURSEWORK',
        v_teacher_id,
        v_group_id,
        '[]'::jsonb,
        NOW() + INTERVAL '14 days',
        false,
        100,
        '{"type": "criteria", "criteria": [{"name": "ER-диаграмма", "weight": 25, "maxPoints": 25}, {"name": "Нормализация", "weight": 25, "maxPoints": 25}, {"name": "SQL-скрипты", "weight": 30, "maxPoints": 30}, {"name": "Документация", "weight": 20, "maxPoints": 20}], "maxTotalScore": 100, "passingScore": 60}'::jsonb,
        '[]'::jsonb,
        NOW(),
        NOW()
    );

    INSERT INTO assignments (
        id, title, description, assignment_type, created_by,
        target_group_id, target_student_ids, deadline,
        allow_late_submission, max_score, scoring_config,
        attached_files, created_at, updated_at
    ) VALUES (
        gen_random_uuid(),
        'Домашнее задание: SQL-запросы',
        'Выполнить 10 SQL-запросов к учебной базе данных. Оптимизация индексов.',
        'HOMEWORK',
        v_teacher_id,
        v_group_id,
        '[]'::jsonb,
        NOW() - INTERVAL '1 day',
        true,
        10,
        '{"type": "pass_fail", "passingScore": 5}'::jsonb,
        '[]'::jsonb,
        NOW(),
        NOW()
    );

    RAISE NOTICE 'Демо-задания созданы для группы %', v_group_id;
END $$;
