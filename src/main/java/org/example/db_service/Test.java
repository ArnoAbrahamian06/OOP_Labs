package org.example.db_service;

import java.sql.*;

public class Test {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver OK");

            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/Mathfunctions_db",
                    "postgres",
                    "Arno2006"
            );
            System.out.println("Connection OK");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}