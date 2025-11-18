package org.example.db_service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionTypeRepository {
    private static final Logger logger = LoggerFactory.getLogger(FunctionTypeRepository.class);

    // ПОИСК
    public FunctionType findById(Integer id) throws SQLException {
        logger.debug("Поиск FunctionType по ID: {}", id);
        String sql = "SELECT * FROM functions_types WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                FunctionType result = mapResultSetToFunctionType(rs);
                logger.debug("Найден FunctionType: {}", result);
                return result;
            }
        }
        logger.debug("FunctionType с ID {} не найден", id);
        return null;
    }

    public FunctionType findByName(String name) throws SQLException {
        logger.debug("Поиск FunctionType по имени: {}", name);
        String sql = "SELECT * FROM functions_types WHERE name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                FunctionType result = mapResultSetToFunctionType(rs);
                logger.debug("Найден FunctionType по имени {}: {}", name, result);
                return result;
            }
        }
        logger.debug("FunctionType с именем {} не найден", name);
        return null;
    }

    public FunctionType findByLocalizedName(String localizedName) throws SQLException {
        logger.debug("Поиск FunctionType по локализованному имени: {}", localizedName);
        String sql = "SELECT * FROM functions_types WHERE localized_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, localizedName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                FunctionType result = mapResultSetToFunctionType(rs);
                logger.debug("Найден FunctionType по локализованному имени {}: {}", localizedName, result);
                return result;
            }
        }
        logger.debug("FunctionType с локализованным именем {} не найден", localizedName);
        return null;
    }

    public List<FunctionType> findByPriorityGreaterThan(Integer minPriority) throws SQLException {
        logger.debug("Поиск FunctionType с приоритетом больше: {}", minPriority);
        List<FunctionType> types = new ArrayList<>();
        String sql = "SELECT * FROM functions_types WHERE priority > ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, minPriority);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                types.add(mapResultSetToFunctionType(rs));
            }
        }
        logger.debug("Найдено {} FunctionType с приоритетом больше {}", types.size(), minPriority);
        return types;
    }

    // ДОБАВЛЕНИЕ
    public FunctionType insert(FunctionType functionType) throws SQLException {
        logger.info("Добавление нового FunctionType: {}", functionType);
        String sql = "INSERT INTO functions_types (name, localized_name, priority) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, functionType.getName());
            pstmt.setString(2, functionType.getLocalizedName());
            pstmt.setInt(3, functionType.getPriority());

            int affectedRows = pstmt.executeUpdate();
            logger.debug("Количество затронутых строк при вставке: {}", affectedRows);

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        functionType.setId(generatedKeys.getInt(1));
                        logger.info("FunctionType успешно добавлен с ID: {}", functionType.getId());
                    }
                }
            } else {
                logger.warn("Не удалось добавить FunctionType: {}", functionType);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении FunctionType: {}", functionType, e);
            throw e;
        }
        return functionType;
    }

    // ОБНОВЛЕНИЕ
    public boolean updatePriority(Integer id, Integer newPriority) throws SQLException {
        logger.info("Обновление приоритета FunctionType ID {} на {}", id, newPriority);
        String sql = "UPDATE functions_types SET priority = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newPriority);
            pstmt.setInt(2, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("Приоритет FunctionType ID {} успешно обновлен на {}", id, newPriority);
            } else {
                logger.warn("Не удалось обновить приоритет FunctionType ID {}", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении приоритета FunctionType ID {}", id, e);
            throw e;
        }
    }

    public boolean updateLocalizedName(Integer id, String newLocalizedName) throws SQLException {
        logger.info("Обновление локализованного имени FunctionType ID {} на '{}'", id, newLocalizedName);
        String sql = "UPDATE functions_types SET localized_name = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newLocalizedName);
            pstmt.setInt(2, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("Локализованное имя FunctionType ID {} успешно обновлено на '{}'", id, newLocalizedName);
            } else {
                logger.warn("Не удалось обновить локализованное имя FunctionType ID {}", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении локализованного имени FunctionType ID {}", id, e);
            throw e;
        }
    }

    public boolean updateFunctionType(Integer id, String name, String localizedName, Integer priority) throws SQLException {
        logger.info("Полное обновление FunctionType ID {}: name='{}', localizedName='{}', priority={}",
                id, name, localizedName, priority);
        String sql = "UPDATE functions_types SET name = ?, localized_name = ?, priority = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, localizedName);
            pstmt.setInt(3, priority);
            pstmt.setInt(4, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("FunctionType ID {} успешно обновлен", id);
            } else {
                logger.warn("Не удалось обновить FunctionType ID {}", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении FunctionType ID {}", id, e);
            throw e;
        }
    }

    // УДАЛЕНИЕ
    public boolean deleteById(Integer id) throws SQLException {
        logger.info("Удаление FunctionType по ID: {}", id);
        String sql = "DELETE FROM functions_types WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("FunctionType с ID {} успешно удален", id);
            } else {
                logger.warn("FunctionType с ID {} не найден для удаления", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении FunctionType с ID {}", id, e);
            throw e;
        }
    }

    public boolean deleteByName(String name) throws SQLException {
        logger.info("Удаление FunctionType по имени: {}", name);
        String sql = "DELETE FROM functions_types WHERE name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("FunctionType с именем '{}' успешно удален", name);
            } else {
                logger.warn("FunctionType с именем '{}' не найден для удаления", name);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении FunctionType с именем {}", name, e);
            throw e;
        }
    }

    // Вспомогательный метод для маппинга ResultSet в FunctionType
    FunctionType mapResultSetToFunctionType(ResultSet rs) throws SQLException {
        FunctionType functionType = new FunctionType();
        functionType.setId(rs.getInt("id"));
        functionType.setName(rs.getString("name"));
        functionType.setLocalizedName(rs.getString("localized_name"));
        functionType.setPriority(rs.getInt("priority"));
        functionType.setCreatedTime(rs.getTimestamp("created_time").toLocalDateTime());
        functionType.setUpdatedTime(rs.getTimestamp("updated_time").toLocalDateTime());
        return functionType;
    }
}