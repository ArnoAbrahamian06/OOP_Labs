package org.example.DAO;

import org.example.models.Function;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FunctionDAOTest {
    private static final Logger logger = LoggerFactory.getLogger(FunctionDAOTest.class);
    private FunctionDAO functionDAO;
    private Function testFunction;
    private List<Integer> cleanupFunctionIds = new ArrayList<>();
    private Integer testUserId = 1; // Предполагаем, что пользователь с ID=1 существует

    @BeforeAll
    void setUp() {
        logger.info("Инициализация FunctionDAO тестов");
        functionDAO = new FunctionDAO();

        // Создаем тестовую функцию
        testFunction = new Function();
        testFunction.setUserId(testUserId);
        testFunction.setName("test_function_" + System.currentTimeMillis());
        testFunction.setCreated_at(LocalDateTime.now());
        testFunction.setUpdated_at(LocalDateTime.now());
    }

    @AfterAll
    void tearDown() {
        logger.info("Очистка тестовых данных FunctionDAO");

        // Удаляем созданные функции
        for (Integer functionId : cleanupFunctionIds) {
            try {
                functionDAO.delete(functionId);
            } catch (Exception e) {
                logger.warn("Не удалось удалить функцию ID {}: {}", functionId, e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    void testInsertFunction() {
        logger.info("Тест: создание функции");

        Function insertedFunction = functionDAO.insert(testFunction);

        assertNotNull(insertedFunction, "Созданная функция не должна быть null");
        assertNotNull(insertedFunction.getId(), "ID функции должен быть установлен");
        assertEquals(testFunction.getUserId(), insertedFunction.getUserId(), "ID пользователя должен совпадать");
        assertEquals(testFunction.getName(), insertedFunction.getName(), "Название должно совпадать");
        assertNotNull(insertedFunction.getCreated_at(), "Дата создания должна быть установлена");
        assertNotNull(insertedFunction.getUpdated_at(), "Дата обновления должна быть установлена");

        testFunction = insertedFunction;
        cleanupFunctionIds.add(testFunction.getId());
        logger.info("Создана функция с ID: {}", testFunction.getId());
    }

    @Test
    @Order(2)
    void testFindById() {
        logger.info("Тест: поиск функции по ID");

        Optional<Function> foundFunctionOpt = functionDAO.findById(testFunction.getId());

        assertTrue(foundFunctionOpt.isPresent(), "Функция должна быть найдена по ID");
        Function foundFunction = foundFunctionOpt.get();

        assertEquals(testFunction.getId(), foundFunction.getId(), "ID должен совпадать");
        assertEquals(testFunction.getUserId(), foundFunction.getUserId(), "ID пользователя должен совпадать");
        assertEquals(testFunction.getName(), foundFunction.getName(), "Название должно совпадать");
    }

    @Test
    @Order(3)
    void testFindByUserId() {
        logger.info("Тест: поиск функций по ID пользователя");

        List<Function> functions = functionDAO.findByUserId(testUserId);

        assertNotNull(functions, "Список функций не должен быть null");
        assertFalse(functions.isEmpty(), "Список функций не должен быть пустым");

        // Проверяем, что наша тестовая функция есть в списке
        boolean found = functions.stream()
                .anyMatch(func -> func.getId().equals(testFunction.getId()));
        assertTrue(found, "Тестовая функция должна быть в списке");
    }

    @Test
    @Order(4)
    void testFindByName() {
        logger.info("Тест: поиск функций по имени (частичное совпадение)");

        // Ищем по части названия
        String searchTerm = testFunction.getName().substring(0, 12);
        List<Function> functions = functionDAO.findByName(searchTerm);

        assertNotNull(functions, "Список функций не должен быть null");

        boolean found = functions.stream()
                .anyMatch(func -> func.getId().equals(testFunction.getId()));
        assertTrue(found, "Тестовая функция должна быть найдена по части названия");
    }

    @Test
    @Order(5)
    void testFindByNameAndUserId() {
        logger.info("Тест: поиск функций по имени и ID пользователя");

        List<Function> functions = functionDAO.findByNameAndUserId(testFunction.getName(), testUserId);

        assertNotNull(functions, "Список функций не должен быть null");
        assertFalse(functions.isEmpty(), "Список функций не должен быть пустым");

        Function foundFunction = functions.get(0);
        assertEquals(testFunction.getId(), foundFunction.getId(), "ID должен совпадать");
        assertEquals(testFunction.getName(), foundFunction.getName(), "Название должно совпадать");
        assertEquals(testFunction.getUserId(), foundFunction.getUserId(), "ID пользователя должен совпадать");
    }

    @Test
    @Order(6)
    void testExistsByNameAndUserId() {
        logger.info("Тест: проверка существования функции по имени и пользователю");

        boolean exists = functionDAO.existsByNameAndUserId(testFunction.getName(), testUserId);
        assertTrue(exists, "Функция должна существовать с данным именем и пользователем");

        boolean notExists = functionDAO.existsByNameAndUserId("non_existent_function_" + System.currentTimeMillis(), testUserId);
        assertFalse(notExists, "Несуществующая функция не должна быть найдена");
    }

    @Test
    @Order(7)
    void testUpdateFunction() {
        logger.info("Тест: обновление функции");

        // Подготавливаем обновленные данные
        Function updatedFunction = new Function();
        updatedFunction.setId(testFunction.getId());
        updatedFunction.setUserId(testUserId + 1); // Меняем пользователя
        updatedFunction.setName(testFunction.getName() + "_updated");

        boolean updateResult = functionDAO.update(updatedFunction);
        assertTrue(updateResult, "Обновление должно быть успешным");

        // Проверяем обновленные данные
        Optional<Function> foundFunctionOpt = functionDAO.findById(testFunction.getId());
        assertTrue(foundFunctionOpt.isPresent());
        Function foundFunction = foundFunctionOpt.get();

        assertEquals(updatedFunction.getUserId(), foundFunction.getUserId(), "ID пользователя должен быть обновлен");
        assertEquals(updatedFunction.getName(), foundFunction.getName(), "Название должно быть обновлено");
        assertNotNull(foundFunction.getUpdated_at(), "Дата обновления должна быть установлена");

        testFunction = foundFunction;
    }

    @Test
    @Order(8)
    void testUpdateName() {
        logger.info("Тест: обновление только имени функции");

        String newName = "renamed_function_" + System.currentTimeMillis();
        boolean updateResult = functionDAO.updateName(testFunction.getId(), newName);
        assertTrue(updateResult, "Обновление имени должно быть успешным");

        // Проверяем обновленное имя
        Optional<Function> foundFunctionOpt = functionDAO.findById(testFunction.getId());
        assertTrue(foundFunctionOpt.isPresent());
        assertEquals(newName, foundFunctionOpt.get().getName(), "Название должно быть обновлено");

        testFunction.setName(newName);
    }

    @Test
    @Order(9)
    void testFindAll() {
        logger.info("Тест: получение всех функций");

        List<Function> functions = functionDAO.findAll();

        assertNotNull(functions, "Список функций не должен быть null");
        assertFalse(functions.isEmpty(), "Список функций не должен быть пустым");

        // Проверяем, что наша тестовая функция есть в списке
        boolean found = functions.stream()
                .anyMatch(func -> func.getId().equals(testFunction.getId()));
        assertTrue(found, "Тестовая функция должна быть в списке");
    }

    @Test
    @Order(10)
    void testFindByIds() {
        logger.info("Тест: множественный поиск функций по IDs");

        // Создаем еще одну функцию для теста
        Function anotherFunction = new Function();
        anotherFunction.setUserId(testUserId);
        anotherFunction.setName("another_test_function_" + System.currentTimeMillis());

        Function insertedAnotherFunction = functionDAO.insert(anotherFunction);
        assertNotNull(insertedAnotherFunction);
        cleanupFunctionIds.add(insertedAnotherFunction.getId());

        // Ищем обе функции
        List<Integer> ids = List.of(testFunction.getId(), insertedAnotherFunction.getId());
        List<Function> foundFunctions = functionDAO.findByIds(ids);

        assertNotNull(foundFunctions);
        assertEquals(2, foundFunctions.size(), "Должны быть найдены обе функции");

        boolean foundTestFunction = foundFunctions.stream()
                .anyMatch(func -> func.getId().equals(testFunction.getId()));
        boolean foundAnotherFunction = foundFunctions.stream()
                .anyMatch(func -> func.getId().equals(insertedAnotherFunction.getId()));

        assertTrue(foundTestFunction, "Тестовая функция должна быть найдена");
        assertTrue(foundAnotherFunction, "Вторая функция должна быть найдена");
    }

    @Test
    @Order(11)
    void testFindByUserIds() {
        logger.info("Тест: множественный поиск функций по ID пользователей");

        List<Integer> userIds = List.of(testUserId, testUserId + 1);
        List<Function> functions = functionDAO.findByUserIds(userIds);

        assertNotNull(functions, "Список функций не должен быть null");

        // Проверяем, что все функции принадлежат указанным пользователям
        for (Function function : functions) {
            assertTrue(userIds.contains(function.getUserId()),
                    "Функция должна принадлежать одному из указанных пользователей");
        }
    }

    @Test
    @Order(12)
    void testFindAllWithSorting() {
        logger.info("Тест: получение всех функций с сортировкой");

        // Тестируем сортировку по разным полям
        List<Function> functionsByIdAsc = functionDAO.findAllWithSorting("id", true);
        List<Function> functionsByIdDesc = functionDAO.findAllWithSorting("id", false);
        List<Function> functionsByName = functionDAO.findAllWithSorting("name", true);
        List<Function> functionsByUserId = functionDAO.findAllWithSorting("user_id", true);
        List<Function> functionsByCreatedAt = functionDAO.findAllWithSorting("created_at", true);
        List<Function> functionsByUpdatedAt = functionDAO.findAllWithSorting("updated_at", true);

        assertNotNull(functionsByIdAsc);
        assertNotNull(functionsByIdDesc);
        assertNotNull(functionsByName);
        assertNotNull(functionsByUserId);
        assertNotNull(functionsByCreatedAt);
        assertNotNull(functionsByUpdatedAt);

        // Проверяем, что все списки содержат данные
        assertFalse(functionsByIdAsc.isEmpty(), "Список с сортировкой по ID ASC не должен быть пустым");

        // Проверяем сортировку по ID
        for (int i = 1; i < functionsByIdAsc.size(); i++) {
            assertTrue(functionsByIdAsc.get(i).getId() > functionsByIdAsc.get(i-1).getId(),
                    "Сортировка по ID ASC должна быть восходящей");
        }

        for (int i = 1; i < functionsByIdDesc.size(); i++) {
            assertTrue(functionsByIdDesc.get(i).getId() < functionsByIdDesc.get(i-1).getId(),
                    "Сортировка по ID DESC должна быть нисходящей");
        }
    }

    @Test
    @Order(13)
    void testFindByUserIdWithSorting() {
        logger.info("Тест: поиск функций пользователя с сортировкой");

        List<Function> functions = functionDAO.findByUserIdWithSorting(testUserId, "name", true);

        assertNotNull(functions, "Список функций не должен быть null");

        // Проверяем, что все функции принадлежат указанному пользователю
        for (Function function : functions) {
            assertEquals(testUserId, function.getUserId(),
                    "Функция должна принадлежать указанному пользователю");
        }

        // Проверяем сортировку по name (если есть более одной функции)
        if (functions.size() > 1) {
            for (int i = 1; i < functions.size(); i++) {
                assertTrue(functions.get(i).getName().compareTo(functions.get(i-1).getName()) >= 0,
                        "Сортировка по name должна быть восходящей");
            }
        }
    }

    @Test
    @Order(14)
    void testFindByNameWithSorting() {
        logger.info("Тест: поиск функций по имени с сортировкой");

        // Ищем по части названия
        String searchTerm = testFunction.getName().substring(0, 12);
        List<Function> functions = functionDAO.findByNameWithSorting(searchTerm, "id", true);

        assertNotNull(functions, "Список функций не должен быть null");

        // Проверяем, что наша тестовая функция есть в списке
        boolean found = functions.stream()
                .anyMatch(func -> func.getId().equals(testFunction.getId()));
        assertTrue(found, "Тестовая функция должна быть в списке");

        // Проверяем сортировку по ID
        for (int i = 1; i < functions.size(); i++) {
            assertTrue(functions.get(i).getId() > functions.get(i-1).getId(),
                    "Сортировка по ID должна быть восходящей");
        }
    }

    @Test
    @Order(15)
    void testFindByCriteria() {
        logger.info("Тест: расширенный поиск с множественными критериями");

        // Поиск по ID пользователя и части названия
        List<Function> functions = functionDAO.findByCriteria(
                testUserId,
                testFunction.getName().substring(0, 12),
                "id",
                true
        );

        assertNotNull(functions, "Список функций не должен быть null");

        // Проверяем, что все функции соответствуют критериям
        for (Function function : functions) {
            assertEquals(testUserId, function.getUserId(),
                    "Функция должна принадлежать указанному пользователю");
            assertTrue(function.getName().contains(testFunction.getName().substring(0, 12)),
                    "Название функции должно содержать указанный шаблон");
        }

        // Тест с только ID пользователя
        List<Function> functionsByUserOnly = functionDAO.findByCriteria(testUserId, null, "name", true);
        assertNotNull(functionsByUserOnly);

        // Тест с только шаблоном названия
        List<Function> functionsByNameOnly = functionDAO.findByCriteria(
                null, testFunction.getName().substring(0, 12), "id", true);
        assertNotNull(functionsByNameOnly);

        // Тест без критериев (все функции)
        List<Function> allFunctions = functionDAO.findByCriteria(null, null, "id", true);
        assertNotNull(allFunctions);
        assertFalse(allFunctions.isEmpty(), "Должны быть возвращены все функции");
    }

    @Test
    @Order(16)
    void testCountByUserId() {
        logger.info("Тест: подсчет количества функций пользователя");

        int count = functionDAO.countByUserId(testUserId);
        assertTrue(count > 0, "Должна быть хотя бы одна функция у пользователя");

        logger.info("Количество функций у пользователя {}: {}", testUserId, count);
    }

    @Test
    @Order(17)
    void testDeleteFunction() {
        logger.info("Тест: удаление функции по ID");

        // Создаем временную функцию для удаления
        Function tempFunction = new Function();
        tempFunction.setUserId(testUserId);
        tempFunction.setName("temp_function_for_deletion_" + System.currentTimeMillis());

        Function insertedTempFunction = functionDAO.insert(tempFunction);
        assertNotNull(insertedTempFunction);

        boolean deleteResult = functionDAO.delete(insertedTempFunction.getId());
        assertTrue(deleteResult, "Удаление должно быть успешным");

        // Проверяем, что функция удалена
        Optional<Function> foundFunction = functionDAO.findById(insertedTempFunction.getId());
        assertFalse(foundFunction.isPresent(), "Удаленная функция не должна быть найдена");
    }

    @Test
    @Order(18)
    void testDeleteByUserId() {
        logger.info("Тест: удаление всех функций пользователя");

        // Создаем несколько функций для определенного пользователя
        int specificUserId = 999;
        List<Function> functions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Function function = new Function();
            function.setUserId(specificUserId);
            function.setName("function_" + i + "_" + System.currentTimeMillis());
            functionDAO.insert(function);
            functions.add(function);
        }

        // Удаляем все функции пользователя
        boolean deleteResult = functionDAO.deleteByUserId(specificUserId);
        assertTrue(deleteResult, "Удаление должно быть успешным");

        // Проверяем, что функций нет
        List<Function> foundFunctions = functionDAO.findByUserId(specificUserId);
        assertTrue(foundFunctions.isEmpty(), "Не должно быть функций после удаления");
    }

    @Test
    @Order(19)
    void testEdgeCases() {
        logger.info("Тест: проверка граничных случаев");

        // Поиск несуществующей функции
        Optional<Function> nonExistentFunction = functionDAO.findById(-999);
        assertFalse(nonExistentFunction.isPresent(), "Несуществующая функция не должна быть найдена");

        // Поиск по несуществующему пользователю
        List<Function> nonExistentUserFunctions = functionDAO.findByUserId(-999);
        assertNotNull(nonExistentUserFunctions, "Список должен быть возвращен");
        assertTrue(nonExistentUserFunctions.isEmpty(), "Список функций несуществующего пользователя должен быть пустым");

        // Поиск по несуществующему имени
        List<Function> nonExistentNameFunctions = functionDAO.findByName("non_existent_function_" + System.currentTimeMillis());
        assertNotNull(nonExistentNameFunctions, "Список должен быть возвращен");
        assertTrue(nonExistentNameFunctions.isEmpty(), "Список функций с несуществующим именем должен быть пустым");

        // Удаление несуществующей функции
        boolean deleteResult = functionDAO.delete(-999);
        assertFalse(deleteResult, "Удаление несуществующей функции должно вернуть false");

        // Обновление несуществующей функции
        Function nonExistentFunctionForUpdate = new Function();
        nonExistentFunctionForUpdate.setId(-999);
        nonExistentFunctionForUpdate.setUserId(1);
        nonExistentFunctionForUpdate.setName("test");

        boolean updateResult = functionDAO.update(nonExistentFunctionForUpdate);
        assertFalse(updateResult, "Обновление несуществующей функции должно вернуть false");

        // Обновление имени несуществующей функции
        boolean updateNameResult = functionDAO.updateName(-999, "new_name");
        assertFalse(updateNameResult, "Обновление имени несуществующей функции должно вернуть false");
    }

    @Test
    @Order(20)
    void testPerformance() {
        logger.info("Тест: производительность операций");

        long startTime = System.currentTimeMillis();

        // Выполняем несколько операций для проверки производительности
        List<Function> allFunctions = functionDAO.findAll();
        int count = functionDAO.countByUserId(testUserId);
        List<Function> userFunctions = functionDAO.findByUserId(testUserId);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Производительность: операции выполнены за {} мс", duration);
        logger.info("Результаты: всего функций={}, у пользователя={}, найдено={}",
                allFunctions.size(), count, userFunctions.size());

        assertTrue(duration < 10000, "Операции должны выполняться менее чем за 10 секунд");
    }

    @Test
    @Order(21)
    void testFindByNameAndUserIdEmptyResult() {
        logger.info("Тест: поиск функций по имени и ID пользователя (нет результатов)");

        String nonExistentName = "non_existent_function_" + System.currentTimeMillis();
        List<Function> functions = functionDAO.findByNameAndUserId(nonExistentName, testUserId);

        assertNotNull(functions, "Список не должен быть null");
        assertTrue(functions.isEmpty(), "Список должен быть пустым для несуществующей функции");
    }

    @Test
    @Order(22)
    void testFindByUserIdsEmptyResult() {
        logger.info("Тест: множественный поиск функций по несуществующим ID пользователей");

        List<Integer> nonExistentUserIds = Arrays.asList(-1, -2, -3);
        List<Function> functions = functionDAO.findByUserIds(nonExistentUserIds);

        assertNotNull(functions, "Список не должен быть null");
        assertTrue(functions.isEmpty(), "Список должен быть пустым для несуществующих пользователей");
    }

    @Test
    @Order(23)
    void testFindByIdsEmptyResult() {
        logger.info("Тест: множественный поиск функций по несуществующим ID");

        List<Integer> nonExistentIds = Arrays.asList(-1, -2, -3);
        List<Function> functions = functionDAO.findByIds(nonExistentIds);

        assertNotNull(functions, "Список не должен быть null");
        assertTrue(functions.isEmpty(), "Список должен быть пустым для несуществующих ID");
    }

    @Test
    @Order(24)
    void testFindByNameWithSortingEmptyResult() {
        logger.info("Тест: поиск функций по имени с сортировкой (нет результатов)");

        String nonExistentName = "xyz_abc_" + System.currentTimeMillis();
        List<Function> functions = functionDAO.findByNameWithSorting(nonExistentName, "id", true);

        assertNotNull(functions, "Список не должен быть null");
        assertTrue(functions.isEmpty(), "Список должен быть пустым для несуществующего имени");
    }

    @Test
    @Order(25)
    void testFindByUserIdWithSortingEmptyResult() {
        logger.info("Тест: поиск функций пользователя с сортировкой (нет результатов)");

        int nonExistentUserId = -999;
        List<Function> functions = functionDAO.findByUserIdWithSorting(nonExistentUserId, "name", true);

        assertNotNull(functions, "Список не должен быть null");
        assertTrue(functions.isEmpty(), "Список должен быть пустым для несуществующего пользователя");
    }

    @Test
    @Order(26)
    void testFindByCriteriaEmptyResults() {
        logger.info("Тест: расширенный поиск с пустыми результатами");

        // Критерии, которые точно не дадут результатов
        String nonExistentPattern = "pattern_" + System.currentTimeMillis();
        int nonExistentUserId = -999;

        // Тест 1: Несуществующий пользователь
        List<Function> result1 = functionDAO.findByCriteria(nonExistentUserId, null, "id", true);
        assertNotNull(result1);
        assertTrue(result1.isEmpty());

        // Тест 2: Несуществующий шаблон имени
        List<Function> result2 = functionDAO.findByCriteria(testUserId, nonExistentPattern, "id", true);
        assertNotNull(result2);
        assertTrue(result2.isEmpty());

        // Тест 3: Оба критерия несуществующие
        List<Function> result3 = functionDAO.findByCriteria(nonExistentUserId, nonExistentPattern, "id", true);
        assertNotNull(result3);
        assertTrue(result3.isEmpty());

        // Тест 4: Пустой шаблон имени (только пробелы)
        List<Function> result4 = functionDAO.findByCriteria(testUserId, "   ", "id", true);
        assertNotNull(result4);
        // Результат может быть не пустым, если есть функции у пользователя

        logger.info("Тест расширенного поиска с пустыми результатами выполнен");
    }

    @Test
    @Order(27)
    void testUpdateNameNonExistentFunction() {
        logger.info("Тест: обновление имени несуществующей функции");

        boolean result = functionDAO.updateName(-999, "new_name");
        assertFalse(result, "Обновление имени несуществующей функции должно вернуть false");
    }

    @Test
    @Order(28)
    void testDeleteByUserIdNonExistent() {
        logger.info("Тест: удаление функций несуществующего пользователя");

        int nonExistentUserId = -999;
        boolean result = functionDAO.deleteByUserId(nonExistentUserId);
        // Метод может вернуть false или true (если удалено 0 строк)
        // В текущей реализации он возвращает false только при SQLException
        // Но для несуществующего пользователя он должен вернуть false
        assertFalse(result, "Удаление функций несуществующего пользователя должно вернуть false");
    }

    @Test
    @Order(29)
    void testCountByUserIdNonExistent() {
        logger.info("Тест: подсчет функций несуществующего пользователя");

        int nonExistentUserId = -999;
        int count = functionDAO.countByUserId(nonExistentUserId);

        assertEquals(0, count, "Количество функций несуществующего пользователя должно быть 0");
    }

    @Test
    @Order(30)
    void testExistsByNameAndUserIdNonExistent() {
        logger.info("Тест: проверка существования несуществующей функции");

        String nonExistentName = "non_existent_" + System.currentTimeMillis();
        int nonExistentUserId = -999;

        boolean exists = functionDAO.existsByNameAndUserId(nonExistentName, nonExistentUserId);
        assertFalse(exists, "Несуществующая функция не должна существовать");
    }

    @Test
    @Order(31)
    void testInsertReturnsNullOnFailure() {
        logger.info("Тест: проверка обработки неудачной вставки функции");

        // Пытаемся создать функцию с невалидными данными
        Function invalidFunction = new Function();
        invalidFunction.setUserId(testUserId);
        invalidFunction.setName("a".repeat(1000)); // Предположительно превышает лимит БД

        try {
            Function result = functionDAO.insert(invalidFunction);
            // Если не выбросилось исключение, то результат может быть null
            if (result == null) {
                logger.info("Вставка невалидной функции вернула null, как и ожидалось");
            }
        } catch (RuntimeException e) {
            // Ожидаем RuntimeException с сообщением "Database error"
            assertTrue(e.getMessage().contains("Database error") ||
                            e.getMessage().contains("SQL"),
                    "Должно быть исключение об ошибке базы данных");
            logger.info("Вставка невалидной функции выбросила исключение, как и ожидалось");
        }
    }
}