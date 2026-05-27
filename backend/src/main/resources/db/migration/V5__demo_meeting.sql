-- V5: Демо-заседание и пункты повестки

INSERT INTO meeting (id, gek_id, meeting_date, start_time, end_time, location, status, quorum_required, created_at, updated_at)
VALUES (
    'a1111111-1111-1111-1111-111111111111',
    '722fcc41-9b09-4d4d-a273-34309011ff8f',
    '2026-05-16 10:00:00',
    '10:00:00',
    '14:00:00',
    'Аудитория 305, корпус А',
    'ACTIVE',
    3,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO protocol (id, meeting_id, protocol_number, status, created_at, updated_at)
VALUES (
    'b2222222-2222-2222-2222-222222222222',
    'a1111111-1111-1111-1111-111111111111',
    '2026-05-16/ИС-101',
    'DRAFT',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

-- Пункты повестки для 5 студентов из ИС-101
INSERT INTO agenda_item (id, meeting_id, student_id, order_number, presentation_duration, presentation_materials, average_score, created_at, updated_at)
VALUES
    ('c1111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'f10b0fb3-e1f5-46df-8c46-90ae8afc699d', 1, 10, 'Презентация PowerPoint, демо', NULL, NOW(), NOW()),
    ('c2222222-2222-2222-2222-222222222222', 'a1111111-1111-1111-1111-111111111111', '1bc8096e-43ea-4d84-9ea4-205e6c0c3207', 2, 10, 'Презентация PDF, видео', NULL, NOW(), NOW()),
    ('c3333333-3333-3333-3333-333333333333', 'a1111111-1111-1111-1111-111111111111', '4198fbe8-cb8a-4f7e-89c5-cfad36669c13', 3, 10, 'Презентация PowerPoint', NULL, NOW(), NOW()),
    ('c4444444-4444-4444-4444-444444444444', 'a1111111-1111-1111-1111-111111111111', 'b4a144ab-3cc7-4801-9618-912df9d57906', 4, 10, 'Презентация PDF', NULL, NOW(), NOW()),
    ('c5555555-5555-5555-5555-555555555555', 'a1111111-1111-1111-1111-111111111111', 'd1835803-5bda-4016-842a-2e5026569db8', 5, 10, 'Презентация PowerPoint, демо', NULL, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
