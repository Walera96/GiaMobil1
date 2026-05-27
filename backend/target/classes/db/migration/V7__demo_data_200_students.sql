-- ============================================================
-- Миграция V7__demo_data_200_students.sql
-- Демо-данные: 200 студентов, 2 направления, 8 групп, 100 допусков
-- ЧОУ ВО СПбУТУИЭ
-- ============================================================

-- ------------------------------------------
-- 0. Добавление поля profile в direction
-- ------------------------------------------
ALTER TABLE direction ADD COLUMN IF NOT EXISTS profile VARCHAR(300);

-- ------------------------------------------
-- 1. Направления подготовки (с profile)
-- ------------------------------------------
INSERT INTO direction (id, code, name, profile, created_at, updated_at) VALUES
('4bbe1878-7b3a-416c-a67e-f8b27ae1c220', '09.03.03', 'Прикладная информатика', 'Прикладная информатика в экономике', NOW(), NOW()),
('fbe9de15-b71d-4c30-b3bb-cab0a8412f70', '09.03.04', 'Бизнес-информатика', 'Бизнес-информатика', NOW(), NOW())
ON CONFLICT (code) DO UPDATE SET
  name = EXCLUDED.name,
  profile = EXCLUDED.profile,
  updated_at = NOW();

-- ------------------------------------------
-- 2. Учебные группы (8 штук)
-- ------------------------------------------
INSERT INTO study_group (id, name, course, direction_id, created_at, updated_at) VALUES
('4977e7dd-7514-485f-a950-9df8c825e114', 'ОУИТб-ПИ01-22-4', 4, (SELECT id FROM direction WHERE code = '09.03.03'), NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO study_group (id, name, course, direction_id, created_at, updated_at) VALUES
('42ebd6bb-0dfe-4159-a38b-49fcc907ed73', 'ОУИТб-ПИ02-22-4', 4, (SELECT id FROM direction WHERE code = '09.03.03'), NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO study_group (id, name, course, direction_id, created_at, updated_at) VALUES
('1313166b-da03-4574-91b0-5b3565309cf2', 'ОУИТб-БИ01-22-4', 4, (SELECT id FROM direction WHERE code = '09.03.04'), NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO study_group (id, name, course, direction_id, created_at, updated_at) VALUES
('c33dd7f4-3d16-49cb-92f6-82e6af6ff5c4', 'ОУИТб-БИ02-22-4', 4, (SELECT id FROM direction WHERE code = '09.03.04'), NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO study_group (id, name, course, direction_id, created_at, updated_at) VALUES
('2b779e11-860e-47f1-aa85-044fa101c847', 'ОУИТб-ПИ01-23-3', 3, (SELECT id FROM direction WHERE code = '09.03.03'), NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO study_group (id, name, course, direction_id, created_at, updated_at) VALUES
('73350bfa-6807-487d-964c-252826a6a07c', 'ОУИТб-БИ01-23-3', 3, (SELECT id FROM direction WHERE code = '09.03.04'), NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO study_group (id, name, course, direction_id, created_at, updated_at) VALUES
('ca8e95c7-cb65-4ae0-b8c7-5aad04ac80df', 'ОУИТб-ПИ01-25-1', 1, (SELECT id FROM direction WHERE code = '09.03.03'), NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
INSERT INTO study_group (id, name, course, direction_id, created_at, updated_at) VALUES
('b3f2e216-1775-480b-b346-0aa9a389f698', 'ОУИТб-БИ01-25-1', 1, (SELECT id FROM direction WHERE code = '09.03.04'), NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- ------------------------------------------
-- 3. Студенты (200 записей)
-- ------------------------------------------
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('64b331c9-fb5e-45e6-8317-940e8c32d88f', 'Иванов', 'Иван', 'Иванович', '12220001', 'Разработка информационной системы управления складом на основе микросервисной архитектуры', 'Иванов И.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('5ef13d89-5193-48f8-9cbb-43b02af9cb0a', 'Петров', 'Екатерина', 'Петровна', '12220002', 'Мобильное приложение для автоматизации процессов доставки с использованием GPS-трекинга', 'Петрова А.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('6c1b33c9-64c6-4aba-b630-00e84b679de0', 'Сидоров', 'Виктор', 'Викторович', '12220003', 'Анализ и оптимизация баз данных высоконагруженных информационных систем', 'Сидоров В.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('449ad801-955a-4a43-a6b1-9c81c2c1219f', 'Кузнецов', 'Мария', 'Дмитриевна', '12220004', 'Разработка чат-бота для технической поддержки на базе машинного обучения', 'Кузнецова Е.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('befb18fb-d5a9-4d75-9466-ba291825be5a', 'Смирнов', 'Александр', 'Александрович', '12220005', 'Система электронного документооборота для образовательных учреждений', 'Морозов Д.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('e6d15ab1-f5e2-4fc5-980c-8f4d16cb1d47', 'Васильев', 'Елена', 'Максимовна', '12220006', 'Исследование методов защиты персональных данных в распределенных системах', 'Волкова О.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('05ddcc00-69cb-485c-8d63-a09ed24acda0', 'Попов', 'Артем', 'Артемович', '12220007', 'Автоматизация тестирования программного обеспечения с применением DevOps-практик', 'Лебедев А.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('31343b2f-2399-4fc5-9e75-343c9e18ac86', 'Новиков', 'Ирина', 'Алексеевна', '12220008', 'Разработка веб-приложения для автоматизации учета итогов аттестации обучающихся', 'Соколова М.Д.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('273bfdf2-4378-4da9-b578-ea1489872f49', 'Морозов', 'Андрей', 'Андреевич', '12220009', 'Прогнозирование спроса на товары с использованием методов машинного обучения', 'Павлов П.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c6b57e2f-7d12-416f-8f00-0f7865f64eaa', 'Волков', 'Виктория', 'Сергеевна', '12220010', 'Информационная система мониторинга состояния оборудования предприятия', 'Семенов И.Г.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('561cf814-1a46-4d60-bffa-b2af8c2841fe', 'Лебедев', 'Никита', 'Никитич', '12220011', 'Разработка платформы для онлайн-обучения с элементами геймификации', 'Голубев К.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('d0481489-8d6e-4c5f-a153-f96b23ecbf53', 'Соколов', 'Ксения', 'Михайловна', '12220012', 'Анализ эффективности алгоритмов шифрования в облачных вычислениях', 'Виноградов С.Н.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('ae48bc94-d1e8-4af7-b706-9355554bde48', 'Павлов', 'Денис', 'Денисович', '12220013', 'Создание рекомендательной системы для электронной коммерции', 'Богданов Л.М.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('6aaba497-43c0-4a32-8995-a92bcbb36cfb', 'Семенов', 'Полина', 'Евгеньевна', '12220014', 'Разработка модуля интеграции CRM-системы с мессенджерами', 'Федоров А.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c830dfe4-b55c-4585-b667-c3dad85b7870', 'Голубев', 'Константин', 'Константинович', '12220015', 'Исследование методов компьютерного зрения для распознавания дефектов продукции', 'Михайлов Р.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('d5ea0f73-8e61-4f62-baff-ed9228d95f48', 'Виноградов', 'Анастасия', 'Владимировна', '12220016', 'Автоматизация бухгалтерского учета в малом бизнесе', 'Пономарев Д.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('8344e560-3474-44d5-a66f-3c1bfbdc772e', 'Богданов', 'Павел', 'Павлович', '12220017', 'Разработка системы управления проектами с использованием Kanban', 'Андреев С.О.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('94afe747-1554-4f6f-a122-765a945f7fa4', 'Федоров', 'Александра', 'Олеговна', '12220018', 'Анализ поведения пользователей мобильных приложений с помощью big data', 'Макаров В.К.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('6f5e74b9-e795-4f70-a300-304914c82a17', 'Михайлов', 'Игорь', 'Игоревич', '12220019', 'Создание голосового помощника для управления умным домом', 'Николаев Г.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c4a1bea0-33ed-46c8-9f93-521748ef4a27', 'Пономарев', 'Алина', 'Антоновна', '12220020', 'Разработка системы электронного голосования на базе блокчейн', 'Орлов Б.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('e05a2d35-018b-4489-b5c0-ac8b3de71d95', 'Андреев', 'Николай', 'Николаевич', '12220021', 'Информационная система управления персоналом для кадрового агентства', 'Иванов И.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('e7fee3dc-bc23-4675-a62e-6f792deaa4f3', 'Макаров', 'София', 'Григорьевна', '12220022', 'Анализ безопасности API веб-приложений методами пентестинга', 'Петрова А.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('cf0ab789-5194-40a8-ae2a-04e8710ac4b4', 'Николаев', 'Степан', 'Степанович', '12220023', 'Разработка платформы для проведения вебинаров и онлайн-конференций', 'Сидоров В.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c1a3c476-ba72-4481-9ba0-8c18669e3129', 'Орлов', 'Людмила', 'Васильевна', '12220024', 'Исследование применения нейронных сетей в финансовом прогнозировании', 'Кузнецова Е.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('7bbf3502-a87e-4bb6-9145-55e6b46b5a2d', 'Захаров', 'Роман', 'Романович', '12220025', 'Создание системы учета и контроля доступа на предприятии', 'Морозов Д.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('d23925dd-2d99-423f-af99-81f56e24a1c4', 'Шестаков', 'Надежда', 'Тимуровна', '12220026', 'Разработка информационной системы управления складом на основе микросервисной архитектуры', 'Волкова О.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('a5193610-fa82-4f0a-a0c6-e5fa03ea0f07', 'Дмитриев', 'Платон', 'Платонович', '12220027', 'Мобильное приложение для автоматизации процессов доставки с использованием GPS-трекинга', 'Лебедев А.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('a2cb33b4-799b-4326-9c61-0e9a81bb20e6', 'Егоров', 'Оксана', 'Глебовна', '12220028', 'Анализ и оптимизация баз данных высоконагруженных информационных систем', 'Соколова М.Д.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('18610b67-4ffc-44ad-9fd8-434f74f3bfd0', 'Осипов', 'Захар', 'Захарович', '12220029', 'Разработка чат-бота для технической поддержки на базе машинного обучения', 'Павлов П.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('f9bfd90d-9c3e-4a0b-b3d0-c2222b809bac', 'Калинин', 'Жанна', 'Марковна', '12220030', 'Система электронного документооборота для образовательных учреждений', 'Семенов И.Г.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('333d0cdf-21b2-4cbe-9db4-f6ce5250282d', 'Беляев', 'Тихон', 'Иванович', '12220031', 'Исследование методов защиты персональных данных в распределенных системах', 'Голубев К.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('972a67be-52a0-45ed-b238-357f59b01ed1', 'Гусев', 'Лилия', 'Петровна', '12220032', 'Автоматизация тестирования программного обеспечения с применением DevOps-практик', 'Виноградов С.Н.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('10fd2b75-b163-4357-8839-d3ddaa46f8de', 'Константинов', 'Матвей', 'Викторович', '12220033', 'Разработка веб-приложения для автоматизации учета итогов аттестации обучающихся', 'Богданов Л.М.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('6f7a7bd7-77c5-41cb-a7f8-072797681a3e', 'Тихонов', 'Валентина', 'Дмитриевна', '12220034', 'Прогнозирование спроса на товары с использованием методов машинного обучения', 'Федоров А.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('20233404-70db-4332-9940-380b816f703a', 'Карпов', 'Ярослав', 'Александрович', '12220035', 'Информационная система мониторинга состояния оборудования предприятия', 'Михайлов Р.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('dd8685ea-91b3-436f-91a1-40f6e76ae4b1', 'Афанасьев', 'Зинаида', 'Максимовна', '12220036', 'Разработка платформы для онлайн-обучения с элементами геймификации', 'Пономарев Д.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('444d2b2a-cc78-4697-978e-12b028cff779', 'Мельников', 'Савелий', 'Артемович', '12220037', 'Анализ эффективности алгоритмов шифрования в облачных вычислениях', 'Андреев С.О.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('fede85ce-9de7-4ce7-8b25-6a20b43cbb61', 'Тарасов', 'Прасковья', 'Алексеевна', '12220038', 'Создание рекомендательной системы для электронной коммерции', 'Макаров В.К.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('1aafd2f1-18d9-424c-a9a9-97bc0ac3aff8', 'Медведев', 'Георгий', 'Андреевич', '12220039', 'Разработка модуля интеграции CRM-системы с мессенджерами', 'Николаев Г.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c71052f6-a71e-408b-8cd7-843e30c4ae43', 'Киселев', 'Матрена', 'Сергеевна', '12220040', 'Исследование методов компьютерного зрения для распознавания дефектов продукции', 'Орлов Б.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('65dfd9f0-ab46-4d32-831d-a547f7f589d7', 'Майоров', 'Иван', 'Никитич', '12220041', 'Автоматизация бухгалтерского учета в малом бизнесе', 'Иванов И.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('f425ec67-4d82-4b77-b72b-b788eb89914e', 'Данилов', 'Екатерина', 'Михайловна', '12220042', 'Разработка системы управления проектами с использованием Kanban', 'Петрова А.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('a8189c4b-8f4a-4e10-bf7b-659956927f9b', 'Сазонов', 'Виктор', 'Денисович', '12220043', 'Анализ поведения пользователей мобильных приложений с помощью big data', 'Сидоров В.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('fe270bc7-87dd-44b7-80df-7473d9ea0e66', 'Яковлев', 'Мария', 'Евгеньевна', '12220044', 'Создание голосового помощника для управления умным домом', 'Кузнецова Е.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('516d7467-74fc-4120-b327-96cb3f48bb1a', 'Романов', 'Александр', 'Константинович', '12220045', 'Разработка системы электронного голосования на базе блокчейн', 'Морозов Д.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('3e27d902-745a-497c-8df7-fc3a9b7f7b3f', 'Мартынов', 'Елена', 'Владимировна', '12220046', 'Информационная система управления персоналом для кадрового агентства', 'Волкова О.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('36fe62d0-9321-4d9b-bba7-3923f8826a87', 'Фомин', 'Артем', 'Павлович', '12220047', 'Анализ безопасности API веб-приложений методами пентестинга', 'Лебедев А.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('1ffa1fec-4872-4eb7-8aee-06a1c6c633b0', 'Комаров', 'Ирина', 'Олеговна', '12220048', 'Разработка платформы для проведения вебинаров и онлайн-конференций', 'Соколова М.Д.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c31dc5f8-e799-4cda-9b11-e802e9f05dec', 'Давыдов', 'Андрей', 'Игоревич', '12220049', 'Исследование применения нейронных сетей в финансовом прогнозировании', 'Павлов П.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('8b65a163-0ad1-40a7-91ce-38e2d4c2666c', 'Быков', 'Виктория', 'Антоновна', '12220050', 'Создание системы учета и контроля доступа на предприятии', 'Семенов И.Г.', (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('271bf325-ec1e-4fa2-bb94-a1fa702bc9b2', 'Герасимов', 'Никита', 'Николаевич', '12220051', 'Разработка информационной системы управления складом на основе микросервисной архитектуры', 'Голубев К.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('33f4cbc0-deb9-4da7-ab23-42ec48d6a53d', 'Марков', 'Ксения', 'Григорьевна', '12220052', 'Мобильное приложение для автоматизации процессов доставки с использованием GPS-трекинга', 'Виноградов С.Н.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('83f1ecfb-fd97-43d3-822c-870dd9d5672d', 'Шаров', 'Денис', 'Степанович', '12220053', 'Анализ и оптимизация баз данных высоконагруженных информационных систем', 'Богданов Л.М.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('27ebb84b-a61c-4887-898e-b12f3ee53a52', 'Антонов', 'Полина', 'Васильевна', '12220054', 'Разработка чат-бота для технической поддержки на базе машинного обучения', 'Федоров А.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('f978e59e-2395-429e-bb2f-a50f89a9cd0f', 'Борисов', 'Константин', 'Романович', '12220055', 'Система электронного документооборота для образовательных учреждений', 'Михайлов Р.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c999191e-3b03-4f7e-965f-b6ed47fd8c2c', 'Власов', 'Анастасия', 'Тимуровна', '12220056', 'Исследование методов защиты персональных данных в распределенных системах', 'Пономарев Д.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('3eabd44f-4fe2-4da4-a6f6-21f644e1c523', 'Григорьев', 'Павел', 'Платонович', '12220057', 'Автоматизация тестирования программного обеспечения с применением DevOps-практик', 'Андреев С.О.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('a236ce09-3c20-484a-8176-b79c0e95f703', 'Зуев', 'Александра', 'Глебовна', '12220058', 'Разработка веб-приложения для автоматизации учета итогов аттестации обучающихся', 'Макаров В.К.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('50b2c04b-b093-4342-a382-fe4d02ab0cb9', 'Ильин', 'Игорь', 'Захарович', '12220059', 'Прогнозирование спроса на товары с использованием методов машинного обучения', 'Николаев Г.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('08e98b54-a832-4ecd-babb-c20116301b08', 'Кириллов', 'Алина', 'Марковна', '12220060', 'Информационная система мониторинга состояния оборудования предприятия', 'Орлов Б.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('b265f242-5f6b-4cb5-a1e2-1c4998892597', 'Козлов', 'Николай', 'Иванович', '12220061', 'Разработка платформы для онлайн-обучения с элементами геймификации', 'Иванов И.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('320b8567-aafb-48ed-a70b-96b1b6f10dcc', 'Крылов', 'София', 'Петровна', '12220062', 'Анализ эффективности алгоритмов шифрования в облачных вычислениях', 'Петрова А.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('cca8e862-05e9-479d-9ab0-e0cb2c90acb2', 'Лазарев', 'Степан', 'Викторович', '12220063', 'Создание рекомендательной системы для электронной коммерции', 'Сидоров В.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('37f46ca5-5a9a-492d-b9b7-566e7dc10c2d', 'Лукашев', 'Людмила', 'Дмитриевна', '12220064', 'Разработка модуля интеграции CRM-системы с мессенджерами', 'Кузнецова Е.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('3b7c8e26-9b9d-41cd-bcff-e86e7663a9e7', 'Матвеев', 'Роман', 'Александрович', '12220065', 'Исследование методов компьютерного зрения для распознавания дефектов продукции', 'Морозов Д.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c89a59c2-b860-4633-8d9f-66c4e6a444b9', 'Назаров', 'Надежда', 'Максимовна', '12220066', 'Автоматизация бухгалтерского учета в малом бизнесе', 'Волкова О.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('839d2a0c-a2e5-4840-a782-f7d87a86f33f', 'Некрасов', 'Платон', 'Артемович', '12220067', 'Разработка системы управления проектами с использованием Kanban', 'Лебедев А.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('a3d794a8-ca46-4ef2-b50f-2658ad5ef031', 'Поляков', 'Оксана', 'Алексеевна', '12220068', 'Анализ поведения пользователей мобильных приложений с помощью big data', 'Соколова М.Д.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('7582f66d-8c49-42d4-bbb8-59d1723eb142', 'Родионов', 'Захар', 'Андреевич', '12220069', 'Создание голосового помощника для управления умным домом', 'Павлов П.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2f96fedc-c8b0-41ef-a297-6aa66770e125', 'Савельев', 'Жанна', 'Сергеевна', '12220070', 'Разработка системы электронного голосования на базе блокчейн', 'Семенов И.Г.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('dcd4007b-12b9-4bad-a226-9298def028b2', 'Соловьев', 'Тихон', 'Никитич', '12220071', 'Информационная система управления персоналом для кадрового агентства', 'Голубев К.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('1b96c536-3a90-46f3-859f-76d2279c2d7e', 'Степанов', 'Лилия', 'Михайловна', '12220072', 'Анализ безопасности API веб-приложений методами пентестинга', 'Виноградов С.Н.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('8095122c-f9b8-4e4f-be4d-8c43715d948c', 'Тимофеев', 'Матвей', 'Денисович', '12220073', 'Разработка платформы для проведения вебинаров и онлайн-конференций', 'Богданов Л.М.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('881d3a0d-318c-4c54-9dce-d4feaf19bd60', 'Титов', 'Валентина', 'Евгеньевна', '12220074', 'Исследование применения нейронных сетей в финансовом прогнозировании', 'Федоров А.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('e587fde0-183b-44f0-ba98-f7bb8d250b18', 'Ушаков', 'Ярослав', 'Константинович', '12220075', 'Создание системы учета и контроля доступа на предприятии', 'Михайлов Р.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('92768edf-e5a0-4620-bac8-0799eda457a0', 'Фролов', 'Зинаида', 'Владимировна', '12220076', 'Разработка информационной системы управления складом на основе микросервисной архитектуры', 'Пономарев Д.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('b6b43e8d-2799-42d9-bab7-416391608f52', 'Чернов', 'Савелий', 'Павлович', '12220077', 'Мобильное приложение для автоматизации процессов доставки с использованием GPS-трекинга', 'Андреев С.О.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('e83e8248-9fdb-42a8-a3aa-3a48c8b0ac60', 'Шилов', 'Прасковья', 'Олеговна', '12220078', 'Анализ и оптимизация баз данных высоконагруженных информационных систем', 'Макаров В.К.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('7ebec3ff-793f-472d-8b68-45673be068d6', 'Щербаков', 'Георгий', 'Игоревич', '12220079', 'Разработка чат-бота для технической поддержки на базе машинного обучения', 'Николаев Г.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('1fb4e124-1137-443e-8190-71055cb9879b', 'Юдин', 'Матрена', 'Антоновна', '12220080', 'Система электронного документооборота для образовательных учреждений', 'Орлов Б.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('5f410f61-b667-4360-a542-8ebcf09cdb53', 'Алексеев', 'Иван', 'Николаевич', '12220081', 'Исследование методов защиты персональных данных в распределенных системах', 'Иванов И.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('11e8499a-fe36-44f1-bab0-7a4c963e3dbb', 'Баранов', 'Екатерина', 'Григорьевна', '12220082', 'Автоматизация тестирования программного обеспечения с применением DevOps-практик', 'Петрова А.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('6ca45f39-9c10-4b89-bb80-9cb87f73264d', 'Воронов', 'Виктор', 'Степанович', '12220083', 'Разработка веб-приложения для автоматизации учета итогов аттестации обучающихся', 'Сидоров В.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('74db6e50-98f4-42d9-8264-f760b5d91155', 'Гаврилов', 'Мария', 'Васильевна', '12220084', 'Прогнозирование спроса на товары с использованием методов машинного обучения', 'Кузнецова Е.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('dae2f198-462c-49a7-b7bd-17b154517068', 'Дорофеев', 'Александр', 'Романович', '12220085', 'Информационная система мониторинга состояния оборудования предприятия', 'Морозов Д.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('4e87606f-ab0a-450d-b383-70ce607ddec6', 'Емельянов', 'Елена', 'Тимуровна', '12220086', 'Разработка платформы для онлайн-обучения с элементами геймификации', 'Волкова О.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('1e3240e7-1df9-4e39-abe4-f8f01a3e71ab', 'Жуков', 'Артем', 'Платонович', '12220087', 'Анализ эффективности алгоритмов шифрования в облачных вычислениях', 'Лебедев А.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('0022f6d6-40ec-4265-a039-46e5649f21af', 'Зайцев', 'Ирина', 'Глебовна', '12220088', 'Создание рекомендательной системы для электронной коммерции', 'Соколова М.Д.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('d6054f7d-2821-4479-a194-36fe7688ab63', 'Исаев', 'Андрей', 'Захарович', '12220089', 'Разработка модуля интеграции CRM-системы с мессенджерами', 'Павлов П.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('675359f3-f254-4d3b-a952-b8c6522ddec9', 'Колесников', 'Виктория', 'Марковна', '12220090', 'Исследование методов компьютерного зрения для распознавания дефектов продукции', 'Семенов И.Г.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('a0e31f4f-89f9-4793-88ef-c5d0c9c86835', 'Иванов', 'Никита', 'Иванович', '12220091', 'Автоматизация бухгалтерского учета в малом бизнесе', 'Голубев К.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('e2fbcb2b-2025-4d6c-b5b2-45e96623c480', 'Петров', 'Ксения', 'Петровна', '12220092', 'Разработка системы управления проектами с использованием Kanban', 'Виноградов С.Н.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2d89116f-ba0e-4649-8db6-7baf1c428f1f', 'Сидоров', 'Денис', 'Викторович', '12220093', 'Анализ поведения пользователей мобильных приложений с помощью big data', 'Богданов Л.М.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('8a719d0f-5a18-4a42-a3d4-5fcd042877fc', 'Кузнецов', 'Полина', 'Дмитриевна', '12220094', 'Создание голосового помощника для управления умным домом', 'Федоров А.П.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('70923699-4a54-46a7-86f2-971e795aebda', 'Смирнов', 'Константин', 'Александрович', '12220095', 'Разработка системы электронного голосования на базе блокчейн', 'Михайлов Р.В.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('d88fd8d5-a8f6-418b-9310-ddf34fe28fa9', 'Васильев', 'Анастасия', 'Максимовна', '12220096', 'Информационная система управления персоналом для кадрового агентства', 'Пономарев Д.И.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2dda1836-38a5-46b7-9f64-71cc3dfeadfb', 'Попов', 'Павел', 'Артемович', '12220097', 'Анализ безопасности API веб-приложений методами пентестинга', 'Андреев С.О.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('80e80d68-c60d-4f9b-9f62-d4112350cad4', 'Новиков', 'Александра', 'Алексеевна', '12220098', 'Разработка платформы для проведения вебинаров и онлайн-конференций', 'Макаров В.К.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('269d8153-c03d-4d69-8528-02714a34cf17', 'Морозов', 'Игорь', 'Андреевич', '12220099', 'Исследование применения нейронных сетей в финансовом прогнозировании', 'Николаев Г.А.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('1e872b4c-ab8a-433f-9ce0-e682359e2ad9', 'Волков', 'Алина', 'Сергеевна', '12220100', 'Создание системы учета и контроля доступа на предприятии', 'Орлов Б.С.', (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ02-22-4'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('59b62774-d49b-4ca1-9c7a-e2d2e179f6a7', 'Лебедев', 'Николай', 'Никитич', '12220101', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('56dd4915-2365-4c93-87d6-9cb9c4b76529', 'Соколов', 'София', 'Михайловна', '12220102', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('39a99707-163a-4825-87f2-82e00fe39c2b', 'Павлов', 'Степан', 'Денисович', '12220103', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('d607885f-c2c9-4be9-9d95-60e181222210', 'Семенов', 'Людмила', 'Евгеньевна', '12220104', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('7fc0e045-f0c0-48ea-b369-883ea1ffb883', 'Голубев', 'Роман', 'Константинович', '12220105', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('d24201bf-0fb5-4859-be53-ba622733b2a5', 'Виноградов', 'Надежда', 'Владимировна', '12220106', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('6deaca7a-7a34-4878-bbf1-bd8bbe3899cb', 'Богданов', 'Платон', 'Павлович', '12220107', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('4bfd3910-c40a-43e0-bfaf-964578eb9c68', 'Федоров', 'Оксана', 'Олеговна', '12220108', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2148a95e-06e4-43a9-acb4-ddea84b6369e', 'Михайлов', 'Захар', 'Игоревич', '12220109', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('e70fc9a8-dcd0-464f-8750-339c357f1601', 'Пономарев', 'Жанна', 'Антоновна', '12220110', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('0c16d962-1bd8-4ea9-9a18-f7c60d87548b', 'Андреев', 'Тихон', 'Николаевич', '12220111', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('608835ea-d40a-43dd-96dc-e8d89e1e9a1d', 'Макаров', 'Лилия', 'Григорьевна', '12220112', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2ab7e553-c282-4c15-9cb6-902d68a833bd', 'Николаев', 'Матвей', 'Степанович', '12220113', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('eae82207-51b6-4ee7-85ef-13e8714e3ef9', 'Орлов', 'Валентина', 'Васильевна', '12220114', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('bff53dab-9901-47e8-a307-7b8a2a211006', 'Захаров', 'Ярослав', 'Романович', '12220115', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('99c9c1fd-9772-4ba3-8ea4-b0ef36d5e461', 'Шестаков', 'Зинаида', 'Тимуровна', '12220116', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2f88acbc-f5e6-4714-907c-aea5c11e6285', 'Дмитриев', 'Савелий', 'Платонович', '12220117', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('aeb8867d-1e8b-409b-97f0-6985620655e3', 'Егоров', 'Прасковья', 'Глебовна', '12220118', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('6c67133b-504f-4d4c-8517-bff57afc6c88', 'Осипов', 'Георгий', 'Захарович', '12220119', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('808532f5-cafa-46de-b299-ece8b01ae0fb', 'Калинин', 'Матрена', 'Марковна', '12220120', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('906dd23a-2f72-46b9-bc19-5d116f35d547', 'Беляев', 'Иван', 'Иванович', '12220121', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('ed9ff8d1-db49-49b7-b379-f541f295bff9', 'Гусев', 'Екатерина', 'Петровна', '12220122', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('26010137-ebd7-4c30-8681-aad77f733271', 'Константинов', 'Виктор', 'Викторович', '12220123', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('6811122e-57d0-480f-9219-3a16dc033550', 'Тихонов', 'Мария', 'Дмитриевна', '12220124', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('b2be99e6-dfff-4205-9ec1-10166982519b', 'Карпов', 'Александр', 'Александрович', '12220125', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c88e6729-d24f-4d6f-8f2e-786c84f96014', 'Афанасьев', 'Елена', 'Максимовна', '12220126', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('3ad648a5-4fb3-4678-8425-ae9346b17bd8', 'Мельников', 'Артем', 'Артемович', '12220127', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('30e5f70f-023b-4509-9263-31629826f84a', 'Тарасов', 'Ирина', 'Алексеевна', '12220128', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('b87491de-1e4a-4598-a173-529f84ae4744', 'Медведев', 'Андрей', 'Андреевич', '12220129', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('8101fec2-fe85-43fa-a318-56fa32355f63', 'Киселев', 'Виктория', 'Сергеевна', '12220130', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c6f213ef-dd0e-4661-87f4-d8059e29bdaa', 'Майоров', 'Никита', 'Никитич', '12220131', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('8f3ace64-92f8-4c12-9850-4c3ead78f67a', 'Данилов', 'Ксения', 'Михайловна', '12220132', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('0e88260c-b876-4389-a65f-e4c00f44c948', 'Сазонов', 'Денис', 'Денисович', '12220133', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('6c5b6a83-ba9c-43b9-b9a3-88dcb6e9cea3', 'Яковлев', 'Полина', 'Евгеньевна', '12220134', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('ff88913c-ba9b-4dc9-9c38-4ee5c7520dfa', 'Романов', 'Константин', 'Константинович', '12220135', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('afb8d580-6471-4dca-981c-addc08fd480b', 'Мартынов', 'Анастасия', 'Владимировна', '12220136', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('b5bbf637-5f58-4c56-8d2e-3b49ceb6235c', 'Фомин', 'Павел', 'Павлович', '12220137', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('49eb6792-9b9a-4e9a-a513-3418844709de', 'Комаров', 'Александра', 'Олеговна', '12220138', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('a98065f6-15fa-438c-8dfc-03570ef472fa', 'Давыдов', 'Игорь', 'Игоревич', '12220139', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('71bbfc0d-6398-4fa7-b732-ee07faec094c', 'Быков', 'Алина', 'Антоновна', '12220140', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('492b9c50-829d-4795-9e90-7a364e8f2cfc', 'Герасимов', 'Николай', 'Николаевич', '12220141', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('9073d062-3f2e-4b2d-b031-1afddb02254d', 'Марков', 'София', 'Григорьевна', '12220142', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('08a02cc1-b4a4-465a-ae27-b644ff8c5c5c', 'Шаров', 'Степан', 'Степанович', '12220143', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('dd704c38-5cee-445e-8435-2d7430930fcb', 'Антонов', 'Людмила', 'Васильевна', '12220144', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('449864d8-e42a-4a0f-b62f-7884f98af92b', 'Борисов', 'Роман', 'Романович', '12220145', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('696122b4-ad2d-4668-bc60-895e09117a0b', 'Власов', 'Надежда', 'Тимуровна', '12220146', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('25a306ee-8c92-4549-b1ed-845c368b0e23', 'Григорьев', 'Платон', 'Платонович', '12220147', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('57224548-3a34-49ac-a11b-73a6382989b2', 'Зуев', 'Оксана', 'Глебовна', '12220148', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('240fba2a-5157-4ea1-b185-d67d84f55948', 'Ильин', 'Захар', 'Захарович', '12220149', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('f66ea13e-19d4-44ca-adf5-24b588b90314', 'Кириллов', 'Жанна', 'Марковна', '12220150', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-23-3'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('7a7c962b-b6f9-4c89-ad12-08521acf48f1', 'Козлов', 'Тихон', 'Иванович', '12220151', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('26be090a-d497-465b-950b-83d22cb97058', 'Крылов', 'Лилия', 'Петровна', '12220152', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('feb43f35-19ce-4605-91b4-e6774cfca489', 'Лазарев', 'Матвей', 'Викторович', '12220153', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('0be5d0cb-0970-4ad1-8fe8-c3a36a834535', 'Лукашев', 'Валентина', 'Дмитриевна', '12220154', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('59750006-01ca-41b1-8ad9-d1ed59079f0d', 'Матвеев', 'Ярослав', 'Александрович', '12220155', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('48b37d28-0f7a-4703-a1bf-c88911185225', 'Назаров', 'Зинаида', 'Максимовна', '12220156', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('8a9c4aff-2c27-4d71-a6b5-dde7cd3314eb', 'Некрасов', 'Савелий', 'Артемович', '12220157', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2181ae70-7c1d-4a3a-bbb3-3e3fbb6ec3f7', 'Поляков', 'Прасковья', 'Алексеевна', '12220158', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('af4c5528-d55c-40da-8bfd-0995be2af70c', 'Родионов', 'Георгий', 'Андреевич', '12220159', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('8fde91f5-0034-4388-a881-eb64f828216e', 'Савельев', 'Матрена', 'Сергеевна', '12220160', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('c38123a5-60f7-4e42-a4f8-4bf1f1fea473', 'Соловьев', 'Иван', 'Никитич', '12220161', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('1983caec-f223-4316-b930-647275935532', 'Степанов', 'Екатерина', 'Михайловна', '12220162', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2f550523-d759-4e49-a8f3-1438871ddd59', 'Тимофеев', 'Виктор', 'Денисович', '12220163', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('13f2d6d4-1f5b-4b65-87b4-04f3ed6d6673', 'Титов', 'Мария', 'Евгеньевна', '12220164', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2f0e484a-526c-4b23-81f5-c5f505179d03', 'Ушаков', 'Александр', 'Константинович', '12220165', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('a233fd84-09af-473c-9713-0a9c2050055c', 'Фролов', 'Елена', 'Владимировна', '12220166', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('e07d3c50-40f0-4151-861b-16c8b631f450', 'Чернов', 'Артем', 'Павлович', '12220167', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('38f04663-5fe8-4157-bf58-88577a385128', 'Шилов', 'Ирина', 'Олеговна', '12220168', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('147cade9-bbdb-47f4-a39b-edc8f3c6055b', 'Щербаков', 'Андрей', 'Игоревич', '12220169', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('8a4addb7-13c9-4a64-83a3-8554b965d13d', 'Юдин', 'Виктория', 'Антоновна', '12220170', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('dba7f567-72bc-4543-86cb-b545dd3bef13', 'Алексеев', 'Никита', 'Николаевич', '12220171', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('11148ae3-dc00-4bf6-941b-443cc4983f17', 'Баранов', 'Ксения', 'Григорьевна', '12220172', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('473ede62-9fa2-4751-ad8c-8f8ce8762e6a', 'Воронов', 'Денис', 'Степанович', '12220173', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('215cf169-4a8f-42cd-994a-7b68599b64d2', 'Гаврилов', 'Полина', 'Васильевна', '12220174', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('48b329ac-03f9-4553-9841-7d6c6bb91cd5', 'Дорофеев', 'Константин', 'Романович', '12220175', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-ПИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('337e9e85-2f1f-478c-8044-3904bf85ae20', 'Емельянов', 'Анастасия', 'Тимуровна', '12220176', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('617a0ae8-b597-4b95-9b60-217376a61109', 'Жуков', 'Павел', 'Платонович', '12220177', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('0a4af240-d0da-4c79-8423-9ed9d0f4e41d', 'Зайцев', 'Александра', 'Глебовна', '12220178', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('69439843-6486-4fa5-8dd3-dfed569b8bbb', 'Исаев', 'Игорь', 'Захарович', '12220179', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('0aec92d4-0a2d-495c-99bc-0ea0a2dbf6a5', 'Колесников', 'Алина', 'Марковна', '12220180', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('84dce544-27e5-4f15-9be2-90426cded00e', 'Иванов', 'Николай', 'Иванович', '12220181', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('b4769336-dc43-42a1-8ef7-c65913adfca6', 'Петров', 'София', 'Петровна', '12220182', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('ea3386fd-079a-4159-b3b7-fe130b0827fc', 'Сидоров', 'Степан', 'Викторович', '12220183', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('ff134e09-9917-4775-b424-7ad90e105173', 'Кузнецов', 'Людмила', 'Дмитриевна', '12220184', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('9ef18377-58cf-4bd0-b696-036c1dc31094', 'Смирнов', 'Роман', 'Александрович', '12220185', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('26a7767f-0eda-4ce5-8162-ccf8cfbeecb3', 'Васильев', 'Надежда', 'Максимовна', '12220186', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('f0cf2cdc-f034-4fc0-aecf-34c46579a0c0', 'Попов', 'Платон', 'Артемович', '12220187', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('50b3f03e-f84f-439b-9f7a-6bf8875881d4', 'Новиков', 'Оксана', 'Алексеевна', '12220188', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('3e05f13a-7619-4837-a6b9-7142e9a29518', 'Морозов', 'Захар', 'Андреевич', '12220189', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('92557bc6-3f3c-457c-b4fa-56c1ff710c85', 'Волков', 'Жанна', 'Сергеевна', '12220190', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('2720b9e1-327b-4ddf-a228-95e962f2bedd', 'Лебедев', 'Тихон', 'Никитич', '12220191', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('14ae7e8e-c6c9-4849-bc54-834213e26ba0', 'Соколов', 'Лилия', 'Михайловна', '12220192', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('53e83814-1137-42f7-938d-40e676216eee', 'Павлов', 'Матвей', 'Денисович', '12220193', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('1cc40fc9-7701-4ad4-8b0f-2f65e82bde70', 'Семенов', 'Валентина', 'Евгеньевна', '12220194', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('cc0a410b-a2b6-49e5-ace3-f4058655220e', 'Голубев', 'Ярослав', 'Константинович', '12220195', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('e66e77fb-4fe4-4a7a-a427-4c64618bbebc', 'Виноградов', 'Зинаида', 'Владимировна', '12220196', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('0cf4fba3-d752-4de7-bc05-ac9f81e178ae', 'Богданов', 'Савелий', 'Павлович', '12220197', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('5c33039d-d649-4913-90ce-37ad7feb9cad', 'Федоров', 'Прасковья', 'Олеговна', '12220198', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('db361592-d9c7-4d2a-9f40-b664865987e3', 'Михайлов', 'Георгий', 'Игоревич', '12220199', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());
INSERT INTO student (id, last_name, first_name, middle_name, record_book_number, thesis_topic, supervisor_name, group_id, created_at, updated_at) VALUES
('a609a3fc-2f38-4c40-9bf1-3e4afa20e83b', 'Пономарев', 'Матрена', 'Антоновна', '12220200', NULL, NULL, (SELECT id FROM study_group WHERE name = 'ОУИТб-БИ01-25-1'), NOW(), NOW());

-- ------------------------------------------
-- 4. Допуски к ГИА (100 записей, 90% admitted)
-- ------------------------------------------
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('92252e7a-d299-4273-a5d5-f836941af56b', (SELECT id FROM student WHERE record_book_number = '12220001'), 70, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('a5c6583e-be1d-4351-84ef-1476fb209f2c', (SELECT id FROM student WHERE record_book_number = '12220002'), 71, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('11f25d9c-7c53-45eb-a8f4-a31d8cdb2abd', (SELECT id FROM student WHERE record_book_number = '12220003'), 72, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('5982a91d-1d45-4f40-9238-acb52fd4229a', (SELECT id FROM student WHERE record_book_number = '12220004'), 73, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('cd8a6c5d-383b-47f9-b6ea-83f93805daa2', (SELECT id FROM student WHERE record_book_number = '12220005'), 74, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('349d8509-e7fb-41e0-bca1-2fe164323d0d', (SELECT id FROM student WHERE record_book_number = '12220006'), 75, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('d2abf07e-421e-4d2b-9a87-76d9901c262f', (SELECT id FROM student WHERE record_book_number = '12220007'), 76, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('cdc0c215-0b3b-4e7f-af0b-c65ed41e0df4', (SELECT id FROM student WHERE record_book_number = '12220008'), 77, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('e255d4a5-d514-49db-8e1c-fe669d8363ad', (SELECT id FROM student WHERE record_book_number = '12220009'), 78, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('cfa1988c-e983-4a8f-9b48-be372dbb2235', (SELECT id FROM student WHERE record_book_number = '12220010'), 79, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('9b5b0ff1-5963-47f3-a2e7-c2aa8cfc96ed', (SELECT id FROM student WHERE record_book_number = '12220011'), 80, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('14715bd7-8a5a-4639-b47e-f8de5456aeb1', (SELECT id FROM student WHERE record_book_number = '12220012'), 81, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('2dffaf73-ac88-4cd9-85e2-ce47e989e5cd', (SELECT id FROM student WHERE record_book_number = '12220013'), 82, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('c189daab-ffcb-4489-8a2b-471a693f5eb7', (SELECT id FROM student WHERE record_book_number = '12220014'), 83, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('69a92bd7-5b66-47db-959b-8a166d172407', (SELECT id FROM student WHERE record_book_number = '12220015'), 84, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('83d621af-dd35-40ff-b86b-d02fa6c7d240', (SELECT id FROM student WHERE record_book_number = '12220016'), 85, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('ad0a9b28-14d5-49e8-9a0f-8731ff4e7337', (SELECT id FROM student WHERE record_book_number = '12220017'), 86, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('f15dc968-5af0-4fdb-91be-c4bad1c8cb06', (SELECT id FROM student WHERE record_book_number = '12220018'), 87, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('c8c33126-c217-433c-a591-92e188679d4c', (SELECT id FROM student WHERE record_book_number = '12220019'), 88, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('36d6a5ce-e6fa-49ad-b8aa-91546053083a', (SELECT id FROM student WHERE record_book_number = '12220020'), 89, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('166a1f3e-b1a9-47c9-a433-6a17718bbfe6', (SELECT id FROM student WHERE record_book_number = '12220021'), 90, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('e912c22e-b4a7-4035-9c83-1cab83c5f5a1', (SELECT id FROM student WHERE record_book_number = '12220022'), 91, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('2db4149b-2e84-42a2-a5b4-c9cd0ecd1f10', (SELECT id FROM student WHERE record_book_number = '12220023'), 92, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('e86bcdb4-4cac-4b7e-aab1-62db00bbcf85', (SELECT id FROM student WHERE record_book_number = '12220024'), 93, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('4e4851b6-e03b-4742-bc74-058b78d28e64', (SELECT id FROM student WHERE record_book_number = '12220025'), 94, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('4cb8e0bf-d197-47c7-a449-a97c85ffe814', (SELECT id FROM student WHERE record_book_number = '12220026'), 70, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('2b07a5b0-875a-4377-be4f-f67e37edf621', (SELECT id FROM student WHERE record_book_number = '12220027'), 71, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('51587399-9373-4f5d-9ffe-5a04844ba165', (SELECT id FROM student WHERE record_book_number = '12220028'), 72, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('44269f6b-a1b2-426c-bf42-f36eee00c76a', (SELECT id FROM student WHERE record_book_number = '12220029'), 73, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('6137a4f0-2f43-4ea9-b5c2-aaf1461260b9', (SELECT id FROM student WHERE record_book_number = '12220030'), 74, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('f26dd598-2b6e-424d-a3e2-9319fb029baf', (SELECT id FROM student WHERE record_book_number = '12220031'), 75, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('c2864151-4337-4d77-978b-2cbf937c3dde', (SELECT id FROM student WHERE record_book_number = '12220032'), 76, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('8a887d32-c558-42c7-8d92-78f1c5f6e929', (SELECT id FROM student WHERE record_book_number = '12220033'), 77, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('6619c56e-3725-4fcd-9738-20f5b4d91956', (SELECT id FROM student WHERE record_book_number = '12220034'), 78, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('273d21c3-5cad-4728-8115-792761ac29a3', (SELECT id FROM student WHERE record_book_number = '12220035'), 79, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('0ddac137-c69c-4176-a668-81133ffc7271', (SELECT id FROM student WHERE record_book_number = '12220036'), 80, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('daccc0d4-f3c4-4692-8afc-45c9141ab99f', (SELECT id FROM student WHERE record_book_number = '12220037'), 81, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('956b5885-5d09-4432-a216-ced9372be05e', (SELECT id FROM student WHERE record_book_number = '12220038'), 82, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('6986f180-1080-4ff4-a54c-165354019112', (SELECT id FROM student WHERE record_book_number = '12220039'), 83, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('29b71796-47ca-46b4-ab85-d91dfc33e206', (SELECT id FROM student WHERE record_book_number = '12220040'), 84, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('f64fdf9a-5706-4fc7-8141-4d1fee2aff41', (SELECT id FROM student WHERE record_book_number = '12220041'), 85, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('f8f53331-838d-4514-ab7b-5b28733c3e7f', (SELECT id FROM student WHERE record_book_number = '12220042'), 86, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('356c41cb-735e-4203-96b5-6bcac1f549bc', (SELECT id FROM student WHERE record_book_number = '12220043'), 87, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('41e8a821-4dbb-4f15-8918-82a28b3bd286', (SELECT id FROM student WHERE record_book_number = '12220044'), 88, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('968a4c55-96c7-470f-895f-ee3dc124b045', (SELECT id FROM student WHERE record_book_number = '12220045'), 89, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('81fb682b-2825-4aaa-bb05-e306449029cc', (SELECT id FROM student WHERE record_book_number = '12220046'), 90, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('261d030f-ade4-45f0-8836-bab8c6c9374f', (SELECT id FROM student WHERE record_book_number = '12220047'), 91, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('1ac74d83-659f-486d-86da-14b8b5299c49', (SELECT id FROM student WHERE record_book_number = '12220048'), 92, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('6f97ae8c-2228-413f-bf8d-ac0cebe94665', (SELECT id FROM student WHERE record_book_number = '12220049'), 93, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('a63c0706-788a-4e41-a3bf-8abb16546827', (SELECT id FROM student WHERE record_book_number = '12220050'), 94, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('cd3b3ed5-ad2b-4845-b1aa-b559a736bfb1', (SELECT id FROM student WHERE record_book_number = '12220051'), 70, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('0b4b7298-cd20-4e20-a49e-eab1c1244e7c', (SELECT id FROM student WHERE record_book_number = '12220052'), 71, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('d5074453-0ee8-47e0-bc36-0ef1b49c5dfb', (SELECT id FROM student WHERE record_book_number = '12220053'), 72, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('864e9fed-6756-4d6d-bf70-9ecc3757ec73', (SELECT id FROM student WHERE record_book_number = '12220054'), 73, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('27ab274c-d15f-4ece-b0bb-2fc78e603356', (SELECT id FROM student WHERE record_book_number = '12220055'), 74, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('1d0f80ed-aee7-4ab7-8fbd-2ab50b0aafe1', (SELECT id FROM student WHERE record_book_number = '12220056'), 75, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('4585ac6c-5439-49c3-9c6c-997819dcd3e5', (SELECT id FROM student WHERE record_book_number = '12220057'), 76, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('9ccbd76a-f523-4ec2-879b-697d711a6d27', (SELECT id FROM student WHERE record_book_number = '12220058'), 77, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('096158d8-3c6c-43c0-ba88-ac468632328b', (SELECT id FROM student WHERE record_book_number = '12220059'), 78, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('0972c596-2990-4cdf-9bb9-615f5b067a0d', (SELECT id FROM student WHERE record_book_number = '12220060'), 79, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('015075b0-5b31-48fb-9230-648d2413c657', (SELECT id FROM student WHERE record_book_number = '12220061'), 80, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('5dab8a03-1f19-43d6-87e7-13b3cf71fe79', (SELECT id FROM student WHERE record_book_number = '12220062'), 81, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('14a49df5-188d-4ee3-bdfe-f32c9a84fd1c', (SELECT id FROM student WHERE record_book_number = '12220063'), 82, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('bb30f299-1570-49c9-b224-0978c8095fe9', (SELECT id FROM student WHERE record_book_number = '12220064'), 83, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('ca1a9681-1faf-4308-87ea-4c480021474e', (SELECT id FROM student WHERE record_book_number = '12220065'), 84, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('8ea72071-111c-4b15-9899-362a4ee86a70', (SELECT id FROM student WHERE record_book_number = '12220066'), 85, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('51b10bdd-f552-43b8-b588-bb4a1dd89634', (SELECT id FROM student WHERE record_book_number = '12220067'), 86, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('e867fb82-aef8-4ca6-8469-4744f30038fa', (SELECT id FROM student WHERE record_book_number = '12220068'), 87, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('810aa7b2-18ea-44ca-95b1-a7842f40308b', (SELECT id FROM student WHERE record_book_number = '12220069'), 88, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('b163cde9-a3f2-493a-8ed0-d4fa7558d628', (SELECT id FROM student WHERE record_book_number = '12220070'), 89, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('cc2b36da-4a05-491f-8954-7b2131385a92', (SELECT id FROM student WHERE record_book_number = '12220071'), 90, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('fc1a58de-ade8-4916-bd46-84752bf5aa5d', (SELECT id FROM student WHERE record_book_number = '12220072'), 91, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('112a010c-444d-42d5-aa11-1df81025aef3', (SELECT id FROM student WHERE record_book_number = '12220073'), 92, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('10511984-8e21-4668-8e4f-eb7e8c72d75c', (SELECT id FROM student WHERE record_book_number = '12220074'), 93, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('fcf852b2-f944-428b-b949-8e1d402f3b45', (SELECT id FROM student WHERE record_book_number = '12220075'), 94, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('19e2b6f8-221d-499b-85f6-8b331c5c18a9', (SELECT id FROM student WHERE record_book_number = '12220076'), 70, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('4c8c64ee-1ae2-4606-aad1-fa38d00f264b', (SELECT id FROM student WHERE record_book_number = '12220077'), 71, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('6fff7403-4244-4b62-8668-55c9ebe09b38', (SELECT id FROM student WHERE record_book_number = '12220078'), 72, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('8aee2ec3-5459-4efb-9030-ea37601334aa', (SELECT id FROM student WHERE record_book_number = '12220079'), 73, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('b6c97d44-9822-474f-92ca-0e9e294be3a2', (SELECT id FROM student WHERE record_book_number = '12220080'), 74, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('e5faef50-5cad-44b0-abc6-af781a8417a5', (SELECT id FROM student WHERE record_book_number = '12220081'), 75, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('e354a94a-bc64-4c99-a023-f1d1b2f66190', (SELECT id FROM student WHERE record_book_number = '12220082'), 76, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('15e52ec1-b1fc-41ab-a899-cb5b86687954', (SELECT id FROM student WHERE record_book_number = '12220083'), 77, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('49d0c925-2ae0-47dc-a606-19de1e4c9b93', (SELECT id FROM student WHERE record_book_number = '12220084'), 78, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('6b63099d-5d8f-4070-aad0-50f0e7803e7c', (SELECT id FROM student WHERE record_book_number = '12220085'), 79, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('8f199bff-1c91-4f5c-b6f2-9e2b28a90589', (SELECT id FROM student WHERE record_book_number = '12220086'), 80, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('5b6a7a1d-c770-46c4-8029-2920ba63cd0c', (SELECT id FROM student WHERE record_book_number = '12220087'), 81, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('b9b2943b-ed1d-44c3-8365-dfb428bbf310', (SELECT id FROM student WHERE record_book_number = '12220088'), 82, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('6c61c53f-8a44-4630-a307-78adccb73d46', (SELECT id FROM student WHERE record_book_number = '12220089'), 83, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('49a973cf-fe3e-465c-b9bf-4cc591dfa5cf', (SELECT id FROM student WHERE record_book_number = '12220090'), 84, false, true, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('1a6f0b99-f06c-4079-98f7-7358b9937030', (SELECT id FROM student WHERE record_book_number = '12220091'), 45, true, false, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('a289137a-ff15-4531-a082-06d09be376db', (SELECT id FROM student WHERE record_book_number = '12220092'), 46, true, false, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('428659dd-77f3-4f96-9f67-27102fa218da', (SELECT id FROM student WHERE record_book_number = '12220093'), 47, true, false, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('5818795c-f6f2-4e8d-8628-b3a135a42354', (SELECT id FROM student WHERE record_book_number = '12220094'), 48, true, false, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('563d44d9-f17b-4929-add3-2d52132a7368', (SELECT id FROM student WHERE record_book_number = '12220095'), 49, true, false, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('36076baa-8d29-47cb-9f77-bb16a811ac9c', (SELECT id FROM student WHERE record_book_number = '12220096'), 50, true, false, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('ddf46875-60cb-41d5-9fbe-ec5e26740a3f', (SELECT id FROM student WHERE record_book_number = '12220097'), 51, true, false, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('ce6be84f-9b9f-4e4a-b8a1-817e5aedc125', (SELECT id FROM student WHERE record_book_number = '12220098'), 52, true, false, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('65a83e24-77ee-418d-82d0-e35ea641d654', (SELECT id FROM student WHERE record_book_number = '12220099'), 53, true, false, NOW(), NOW(), NOW());
INSERT INTO admission (id, student_id, brs_score, has_debt, is_admitted, checked_at, created_at, updated_at) VALUES
('592f7305-349f-4013-b487-d3e4668e2bf5', (SELECT id FROM student WHERE record_book_number = '12220100'), 54, true, false, NOW(), NOW(), NOW());

-- ------------------------------------------
-- 5. Статистика миграции
-- ------------------------------------------
-- student: 200
-- study_group: 8 (+ существующие)
-- direction: 2 (+ существующие)
-- admission: 100 (+ существующие)
