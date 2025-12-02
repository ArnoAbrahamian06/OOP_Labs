package org.example.db_service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabulatedFunctionRepository {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionRepository.class);

    // ПОИСК
    public TabulatedFunction findById(Long id) throws SQLException {
        logger.debug("Поиск TabulatedFunction по ID: {}", id);
        String sql = "SELECT * FROM tabulated_function WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                TabulatedFunction function = mapResultSetToTabulatedFunction(rs);
                logger.debug("Найдена TabulatedFunction: {}", function);
                return function;
            }
        }
        logger.debug("TabulatedFunction с ID {} не найдена", id);
        return null;
    }

    public List<TabulatedFunction> findByUserId(Long userId) throws SQLException {
        logger.debug("Поиск TabulatedFunction по userId: {}", userId);
        List<TabulatedFunction> functions = new ArrayList<>();
        String sql = "SELECT * FROM tabulated_function WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                functions.add(mapResultSetToTabulatedFunction(rs));
            }
        }
        logger.debug("Найдено {} функций для пользователя {}", functions.size(), userId);
        return functions;
    }

    public List<TabulatedFunction> findByUserIdAndFunctionTypeId(Long userId, Integer functionTypeId) throws SQLException {
        logger.debug("Поиск TabulatedFunction по userId {} и functionTypeId {}", userId, functionTypeId);
        List<TabulatedFunction> functions = new ArrayList<>();
        String sql = "SELECT * FROM tabulated_function WHERE user_id = ? AND function_type_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            pstmt.setInt(2, functionTypeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                functions.add(mapResultSetToTabulatedFunction(rs));
            }
        }
        logger.debug("Найдено {} функций для userId {} и functionTypeId {}", functions.size(), userId, functionTypeId);
        return functions;
    }

    public List<TabulatedFunction> findByFunctionTypeId(Integer functionTypeId) throws SQLException {
        logger.debug("Поиск TabulatedFunction по functionTypeId: {}", functionTypeId);
        List<TabulatedFunction> functions = new ArrayList<>();
        String sql = "SELECT * FROM tabulated_function WHERE function_type_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionTypeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                functions.add(mapResultSetToTabulatedFunction(rs));
            }
        }
        logger.debug("Найдено {} функций для типа функции {}", functions.size(), functionTypeId);
        return functions;
    }

    public List<TabulatedFunction> findByCreatedTimeAfter(Timestamp date) throws SQLException {
        logger.debug("Поиск TabulatedFunction созданных после: {}", date);
        List<TabulatedFunction> functions = new ArrayList<>();
        String sql = "SELECT * FROM tabulated_function WHERE created_time > ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, date);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                functions.add(mapResultSetToTabulatedFunction(rs));
            }
        }
        logger.debug("Найдено {} функций созданных после {}", functions.size(), date);
        return functions;
    }

    public List<TabulatedFunction> findWithPagination(int limit, int offset) throws SQLException {
        logger.debug("Постраничный поиск TabulatedFunction: limit={}, offset={}", limit, offset);
        List<TabulatedFunction> functions = new ArrayList<>();
        String sql = "SELECT * FROM tabulated_function ORDER BY created_time DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                functions.add(mapResultSetToTabulatedFunction(rs));
            }
        }
        logger.debug("Найдено {} функций для страницы limit={}, offset={}", functions.size(), limit, offset);
        return functions;
    }

    // Расширенный поиск с JOIN
    public List<TabulatedFunction> findWithUserAndTypeInfo() throws SQLException {
        logger.debug("Поиск TabulatedFunction с информацией о пользователе и типе функции");
        List<TabulatedFunction> functions = new ArrayList<>();
        String sql = """
            SELECT tf.*, u.login as user_login, u.email as user_email, 
                   ft.name as function_type_name, ft.localized_name as function_type_localized
            FROM tabulated_function tf
            JOIN "user" u ON tf.user_id = u.id
            JOIN functions_types ft ON tf.function_type_id = ft.id
            """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                functions.add(mapResultSetToTabulatedFunctionWithJoins(rs));
            }
        }
        logger.debug("Найдено {} функций с расширенной информацией", functions.size());
        return functions;
    }

    // ДОБАВЛЕНИЕ
    public TabulatedFunction insert(TabulatedFunction function) throws SQLException {
        logger.info("Добавление новой TabulatedFunction: {}", function);
        String sql = "INSERT INTO tabulated_function (user_id, function_type_id, serialized_data) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, function.getUserId());
            pstmt.setInt(2, function.getFunctionTypeId());
            pstmt.setBytes(3, function.getSerializedData());

            int affectedRows = pstmt.executeUpdate();
            logger.debug("Количество затронутых строк при вставке: {}", affectedRows);

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        function.setId(generatedKeys.getLong(1));
                        logger.info("TabulatedFunction успешно добавлена с ID: {}", function.getId());
                    }
                }
            } else {
                logger.warn("Не удалось добавить TabulatedFunction: {}", function);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при добавлении TabulatedFunction: {}", function, e);
            throw e;
        }
        return function;
    }

    // ОБНОВЛЕНИЕ
    public boolean updateSerializedData(Long id, byte[] newData) throws SQLException {
        logger.info("Обновление serializedData для TabulatedFunction ID {}, размер данных: {} байт",
                id, newData != null ? newData.length : 0);
        String sql = "UPDATE tabulated_function SET serialized_data = ?, updated_time = NOW() WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBytes(1, newData);
            pstmt.setLong(2, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("SerializedData для TabulatedFunction ID {} успешно обновлен", id);
            } else {
                logger.warn("Не удалось обновить serializedData для TabulatedFunction ID {}", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении serializedData для TabulatedFunction ID {}", id, e);
            throw e;
        }
    }

    public boolean updateFunctionType(Long id, Integer newFunctionTypeId) throws SQLException {
        logger.info("Обновление functionTypeId для TabulatedFunction ID {} на {}", id, newFunctionTypeId);
        String sql = "UPDATE tabulated_function SET function_type_id = ?, updated_time = NOW() WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newFunctionTypeId);
            pstmt.setLong(2, id);

            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("FunctionTypeId для TabulatedFunction ID {} успешно обновлен на {}", id, newFunctionTypeId);
            } else {
                logger.warn("Не удалось обновить functionTypeId для TabulatedFunction ID {}", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении functionTypeId для TabulatedFunction ID {}", id, e);
            throw e;
        }
    }

    public boolean updateUserFunctions(Long userId) throws SQLException {
        logger.info("Обновление времени модификации всех функций пользователя ID: {}", userId);
        String sql = "UPDATE tabulated_function SET updated_time = NOW() WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            int updatedRows = pstmt.executeUpdate();
            logger.info("Обновлено {} функций для пользователя ID: {}", updatedRows, userId);
            return updatedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении функций пользователя ID: {}", userId, e);
            throw e;
        }
    }

    // УДАЛЕНИЕ
    public boolean deleteById(Long id) throws SQLException {
        logger.info("Удаление TabulatedFunction по ID: {}", id);
        String sql = "DELETE FROM tabulated_function WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            boolean result = pstmt.executeUpdate() > 0;
            if (result) {
                logger.info("TabulatedFunction с ID {} успешно удалена", id);
            } else {
                logger.warn("TabulatedFunction с ID {} не найдена для удаления", id);
            }
            return result;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении TabulatedFunction с ID {}", id, e);
            throw e;
        }
    }

    public boolean deleteByUserId(Long userId) throws SQLException {
        logger.info("Удаление всех TabulatedFunction для пользователя ID: {}", userId);
        String sql = "DELETE FROM tabulated_function WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            int deletedRows = pstmt.executeUpdate();
            logger.info("Удалено {} функций для пользователя ID: {}", deletedRows, userId);
            return deletedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении функций пользователя ID: {}", userId, e);
            throw e;
        }
    }

    public boolean deleteByFunctionTypeId(Integer functionTypeId) throws SQLException {
        logger.info("Удаление всех TabulatedFunction для типа функции ID: {}", functionTypeId);
        String sql = "DELETE FROM tabulated_function WHERE function_type_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, functionTypeId);
            int deletedRows = pstmt.executeUpdate();
            logger.info("Удалено {} функций для типа функции ID: {}", deletedRows, functionTypeId);
            return deletedRows > 0;
        } catch (SQLException e) {
            logger.error("Ошибка при удалении функций типа ID: {}", functionTypeId, e);
            throw e;
        }
    }

    // Вспомогательные методы для маппинга
    private TabulatedFunction mapResultSetToTabulatedFunction(ResultSet rs) throws SQLException {
        TabulatedFunction function = new TabulatedFunction();
        function.setId(rs.getLong("id"));
        function.setUserId(rs.getLong("user_id"));
        function.setFunctionTypeId(rs.getInt("function_type_id"));
        function.setSerializedData(rs.getBytes("serialized_data"));
        function.setCreatedTime(rs.getTimestamp("created_time").toLocalDateTime());
        function.setUpdatedTime(rs.getTimestamp("updated_time").toLocalDateTime());
        return function;
    }

    private TabulatedFunction mapResultSetToTabulatedFunctionWithJoins(ResultSet rs) throws SQLException {
        TabulatedFunction function = mapResultSetToTabulatedFunction(rs);
        function.setUserLogin(rs.getString("user_login"));
        function.setUserEmail(rs.getString("user_email"));
        function.setFunctionTypeName(rs.getString("function_type_name"));
        function.setFunctionTypeLocalized(rs.getString("function_type_localized"));
        return function;
    }
}