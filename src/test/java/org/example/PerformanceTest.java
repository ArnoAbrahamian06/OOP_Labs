package org.example.repository;

import org.example.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class PerformanceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private FunctionTypeRepository functionTypeRepository;

    private static final int TOTAL_USERS = 10000;
    private static final int TOTAL_TABULATED_FUNCTIONS = 15000;
    private static final int TOTAL_FUNCTION_TYPES = 20000;
    private static final int WARMUP_ITERATIONS = 5;
    private static final int TEST_ITERATIONS = 10;

    private final Random random = new Random();
    private final List<Long> userIds = new ArrayList<>();
    private final List<Long> functionIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед тестами
        functionTypeRepository.deleteAll();
        tabulatedFunctionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @Rollback(false) // Отключаем rollback чтобы данные сохранились для следующих тестов
    void populateTestData() {
        System.out.println("=== Генерация тестовых данных ===");

        // Генерация пользователей
        long startTime = System.currentTimeMillis();
        List<User> users = new ArrayList<>();
        for (int i = 0; i < TOTAL_USERS; i++) {
            User user = new User(
                    "user_" + i,
                    "user_" + i + "@test.com",
                    "password_hash_" + i
            );
            if (i % 10 == 0) user.setRole(Role.ADMIN);
            else if (i % 7 == 0) user.setRole(Role.MODERATOR);
            users.add(user);
        }
        userRepository.saveAll(users);
        long userTime = System.currentTimeMillis() - startTime;
        System.out.printf("Создано %d пользователей за %d мс%n", TOTAL_USERS, userTime);

        // Сохраняем ID пользователей для использования в следующих тестах
        userIds.addAll(userRepository.findAll().stream()
                .map(User::getId)
                .toList());

        // Генерация табулированных функций
        startTime = System.currentTimeMillis();
        List<Tabulated_function> functions = new ArrayList<>();
        for (int i = 0; i < TOTAL_TABULATED_FUNCTIONS; i++) {
            User randomUser = users.get(random.nextInt(users.size()));
            Tabulated_function function = new Tabulated_function(
                    "serialized_data_" + i + "_" + System.currentTimeMillis(),
                    randomUser
            );
            functions.add(function);
        }
        tabulatedFunctionRepository.saveAll(functions);
        long functionTime = System.currentTimeMillis() - startTime;
        System.out.printf("Создано %d табулированных функций за %d мс%n", TOTAL_TABULATED_FUNCTIONS, functionTime);

        // Сохраняем ID функций
        functionIds.addAll(tabulatedFunctionRepository.findAll().stream()
                .map(Tabulated_function::getId)
                .toList());

        // Генерация типов функций
        startTime = System.currentTimeMillis();
        List<Function_type> functionTypes = new ArrayList<>();
        String[] functionNames = {"linear", "quadratic", "exponential", "logarithmic", "trigonometric"};

        for (int i = 0; i < TOTAL_FUNCTION_TYPES; i++) {
            Tabulated_function randomFunction = functions.get(random.nextInt(functions.size()));
            String functionName = functionNames[random.nextInt(functionNames.length)];

            Function_type functionType = new Function_type(
                    functionName,
                    "Локализованное имя " + functionName,
                    random.nextInt(10),
                    randomFunction
            );
            functionTypes.add(functionType);
        }
        functionTypeRepository.saveAll(functionTypes);
        long typeTime = System.currentTimeMillis() - startTime;
        System.out.printf("Создано %d типов функций за %d мс%n", TOTAL_FUNCTION_TYPES, typeTime);

        System.out.println("=== Данные сгенерированы ===");
    }

    @Test
    @Order(2)
    void testUserRepositoryQueries() {
        System.out.println("\n=== Тестирование UserRepository ===");

        // Прогрев JPA
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            userRepository.findByUsername("user_5000");
        }

        // Тест 1: Поиск по username
        measureTime("UserRepository.findByUsername", () -> {
            userRepository.findByUsername("user_5000");
        });

        // Тест 2: Поиск по email
        measureTime("UserRepository.findByEmail", () -> {
            userRepository.findByEmail("user_5000@test.com");
        });

        // Тест 3: Поиск по роли
        measureTime("UserRepository.findByRole", () -> {
            userRepository.findByRole("ADMIN");
        });

        // Тест 4: Проверка существования
        measureTime("UserRepository.existsByUsername", () -> {
            userRepository.existsByUsername("user_7500");
        });

        // Тест 5: Поиск по части username
        measureTime("UserRepository.findByUsernameContaining", () -> {
            userRepository.findByUsernameContaining("user_100");
        });
    }

    @Test
    @Order(3)
    void testTabulatedFunctionRepositoryQueries() {
        System.out.println("\n=== Тестирование TabulatedFunctionRepository ===");

        Long randomUserId = userIds.get(random.nextInt(userIds.size()));
        Long randomFunctionId = functionIds.get(random.nextInt(functionIds.size()));

        // Прогрев
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            tabulatedFunctionRepository.findByUserId(randomUserId);
        }

        // Тест 1: Поиск по пользователю
        measureTime("TabulatedFunctionRepository.findByUserId", () -> {
            tabulatedFunctionRepository.findByUserId(randomUserId);
        });

        // Тест 2: Поиск по содержанию данных
        measureTime("TabulatedFunctionRepository.findBySerializedDataContaining", () -> {
            tabulatedFunctionRepository.findBySerializedDataContaining("serialized_data_5000");
        });

        // Тест 3: Поиск по дате создания
        measureTime("TabulatedFunctionRepository.findByCreatedAtAfter", () -> {
            tabulatedFunctionRepository.findByCreatedAtAfter(LocalDateTime.now().minusDays(1));
        });

        // Тест 4: Поиск с минимальным количеством типов
        measureTime("TabulatedFunctionRepository.findWithMinFunctionTypes", () -> {
            tabulatedFunctionRepository.findWithMinFunctionTypes(1);
        });

        // Тест 5: Поиск по имени типа функции
        measureTime("TabulatedFunctionRepository.findByFunctionTypeName", () -> {
            tabulatedFunctionRepository.findByFunctionTypeName("linear");
        });
    }

    @Test
    @Order(4)
    void testFunctionTypeRepositoryQueries() {
        System.out.println("\n=== Тестирование FunctionTypeRepository ===");

        Long randomFunctionId = functionIds.get(random.nextInt(functionIds.size()));

        // Прогрев
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            functionTypeRepository.findByName("linear");
        }

        // Тест 1: Поиск по имени
        measureTime("FunctionTypeRepository.findByName", () -> {
            functionTypeRepository.findByName("linear");
        });

        // Тест 2: Поиск по приоритету
        measureTime("FunctionTypeRepository.findByPriority", () -> {
            functionTypeRepository.findByPriority(5);
        });

        // Тест 3: Поиск по ID табулированной функции
        measureTime("FunctionTypeRepository.findByTabulatedFunctionId", () -> {
            functionTypeRepository.findByTabulatedFunctionId(randomFunctionId);
        });

        // Тест 4: Поиск по диапазону приоритетов
        measureTime("FunctionTypeRepository.findByPriorityBetween", () -> {
            functionTypeRepository.findByPriorityBetween(3, 7);
        });

        // Тест 5: Поиск по части имени
        measureTime("FunctionTypeRepository.findByNameContaining", () -> {
            functionTypeRepository.findByNameContaining("exp");
        });
    }

    @Test
    @Order(5)
    void testComplexQueries() {
        System.out.println("\n=== Тестирование сложных запросов ===");

        // Тест 1: Подсчет пользователей по роли
        measureTime("UserRepository.countByRole", () -> {
            userRepository.countByRole("USER");
        });

        // Тест 2: Подсчет типов функций
        measureTime("FunctionTypeRepository.countByTabulatedFunctionId", () -> {
            if (!functionIds.isEmpty()) {
                functionTypeRepository.countByTabulatedFunctionId(functionIds.get(0));
            }
        });

        // Тест 3: Поиск пользователей по дате с пагинацией
        measureTime("UserRepository.findByCreatedAtAfter with pagination", () -> {
            userRepository.findByCreatedAtAfter(LocalDateTime.now().minusYears(1))
                    .stream()
                    .limit(100)
                    .toList();
        });
    }

    private void measureTime(String testName, Runnable test) {
        System.out.println("\n--- " + testName + " ---");

        // Прогрев
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            test.run();
        }

        // Основные измерения
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            test.run();
            long endTime = System.nanoTime();

            long duration = TimeUnit.NANOSECONDS.toMicros(endTime - startTime);
            totalTime += duration;
            minTime = Math.min(minTime, duration);
            maxTime = Math.max(maxTime, duration);
        }

        long avgTime = totalTime / TEST_ITERATIONS;

        System.out.printf("Среднее время: %d мкс\n", avgTime);
        System.out.printf("Минимальное время: %d мкс\n", minTime);
        System.out.printf("Максимальное время: %d мкс\n", maxTime);
        System.out.printf("Общее время (%d итераций): %d мкс\n", TEST_ITERATIONS, totalTime);
    }

    @AfterAll
    static void printSummary() {
        System.out.println("\n=== Сводка по тестированию ===");
        System.out.println("Для улучшения производительности:");
        System.out.println("1. Добавьте индексы на часто используемые поля");
        System.out.println("2. Используйте пагинацию для больших результатов");
        System.out.println("3. Избегайте LIKE '%pattern%' для больших таблиц");
        System.out.println("4. Используйте кэширование для часто читаемых данных");
    }
}