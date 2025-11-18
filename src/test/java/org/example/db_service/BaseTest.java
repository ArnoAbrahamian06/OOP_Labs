package org.example.db_service;

import org.example.db_service.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.Statement;

public abstract class BaseTest {

    @BeforeEach
    void setUp() throws Exception {
        // Очистка таблиц перед каждым тестом
        clearTables();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Дополнительная очистка после тестов
        clearTables();
    }

    private void clearTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Отключаем foreign key constraints для безопасной очистки
            stmt.execute("SET session_replication_role = 'replica'");

            // Очищаем таблицы в правильном порядке из-за foreign keys
            stmt.execute("DELETE FROM tabulated_function");
            stmt.execute("DELETE FROM functions_types");
            stmt.execute("DELETE FROM \"user\"");

            // Включаем обратно foreign key constraints
            stmt.execute("SET session_replication_role = 'origin'");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}