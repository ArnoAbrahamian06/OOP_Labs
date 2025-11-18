package org.example.db_service;

import org.example.db_service.User;
import org.example.db_service.FunctionType;
import org.example.db_service.TabulatedFunction;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class TestDataGenerator {
    private static final Random random = new SecureRandom();
    private static final String[] DOMAINS = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "yandex.ru"};
    private static final String[] ROLES = {"user", "admin", "moderator", "viewer"};
    private static final String[] FUNCTION_NAMES = {"linear", "quadratic", "exponential", "logarithmic", "trigonometric", "polynomial"};
    private static final String[] LOCALIZED_NAMES = {"Линейная", "Квадратичная", "Экспоненциальная", "Логарифмическая", "Тригонометрическая", "Полиномиальная"};

    // Генерация пользователей
    public static User generateUser() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String email = "user" + uuid + "@" + DOMAINS[random.nextInt(DOMAINS.length)];
        String login = "user_" + uuid;
        String passwordHash = "hash_" + UUID.randomUUID().toString();
        String role = ROLES[random.nextInt(ROLES.length)];

        return new User(email, login, passwordHash, role);
    }

    public static User generateUserWithSpecificRole(String role) {
        User user = generateUser();
        user.setRole(role);
        return user;
    }

    // Генерация типов функций
    public static FunctionType generateFunctionType() {
        int index = random.nextInt(FUNCTION_NAMES.length);
        String name = FUNCTION_NAMES[index] + "_" + UUID.randomUUID().toString().substring(0, 6);
        String localizedName = LOCALIZED_NAMES[index] + "_" + UUID.randomUUID().toString().substring(0, 6);
        int priority = random.nextInt(10);

        return new FunctionType(name, localizedName, priority);
    }

    public static FunctionType generateFunctionTypeWithHighPriority() {
        FunctionType type = generateFunctionType();
        type.setPriority(5 + random.nextInt(10)); // Приоритет от 5 до 15
        return type;
    }

    // Генерация табличных функций
    public static TabulatedFunction generateTabulatedFunction(Long userId, Integer functionTypeId) {
        byte[] data = generateRandomData(50 + random.nextInt(200)); // Данные размером 50-250 байт
        return new TabulatedFunction(userId, functionTypeId, data);
    }

    private static byte[] generateRandomData(int size) {
        byte[] data = new byte[size];
        random.nextBytes(data);
        return data;
    }

    // Генерация случайных строк
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}