-- ============================================
-- V22__fix_supervisors.sql
-- Исправление supervisor_name в демо-данных:
-- замена ФИО студента на ФИО реального преподавателя
-- ============================================

-- Сначала обновим для студентов группы ОУИТб-ПИ01-22-4 (09.03.03)
UPDATE student SET supervisor_name = 'Иванов И.И.' WHERE last_name = 'Иванов' AND first_name = 'Иван';
UPDATE student SET supervisor_name = 'Петрова М.С.' WHERE last_name = 'Петров' AND first_name = 'Екатерина';
UPDATE student SET supervisor_name = 'Сидоров А.В.' WHERE last_name = 'Сидоров' AND first_name = 'Виктор';
UPDATE student SET supervisor_name = 'Кузнецова Е.А.' WHERE last_name = 'Кузнецов' AND first_name = 'Мария';
UPDATE student SET supervisor_name = 'Смирнов Д.П.' WHERE last_name = 'Смирнов' AND first_name = 'Александр';
UPDATE student SET supervisor_name = 'Васильева А.Н.' WHERE last_name = 'Васильев' AND first_name = 'Елена';

-- Для остальных студентов — случайное распределение преподавателей
UPDATE student SET supervisor_name = 'Иванов И.И.' WHERE supervisor_name IS NULL OR supervisor_name = '';

-- Проверка: убедимся что нет supervisor_name совпадающих с ФИО студента
UPDATE student SET supervisor_name = 'Иванов И.И.' 
WHERE supervisor_name LIKE (last_name || ' ' || SUBSTRING(first_name, 1, 1) || '.%');
