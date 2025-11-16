-- ПОИСК
-- Найти тип по ID
SELECT * FROM functions_types WHERE id = ?;

-- Найти тип по имени
SELECT * FROM functions_types WHERE name = ?;

-- Найти тип по локализованному имени
SELECT * FROM functions_types WHERE localized_name = ?;

-- Типы с высоким приоритетом
SELECT * FROM functions_types WHERE priority > ?;

-- ДОБАВЛЕНИЕ
-- Добавить новый тип функции
INSERT INTO functions_types (name, localized_name, priority)
VALUES (?, ?, ?);

-- ОБНОВЛЕНИЕ
-- Обновить приоритет
UPDATE functions_types SET priority = ? WHERE id = ?;

-- Обновить локализованное имя
UPDATE functions_types SET localized_name = ? WHERE id = ?;

-- Обновить несколько полей
UPDATE functions_types
SET name = ?, localized_name = ?, priority = ?
WHERE id = ?;

-- УДАЛЕНИЕ
-- Удалить тип по ID
DELETE FROM functions_types WHERE id = ?;

-- Удалить по имени
DELETE FROM functions_types WHERE name = ?;