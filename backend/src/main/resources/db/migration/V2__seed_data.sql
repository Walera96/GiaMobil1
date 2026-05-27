-- ============================================================
-- Миграция V2: Тестовые данные (seed data)
-- ЧОУ ВО СПбУТУИЭ — демонстрационный набор для ВКР
-- Пароли захешированы алгоритмом BCrypt (12 раундов)
-- ============================================================

-- ------------------------------------------
-- 1. Пользователи системы
-- ------------------------------------------
-- admin: admin123
-- methodist: methodist123
-- secretary: secretary123
-- chairman: chairman123
-- member1..4: member123
-- student1: student123
INSERT INTO app_user (id, username, password, email, full_name, role) VALUES
('40bf8054-bb90-4279-903e-e2faf58d1fcd', 'admin', '$2b$12$SloarKA7twXboGOHaytyGu322G4nYXXdELeK5Xs1WvWds5pauPF0W', 'admin@spbutu.ru', 'Администратор Системы', 'ADMIN'),
('4e0b2f3a-fdad-4d1d-9264-6fed29fe6d0c', 'methodist', '$2b$12$9GAkwWsRoyD13kpR44dF7uNLaMK0XCOjXtUpNvAIqBblbR.vFiyde', 'methodist@spbutu.ru', 'Иванова Мария Петровна', 'METHODIST'),
('dc6d0fc2-f60e-49b8-b083-a00fcadb00b5', 'secretary', '$2b$12$rPvTSQV1v7mE/AKG4WK6re1eShpCBYD4BWG.CgXKab3Z0YpwAJ8tK', 'secretary@spbutu.ru', 'Петрова Анна Сергеевна', 'SECRETARY'),
('2026ed35-b953-4f6c-8355-164ed9fde347', 'chairman', '$2b$12$uuJcx25Qizgd2fBurv8j2urWQu1ezTKrg62CnXkP9szthRFvcmZfS', 'chairman@spbutu.ru', 'Сидоров Иван Иванович', 'CHAIRMAN'),
('d683575e-f892-471c-b5f5-57b2fb295128', 'member1', '$2b$12$RICY6vexx8J6Dn9qEeh3MuwVj8ZBTcFjOp3C3lBtG9t4RMrn98KRG', 'member1@spbutu.ru', 'Кузнецов Павел Викторович', 'GEK_MEMBER'),
('e6cffb28-4e60-49f9-9e71-44e021e4071f', 'member2', '$2b$12$RICY6vexx8J6Dn9qEeh3MuwVj8ZBTcFjOp3C3lBtG9t4RMrn98KRG', 'member2@spbutu.ru', 'Смирнова Ольга Дмитриевна', 'GEK_MEMBER'),
('329a7a9a-fe09-46f9-9138-68d8a5c956f0', 'member3', '$2b$12$RICY6vexx8J6Dn9qEeh3MuwVj8ZBTcFjOp3C3lBtG9t4RMrn98KRG', 'member3@spbutu.ru', 'Васильев Дмитрий Алексеевич', 'GEK_MEMBER'),
('8217de1b-8131-45b5-8753-1afe9814a403', 'member4', '$2b$12$RICY6vexx8J6Dn9qEeh3MuwVj8ZBTcFjOp3C3lBtG9t4RMrn98KRG', 'member4@spbutu.ru', 'Новикова Елена Владимировна', 'GEK_MEMBER'),
('cfd09e26-9f56-42b6-9bd8-d0cd79c88eb0', 'student1', '$2b$12$fE9Si4WqQ2gVyINVjtH04ey30yo5RjBNoGX296ctTRsoe8PtHh1N2', 'student1@spbutu.ru', 'Алексеев Алексей Алексеевич', 'STUDENT');

-- ------------------------------------------
-- 2. Направления подготовки
-- ------------------------------------------
INSERT INTO direction (id, code, name) VALUES
('8522eabe-9f07-468b-bf34-f55e4db50ba7', '09.03.01', 'Информатика и вычислительная техника'),
('139321fa-5c85-47cf-aaba-1ea5bad379f2', '09.03.04', 'Программная инженерия');

-- ------------------------------------------
-- 3. Учебные группы
-- ------------------------------------------
INSERT INTO study_group (id, name, direction_id, course) VALUES
('3cb68b4f-fd0e-4b43-8735-cb567547a92d', 'ИС-101', '8522eabe-9f07-468b-bf34-f55e4db50ba7', 4),
('6bc7b883-3877-4b20-8f09-df41b08c0d34', 'ИС-201', '139321fa-5c85-47cf-aaba-1ea5bad379f2', 4),
('94afc955-4423-485f-beef-b06483c561f0', 'ИС-301', '8522eabe-9f07-468b-bf34-f55e4db50ba7', 3);

-- ------------------------------------------
-- 4. Студенты (10 записей)
-- ------------------------------------------
INSERT INTO student (id, user_id, last_name, first_name, middle_name, record_book_number, group_id) VALUES
('f10b0fb3-e1f5-46df-8c46-90ae8afc699d', 'cfd09e26-9f56-42b6-9bd8-d0cd79c88eb0', 'Алексеев', 'Алексей', 'Алексеевич', '2020-001', '3cb68b4f-fd0e-4b43-8735-cb567547a92d'),
('1bc8096e-43ea-4d84-9ea4-205e6c0c3207', NULL, 'Борисов', 'Борис', 'Борисович', '2020-002', '3cb68b4f-fd0e-4b43-8735-cb567547a92d'),
('4198fbe8-cb8a-4f7e-89c5-cfad36669c13', NULL, 'Викторов', 'Виктор', 'Викторович', '2020-003', '3cb68b4f-fd0e-4b43-8735-cb567547a92d'),
('b4a144ab-3cc7-4801-9618-912df9d57906', NULL, 'Григорьев', 'Григорий', 'Григорьевич', '2020-004', '6bc7b883-3877-4b20-8f09-df41b08c0d34'),
('d1835803-5bda-4016-842a-2e5026569db8', NULL, 'Дмитриев', 'Дмитрий', 'Дмитриевич', '2020-005', '6bc7b883-3877-4b20-8f09-df41b08c0d34'),
('97eca069-4706-4aa7-97e9-96146408acb3', NULL, 'Евгеньев', 'Евгений', 'Евгеньевич', '2020-006', '6bc7b883-3877-4b20-8f09-df41b08c0d34'),
('c2a7bcdc-296c-42bd-9aaa-1b9aa01bd1e9', NULL, 'Жуков', 'Жук', 'Жукович', '2020-007', '94afc955-4423-485f-beef-b06483c561f0'),
('53399f71-89da-445a-8f11-15b08b66f85a', NULL, 'Зайцев', 'Зай', 'Зайцевич', '2020-008', '94afc955-4423-485f-beef-b06483c561f0'),
('3969abe0-a7b0-4329-802e-0e9b58cb95de', NULL, 'Иванов', 'Иван', 'Иванович', '2020-009', '94afc955-4423-485f-beef-b06483c561f0'),
('05b5e051-12e3-432d-af95-7deb9c65c351', NULL, 'Кузнецов', 'Кузьма', 'Кузнецович', '2020-010', '94afc955-4423-485f-beef-b06483c561f0');

-- ------------------------------------------
-- 5. ГЭК
-- ------------------------------------------
INSERT INTO gek (id, name) VALUES
('722fcc41-9b09-4d4d-a273-34309011ff8f', 'ГЭК-2024-ИС');

-- ------------------------------------------
-- 6. Члены ГЭК (личные карточки)
-- ------------------------------------------
INSERT INTO gek_member (id, user_id, academic_title, department) VALUES
('e7dd877f-1de8-4122-9784-d2ff7db1c149', '2026ed35-b953-4f6c-8355-164ed9fde347', 'профессор', 'Кафедра Прикладной информатики'),
('bba19f70-7645-42f9-bde1-a7c96407184b', 'd683575e-f892-471c-b5f5-57b2fb295128', 'доцент', 'Кафедра Прикладной информатики'),
('ddca759b-ce34-4b94-802e-512e4870f379', 'e6cffb28-4e60-49f9-9e71-44e021e4071f', 'доцент', 'Кафедра Информационных систем'),
('bd3d515b-0f1c-4948-bed0-8415dd7d5981', '329a7a9a-fe09-46f9-9138-68d8a5c956f0', 'старший преподаватель', 'Кафедра Прикладной информатики'),
('dc87b6e6-01f6-49b3-a61c-647cb3e7a40b', '8217de1b-8131-45b5-8753-1afe9814a403', 'профессор', 'Кафедра Программной инженерии');

-- ------------------------------------------
-- 7. Состав ГЭК (связь членов с комиссией)
-- ------------------------------------------
INSERT INTO gek_membership (id, gek_id, gek_member_id, position_in_gek) VALUES
('a0000000-0000-0000-0000-000000000001', '722fcc41-9b09-4d4d-a273-34309011ff8f', 'e7dd877f-1de8-4122-9784-d2ff7db1c149', 'CHAIRMAN'),
('a0000000-0000-0000-0000-000000000002', '722fcc41-9b09-4d4d-a273-34309011ff8f', 'bba19f70-7645-42f9-bde1-a7c96407184b', 'MEMBER'),
('a0000000-0000-0000-0000-000000000003', '722fcc41-9b09-4d4d-a273-34309011ff8f', 'ddca759b-ce34-4b94-802e-512e4870f379', 'MEMBER'),
('a0000000-0000-0000-0000-000000000004', '722fcc41-9b09-4d4d-a273-34309011ff8f', 'bd3d515b-0f1c-4948-bed0-8415dd7d5981', 'MEMBER'),
('a0000000-0000-0000-0000-000000000005', '722fcc41-9b09-4d4d-a273-34309011ff8f', 'dc87b6e6-01f6-49b3-a61c-647cb3e7a40b', 'MEMBER');
