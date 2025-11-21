package org.example.db_service;

import java.sql.SQLException;

public class SimpleTimer {

    public static void measureExecution(Runnable operation, String operationName) {
        long startTime = System.currentTimeMillis();
        try {
            operation.run();
            long endTime = System.currentTimeMillis();
            System.out.printf("%s выполнено за %d мс%n", operationName, endTime - startTime);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            System.out.printf("%s завершилось ошибкой за %d мс%n", operationName, endTime - startTime);
            e.printStackTrace();
        }
    }

    // Пример использования
    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository();
        User user = new User("bibika22@gmail.com","yughkv","yhktgcv","admin");
        SimpleTimer.measureExecution(() -> {
            try {
                userRepo.findAll();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, "Поиск всех пользователей");

//        SimpleTimer.measureExecution(() -> {
//            try {
//                userRepo.findWithPagination(10, 0);
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }, "Постраничный поиск");
    }
}