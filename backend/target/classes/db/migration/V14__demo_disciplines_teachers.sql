-- ============================================================
-- Миграция V14: Демо-данные дисциплин и преподавателей
-- ============================================================

-- Дисциплины для направления 09.03.03 Прикладная информатика
INSERT INTO discipline (id, code, name, hours, ects_credits, course, semester, control_type, direction_id)
SELECT gen_random_uuid(), d.code, d.name, d.hours, d.ects, d.course, d.semester, d.control, dir.id
FROM (VALUES
    ('ПИ.01', 'Математический анализ', 144, 6, 1, '2025-1', 'EXAM'),
    ('ПИ.02', 'Программирование на Java', 180, 8, 1, '2025-1', 'EXAM'),
    ('ПИ.03', 'Базы данных', 144, 6, 2, '2025-2', 'EXAM'),
    ('ПИ.04', 'Веб-технологии', 108, 4, 2, '2025-2', 'TEST'),
    ('ПИ.05', 'Экономика предприятия', 72, 3, 3, '2026-1', 'TEST'),
    ('ПИ.06', 'Управление проектами', 108, 4, 3, '2026-1', 'EXAM'),
    ('ПИ.07', 'Информационная безопасность', 144, 6, 4, '2026-2', 'EXAM'),
    ('ПИ.08', 'Теория вероятностей', 108, 4, 2, '2025-2', 'EXAM')
) AS d(code, name, hours, ects, course, semester, control)
CROSS JOIN direction dir WHERE dir.code = '09.03.03'
ON CONFLICT DO NOTHING;

-- Дисциплины для направления 09.03.01 Информатика и вычислительная техника
INSERT INTO discipline (id, code, name, hours, ects_credits, course, semester, control_type, direction_id)
SELECT gen_random_uuid(), d.code, d.name, d.hours, d.ects, d.course, d.semester, d.control, dir.id
FROM (VALUES
    ('ИВТ.01', 'Алгоритмы и структуры данных', 180, 8, 1, '2025-1', 'EXAM'),
    ('ИВТ.02', 'Операционные системы', 144, 6, 2, '2025-2', 'EXAM'),
    ('ИВТ.03', 'Компьютерные сети', 144, 6, 3, '2026-1', 'EXAM'),
    ('ИВТ.04', 'Искусственный интеллект', 108, 4, 4, '2026-2', 'TEST')
) AS d(code, name, hours, ects, course, semester, control)
CROSS JOIN direction dir WHERE dir.code = '09.03.01'
ON CONFLICT DO NOTHING;

-- Преподаватели
INSERT INTO teacher (id, last_name, first_name, middle_name, department, position, degree, email)
VALUES
    (gen_random_uuid(), 'Иванов', 'Иван', 'Иванович', 'Информационных технологий и математики', 'Профессор', 'д.т.н.', 'ivanov@spbutu.ru'),
    (gen_random_uuid(), 'Петрова', 'Мария', 'Сергеевна', 'Информационных технологий и математики', 'Доцент', 'к.т.н.', 'petrova@spbutu.ru'),
    (gen_random_uuid(), 'Сидоров', 'Алексей', 'Владимирович', 'Экономики и управления', 'Профессор', 'д.э.н.', 'sidorov@spbutu.ru'),
    (gen_random_uuid(), 'Кузнецова', 'Елена', 'Андреевна', 'Информационных технологий и математики', 'Доцент', 'к.ф.-м.н.', 'kuznetsova@spbutu.ru'),
    (gen_random_uuid(), 'Смирнов', 'Дмитрий', 'Павлович', 'Информационных технологий и математики', 'Старший преподаватель', '', 'smirnov@spbutu.ru'),
    (gen_random_uuid(), 'Васильева', 'Анна', 'Николаевна', 'Экономики и управления', 'Доцент', 'к.э.н.', 'vasilieva@spbutu.ru')
ON CONFLICT DO NOTHING;
