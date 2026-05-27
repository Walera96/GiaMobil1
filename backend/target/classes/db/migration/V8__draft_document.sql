-- ============================================================
-- Миграция V8__draft_document.sql
-- Таблица черновиков документов для предпросмотра и редактирования
-- ============================================================

CREATE TABLE IF NOT EXISTS draft_document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    protocol_id UUID NOT NULL,
    document_type VARCHAR(20) NOT NULL CHECK (document_type IN ('INDIVIDUAL', 'FINAL', 'SCORESHEET')),
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'APPROVED')),
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_draft_protocol_id ON draft_document(protocol_id);
CREATE INDEX IF NOT EXISTS idx_draft_status ON draft_document(status);
