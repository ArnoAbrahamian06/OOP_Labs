-- User CRUD
INSERT INTO users (username, role, password_hash) VALUES (?, ?, ?);
SELECT * FROM users WHERE id = ?;
SELECT * FROM users WHERE username = ?;
UPDATE users SET password_hash = ? WHERE id = ?;
UPDATE users SET role = ? WHERE id = ?;
DELETE FROM users WHERE id = ?;