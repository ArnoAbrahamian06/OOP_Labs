-- Создание таблицы Function Types
CREATE TABLE IF NOT EXISTS functions_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    localized_name VARCHAR(150) NOT NULL UNIQUE,
    priority SMALLINT DEFAULT 0 CHECK (priority >= 0),
    created_time TIMESTAMPTZ DEFAULT NOW(),
    updated_time TIMESTAMPTZ DEFAULT NOW()
);