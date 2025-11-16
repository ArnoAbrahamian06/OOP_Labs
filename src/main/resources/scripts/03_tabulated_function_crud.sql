-- ПОИСК
-- Функции конкретного пользователя
SELECT * FROM tabulated_function WHERE user_id = ?;

-- Функции определенного типа
SELECT * FROM tabulated_function WHERE function_type_id = ?;

-- Функции созданные после определенной даты
SELECT * FROM tabulated_function WHERE created_time > ?;

-- Функции с пагинацией
SELECT * FROM tabulated_function
ORDER BY created_time DESC
LIMIT ? OFFSET ?;

-- ДОБАВЛЕНИЕ
-- Добавить новую функцию
INSERT INTO tabulated_function (user_id, function_type_id, serialized_data)
VALUES (?, ?, ?);

-- ОБНОВЛЕНИЕ
-- Обновить данные функции
UPDATE tabulated_function
SET serialized_data = ?,
    updated_time = NOW()
WHERE id = ?;

-- Изменить тип функции
UPDATE tabulated_function
SET function_type_id = ?, updated_time = NOW()
WHERE id = ?;

-- Обновить несколько функций пользователя
UPDATE tabulated_function
SET updated_time = NOW()
WHERE user_id = ?;

-- УДАЛЕНИЕ
-- Удалить функцию по ID
DELETE FROM tabulated_function WHERE id = ?;

-- Удалить все функции пользователя
DELETE FROM tabulated_function WHERE user_id = ?;

-- Удалить функции определенного типа
DELETE FROM tabulated_function WHERE function_type_id = ?;