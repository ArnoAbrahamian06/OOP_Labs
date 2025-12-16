package org.example.DAO;

import org.example.db_service.DBConnection;
import org.example.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    // SELECT - получение всех пользователей
    public List<User> findAll() {
        logger.info("Начало получения всех пользователей");
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role, created_at FROM users";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            logger.debug("Выполнение SQL: {}", sql);

            while (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                users.add(user);
            }
            logger.info("Успешно получено {} пользователей", users.size());

        } catch (SQLException e) {
            logger.error("Ошибка при получении пользователей", e);
            throw new RuntimeException("Database error", e);
        }
        return users;
    }

    // SELECT - поиск пользователя по ID
    public Optional<User> findById(Integer id) {
        logger.debug("Поиск пользователя по ID: {}", id);
        String sql = "SELECT id, username, password_hash, role, created_at FROM users WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            logger.debug("Выполнение SQL: {} с параметром id={}", sql, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.debug("Пользователь с ID {} найден: {}", id, user.getUsername());
                    return Optional.of(user);
                }
            }
            logger.debug("Пользователь с ID {} не найден", id);

        } catch (SQLException e) {
            logger.error("Ошибка при поиске пользователя по ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
        return Optional.empty();
    }

    // SELECT - поиск пользователя по username
    public Optional<User> findByUsername(String username) {
        logger.debug("Поиск пользователя по username: {}", username);
        String sql = "SELECT id, username, password_hash, role, created_at FROM users WHERE username = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            logger.debug("Выполнение SQL: {} с параметром username={}", sql, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    logger.debug("Пользователь с username {} найден", username);
                    return Optional.of(user);
                }
            }
            logger.debug("Пользователь с username {} не найден", username);

        } catch (SQLException e) {
            logger.error("Ошибка при поиске пользователя по username: {}", username, e);
            throw new RuntimeException("Database error", e);
        }
        return Optional.empty();
    }

    // SELECT - поиск пользователей по роли
    public List<User> findByRole(String role) {
        logger.debug("Поиск пользователей по роли: {}", role);
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role, created_at FROM users WHERE role = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role);
            logger.debug("Выполнение SQL: {} с параметром role={}", sql, role);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    users.add(user);
                }
            }
            logger.debug("Найдено {} пользователей с ролью {}", users.size(), role);

        } catch (SQLException e) {
            logger.error("Ошибка при поиске пользователей по роли: {}", role, e);
            throw new RuntimeException("Database error", e);
        }
        return users;
    }

    // SELECT - поиск пользователей по маске username
    public List<User> findByUsernameLike(String usernamePattern) {
        logger.debug("Поиск пользователей по маске username: {}", usernamePattern);
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role, created_at FROM users WHERE username LIKE ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String searchPattern = "%" + usernamePattern + "%";
            statement.setString(1, searchPattern);
            logger.debug("Выполнение SQL: {} с параметром username={}", sql, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    users.add(user);
                }
            }
            logger.debug("Найдено {} пользователей по маске username", users.size());

        } catch (SQLException e) {
            logger.error("Ошибка при поиске пользователей по маске username: {}", usernamePattern, e);
            throw new RuntimeException("Database error", e);
        }
        return users;
    }

    // INSERT - создание нового пользователя
    public User insert(User user) {
        logger.info("Создание нового пользователя: {}", user.getUsername());
        String sql = "INSERT INTO users (username, password_hash, role, created_at) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole());
            statement.setTimestamp(4, Timestamp.valueOf(now));

            logger.debug("Выполнение SQL: {} с параметрами username={}, role={}",
                    sql, user.getUsername(), user.getRole());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        user.setCreated_at(now);
                        logger.info("Пользователь {} успешно создан с ID: {}", user.getUsername(), user.getId());
                        return user;
                    }
                }
            }
            logger.warn("Пользователь {} не был создан, затронуто 0 строк", user.getUsername());

        } catch (SQLException e) {
            logger.error("Ошибка при создании пользователя: {}", user.getUsername(), e);
            throw new RuntimeException("Database error", e);
        }
        return null;
    }

    // UPDATE - обновление пользователя
    public boolean update(User user) {
        logger.info("Обновление пользователя с ID: {}", user.getId());
        String sql = "UPDATE users SET username = ?, password_hash = ?, role = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole());
            statement.setInt(4, user.getId());

            logger.debug("Выполнение SQL: {} с параметрами username={}, role={}, id={}",
                    sql, user.getUsername(), user.getRole(), user.getId());

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Пользователь с ID {} успешно обновлен", user.getId());
            } else {
                logger.warn("Пользователь с ID {} не найден для обновления", user.getId());
            }
            return success;

        } catch (SQLException e) {
            logger.error("Ошибка при обновлении пользователя с ID: {}", user.getId(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    // UPDATE - обновление только роли пользователя
    public boolean updateRole(Integer id, String newRole) {
        logger.info("Обновление роли пользователя с ID: {}, новая роль: {}", id, newRole);
        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newRole);
            statement.setInt(2, id);
            logger.debug("Выполнение SQL: {} с параметрами role={}, id={}", sql, newRole, id);

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Роль пользователя с ID {} успешно обновлена на {}", id, newRole);
            } else {
                logger.warn("Пользователь с ID {} не найден для обновления роли", id);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Ошибка при обновлении роли пользователя с ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    // UPDATE - обновление только пароля пользователя
    public boolean updatePassword(Integer id, String newPasswordHash) {
        logger.info("Обновление пароля пользователя с ID: {}", id);
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newPasswordHash);
            statement.setInt(2, id);
            logger.debug("Выполнение SQL: {} с параметрами password_hash=***, id={}", sql, id);

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Пароль пользователя с ID {} успешно обновлен", id);
            } else {
                logger.warn("Пользователь с ID {} не найден для обновления пароля", id);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Ошибка при обновлении пароля пользователя с ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    // DELETE - удаление пользователя по ID
    public boolean delete(Integer id) {
        logger.info("Удаление пользователя с ID: {}", id);
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            logger.debug("Выполнение SQL: {} с параметром id={}", sql, id);

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Пользователь с ID {} успешно удален", id);
            } else {
                logger.warn("Пользователь с ID {} не найден для удаления", id);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Ошибка при удалении пользователя с ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    // DELETE - удаление пользователя по username
    public boolean deleteByUsername(String username) {
        logger.info("Удаление пользователя с username: {}", username);
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            logger.debug("Выполнение SQL: {} с параметром username={}", sql, username);

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Пользователь с username {} успешно удален", username);
            } else {
                logger.warn("Пользователь с username {} не найден для удаления", username);
            }
            return success;

        } catch (SQLException e) {
            logger.error("Ошибка при удалении пользователя с username: {}", username, e);
            throw new RuntimeException("Database error", e);
        }
    }

    // COUNT - подсчет количества пользователей
    public int countAll() {
        logger.debug("Подсчет количества всех пользователей");
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            logger.debug("Выполнение SQL: {}", sql);

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                logger.debug("Всего пользователей: {}", count);
                return count;
            }

        } catch (SQLException e) {
            logger.error("Ошибка при подсчете количества пользователей", e);
            throw new RuntimeException("Database error", e);
        }
        return 0;
    }

    // COUNT - подсчет пользователей по роли
    public int countByRole(String role) {
        logger.debug("Подсчет количества пользователей с ролью: {}", role);
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role);
            logger.debug("Выполнение SQL: {} с параметром role={}", sql, role);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    logger.debug("Найдено {} пользователей с ролью {}", count, role);
                    return count;
                }
            }

        } catch (SQLException e) {
            logger.error("Ошибка при подсчете пользователей с ролью: {}", role, e);
            throw new RuntimeException("Database error", e);
        }
        return 0;
    }

    // EXISTS - проверка существования пользователя по username
    public boolean existsByUsername(String username) {
        logger.debug("Проверка существования пользователя с username: {}", username);
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            logger.debug("Выполнение SQL: {} с параметром username={}", sql, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                boolean exists = resultSet.next();
                logger.debug("Пользователь с username {} {}существует",
                        username, exists ? "" : "не ");
                return exists;
            }

        } catch (SQLException e) {
            logger.error("Ошибка при проверке существования пользователя с username: {}", username, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<User> findByIds(List<Integer> ids) {
        logger.debug("Множественный поиск пользователей по IDs: {}", ids);
        List<User> users = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            logger.debug("Передан пустой список IDs");
            return users;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT id, username, password_hash, role, created_at FROM users WHERE id IN (%s)", placeholders);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                statement.setInt(i + 1, ids.get(i));
            }

            logger.debug("Выполнение SQL: {} с параметрами ids={}", sql, ids);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    users.add(user);
                }
            }
            logger.debug("Найдено {} пользователей из запрошенных {}", users.size(), ids.size());

        } catch (SQLException e) {
            logger.error("Ошибка при множественном поиске пользователей по IDs: {}", ids, e);
            throw new RuntimeException("Database error", e);
        }
        return users;
    }

    public List<User> findAllWithSorting(String sortField, boolean ascending) {
        logger.debug("Получение всех пользователей с сортировкой по полю: {}, порядок: {}",
                sortField, ascending ? "ASC" : "DESC");
        List<User> users = new ArrayList<>();

        List<String> allowedFields = List.of("id", "username", "role", "created_at");
        if (!allowedFields.contains(sortField.toLowerCase())) {
            logger.warn("Недопустимое поле для сортировки: {}, используется поле по умолчанию: id", sortField);
            sortField = "id";
        }

        String direction = ascending ? "ASC" : "DESC";
        String sql = String.format("SELECT id, username, password_hash, role, created_at FROM users ORDER BY %s %s", sortField, direction);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            logger.debug("Выполнение SQL: {}", sql);

            while (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                users.add(user);
            }
            logger.debug("Успешно получено {} пользователей с сортировкой", users.size());

        } catch (SQLException e) {
            logger.error("Ошибка при получении пользователей с сортировкой по полю: {}", sortField, e);
            throw new RuntimeException("Database error", e);
        }
        return users;
    }

    public List<User> findByRoleWithSorting(String role, String sortField, boolean ascending) {
        logger.debug("Поиск пользователей по роли {} с сортировкой по полю: {}, порядок: {}",
                role, sortField, ascending ? "ASC" : "DESC");
        List<User> users = new ArrayList<>();

        List<String> allowedFields = List.of("id", "username", "role", "created_at");
        if (!allowedFields.contains(sortField.toLowerCase())) {
            logger.warn("Недопустимое поле для сортировки: {}, используется поле по умолчанию: id", sortField);
            sortField = "id";
        }

        String direction = ascending ? "ASC" : "DESC";
        String sql = String.format("SELECT id, username, password_hash, role, created_at FROM users WHERE role = ? ORDER BY %s %s", sortField, direction);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, role);
            logger.debug("Выполнение SQL: {} с параметром role={}", sql, role);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    users.add(user);
                }
            }
            logger.debug("Найдено {} пользователей с ролью {} с сортировкой", users.size(), role);

        } catch (SQLException e) {
            logger.error("Ошибка при поиске пользователей по роли {} с сортировкой", role, e);
            throw new RuntimeException("Database error", e);
        }
        return users;
    }

    // Вспомогательный метод для маппинга ResultSet в User
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreated_at(createdAt.toLocalDateTime());
        }

        return user;
    }
}