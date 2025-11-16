-- Создание таблицы Tabulated Function
CREATE TABLE IF NOT EXISTS tabulated_function (
     id BIGSERIAL PRIMARY KEY,
     user_id BIGINT NOT NULL,
     function_type_id INT NOT NULL,
     serialized_data BYTEA NOT NULL, -- бинарные данные (например, сериализованный JSON/Protobuf)
     created_time TIMESTAMPTZ DEFAULT NOW(),
     updated_time TIMESTAMPTZ DEFAULT NOW(),

    -- Внешние ключи
    CONSTRAINT fk_tabulated_function_owner
        FOREIGN KEY (user_id) REFERENCES "user"(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_tabulated_function_type
        FOREIGN KEY (function_type_id) REFERENCES functions_types(id)
            ON DELETE RESTRICT
);