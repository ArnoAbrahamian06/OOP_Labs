--Functions CRUD
INSERT INTO functions (user_id, name) VALUES (?, ?) RETURNING id;
SELECT * FROM functions WHERE id = ?;
SELECT * FROM functions WHERE user_id = ?;
UPDATE functions SET name = ? WHERE id = ?;
DELETE FROM functions WHERE id = ?;