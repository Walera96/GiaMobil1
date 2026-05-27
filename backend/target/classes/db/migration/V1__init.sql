-- ============================================================
-- Миграция V1: Инициализация схемы БД
-- ВКР: Автоматизация учета итогов аттестации (ЧОУ ВО СПбУТУИЭ)
-- 14 таблиц: app_user, student, study_group, direction,
-- gek, gek_member, gek_membership, meeting, agenda_item,
-- protocol, protocol_record, vote, admission, audit_log
-- ============================================================

-- Расширение для генерации UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Пользователи системы (все роли: ADMIN, METHODIST, SECRETARY, CHAIRMAN, GEK_MEMBER, STUDENT)
CREATE TABLE app_user (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(200),
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','METHODIST','SECRETARY','CHAIRMAN','GEK_MEMBER','STUDENT')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_app_user_username ON app_user(username);
CREATE INDEX idx_app_user_role ON app_user(role);

-- 2. Направления подготовки
CREATE TABLE direction (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Учебные группы
CREATE TABLE study_group (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE,
    direction_id UUID NOT NULL REFERENCES direction(id) ON DELETE RESTRICT,
    course INT NOT NULL CHECK (course BETWEEN 1 AND 6),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_study_group_direction ON study_group(direction_id);

-- 4. Студенты
CREATE TABLE student (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE REFERENCES app_user(id) ON DELETE SET NULL,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    record_book_number VARCHAR(50) UNIQUE,
    group_id UUID NOT NULL REFERENCES study_group(id) ON DELETE RESTRICT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_student_last_name ON student(last_name);
CREATE INDEX idx_student_group ON student(group_id);

-- 5. ГЭК (государственная экзаменационная комиссия)
CREATE TABLE gek (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. Члены ГЭК (личные карточки преподавателей)
CREATE TABLE gek_member (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    academic_title VARCHAR(100),
    department VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gek_member_user ON gek_member(user_id);

-- 7. Состав ГЭК (связь члена с конкретной комиссией, many-to-many)
CREATE TABLE gek_membership (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    gek_id UUID NOT NULL REFERENCES gek(id) ON DELETE CASCADE,
    gek_member_id UUID NOT NULL REFERENCES gek_member(id) ON DELETE CASCADE,
    position_in_gek VARCHAR(20) NOT NULL CHECK (position_in_gek IN ('CHAIRMAN','MEMBER')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(gek_id, gek_member_id)
);

CREATE INDEX idx_gek_membership_gek ON gek_membership(gek_id);

-- 8. Заседания ГЭК
CREATE TABLE meeting (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    gek_id UUID NOT NULL REFERENCES gek(id) ON DELETE RESTRICT,
    meeting_date TIMESTAMP NOT NULL,
    location VARCHAR(200),
    status VARCHAR(20) NOT NULL DEFAULT 'PLANNED' CHECK (status IN ('PLANNED','ACTIVE','CLOSED','CANCELLED')),
    quorum_required INT NOT NULL DEFAULT 3 CHECK (quorum_required >= 2),
    created_by UUID REFERENCES app_user(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_meeting_date ON meeting(meeting_date);
CREATE INDEX idx_meeting_status ON meeting(status);

-- 9. Повестка заседания (студенты на заседании)
CREATE TABLE agenda_item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    meeting_id UUID NOT NULL REFERENCES meeting(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES student(id) ON DELETE RESTRICT,
    order_number INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(meeting_id, student_id)
);

CREATE INDEX idx_agenda_meeting ON agenda_item(meeting_id);

-- 10. Протокол заседания
CREATE TABLE protocol (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    meeting_id UUID NOT NULL UNIQUE REFERENCES meeting(id) ON DELETE CASCADE,
    protocol_number VARCHAR(50) UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','SIGNED','ARCHIVED')),
    generated_at TIMESTAMP,
    file_path VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 11. Записи протокола (итоговые оценки студентов)
CREATE TABLE protocol_record (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    protocol_id UUID NOT NULL REFERENCES protocol(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES student(id) ON DELETE RESTRICT,
    average_score INT CHECK (average_score BETWEEN 2 AND 5),
    final_score INT CHECK (final_score BETWEEN 2 AND 5),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(protocol_id, student_id)
);

CREATE INDEX idx_protocol_record_protocol ON protocol_record(protocol_id);

-- 12. Голоса членов ГЭК (индивидуальные оценки за студента)
CREATE TABLE vote (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agenda_item_id UUID NOT NULL REFERENCES agenda_item(id) ON DELETE CASCADE,
    gek_member_id UUID NOT NULL REFERENCES gek_member(id) ON DELETE RESTRICT,
    score INT NOT NULL CHECK (score BETWEEN 2 AND 5),
    voted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(agenda_item_id, gek_member_id)
);

CREATE INDEX idx_vote_agenda ON vote(agenda_item_id);

-- 13. Допуск к государственной итоговой аттестации
CREATE TABLE admission (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id UUID NOT NULL UNIQUE REFERENCES student(id) ON DELETE CASCADE,
    brs_score INT CHECK (brs_score BETWEEN 0 AND 100),
    has_debt BOOLEAN NOT NULL DEFAULT FALSE,
    is_admitted BOOLEAN NOT NULL DEFAULT FALSE,
    checked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 14. Журнал аудита (кто, когда, старое/новое значение, IP)
CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    table_name VARCHAR(50) NOT NULL,
    record_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL CHECK (action IN ('INSERT','UPDATE','DELETE')),
    old_value TEXT,
    new_value TEXT,
    changed_by UUID REFERENCES app_user(id) ON DELETE SET NULL,
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);
