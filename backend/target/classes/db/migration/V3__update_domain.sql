-- V3: Добавление полей для ВКР, заседаний, протоколов и PIN-кода членов ГЭК

ALTER TABLE student
    ADD COLUMN IF NOT EXISTS thesis_topic VARCHAR(500),
    ADD COLUMN IF NOT EXISTS supervisor_name VARCHAR(200);

ALTER TABLE meeting
    ADD COLUMN IF NOT EXISTS start_time TIME,
    ADD COLUMN IF NOT EXISTS end_time TIME;
-- location уже существует

ALTER TABLE agenda_item
    ADD COLUMN IF NOT EXISTS presentation_duration INTEGER DEFAULT 10,
    ADD COLUMN IF NOT EXISTS presentation_materials VARCHAR(300),
    ADD COLUMN IF NOT EXISTS average_score NUMERIC(4,2);

ALTER TABLE protocol_record
    ADD COLUMN IF NOT EXISTS score_points INTEGER,
    ADD COLUMN IF NOT EXISTS is_absent BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS qualification VARCHAR(200),
    ADD COLUMN IF NOT EXISTS is_with_honors BOOLEAN;
ALTER TABLE protocol_record ADD COLUMN IF NOT EXISTS decision TEXT;

ALTER TABLE gek_member
    ADD COLUMN IF NOT EXISTS pin_code VARCHAR(10);

ALTER TABLE protocol
    ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS approved_by_id UUID REFERENCES app_user(id);
