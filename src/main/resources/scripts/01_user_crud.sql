
-- ПОИСК
-- Найти всех пользователей
SELECT * FROM "user";

-- Найти пользователя по ID
SELECT * FROM "user" WHERE id = ?;

-- Найти пользователя по email
SELECT * FROM "user" WHERE email = ?;

-- Найти пользователя по логину
SELECT * FROM "user" WHERE login = ?;

-- Поиск пользователей по роли
SELECT * FROM "user" WHERE role = ?;

-- Поиск с пагинацией
SELECT * FROM "user" ORDER BY created_time DESC LIMIT ? OFFSET ?;

-- ДОБАВЛЕНИЕ
-- Добавить нового пользователя
INSERT INTO "user" (email, login, password_hash, role)
VALUES (?, ?, ?, ?);

-- ОБНОВЛЕНИЕ
-- Обновить email пользователя
UPDATE "user" SET email = ? WHERE id = ?;

-- Обновить пароль
UPDATE "user" SET password_hash = ? WHERE id = ?;

-- Обновить роль
UPDATE "user" SET role = ? WHERE id = ?;

-- Обновить несколько полей
UPDATE "user"
SET email = ?, login = ?
WHERE id = ?;

-- УДАЛЕНИЕ
-- Удалить пользователя по ID
DELETE FROM "user" WHERE id = ?;

-- Удалить пользователя по email
DELETE FROM "user" WHERE email = ?;

-- Удалить нескольких пользователей
DELETE FROM "user" WHERE id IN (?);