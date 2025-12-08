--Points CRUD
INSERT INTO points (f_id, x_value, y_value) VALUES (?, ?, ?);
SELECT * FROM points WHERE f_id = ?;
UPDATE points SET x_value = ?, y_value = ? WHERE id = ?;
DELETE FROM points WHERE id = ?;
DELETE FROM points WHERE f_id = ?;