-- Make grade.score nullable to support component-based grading (current_control, attendance, activity, exam_score)
ALTER TABLE grade ALTER COLUMN score DROP NOT NULL;
