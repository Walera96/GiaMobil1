CREATE TABLE scos_export_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    direction_code VARCHAR(50) NOT NULL UNIQUE,
    scos_direction_code VARCHAR(50),
    scos_direction_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE scos_export_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    export_date TIMESTAMP,
    file_name VARCHAR(255),
    record_count INTEGER,
    status VARCHAR(20),
    error_details TEXT,
    direction_code VARCHAR(50),
    academic_year VARCHAR(20),
    file_content TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_scos_export_log_date ON scos_export_log(export_date);
CREATE INDEX idx_scos_export_log_direction ON scos_export_log(direction_code);

-- Demo data
INSERT INTO scos_export_config (direction_code, scos_direction_code, scos_direction_name, is_active)
VALUES ('09.03.03', '09.03.03', 'Прикладная информатика', TRUE);
