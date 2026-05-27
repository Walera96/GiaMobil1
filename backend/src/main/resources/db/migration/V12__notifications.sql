-- ============================================================
-- Миграция V12: Таблица уведомлений (in-app notifications)
-- ============================================================

CREATE TABLE IF NOT EXISTS notification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'INFO',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notification_user ON notification(user_id);
CREATE INDEX idx_notification_user_read ON notification(user_id, is_read);
CREATE INDEX idx_notification_created_at ON notification(created_at);
