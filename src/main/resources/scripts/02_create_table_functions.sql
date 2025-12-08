CREATE TABLE tabulated_functions
(
    id         SERIAL PRIMARY KEY,
    user_id    INT          NOT NULL,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);