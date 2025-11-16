-- Создание таблицы User
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_time TIMESTAMPTZ DEFAULT NOW(),
    role VARCHAR(50) DEFAULT 'user'
);
