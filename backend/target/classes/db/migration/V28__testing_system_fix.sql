-- Fix missing updated_at column for student_test_attempt
ALTER TABLE student_test_attempt ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();

-- Fix missing updated_at column for test
ALTER TABLE test ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();

-- Fix missing updated_at column for test_question
ALTER TABLE test_question ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();

-- Fix missing updated_at column for test_answer_option
ALTER TABLE test_answer_option ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();

-- Fix missing updated_at column for student_test_answer
ALTER TABLE student_test_answer ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();
