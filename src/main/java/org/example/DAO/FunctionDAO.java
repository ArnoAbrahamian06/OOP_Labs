package org.example.DAO;

import org.example.db_service.DBConnection;
import org.example.models.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FunctionDAO {
    private static final Logger logger = LoggerFactory.getLogger(FunctionDAO.class);

    // SELECT - получение всех функций
    public List<Function> findAll() {
        logger.info("Начало получения всех функций");
        List<Function> functions = new ArrayList<>();
        String sql = "SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            logger.debug("Выполнение SQL: {}", sql);

            while (resultSet.next()) {
                Function function = mapResultSetToFunction(resultSet);
                functions.add(function);
            }

            logger.info("Успешно получено {} функций", functions.size());

        } catch (SQLException e) {
            logger.error("Ошибка при получении всех функций", e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    // SELECT - поиск функции по ID
    public Optional<Function> findById(Integer id) {
        logger.debug("Поиск функции по ID: {}", id);
        String sql = "SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            logger.debug("Выполнение SQL: {} с параметром id={}", sql, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Function function = mapResultSetToFunction(resultSet);
                    logger.debug("Найдена функция: {}", function);
                    return Optional.of(function);
                }
            }

            logger.debug("Функция с ID {} не найдена", id);

        } catch (SQLException e) {
            logger.error("Ошибка при поиске функции по ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
        return Optional.empty();
    }

    // SELECT - поиск функций по ID пользователя
    public List<Function> findByUserId(Integer userId) {
        logger.debug("Поиск функций по ID пользователя: {}", userId);
        List<Function> functions = new ArrayList<>();
        String sql = "SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            logger.debug("Выполнение SQL: {} с параметром userId={}", sql, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Function function = mapResultSetToFunction(resultSet);
                    functions.add(function);
                }
            }

            logger.debug("Найдено {} функций для пользователя {}", functions.size(), userId);

        } catch (SQLException e) {
            logger.error("Ошибка при поиске функций по ID пользователя: {}", userId, e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    // SELECT - поиск функций по имени (частичное совпадение)
    public List<Function> findByName(String name) {
        logger.debug("Поиск функций по имени: {}", name);
        List<Function> functions = new ArrayList<>();
        String sql = "SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions WHERE name LIKE ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String searchPattern = "%" + name + "%";
            statement.setString(1, searchPattern);
            logger.debug("Выполнение SQL: {} с параметром name={}", sql, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Function function = mapResultSetToFunction(resultSet);
                    functions.add(function);
                }
            }

            logger.debug("Найдено {} функций по шаблону имени: {}", functions.size(), name);

        } catch (SQLException e) {
            logger.error("Ошибка при поиске функций по имени: {}", name, e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    public List<Function> findByIds(List<Integer> ids) {
        logger.debug("Множественный поиск функций по {} ID", ids != null ? ids.size() : 0);
        List<Function> functions = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            logger.warn("Передан пустой список ID для поиска");
            return functions;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = String.format("SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions WHERE id IN (%s)", placeholders);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                statement.setInt(i + 1, ids.get(i));
            }

            logger.debug("Выполнение SQL: {} с параметрами ids={}", sql, ids);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Function function = mapResultSetToFunction(resultSet);
                    functions.add(function);
                }
            }

            logger.debug("Найдено {} функций по списку ID", functions.size());

        } catch (SQLException e) {
            logger.error("Ошибка при множественном поиске функций по ID: {}", ids, e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    public List<Function> findByUserIds(List<Integer> userIds) {
        logger.debug("Множественный поиск функций по {} ID пользователей", userIds != null ? userIds.size() : 0);
        List<Function> functions = new ArrayList<>();
        if (userIds == null || userIds.isEmpty()) {
            logger.warn("Передан пустой список ID пользователей для поиска");
            return functions;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(userIds.size(), "?"));
        String sql = String.format("SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions WHERE user_id IN (%s)", placeholders);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < userIds.size(); i++) {
                statement.setInt(i + 1, userIds.get(i));
            }

            logger.debug("Выполнение SQL: {} с параметрами userIds={}", sql, userIds);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Function function = mapResultSetToFunction(resultSet);
                    functions.add(function);
                }
            }

            logger.debug("Найдено {} функций по списку ID пользователей", functions.size());

        } catch (SQLException e) {
            logger.error("Ошибка при множественном поиске функций по ID пользователей: {}", userIds, e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    public List<Function> findAllWithSorting(String sortField, boolean ascending) {
        logger.debug("Получение всех функций с сортировкой по полю: {}, направление: {}",
                sortField, ascending ? "ASC" : "DESC");
        List<Function> functions = new ArrayList<>();

        List<String> allowedFields = List.of("id", "user_id", "name", "created_at", "updated_at");
        if (!allowedFields.contains(sortField.toLowerCase())) {
            logger.warn("Недопустимое поле для сортировки: {}, используется поле по умолчанию: id", sortField);
            sortField = "id";
        }

        String direction = ascending ? "ASC" : "DESC";
        String sql = String.format("SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions ORDER BY %s %s", sortField, direction);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            logger.debug("Выполнение SQL: {}", sql);

            while (resultSet.next()) {
                Function function = mapResultSetToFunction(resultSet);
                functions.add(function);
            }

            logger.debug("Получено {} функций с сортировкой", functions.size());

        } catch (SQLException e) {
            logger.error("Ошибка при получении функций с сортировкой по полю: {}", sortField, e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    // SELECT - поиск функций по ID пользователя с сортировкой
    public List<Function> findByUserIdWithSorting(Integer userId, String sortField, boolean ascending) {
        logger.debug("Поиск функций пользователя {} с сортировкой по полю: {}, направление: {}",
                userId, sortField, ascending ? "ASC" : "DESC");
        List<Function> functions = new ArrayList<>();

        List<String> allowedFields = List.of("id", "user_id", "name", "created_at", "updated_at");
        if (!allowedFields.contains(sortField.toLowerCase())) {
            logger.warn("Недопустимое поле для сортировки: {}, используется поле по умолчанию: id", sortField);
            sortField = "id";
        }

        String direction = ascending ? "ASC" : "DESC";
        String sql = String.format("SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions WHERE user_id = ? ORDER BY %s %s", sortField, direction);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            logger.debug("Выполнение SQL: {} с параметром userId={}", sql, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Function function = mapResultSetToFunction(resultSet);
                    functions.add(function);
                }
            }

            logger.debug("Найдено {} функций пользователя {} с сортировкой", functions.size(), userId);

        } catch (SQLException e) {
            logger.error("Ошибка при поиске функций пользователя {} с сортировкой", userId, e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    // SELECT - поиск функций по имени с сортировкой
    public List<Function> findByNameWithSorting(String name, String sortField, boolean ascending) {
        logger.debug("Поиск функций по имени {} с сортировкой по полю: {}, направление: {}",
                name, sortField, ascending ? "ASC" : "DESC");
        List<Function> functions = new ArrayList<>();

        List<String> allowedFields = List.of("id", "user_id", "name", "created_at", "updated_at");
        if (!allowedFields.contains(sortField.toLowerCase())) {
            logger.warn("Недопустимое поле для сортировки: {}, используется поле по умолчанию: id", sortField);
            sortField = "id";
        }

        String direction = ascending ? "ASC" : "DESC";
        String sql = String.format("SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions WHERE name LIKE ? ORDER BY %s %s", sortField, direction);

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String searchPattern = "%" + name + "%";
            statement.setString(1, searchPattern);
            logger.debug("Выполнение SQL: {} с параметром name={}", sql, searchPattern);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Function function = mapResultSetToFunction(resultSet);
                    functions.add(function);
                }
            }

            logger.debug("Найдено {} функций по имени {} с сортировкой", functions.size(), name);

        } catch (SQLException e) {
            logger.error("Ошибка при поиске функций по имени {} с сортировкой", name, e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    // SELECT - расширенный поиск с множественными критериями и сортировкой
    public List<Function> findByCriteria(Integer userId, String namePattern, String sortField, boolean ascending) {
        logger.debug("Расширенный поиск функций: userId={}, namePattern={}, sortField={}, ascending={}",
                userId, namePattern, sortField, ascending);
        List<Function> functions = new ArrayList<>();

        List<String> allowedFields = List.of("id", "user_id", "name", "created_at", "updated_at");
        if (!allowedFields.contains(sortField.toLowerCase())) {
            logger.warn("Недопустимое поле для сортировки: {}, используется поле по умолчанию: id", sortField);
            sortField = "id";
        }

        String direction = ascending ? "ASC" : "DESC";
        StringBuilder sqlBuilder = new StringBuilder("SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (userId != null) {
            sqlBuilder.append(" AND user_id = ?");
            parameters.add(userId);
        }

        if (namePattern != null && !namePattern.trim().isEmpty()) {
            sqlBuilder.append(" AND name LIKE ?");
            parameters.add("%" + namePattern + "%");
        }

        sqlBuilder.append(String.format(" ORDER BY %s %s", sortField, direction));
        String sql = sqlBuilder.toString();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Integer) {
                    statement.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    statement.setString(i + 1, (String) param);
                }
            }

            logger.debug("Выполнение SQL: {} с параметрами: {}", sql, parameters);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Function function = mapResultSetToFunction(resultSet);
                    functions.add(function);
                }
            }

            logger.debug("Найдено {} функций по расширенным критериям", functions.size());

        } catch (SQLException e) {
            logger.error("Ошибка при расширенном поиске функций: userId={}, namePattern={}", userId, namePattern, e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    // SELECT - поиск функций по имени и ID пользователя
    public List<Function> findByNameAndUserId(String name, Integer userId) {
        logger.debug("Поиск функций по имени {} и ID пользователя {}", name, userId);
        List<Function> functions = new ArrayList<>();
        String sql = "SELECT id, user_id, name, created_at, updated_at FROM tabulated_functions WHERE name = ? AND user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setInt(2, userId);
            logger.debug("Выполнение SQL: {} с параметрами name={}, userId={}", sql, name, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Function function = mapResultSetToFunction(resultSet);
                    functions.add(function);
                }
            }

            logger.debug("Найдено {} функций по имени и ID пользователя", functions.size());

        } catch (SQLException e) {
            logger.error("Ошибка при поиске функций по имени {} и ID пользователя {}", name, userId, e);
            throw new RuntimeException("Database error", e);
        }
        return functions;
    }

    // INSERT - создание новой функции
    public Function insert(Function function) {
        logger.info("Создание новой функции: {}", function);
        String sql = "INSERT INTO tabulated_functions (user_id, name, created_at, updated_at) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();

            statement.setInt(1, function.getUserId());
            statement.setString(2, function.getName());
            statement.setTimestamp(3, Timestamp.valueOf(now));
            statement.setTimestamp(4, Timestamp.valueOf(now));

            logger.debug("Выполнение SQL: {} с параметрами user_id={}, name={}",
                    sql, function.getUserId(), function.getName());

            int affectedRows = statement.executeUpdate();
            logger.debug("Количество затронутых строк: {}", affectedRows);

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        function.setId(generatedKeys.getInt(1));
                        function.setCreated_at(now);
                        function.setUpdated_at(now);
                        logger.info("Функция успешно создана с ID: {}", function.getId());
                        return function;
                    }
                }
            }

            logger.warn("Функция не была создана, затронуто 0 строк");

        } catch (SQLException e) {
            logger.error("Ошибка при создании функции: {}", function, e);
            throw new RuntimeException("Database error", e);
        }
        return null;
    }

    // UPDATE - обновление функции
    public boolean update(Function function) {
        logger.info("Обновление функции с ID: {}", function.getId());
        String sql = "UPDATE tabulated_functions SET user_id = ?, name = ?, updated_at = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();

            statement.setInt(1, function.getUserId());
            statement.setString(2, function.getName());
            statement.setTimestamp(3, Timestamp.valueOf(now));
            statement.setInt(4, function.getId());

            logger.debug("Выполнение SQL: {} с параметрами user_id={}, name={}, id={}",
                    sql, function.getUserId(), function.getName(), function.getId());

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                function.setUpdated_at(now);
                logger.info("Функция с ID {} успешно обновлена", function.getId());
            } else {
                logger.warn("Функция с ID {} не найдена для обновления", function.getId());
            }

            return success;

        } catch (SQLException e) {
            logger.error("Ошибка при обновлении функции с ID: {}", function.getId(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    // UPDATE - обновление только имени функции
    public boolean updateName(Integer functionId, String newName) {
        logger.info("Обновление имени функции с ID: {}", functionId);
        String sql = "UPDATE tabulated_functions SET name = ?, updated_at = ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();

            statement.setString(1, newName);
            statement.setTimestamp(2, Timestamp.valueOf(now));
            statement.setInt(3, functionId);
            logger.debug("Выполнение SQL: {} с параметрами name={}, id={}", sql, newName, functionId);

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Имя функции с ID {} успешно обновлено", functionId);
            } else {
                logger.warn("Функция с ID {} не найдена для обновления имени", functionId);
            }

            return success;

        } catch (SQLException e) {
            logger.error("Ошибка при обновлении имени функции с ID: {}", functionId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    // DELETE - удаление функции по ID
    public boolean delete(Integer id) {
        logger.info("Удаление функции с ID: {}", id);
        String sql = "DELETE FROM tabulated_functions WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            logger.debug("Выполнение SQL: {} с параметром id={}", sql, id);

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Функция с ID {} успешно удалена", id);
            } else {
                logger.warn("Функция с ID {} не найдена для удаления", id);
            }

            return success;

        } catch (SQLException e) {
            logger.error("Ошибка при удалении функции с ID: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    // DELETE - удаление всех функций пользователя
    public boolean deleteByUserId(Integer userId) {
        logger.info("Удаление всех функций пользователя с ID: {}", userId);
        String sql = "DELETE FROM tabulated_functions WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            logger.debug("Выполнение SQL: {} с параметром userId={}", sql, userId);

            int affectedRows = statement.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Удалено {} функций пользователя с ID {}", affectedRows, userId);
            } else {
                logger.warn("Функции пользователя с ID {} не найдены для удаления", userId);
            }

            return success;

        } catch (SQLException e) {
            logger.error("Ошибка при удалении функций пользователя с ID: {}", userId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    // COUNT - подсчет количества функций пользователя
    public int countByUserId(Integer userId) {
        logger.debug("Подсчет количества функций пользователя с ID: {}", userId);
        String sql = "SELECT COUNT(*) FROM tabulated_functions WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            logger.debug("Выполнение SQL: {} с параметром userId={}", sql, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    logger.debug("Найдено {} функций пользователя с ID {}", count, userId);
                    return count;
                }
            }

        } catch (SQLException e) {
            logger.error("Ошибка при подсчете функций пользователя с ID: {}", userId, e);
            throw new RuntimeException("Database error", e);
        }
        return 0;
    }

    // EXISTS - проверка существования функции по имени и пользователю
    public boolean existsByNameAndUserId(String name, Integer userId) {
        logger.debug("Проверка существования функции с именем {} для пользователя {}", name, userId);
        String sql = "SELECT 1 FROM tabulated_functions WHERE name = ? AND user_id = ? LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setInt(2, userId);
            logger.debug("Выполнение SQL: {} с параметрами name={}, userId={}", sql, name, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                boolean exists = resultSet.next();
                logger.debug("Функция с именем {} для пользователя {} {}существует",
                        name, userId, exists ? "" : "не ");
                return exists;
            }

        } catch (SQLException e) {
            logger.error("Ошибка при проверке существования функции с именем {} для пользователя {}", name, userId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    // Вспомогательный метод для маппинга ResultSet в Function
    private Function mapResultSetToFunction(ResultSet resultSet) throws SQLException {
        Function function = new Function();
        function.setId(resultSet.getInt("id"));
        function.setUserId(resultSet.getInt("user_id"));
        function.setName(resultSet.getString("name"));

        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");

        if (createdAt != null) {
            function.setCreated_at(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            function.setUpdated_at(updatedAt.toLocalDateTime());
        }

        return function;
    }
}