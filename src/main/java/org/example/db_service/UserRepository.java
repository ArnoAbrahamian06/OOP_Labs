package org.example.db_service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    // ПОИСК
    public List<User> findAll() throws SQLException {
        logger.debug("Получение всех пользователей");
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM \"user\"";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        logger.debug("Найдено {} пользователей", users.size());
        return users;
    }

    public User findById(Long id) throws SQLException {
        logger.debug("Поиск пользователя по ID: {}", id);
        String sql = "SELECT * FROM \"user\" WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                logger.debug("Найден пользователь: {}", user);
                return user;
            }
        }
        logger.debug("Пользователь с ID {} не найден", id);
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        logger.debug("Поиск пользователя по email: {}", email);
        String sql = "SELECT * FROM \"user\" WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                logger.debug("Найден пользователь по email {}: {}", email, user);
                return user;
            }
        }
        logger.debug("Пользователь с email {} не найден", email);
        return null;
    }

    public User findByLogin(String login) throws SQLException {
        logger.debug("Поиск пользователя по логину: {}", login);
        String sql = "SELECT * FROM \"user\" WHERE login = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapResultSetToUser(rs);
                logger.debug("Найден пользователь по логину {}: {}", login, user);
                return user;
            }
        }
        logger.debug("Пользователь с логином {} не найден", login);
        return null;
    }

    public List<User> findByRole(String role) throws SQLException {
        logger.debug("Поиск пользователей по роли: {}", role);
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM \"user\" WHERE role = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        logger.debug("Найдено {} пользователей с ролью {}", users.size(), role);
        return users;
    }

    public List<User> findWithPagination(int limit, int offset) throws SQLException {
        logger.debug("Постраничный поиск пользователей: limit={}, offset={}", limit, offset);
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM \"user\" ORDER BY created_time DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        logger.debug("Найдено {} пользователей для страницы limit={}, offset={}", users.size(), limit, offset);
        return users;
    }

    // ДОБАВЛЕНИЕ
    public User insert(User user) throws SQLException {
        logger.info("Добавление нового пользователя: {}", user);
        String sql = "INSERT INTO \"user\" (email, login, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getLogin());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getRole());

            int affectedRows = pstmt.executeUpdate();
            logger.debug("Количество затронутых строк при вставке: {}", affectedRows);

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                        logger.info("Пользователь успешно добавлен с ID: {}", user.getId());
                    }
                }
            } else {
                logger.warn("Не удалось добавить пользователя: {}", user);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении пользователя: {}", user, e);
            throw e;
        }
        return user;
    }

    // ОБНОВЛЕНИЕ
    public boolean updateEmail(Long id, String newEmail) throws SQLException {
        logger.info("Обновление email пользователя ID {} на '{}'", id, newEmail);
        String sql = "UPDATE \"user\" SET email = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newEmail);
            pstmt.setLong(2, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("Email пользователя ID {} успешно обновлен на '{}'", id, newEmail);
            } else {
                logger.warn("Не удалось обновить email пользователя ID {}", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении email пользователя ID {}", id, e);
            throw e;
        }
    }

    public boolean updatePassword(Long id, String newPasswordHash) throws SQLException {
        logger.info("Обновление пароля пользователя ID {}", id);
        String sql = "UPDATE \"user\" SET password_hash = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setLong(2, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("Пароль пользователя ID {} успешно обновлен", id);
            } else {
                logger.warn("Не удалось обновить пароль пользователя ID {}", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении пароля пользователя ID {}", id, e);
            throw e;
        }
    }

    public boolean updateRole(Long id, String newRole) throws SQLException {
        logger.info("Обновление роли пользователя ID {} на '{}'", id, newRole);
        String sql = "UPDATE \"user\" SET role = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newRole);
            pstmt.setLong(2, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("Роль пользователя ID {} успешно обновлена на '{}'", id, newRole);
            } else {
                logger.warn("Не удалось обновить роль пользователя ID {}", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении роли пользователя ID {}", id, e);
            throw e;
        }
    }

    public boolean updateUser(Long id, String email, String login) throws SQLException {
        logger.info("Обновление пользователя ID {}: email='{}', login='{}'", id, email, login);
        String sql = "UPDATE \"user\" SET email = ?, login = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, login);
            pstmt.setLong(3, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("Пользователь ID {} успешно обновлен", id);
            } else {
                logger.warn("Не удалось обновить пользователя ID {}", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении пользователя ID {}", id, e);
            throw e;
        }
    }

    // УДАЛЕНИЕ
    public boolean deleteById(Long id) throws SQLException {
        logger.info("Удаление пользователя по ID: {}", id);
        String sql = "DELETE FROM \"user\" WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("Пользователь с ID {} успешно удален", id);
            } else {
                logger.warn("Пользователь с ID {} не найден для удаления", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении пользователя с ID {}", id, e);
            throw e;
        }
    }

    public boolean deleteByEmail(String email) throws SQLException {
        logger.info("Удаление пользователя по email: {}", email);
        String sql = "DELETE FROM \"user\" WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("Пользователь с email '{}' успешно удален", email);
            } else {
                logger.warn("Пользователь с email '{}' не найден для удаления", email);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении пользователя с email {}", email, e);
            throw e;
        }
    }

    // Вспомогательный метод для маппинга ResultSet в User
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setCreatedTime(rs.getTimestamp("created_time").toLocalDateTime());
        user.setRole(rs.getString("role"));
        return user;
    }
}