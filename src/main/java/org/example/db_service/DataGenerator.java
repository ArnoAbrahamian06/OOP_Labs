package org.example.db_service;

import org.example.DAO.PointRepository;
import org.example.DAO.FunctionRepository;
import org.example.DAO.UserRepository;
import org.example.models.Point;
import org.example.models.Function;

import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class DataGenerator {
    private static final Random random = new Random();
    private static final String[] ROLES = {"USER", "ADMIN", "MODERATOR", "GUEST"};
    private static final String[] FUNCTION_NAMES = {"linear", "quadratic", "exponential", "logarithmic", "trigonometric", "polynomial"};
    private static final String[] FUNCTION_LOCALIZED_NAMES = {"Линейная", "Квадратичная", "Экспоненциальная", "Логарифмическая", "Тригонометрическая", "Полиномиальная"};

    private final UserRepository userRepository;
    private final PointRepository functionTypeRepository;
    private final FunctionRepository tabulatedFunctionRepository;

    public DataGenerator() {
        this.userRepository = new UserRepository();
        this.functionTypeRepository = new PointRepository();
        this.tabulatedFunctionRepository = new FunctionRepository();
    }

    public void generateAllData(int count) throws SQLException {
        System.out.println("Начало генерации " + count + " записей для каждой таблицы...");

        // Генерация пользователей
        System.out.println("Генерация пользователей...");
        for (int i = 0; i < count; i++) {
            User user = generateRandomUser();
            userRepository.insert(user);
            if ((i + 1) % 1000 == 0) {
                System.out.println("Создано пользователей: " + (i + 1));
            }
        }

        // Генерация типов функций
        System.out.println("Генерация типов функций...");
        for (int i = 0; i < count; i++) {
            Point functionType = generateRandomFunctionType();
            functionTypeRepository.insert(functionType);
            if ((i + 1) % 1000 == 0) {
                System.out.println("Создано типов функций: " + (i + 1));
            }
        }

        // Генерация табулированных функций
        System.out.println("Генерация табулированных функций...");
        for (int i = 0; i < count; i++) {
            Function function = generateRandomTabulatedFunction();
            tabulatedFunctionRepository.insert(function);
            if ((i + 1) % 1000 == 0) {
                System.out.println("Создано табулированных функций: " + (i + 1));
            }
        }

        System.out.println("Генерация данных завершена!");
    }

    private User generateRandomUser() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String email = "user" + uuid + "@example.com";
        String username = "user_" + uuid;
        String passwordHash = generateRandomPasswordHash();
        String role = ROLES[random.nextInt(ROLES.length)];

        return new User(email, username, passwordHash, role);
    }

    private Point generateRandomFunctionType() {
        int nameIndex = random.nextInt(FUNCTION_NAMES.length);
        String name = FUNCTION_NAMES[nameIndex] + "_" + UUID.randomUUID().toString().substring(0, 4);
        String localizedName = FUNCTION_LOCALIZED_NAMES[nameIndex] + "_" + UUID.randomUUID().toString().substring(0, 4);
        int priority = random.nextInt(100);

        return new Point(name, localizedName, priority);
    }

    private Function generateRandomTabulatedFunction() {
        // Предполагаем, что ID пользователей и типов функций начинаются с 1
        Long userId = (long) (random.nextInt(10000) + 1);
        Integer functionTypeId = random.nextInt(10000) + 1;
        String name = FUNCTION_NAMES[random.nextInt(FUNCTION_NAMES.length)];
        byte[] serializedData = generateRandomSerializedData();

        return new Function(userId, functionTypeId, serializedData, name);
    }

    private String generateRandomPasswordHash() {
        // Генерация случайного хеша пароля (в реальном приложении используйте BCrypt)
        return UUID.randomUUID().toString().replace("-", "");
    }

    private byte[] generateRandomSerializedData() {
        // Генерация случайных байтовых данных (имитация сериализованной функции)
        int dataSize = random.nextInt(1000) + 100; // от 100 до 1100 байт
        byte[] data = new byte[dataSize];
        random.nextBytes(data);
        return data;
    }

    // Метод для очистки всех данных (опционально)
    public void clearAllData() throws SQLException {
        System.out.println("Очистка всех данных...");

        // Удаление в правильном порядке из-за foreign key constraints
        tabulatedFunctionRepository.deleteByUserId(1L); // Удаляем все функции
        userRepository.deleteById(1L); // Удаляем всех пользователей
        functionTypeRepository.deleteById(1); // Удаляем все типы функций

        System.out.println("Очистка данных завершена!");
    }

    public static void main(String[] args) {
        DataGenerator generator = new DataGenerator();

        try {
            // Очистка данных (раскомментируйте если нужно)
            // generator.clearAllData();

            // Генерация 10000 записей для каждой таблицы
            generator.generateAllData(10000);

        } catch (SQLException e) {
            System.err.println("Ошибка при работе с базой данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
}