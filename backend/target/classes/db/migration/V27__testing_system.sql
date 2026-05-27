-- Testing System Module
-- Tables: test, test_question, test_answer_option, student_test_attempt, student_test_answer

CREATE TABLE test (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    discipline_id UUID,
    direction_id UUID,
    duration_minutes INTEGER,
    passing_score INTEGER,
    max_score INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE test_question (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    test_id UUID NOT NULL REFERENCES test(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    type VARCHAR(30) NOT NULL DEFAULT 'SINGLE_CHOICE',
    points INTEGER DEFAULT 1,
    order_number INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE test_answer_option (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL REFERENCES test_question(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE student_test_attempt (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    test_id UUID NOT NULL REFERENCES test(id) ON DELETE CASCADE,
    started_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    score INTEGER,
    total_correct INTEGER,
    status VARCHAR(20) NOT NULL DEFAULT 'STARTED',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE student_test_answer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    attempt_id UUID NOT NULL REFERENCES student_test_attempt(id) ON DELETE CASCADE,
    question_id UUID NOT NULL,
    selected_option_id UUID,
    text_answer TEXT,
    is_correct BOOLEAN,
    points_earned INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Demo data: sample test
INSERT INTO test (id, title, description, duration_minutes, passing_score, max_score, status)
VALUES ('a0000027-0000-0000-0000-000000000001', 'Демо-тест по Java', 'Базовые вопросы по Java Core', 30, 60, 100, 'ACTIVE');

INSERT INTO test_question (id, test_id, text, type, points, order_number)
VALUES 
('a0000027-0000-0000-0000-000000000002', 'a0000027-0000-0000-0000-000000000001', 'Какой тип данных используется для хранения целых чисел в Java?', 'SINGLE_CHOICE', 10, 1),
('a0000027-0000-0000-0000-000000000003', 'a0000027-0000-0000-0000-000000000001', 'Что такое JVM?', 'SINGLE_CHOICE', 10, 2),
('a0000027-0000-0000-0000-000000000004', 'a0000027-0000-0000-0000-000000000001', 'Выберите ключевые слова Java', 'SINGLE_CHOICE', 10, 3);

INSERT INTO test_answer_option (id, question_id, text, is_correct)
VALUES 
('a0000027-0000-0000-0000-000000000005', 'a0000027-0000-0000-0000-000000000002', 'int', true),
('a0000027-0000-0000-0000-000000000006', 'a0000027-0000-0000-0000-000000000002', 'float', false),
('a0000027-0000-0000-0000-000000000007', 'a0000027-0000-0000-0000-000000000002', 'String', false),
('a0000027-0000-0000-0000-000000000008', 'a0000027-0000-0000-0000-000000000003', 'Java Virtual Machine', true),
('a0000027-0000-0000-0000-000000000009', 'a0000027-0000-0000-0000-000000000003', 'Java Visual Model', false),
('a0000027-0000-0000-0000-00000000000a', 'a0000027-0000-0000-0000-000000000003', 'Java Version Manager', false),
('a0000027-0000-0000-0000-00000000000b', 'a0000027-0000-0000-0000-000000000004', 'class', true),
('a0000027-0000-0000-0000-00000000000c', 'a0000027-0000-0000-0000-000000000004', 'object', false),
('a0000027-0000-0000-0000-00000000000d', 'a0000027-0000-0000-0000-000000000004', 'function', false);
