-- ============================================================
-- Миграция V11: Исправление CHECK constraint для таблицы protocol
-- Добавлен статус 'APPROVED' (утверждён)
-- ============================================================

ALTER TABLE protocol DROP CONSTRAINT IF EXISTS protocol_status_check;
ALTER TABLE protocol ADD CONSTRAINT protocol_status_check CHECK (status IN ('DRAFT','SIGNED','APPROVED','ARCHIVED'));
