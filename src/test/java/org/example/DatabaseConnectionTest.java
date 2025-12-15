package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");

            // Детальная информация о подключении
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("✅ Успешное подключение к базе данных!");
            System.out.println("📡 URL подключения: " + connection.getMetaData().getURL());
            System.out.println("👤 Пользователь БД: " + connection.getMetaData().getUserName());
            System.out.println("🗄️ Имя БД: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
            System.out.println("🔗 Схема по умолчанию: " + connection.getSchema());
        }
    }

    @Test
    public void testDatabaseDiagnostics() {
        try {
            // 1. Проверяем текущую схему
            String currentSchema = jdbcTemplate.queryForObject(
                    "SELECT current_schema()",
                    String.class
            );
            System.out.println("📋 Текущая схема: " + currentSchema);

            // 2. Проверяем существование таблицы users в текущей схеме
            Boolean tableExists = jdbcTemplate.queryForObject(
                    "SELECT EXISTS (SELECT 1 FROM information_schema.tables " +
                            "WHERE table_schema = ? AND table_name = 'users')",
                    new Object[]{currentSchema},
                    Boolean.class
            );

            System.out.println("📊 Таблица 'users' существует в схеме '" + currentSchema + "': " + tableExists);

            if (!tableExists) {
                // 3. Если таблицы нет в текущей схеме, ищем её в других схемах
                System.out.println("🔍 Поиск таблицы 'users' в других схемах...");
                List<Map<String, Object>> schemasWithUsers = jdbcTemplate.queryForList(
                        "SELECT table_schema FROM information_schema.tables " +
                                "WHERE table_name = 'users' AND table_schema NOT IN ('information_schema', 'pg_catalog')"
                );

                if (!schemasWithUsers.isEmpty()) {
                    System.out.println("✅ Таблица 'users' найдена в схемах:");
                    for (Map<String, Object> schema : schemasWithUsers) {
                        System.out.println("   • " + schema.get("table_schema"));
                    }
                } else {
                    System.out.println("❌ Таблица 'users' не найдена ни в одной схеме!");
                }
            }

            // 4. Проверяем общее количество записей в таблице users (если она существует)
            try {
                Integer totalCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM users",
                        Integer.class
                );
                System.out.println("📈 Общее количество записей в таблице 'users': " + totalCount);

                // 5. Показываем первые 5 записей для проверки структуры
                if (totalCount > 0) {
                    List<Map<String, Object>> sampleUsers = jdbcTemplate.queryForList(
                            "SELECT id, username, role FROM users LIMIT 5"
                    );
                    System.out.println("📋 Пример данных из таблицы (первые 5 записей):");
                    for (Map<String, Object> user : sampleUsers) {
                        System.out.println("   • ID: " + user.get("id") +
                                ", Username: " + user.get("username") +
                                ", Role: " + user.get("role"));
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ Не удалось получить данные из таблицы 'users': " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("❌ Ошибка диагностики базы данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testQueryUserById() {
        Long userId = 200L;

        try {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🔍 ПОИСК ПОЛЬЗОВАТЕЛЯ С ID = " + userId);
            System.out.println("=".repeat(50));

            // Вариант 1: Простой запрос (может не работать из-за схемы)
            String simpleSql = "SELECT * FROM users WHERE id = ?";

            // Вариант 2: Явное указание схемы (рекомендуется)
            String schemaQualifiedSql = "SELECT * FROM public.users WHERE id = ?"; // замените 'public' на вашу схему

            System.out.println("🔍 Пробуем запрос: " + simpleSql.replace("?", userId.toString()));

            try {
                // Сначала пробуем простой запрос
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM users WHERE id = ?",
                        new Object[]{userId},
                        Integer.class
                );
                System.out.println("📊 [Простой запрос] Количество записей с ID=" + userId + ": " + count);

                if (count == null || count == 0) {
                    System.out.println("⚠️ Пользователь не найден. Пробуем с явным указанием схемы...");

                    // Пробуем с явным указанием схемы
                    Integer countWithSchema = jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM public.users WHERE id = ?", // замените 'public' на вашу схему
                            new Object[]{userId},
                            Integer.class
                    );
                    System.out.println("📊 [Схема public] Количество записей с ID=" + userId + ": " + countWithSchema);

                    if (countWithSchema > 0) {
                        System.out.println("✅ Пользователь найден в схеме 'public'!");
                        // Здесь можно выполнить полный запрос к схеме public
                    }
                } else {
                    System.out.println("✅ Пользователь найден!");
                }

            } catch (Exception e) {
                System.err.println("❌ Ошибка при выполнении запроса: " + e.getMessage());

                // Пробуем альтернативные варианты
                System.out.println("🔄 Пробуем альтернативные подходы...");

                // Проверяем все возможные варианты имен таблиц
                String[] tableVariants = {
                        "users", "Users", "user", "USER", "public.users", "public.Users"
                };

                for (String table : tableVariants) {
                    try {
                        String testSql = "SELECT COUNT(*) FROM " + table + " WHERE id = " + userId;
                        Integer altCount = jdbcTemplate.queryForObject(testSql, Integer.class);
                        System.out.println("📊 [" + table + "] Количество записей: " + altCount);
                    } catch (Exception ex) {
                        System.out.println("❌ [" + table + "] Ошибка: " + ex.getMessage().split("\n")[0]);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Критическая ошибка в тесте: " + e.getMessage());
            e.printStackTrace();
            fail("Тест завершился с ошибкой: " + e.getMessage());
        }
    }
}