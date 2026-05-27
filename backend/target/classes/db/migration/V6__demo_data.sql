-- =====================================================
-- V6__demo_data.sql
-- Демонстрационные данные для ВКР
-- Покрывает сценарии: допуск, голосование, протоколы, ведомость
-- =====================================================

-- -----------------------------------------------------
-- 0. Добавление недостающих колонок (если отсутствуют)
-- -----------------------------------------------------
ALTER TABLE app_user ADD COLUMN IF NOT EXISTS pin_code VARCHAR(4);
ALTER TABLE student ADD COLUMN IF NOT EXISTS thesis_topic VARCHAR(500);
ALTER TABLE student ADD COLUMN IF NOT EXISTS supervisor_name VARCHAR(200);
ALTER TABLE meeting ADD COLUMN IF NOT EXISTS start_time TIME;
ALTER TABLE meeting ADD COLUMN IF NOT EXISTS end_time TIME;
ALTER TABLE meeting ADD COLUMN IF NOT EXISTS location VARCHAR(200);
ALTER TABLE agenda_item ADD COLUMN IF NOT EXISTS presentation_duration INTEGER DEFAULT 10;
ALTER TABLE protocol_record ADD COLUMN IF NOT EXISTS score_points INTEGER;
ALTER TABLE protocol_record ADD COLUMN IF NOT EXISTS is_absent BOOLEAN DEFAULT FALSE;
ALTER TABLE protocol_record ADD COLUMN IF NOT EXISTS qualification VARCHAR(200);
ALTER TABLE protocol_record ADD COLUMN IF NOT EXISTS is_with_honors BOOLEAN;

-- -----------------------------------------------------
-- 1. Направление подготовки (09.03.03 — если отсутствует)
-- -----------------------------------------------------
INSERT INTO direction (id, code, name, created_at, updated_at)
VALUES ('002df46d-b3cd-4c1e-b927-2a30ff8c81ee', '09.03.03', 'Прикладная информатика', NOW(), NOW())
ON CONFLICT (code) DO NOTHING;

-- -----------------------------------------------------
-- 2. Учебные группы (4 курс, выпускные)
-- -----------------------------------------------------
INSERT INTO study_group (id, name, course, direction_id, created_at, updated_at) VALUES
('f61372e7-21bd-4ea0-b4e9-a89c24e54123', 'ПИ-б-01-22', 4, '002df46d-b3cd-4c1e-b927-2a30ff8c81ee', NOW(), NOW()),
('46a6f430-e436-499e-a01a-daedc481ba1e', 'ПИ-б-02-22', 4, '002df46d-b3cd-4c1e-b927-2a30ff8c81ee', NOW(), NOW());

-- -----------------------------------------------------
-- 3. Пользователи системы (новые: член ГЭК №5 + студенты 2–8)
-- Пароли: member123 / student123 (BCrypt, 12 раундов)
-- -----------------------------------------------------
INSERT INTO app_user (id, username, password, email, full_name, role, created_at, updated_at) VALUES
('6dc3aa76-8e63-4896-b702-05df5d72e6aa', 'member5', '$2b$12$oTpNrwwvpU6Duf31UUeRvOXaFLmgARG.rkBK16Gu04zm.cW5Rm53G', 'member5@spbutu.ru', 'Семенов Игорь Петрович', 'GEK_MEMBER', NOW(), NOW()),
('3a803fad-0760-4223-ad0b-1a7a592ba7ee', 'student2', '$2b$12$Zb4xT7TOxLBE36TIXmBqO.9PWejBch4V76CwuydExwJlc9JP83i6O', 'student2@spbutu.ru', 'Петрова Анна Владимировна', 'STUDENT', NOW(), NOW()),
('060893c7-f491-470a-9ade-08c59ea14d8b', 'student3', '$2b$12$Zb4xT7TOxLBE36TIXmBqO.9PWejBch4V76CwuydExwJlc9JP83i6O', 'student3@spbutu.ru', 'Сидоров Виктор Викторович', 'STUDENT', NOW(), NOW()),
('aa516d0f-22ba-4cc3-8e33-5ce22782ca19', 'student4', '$2b$12$Zb4xT7TOxLBE36TIXmBqO.9PWejBch4V76CwuydExwJlc9JP83i6O', 'student4@spbutu.ru', 'Кузнецова Екатерина Андреевна', 'STUDENT', NOW(), NOW()),
('4ebe4998-2af8-4f5d-b9b3-cafdf9fe7426', 'student5', '$2b$12$Zb4xT7TOxLBE36TIXmBqO.9PWejBch4V76CwuydExwJlc9JP83i6O', 'student5@spbutu.ru', 'Морозов Дмитрий Сергеевич', 'STUDENT', NOW(), NOW()),
('1e592657-625e-4894-8523-4c16f7ec0f3f', 'student6', '$2b$12$Zb4xT7TOxLBE36TIXmBqO.9PWejBch4V76CwuydExwJlc9JP83i6O', 'student6@spbutu.ru', 'Волкова Ольга Павловна', 'STUDENT', NOW(), NOW()),
('286bc9d8-3e9a-46c2-a250-e2292625b48b', 'student7', '$2b$12$Zb4xT7TOxLBE36TIXmBqO.9PWejBch4V76CwuydExwJlc9JP83i6O', 'student7@spbutu.ru', 'Лебедев Артём Игоревич', 'STUDENT', NOW(), NOW()),
('a725908d-b886-4fb8-b7a7-cd71c55caf91', 'student8', '$2b$12$Zb4xT7TOxLBE36TIXmBqO.9PWejBch4V76CwuydExwJlc9JP83i6O', 'student8@spbutu.ru', 'Соколова Мария Дмитриевна', 'STUDENT', NOW(), NOW());

-- -----------------------------------------------------
-- 4. Студенты (8 человек, реалистичные ФИО и темы ВКР)
-- -----------------------------------------------------
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('f888a821-09dd-4b46-a7a3-3e6e0faec6b2', 'Иванов', 'Иван', 'Иванович', '1222101', 'Разработка веб-приложения для автоматизации учета итогов аттестации обучающихся', 'Иванов И.И.', 'f61372e7-21bd-4ea0-b4e9-a89c24e54123', NOW(), NOW()),
('97f2d30b-8612-4a2e-808d-3d59086a8e80', 'Петрова', 'Анна', 'Владимировна', '1222102', 'Информационная система управления складом на основе микросервисной архитектуры', 'Петрова А.В.', 'f61372e7-21bd-4ea0-b4e9-a89c24e54123', NOW(), NOW()),
('e6096386-59dc-4d7d-b913-5e2ed3d78c76', 'Сидоров', 'Виктор', 'Викторович', '1222103', 'Мобильное приложение для автоматизации процессов доставки с использованием GPS-трекинга', 'Сидоров В.В.', 'f61372e7-21bd-4ea0-b4e9-a89c24e54123', NOW(), NOW()),
('2008c176-d5cf-4eac-a577-5f4da9707369', 'Кузнецова', 'Екатерина', 'Андреевна', '1222104', 'Анализ и оптимизация баз данных высоконагруженных информационных систем', 'Кузнецова Е.А.', '46a6f430-e436-499e-a01a-daedc481ba1e', NOW(), NOW()),
('f726e89c-73bb-40db-b527-d89b08ff6e8d', 'Морозов', 'Дмитрий', 'Сергеевич', '1222105', 'Разработка чат-бота для технической поддержки на базе машинного обучения', 'Морозов Д.С.', '46a6f430-e436-499e-a01a-daedc481ba1e', NOW(), NOW()),
('acd44169-f3c3-44a2-8f86-e3ac6a0feda2', 'Волкова', 'Ольга', 'Павловна', '1222106', 'Система электронного документооборота для образовательных учреждений', 'Волкова О.П.', '46a6f430-e436-499e-a01a-daedc481ba1e', NOW(), NOW()),
('997f69d4-260d-4fdc-b63d-4f65524401f8', 'Лебедев', 'Артём', 'Игоревич', '1222107', 'Исследование методов защиты персональных данных в распределенных системах', 'Лебедев А.И.', 'f61372e7-21bd-4ea0-b4e9-a89c24e54123', NOW(), NOW()),
('c79ef0bd-0759-4902-871b-9dacba3f193b', 'Соколова', 'Мария', 'Дмитриевна', '1222108', 'Автоматизация тестирования программного обеспечения с применением DevOps-практик', 'Соколова М.Д.', '46a6f430-e436-499e-a01a-daedc481ba1e', NOW(), NOW());

-- -----------------------------------------------------
-- 5. ГЭК (новая комиссия для демо)
-- -----------------------------------------------------
INSERT INTO gek (id, name, created_at, updated_at)
VALUES ('2d9344ac-76d3-44ee-ae3f-24875c2ae0a3', 'ГЭК-2026-ПИ', NOW(), NOW());

-- -----------------------------------------------------
-- 6. Члены ГЭК (5 человек, включая председателя)
-- -----------------------------------------------------
INSERT INTO gek_member (id, user_id, academic_title, department, pin_code, created_at, updated_at) VALUES
('aa7a44f7-9388-4e74-9955-42d3c55038e3', (SELECT id FROM app_user WHERE username = 'member1'), 'проф., д.т.н.', 'Кафедра прикладной информатики', '1111', NOW(), NOW()),
('78d16067-fb3b-4543-b8d3-c9c4e4323600', (SELECT id FROM app_user WHERE username = 'member2'), 'доц., к.т.н.', 'Кафедра информационных систем', '2222', NOW(), NOW()),
('709eba95-0247-4f35-b29d-5d57cbb2f199', (SELECT id FROM app_user WHERE username = 'member3'), 'ст. преподаватель', 'Кафедра прикладной информатики', '3333', NOW(), NOW()),
('f2e94390-276f-4c6e-89ed-196cfba6d3c7', (SELECT id FROM app_user WHERE username = 'member4'), 'представитель работодателя (ООО «ТехноСофт»)', 'Кафедра программной инженерии', '4444', NOW(), NOW()),
('6cebf361-9e31-4758-b7f6-da3aba721cd3', '6dc3aa76-8e63-4896-b702-05df5d72e6aa', 'доц., к.э.н.', 'Кафедра экономики', '5555', NOW(), NOW());

-- -----------------------------------------------------
-- 7. Состав ГЭК (связи + роли председателя/члена)
-- -----------------------------------------------------
INSERT INTO gek_membership (id, gek_id, gek_member_id, position_in_gek, created_at, updated_at) VALUES
('8a1b2c3d-4e5f-6789-0123-456789abcdef', '2d9344ac-76d3-44ee-ae3f-24875c2ae0a3', 'aa7a44f7-9388-4e74-9955-42d3c55038e3', 'CHAIRMAN', NOW(), NOW()),
('9b2c3d4e-5f67-7890-1234-567890abcdef', '2d9344ac-76d3-44ee-ae3f-24875c2ae0a3', '78d16067-fb3b-4543-b8d3-c9c4e4323600', 'MEMBER', NOW(), NOW()),
('0c3d4e5f-6789-0123-2345-678901abcdef', '2d9344ac-76d3-44ee-ae3f-24875c2ae0a3', '709eba95-0247-4f35-b29d-5d57cbb2f199', 'MEMBER', NOW(), NOW()),
('1d4e5f67-7890-1234-3456-789012abcdef', '2d9344ac-76d3-44ee-ae3f-24875c2ae0a3', 'f2e94390-276f-4c6e-89ed-196cfba6d3c7', 'MEMBER', NOW(), NOW()),
('2e5f6789-0123-2345-4567-890123abcdef', '2d9344ac-76d3-44ee-ae3f-24875c2ae0a3', '6cebf361-9e31-4758-b7f6-da3aba721cd3', 'MEMBER', NOW(), NOW());

-- -----------------------------------------------------
-- 8. Заседание ГЭК (статус ACTIVE — для демо голосования)
-- -----------------------------------------------------
INSERT INTO meeting (id, meeting_date, start_time, end_time, location, status, quorum_required, gek_id, created_by, created_at, updated_at)
VALUES (
    '1594fff6-5f20-4f21-8bb1-6747c1704335',
    '2026-06-15 10:00:00',
    '10:00:00',
    '16:00:00',
    'Ауд. 305',
    'ACTIVE',
    3,
    '2d9344ac-76d3-44ee-ae3f-24875c2ae0a3',
    (SELECT id FROM app_user WHERE username = 'secretary'),
    NOW(), NOW()
);

-- -----------------------------------------------------
-- 9. Пункты повестки (6 студентов, допущенных к ГИА)
-- -----------------------------------------------------
INSERT INTO agenda_item (id, meeting_id, student_id, order_number, presentation_duration, average_score, created_at, updated_at) VALUES
('dc71a12f-254d-4d63-95d1-a0a9b6c726dc', '1594fff6-5f20-4f21-8bb1-6747c1704335', 'f888a821-09dd-4b46-a7a3-3e6e0faec6b2', 1, 10, NULL, NOW(), NOW()),
('c741d072-2aa7-4cd5-abef-25c3f07ca4b8', '1594fff6-5f20-4f21-8bb1-6747c1704335', '97f2d30b-8612-4a2e-808d-3d59086a8e80', 2, 10, NULL, NOW(), NOW()),
('feff107d-3e0d-4583-9aa1-49233f540f61', '1594fff6-5f20-4f21-8bb1-6747c1704335', 'e6096386-59dc-4d7d-b913-5e2ed3d78c76', 3, 10, NULL, NOW(), NOW()),
('78b1130b-a917-4356-9551-2d739f2c391d', '1594fff6-5f20-4f21-8bb1-6747c1704335', '2008c176-d5cf-4eac-a577-5f4da9707369', 4, 10, NULL, NOW(), NOW()),
('fa32dfa7-505d-4178-9560-0325f7354b24', '1594fff6-5f20-4f21-8bb1-6747c1704335', 'f726e89c-73bb-40db-b527-d89b08ff6e8d', 5, 10, NULL, NOW(), NOW()),
('be304249-ef7d-4ab4-a783-753efd35bcc4', '1594fff6-5f20-4f21-8bb1-6747c1704335', 'acd44169-f3c3-44a2-8f86-e3ac6a0feda2', 6, 10, NULL, NOW(), NOW());

-- -----------------------------------------------------
-- 10. Протокол (черновик, привязан к заседанию)
-- -----------------------------------------------------
INSERT INTO protocol (id, meeting_id, protocol_number, status, created_at, updated_at)
VALUES ('c4cd64c2-588d-43d5-bad3-23c1c18b2504', '1594fff6-5f20-4f21-8bb1-6747c1704335', '01/2026-ПИ', 'DRAFT', NOW(), NOW());

-- -----------------------------------------------------
-- 11. Голоса членов ГЭК (только для первых 3 студентов, все 5 членов)
-- Студент 1: 5,5,4,5,5 → средняя 4.8 → итог 5
-- Студент 2: 4,4,4,5,4 → средняя 4.2 → итог 4
-- Студент 3: 3,3,4,3,3 → средняя 3.2 → итог 3
-- -----------------------------------------------------
INSERT INTO vote (id, agenda_item_id, gek_member_id, score, voted_at, created_at, updated_at) VALUES
-- Студент 1 (Иванов) — 5 голосов
('f1111111-1111-1111-1111-111111111111', 'dc71a12f-254d-4d63-95d1-a0a9b6c726dc', 'aa7a44f7-9388-4e74-9955-42d3c55038e3', 5, NOW(), NOW(), NOW()),
('f2222222-2222-2222-2222-222222222222', 'dc71a12f-254d-4d63-95d1-a0a9b6c726dc', '78d16067-fb3b-4543-b8d3-c9c4e4323600', 5, NOW(), NOW(), NOW()),
('f3333333-3333-3333-3333-333333333333', 'dc71a12f-254d-4d63-95d1-a0a9b6c726dc', '709eba95-0247-4f35-b29d-5d57cbb2f199', 4, NOW(), NOW(), NOW()),
('f4444444-4444-4444-4444-444444444444', 'dc71a12f-254d-4d63-95d1-a0a9b6c726dc', 'f2e94390-276f-4c6e-89ed-196cfba6d3c7', 5, NOW(), NOW(), NOW()),
('f5555555-5555-5555-5555-555555555555', 'dc71a12f-254d-4d63-95d1-a0a9b6c726dc', '6cebf361-9e31-4758-b7f6-da3aba721cd3', 5, NOW(), NOW(), NOW()),
-- Студент 2 (Петрова) — 5 голосов
('f6666666-6666-6666-6666-666666666666', 'c741d072-2aa7-4cd5-abef-25c3f07ca4b8', 'aa7a44f7-9388-4e74-9955-42d3c55038e3', 4, NOW(), NOW(), NOW()),
('f7777777-7777-7777-7777-777777777777', 'c741d072-2aa7-4cd5-abef-25c3f07ca4b8', '78d16067-fb3b-4543-b8d3-c9c4e4323600', 4, NOW(), NOW(), NOW()),
('f8888888-8888-8888-8888-888888888888', 'c741d072-2aa7-4cd5-abef-25c3f07ca4b8', '709eba95-0247-4f35-b29d-5d57cbb2f199', 4, NOW(), NOW(), NOW()),
('f9999999-9999-9999-9999-999999999999', 'c741d072-2aa7-4cd5-abef-25c3f07ca4b8', 'f2e94390-276f-4c6e-89ed-196cfba6d3c7', 5, NOW(), NOW(), NOW()),
('f1010101-0101-0101-0101-010101010101', 'c741d072-2aa7-4cd5-abef-25c3f07ca4b8', '6cebf361-9e31-4758-b7f6-da3aba721cd3', 4, NOW(), NOW(), NOW()),
-- Студент 3 (Сидоров) — 5 голосов
('f1111111-1111-1111-1111-111111111112', 'feff107d-3e0d-4583-9aa1-49233f540f61', 'aa7a44f7-9388-4e74-9955-42d3c55038e3', 3, NOW(), NOW(), NOW()),
('f1212121-2121-2121-2121-212121212121', 'feff107d-3e0d-4583-9aa1-49233f540f61', '78d16067-fb3b-4543-b8d3-c9c4e4323600', 3, NOW(), NOW(), NOW()),
('f1313131-3131-3131-3131-313131313131', 'feff107d-3e0d-4583-9aa1-49233f540f61', '709eba95-0247-4f35-b29d-5d57cbb2f199', 4, NOW(), NOW(), NOW()),
('f1414141-4141-4141-4141-414141414141', 'feff107d-3e0d-4583-9aa1-49233f540f61', 'f2e94390-276f-4c6e-89ed-196cfba6d3c7', 3, NOW(), NOW(), NOW()),
('f1515151-5151-5151-5151-515151515151', 'feff107d-3e0d-4583-9aa1-49233f540f61', '6cebf361-9e31-4758-b7f6-da3aba721cd3', 3, NOW(), NOW(), NOW());

-- -----------------------------------------------------
-- 12. Обновление средних оценок у AgendaItem (3 студента)
-- -----------------------------------------------------
UPDATE agenda_item SET average_score = 4.80, updated_at = NOW() WHERE id = 'dc71a12f-254d-4d63-95d1-a0a9b6c726dc';
UPDATE agenda_item SET average_score = 4.20, updated_at = NOW() WHERE id = 'c741d072-2aa7-4cd5-abef-25c3f07ca4b8';
UPDATE agenda_item SET average_score = 3.20, updated_at = NOW() WHERE id = 'feff107d-3e0d-4583-9aa1-49233f540f61';

-- -----------------------------------------------------
-- 13. Итоговые записи протокола (ProtocolRecord)
-- Первые 3 — с итоговыми оценками; остальные 3 — без оценок (ещё не голосовали)
-- -----------------------------------------------------
INSERT INTO protocol_record (id, protocol_id, student_id, average_score, final_score, score_points, is_absent, qualification, is_with_honors, decision, created_at, updated_at) VALUES
('76d37dab-f3c1-4aa1-a50f-66af3fe1204f', 'c4cd64c2-588d-43d5-bad3-23c1c18b2504', 'f888a821-09dd-4b46-a7a3-3e6e0faec6b2', 5, 5, 95, false, 'бакалавр по направлению подготовки 09.03.03 Прикладная информатика', true, 'Признать выполнившим и защитившим ВКР с оценкой отлично', NOW(), NOW()),
('b1b361c6-e63e-44e1-97b8-60cd472142d6', 'c4cd64c2-588d-43d5-bad3-23c1c18b2504', '97f2d30b-8612-4a2e-808d-3d59086a8e80', 4, 4, 82, false, 'бакалавр по направлению подготовки 09.03.03 Прикладная информатика', false, 'Признать выполнившим и защитившим ВКР с оценкой хорошо', NOW(), NOW()),
('19416c1e-9a2f-4a26-8c8b-d1f201b35aa0', 'c4cd64c2-588d-43d5-bad3-23c1c18b2504', 'e6096386-59dc-4d7d-b913-5e2ed3d78c76', 3, 3, 70, false, 'бакалавр по направлению подготовки 09.03.03 Прикладная информатика', false, 'Признать выполнившим и защитившим ВКР с оценкой удовлетворительно', NOW(), NOW()),
('bf435d93-de6a-455b-a753-f07dfb2d1be5', 'c4cd64c2-588d-43d5-bad3-23c1c18b2504', '2008c176-d5cf-4eac-a577-5f4da9707369', NULL, NULL, NULL, false, NULL, false, NULL, NOW(), NOW()),
('1bbcbbf3-a4c9-4235-bf85-b86e3d3a9ff5', 'c4cd64c2-588d-43d5-bad3-23c1c18b2504', 'f726e89c-73bb-40db-b527-d89b08ff6e8d', NULL, NULL, NULL, false, NULL, false, NULL, NOW(), NOW()),
('55cc344b-c065-46d7-a071-acf195fbeb5c', 'c4cd64c2-588d-43d5-bad3-23c1c18b2504', 'acd44169-f3c3-44a2-8f86-e3ac6a0feda2', NULL, NULL, NULL, false, NULL, false, NULL, NOW(), NOW());

-- -----------------------------------------------------
-- 14. Допуски к ГИА (Admission)
-- 6 допущены, 2 не допущены (1 — долги, 1 — недобор баллов)
-- -----------------------------------------------------
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('e1111111-1111-1111-1111-111111111111', 'f888a821-09dd-4b46-a7a3-3e6e0faec6b2', 82, false, true, NOW(), NOW(), NOW()),
('e2222222-2222-2222-2222-222222222222', '97f2d30b-8612-4a2e-808d-3d59086a8e80', 78, false, true, NOW(), NOW(), NOW()),
('e3333333-3333-3333-3333-333333333333', 'e6096386-59dc-4d7d-b913-5e2ed3d78c76', 71, false, true, NOW(), NOW(), NOW()),
('e4444444-4444-4444-4444-444444444444', '2008c176-d5cf-4eac-a577-5f4da9707369', 88, false, true, NOW(), NOW(), NOW()),
('e5555555-5555-5555-5555-555555555555', 'f726e89c-73bb-40db-b527-d89b08ff6e8d', 65, false, true, NOW(), NOW(), NOW()),
('e6666666-6666-6666-6666-666666666666', 'acd44169-f3c3-44a2-8f86-e3ac6a0feda2', 91, false, true, NOW(), NOW(), NOW()),
('e7777777-7777-7777-7777-777777777777', '997f69d4-260d-4fdc-b63d-4f65524401f8', 55, true, false, NOW(), NOW(), NOW()),
('e8888888-8888-8888-8888-888888888888', 'c79ef0bd-0759-4902-871b-9dacba3f193b', 45, false, false, NOW(), NOW(), NOW());

-- -----------------------------------------------------
-- 15. Журнал аудита (3 записи для демонстрации)
-- -----------------------------------------------------
INSERT INTO audit_log (id, table_name, record_id, action, old_value, new_value, changed_by, ip_address, created_at) VALUES
('d1111111-1111-1111-1111-111111111111', 'protocol_record', '76d37dab-f3c1-4aa1-a50f-66af3fe1204f', 'INSERT', NULL, '{"final_score":5,"score_points":95}', (SELECT id FROM app_user WHERE username = 'secretary'), '192.168.1.100', NOW()),
('d2222222-2222-2222-2222-222222222222', 'protocol_record', 'b1b361c6-e63e-44e1-97b8-60cd472142d6', 'INSERT', NULL, '{"final_score":4,"score_points":82}', (SELECT id FROM app_user WHERE username = 'secretary'), '192.168.1.100', NOW()),
('d3333333-3333-3333-3333-333333333333', 'vote', 'f1111111-1111-1111-1111-111111111111', 'INSERT', NULL, '{"score":5,"gek_member":"Александров П.С."}', (SELECT id FROM app_user WHERE username = 'member1'), '192.168.1.105', NOW());
