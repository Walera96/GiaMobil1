-- Добавление поля scheduled_time для точного времени выступления студента на ГЭК
ALTER TABLE agenda_item ADD COLUMN IF NOT EXISTS scheduled_time TIMESTAMP;

-- Индекс для быстрого поиска по времени
CREATE INDEX IF NOT EXISTS idx_agenda_item_scheduled_time ON agenda_item(scheduled_time);

-- Добавление поля slot_date в meeting для удобства фильтрации по дням (если meeting_date хранит дату+время)
-- ALTER TABLE meeting ADD COLUMN IF NOT EXISTS meeting_day DATE;
-- UPDATE meeting SET meeting_day = meeting_date::date WHERE meeting_date IS NOT NULL;
