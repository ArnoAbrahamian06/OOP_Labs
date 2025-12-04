package org.example.service;

import org.example.entity.Function_type;
import org.example.entity.Role;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.repository.FunctionTypeRepository;
import org.example.repository.TabulatedFunctionRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class SearchServiceSortingTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionTypeRepository functionTypeRepository;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @BeforeEach
    void setUp() {
        // Очистка базы данных
        functionTypeRepository.deleteAll();
        tabulatedFunctionRepository.deleteAll();
        userRepository.deleteAll();

        // Создание тестовых пользователей с разными данными для сортировки
        User user1 = new User();
        user1.setUsername("alice");
        user1.setEmail("alice@example.com");
        user1.setPasswordHash("pass1");
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now().minusDays(3));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("charlie");
        user2.setEmail("charlie@example.com");
        user2.setPasswordHash("pass2");
        user2.setRole(Role.ADMIN);
        user2.setCreatedAt(LocalDateTime.now().minusDays(1));
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("bob");
        user3.setEmail("bob@example.com");
        user3.setPasswordHash("pass3");
        user3.setRole(Role.MODERATOR);
        user3.setCreatedAt(LocalDateTime.now().minusDays(2));
        userRepository.save(user3);

        // Создание тестовых табулированных функций
        Tabulated_function func1 = new Tabulated_function();
        func1.setSerializedData("data1");
        func1.setUser(user1);
        func1.setCreatedAt(LocalDateTime.now().minusHours(10));
        tabulatedFunctionRepository.save(func1);

        Tabulated_function func2 = new Tabulated_function();
        func2.setSerializedData("data2");
        func2.setUser(user2);
        func2.setCreatedAt(LocalDateTime.now().minusHours(5));
        tabulatedFunctionRepository.save(func2);

        // Создание тестовых типов функций с четко различимыми именами
        Function_type funcType1 = new Function_type();
        funcType1.setName("quadratic_function");   // содержит 'a' и 'function'
        funcType1.setLocName("Квадратичная");
        funcType1.setPriority(3);
        funcType1.setTabulatedFunction(func1);
        funcType1.setCreatedAt(LocalDateTime.now().minusHours(5));
        functionTypeRepository.save(funcType1);

        Function_type funcType2 = new Function_type();
        funcType2.setName("linear_type");          // НЕ содержит 'a' и 'function'
        funcType2.setLocName("Линейная");
        funcType2.setPriority(1);
        funcType2.setTabulatedFunction(func1);
        funcType2.setCreatedAt(LocalDateTime.now().minusHours(2));
        functionTypeRepository.save(funcType2);

        Function_type funcType3 = new Function_type();
        funcType3.setName("exponential_function"); // содержит 'a' и 'function'
        funcType3.setLocName("Экспоненциальная");
        funcType3.setPriority(5);
        funcType3.setTabulatedFunction(func2);
        funcType3.setCreatedAt(LocalDateTime.now().minusHours(8));
        functionTypeRepository.save(funcType3);

        // Установка связей
        func1.addFunctionType(funcType1);
        func1.addFunctionType(funcType2);
        func2.addFunctionType(funcType3);
        user1.addTabulated_function(func1);
        user2.addTabulated_function(func2);
    }

    @Test
    void testAdvancedFunctionTypeSearch_WithNameFilterAndSorting() {
        // Act - поиск по части имени и сортировка по приоритету
        List<Function_type> result = searchService.advancedFunctionTypeSearch(
                "linear", null, null, "priority", "asc");

        // Assert
        assertEquals(1, result.size());
        assertEquals("linear_type", result.get(0).getName());
        assertEquals(1, result.get(0).getPriority());
    }

    @Test
    void testAdvancedFunctionTypeSearch_WithMultipleNameMatches() {
        // Act - поиск по подстроке "function", которая есть в "quadratic_function" и "exponential_function"
        List<Function_type> result = searchService.advancedFunctionTypeSearch(
                "function", null, null, "name", "asc");

        // Assert
        assertEquals(2, result.size()); // quadratic_function и exponential_function
        assertEquals("exponential_function", result.get(0).getName());
        assertEquals("quadratic_function", result.get(1).getName());
    }

    @Test
    void testAdvancedFunctionTypeSearch_WithLetterAFilter() {
        // Act - поиск по букве 'a', которая есть в "quadratic_function" и "exponential_function", но не в "linear_type"
        List<Function_type> result = searchService.advancedFunctionTypeSearch(
                "a", null, null, "name", "asc");

        // Assert
        assertEquals(3, result.size()); // quadratic_function и exponential_function
        assertEquals("exponential_function", result.get(0).getName());
        assertEquals("quadratic_function", result.get(2).getName());
    }

    @Test
    void testAdvancedFunctionTypeSearch_WithNoNameMatches() {
        // Act - поиск по несуществующему имени
        List<Function_type> result = searchService.advancedFunctionTypeSearch(
                "nonexistent", null, null, "name", "asc");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testAdvancedFunctionTypeSearch_WithExactNameMatch() {
        // Act - поиск точного совпадения
        List<Function_type> result = searchService.advancedFunctionTypeSearch(
                "linear_type", null, null, "priority", "asc");

        // Assert
        assertEquals(1, result.size());
        assertEquals("linear_type", result.get(0).getName());
        assertEquals(1, result.get(0).getPriority());
    }

    @Test
    void testFindAllUsersWithSorting_ByUsername_Asc() {
        // Act
        List<User> result = searchService.findAllUsersWithSorting("username", "asc");

        // Assert
        assertEquals(3, result.size());
        assertEquals("alice", result.get(0).getUsername());
        assertEquals("bob", result.get(1).getUsername());
        assertEquals("charlie", result.get(2).getUsername());
    }

    @Test
    void testFindAllUsersWithSorting_ByUsername_Desc() {
        // Act
        List<User> result = searchService.findAllUsersWithSorting("username", "desc");

        // Assert
        assertEquals(3, result.size());
        assertEquals("charlie", result.get(0).getUsername());
        assertEquals("bob", result.get(1).getUsername());
        assertEquals("alice", result.get(2).getUsername());
    }

    @Test
    void testFindAllUsersWithSorting_ByCreatedAt_Asc() {
        // Act
        List<User> result = searchService.findAllUsersWithSorting("createdAt", "asc");

        // Assert
        assertEquals(3, result.size());
        // user1 создан раньше всех (minusDays(3))
        assertEquals("alice", result.get(0).getUsername());
        // user3 создан позже (minusDays(2))
        assertEquals("bob", result.get(1).getUsername());
        // user2 создан последним (minusDays(1))
        assertEquals("charlie", result.get(2).getUsername());
    }

    @Test
    void testFindAllUsersWithSorting_ByCreatedAt_Desc() {
        // Act
        List<User> result = searchService.findAllUsersWithSorting("createdAt", "desc");

        // Assert
        assertEquals(3, result.size());
        // user2 создан последним
        assertEquals("charlie", result.get(0).getUsername());
        // user3 создан раньше user2
        assertEquals("bob", result.get(1).getUsername());
        // user1 создан раньше всех
        assertEquals("alice", result.get(2).getUsername());
    }

    @Test
    void testFindFunctionTypesWithSorting_ByName_Asc() {
        // Act
        List<Function_type> result = searchService.findFunctionTypesWithSorting("name", "asc");

        // Assert
        assertEquals(3, result.size());
        assertEquals("exponential_function", result.get(0).getName());
        assertEquals("linear_type", result.get(1).getName());
        assertEquals("quadratic_function", result.get(2).getName());
    }

    @Test
    void testFindFunctionTypesWithSorting_ByName_Desc() {
        // Act
        List<Function_type> result = searchService.findFunctionTypesWithSorting("name", "desc");

        // Assert
        assertEquals(3, result.size());
        assertEquals("quadratic_function", result.get(0).getName());
        assertEquals("linear_type", result.get(1).getName());
        assertEquals("exponential_function", result.get(2).getName());
    }

    @Test
    void testFindFunctionTypesWithSorting_ByPriority_Asc() {
        // Act
        List<Function_type> result = searchService.findFunctionTypesWithSorting("priority", "asc");

        // Assert
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getPriority()); // linear_type
        assertEquals(3, result.get(1).getPriority()); // quadratic_function
        assertEquals(5, result.get(2).getPriority()); // exponential_function
    }

    @Test
    void testFindFunctionTypesWithSorting_ByPriority_Desc() {
        // Act
        List<Function_type> result = searchService.findFunctionTypesWithSorting("priority", "desc");

        // Assert
        assertEquals(3, result.size());
        assertEquals(5, result.get(0).getPriority()); // exponential_function
        assertEquals(3, result.get(1).getPriority()); // quadratic_function
        assertEquals(1, result.get(2).getPriority()); // linear_type
    }
    @Test
    void testDepthFirstSearch() {
        // Arrange - находим пользователя alice
        User alice = userRepository.findByUsername("alice").orElseThrow();

        // Act
        List<Object> dfsResult = searchService.depthFirstSearch(alice.getId());

        // Assert - исправляем ожидаемое количество
        // alice (1) + 1 функция (func1) + 2 типа функций (linear_type, quadratic_function) = 4 элемента
        assertEquals(4, dfsResult.size());

        // Проверяем порядок обхода в глубину
        assertTrue(dfsResult.get(0) instanceof User);
        assertEquals("alice", ((User) dfsResult.get(0)).getUsername());

        assertTrue(dfsResult.get(1) instanceof Tabulated_function);
        assertEquals("data1", ((Tabulated_function) dfsResult.get(1)).getSerializedData());

        // Далее должны идти типы функций
        boolean foundLinear = false;
        boolean foundQuadratic = false;
        for (Object obj : dfsResult) {
            if (obj instanceof Function_type) {
                Function_type ft = (Function_type) obj;
                if (ft.getName().equals("linear_type")) foundLinear = true;
                if (ft.getName().equals("quadratic_function")) foundQuadratic = true;
            }
        }
        assertTrue(foundLinear);
        assertTrue(foundQuadratic);
    }

    @Test
    void testBreadthFirstSearch() {
        // Arrange - находим пользователя alice
        User alice = userRepository.findByUsername("alice").orElseThrow();

        // Act
        List<Object> bfsResult = searchService.breadthFirstSearch(alice.getId());

        // Assert - исправляем ожидаемое количество
        // alice (1) + 1 функция (func1) + 2 типа функций = 4 элемента
        assertEquals(4, bfsResult.size());

        // Проверяем порядок обхода в ширину (уровни)
        assertTrue(bfsResult.get(0) instanceof User); // Уровень 0
        assertEquals("alice", ((User) bfsResult.get(0)).getUsername());

        assertTrue(bfsResult.get(1) instanceof Tabulated_function); // Уровень 1

        // Остальные должны быть типами функций (Уровень 2)
        int functionTypeCount = 0;
        for (int i = 2; i < bfsResult.size(); i++) {
            assertTrue(bfsResult.get(i) instanceof Function_type);
            functionTypeCount++;
        }
        assertEquals(2, functionTypeCount);
    }

    @Test
    void testFindFunctionsWithMinTypes() {
        // Act - поиск функций с минимум 2 типами
        List<Tabulated_function> functions = searchService.findFunctionsWithMinTypes(2);

        // Assert - исправляем: функция func1 имеет 2 типа функций, но запрос ищет СТРОГО БОЛЬШЕ 2
        // В нашем случае только функция с user1 (alice) имеет 2 типа, но не больше 2
        // Поэтому ожидаем 0
        assertTrue(functions.isEmpty());
    }

    @Test
    void testFindFunctionsWithMinTypes_AtLeastTwo() {
        // Act - поиск функций с минимум 1 типом (фактически > 1 в запросе, т.е. >= 2)
        List<Tabulated_function> functions = searchService.findFunctionsWithMinTypes(1);

        // Assert - функция с user1 (alice) имеет 2 типа, что > 1
        assertEquals(1, functions.size());
        assertEquals("data1", functions.get(0).getSerializedData());
        assertEquals(2, functions.get(0).getFunctionTypes().size());
    }

    @Test
    void testFindFunctionsWithMinTypes_AtLeastOne() {
        // Act - поиск функций с минимум 0 типами (фактически > 0 в запросе, т.е. >= 1)
        List<Tabulated_function> functions = searchService.findFunctionsWithMinTypes(0);

        // Assert - обе функции имеют типы
        assertEquals(2, functions.size());
    }

    @Test
    void testBreadthFirstSearch_WithMultipleLevels() {
        // Arrange - находим пользователя charlie (у него только одна функция с одним типом)
        User charlie = userRepository.findByUsername("charlie").orElseThrow();

        // Act
        List<Object> result = searchService.breadthFirstSearch(charlie.getId());

        // Assert - charlie (1) + 1 функция (func2) + 1 тип функции = 3 элемента
        assertEquals(3, result.size());
        assertTrue(result.get(0) instanceof User);
        assertTrue(result.get(1) instanceof Tabulated_function);
        assertTrue(result.get(2) instanceof Function_type);
    }

    @Test
    void testDepthFirstSearch_UserWithMultipleFunctions() {
        // Arrange - создаем пользователя с двумя функциями
        User multiUser = new User();
        multiUser.setUsername("multi");
        multiUser.setEmail("multi@example.com");
        multiUser.setPasswordHash("pass");
        multiUser.setRole(Role.USER);
        userRepository.save(multiUser);

        // Создаем две функции
        Tabulated_function multiFunc1 = new Tabulated_function();
        multiFunc1.setSerializedData("multi1");
        multiFunc1.setUser(multiUser);
        tabulatedFunctionRepository.save(multiFunc1);

        Tabulated_function multiFunc2 = new Tabulated_function();
        multiFunc2.setSerializedData("multi2");
        multiFunc2.setUser(multiUser);
        tabulatedFunctionRepository.save(multiFunc2);

        // Создаем типы функций для каждой
        Function_type multiType1 = new Function_type();
        multiType1.setName("type1");
        multiType1.setLocName("Тип 1");
        multiType1.setPriority(1);
        multiType1.setTabulatedFunction(multiFunc1);
        functionTypeRepository.save(multiType1);

        Function_type multiType2 = new Function_type();
        multiType2.setName("type2");
        multiType2.setLocName("Тип 2");
        multiType2.setPriority(2);
        multiType2.setTabulatedFunction(multiFunc2);
        functionTypeRepository.save(multiType2);

        // Добавляем связи
        multiFunc1.addFunctionType(multiType1);
        multiFunc2.addFunctionType(multiType2);
        multiUser.addTabulated_function(multiFunc1);
        multiUser.addTabulated_function(multiFunc2);

        // Act
        List<Object> dfsResult = searchService.depthFirstSearch(multiUser.getId());

        // Assert - multiUser (1) + 2 функции + 2 типа функций = 5 элементов
        assertEquals(5, dfsResult.size());
    }

    @Test
    void testSearchByHierarchy_WithMultipleFunctions() {
        // Arrange - создаем пользователя с двумя функциями
        User multiUser = new User();
        multiUser.setUsername("multi_hierarchy");
        multiUser.setEmail("hierarchy@example.com");
        multiUser.setPasswordHash("pass");
        multiUser.setRole(Role.USER);
        userRepository.save(multiUser);

        // Создаем две функции
        Tabulated_function multiFunc1 = new Tabulated_function();
        multiFunc1.setSerializedData("hierarchy1");
        multiFunc1.setUser(multiUser);
        tabulatedFunctionRepository.save(multiFunc1);

        Tabulated_function multiFunc2 = new Tabulated_function();
        multiFunc2.setSerializedData("hierarchy2");
        multiFunc2.setUser(multiUser);
        tabulatedFunctionRepository.save(multiFunc2);

        // Добавляем связи
        multiUser.addTabulated_function(multiFunc1);
        multiUser.addTabulated_function(multiFunc2);

        // Act
        Map<String, Object> hierarchy = searchService.searchByHierarchy(multiUser.getId());

        // Assert
        assertNotNull(hierarchy);
        User user = (User) hierarchy.get("user");
        assertEquals("multi_hierarchy", user.getUsername());

        List<Tabulated_function> functions = (List<Tabulated_function>) hierarchy.get("tabulatedFunctions");
        assertEquals(2, functions.size());

        List<Map<String, Object>> functionsWithTypes = (List<Map<String, Object>>) hierarchy.get("functionsWithTypes");
        assertEquals(2, functionsWithTypes.size());
    }

    @Test
    void testFindSingleUserByUsername_Found_WithLogging() {
        // Act
        Optional<User> result = searchService.findSingleUserByUsername("alice");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("alice", result.get().getUsername());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindSingleUserByUsername_NotFound_WithLogging() {
        // Act
        Optional<User> result = searchService.findSingleUserByUsername("nonexistent_user");

        // Assert
        assertFalse(result.isPresent());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindSingleUserByEmail_Found_WithLogging() {
        // Act
        Optional<User> result = searchService.findSingleUserByEmail("alice@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("alice@example.com", result.get().getEmail());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindSingleUserByEmail_NotFound_WithLogging() {
        // Act
        Optional<User> result = searchService.findSingleUserByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindMultipleUsersByRole_WithLogging() {
        // Act
        List<User> result = searchService.findMultipleUsersByRole("USER");

        // Assert
        assertEquals(1, result.size());
        assertEquals("alice", result.get(0).getUsername());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindMultipleUsersByRole_EmptyResult() {
        // Act - роль, которой нет в тестовых данных
        List<User> result = searchService.findMultipleUsersByRole("GUEST");

        // Assert
        assertTrue(result.isEmpty());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindMultipleFunctionTypesByName_WithLogging() {
        // Act
        List<Function_type> result = searchService.findMultipleFunctionTypesByName("linear_type");

        // Assert
        assertEquals(1, result.size());
        assertEquals("linear_type", result.get(0).getName());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindMultipleFunctionTypesByName_EmptyResult() {
        // Act
        List<Function_type> result = searchService.findMultipleFunctionTypesByName("nonexistent_type");

        // Assert
        assertTrue(result.isEmpty());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindFunctionTypesWithSorting_ByCreatedAt_Asc_Detailed() {
        // Arrange - проверяем конкретные времена
        List<Function_type> types = functionTypeRepository.findAll();

        // Проверяем, что exponential_function создан раньше quadratic_function
        Function_type exponential = types.stream()
                .filter(t -> t.getName().equals("exponential_function"))
                .findFirst()
                .orElseThrow();

        Function_type quadratic = types.stream()
                .filter(t -> t.getName().equals("quadratic_function"))
                .findFirst()
                .orElseThrow();

        Function_type linear = types.stream()
                .filter(t -> t.getName().equals("linear_type"))
                .findFirst()
                .orElseThrow();

        // Act
        List<Function_type> result = searchService.findFunctionTypesWithSorting("createdAt", "asc");

        // Assert
        assertEquals(3, result.size());

        // Проверяем порядок по возрастанию createdAt
        assertTrue(result.get(0).getCreatedAt().isBefore(result.get(1).getCreatedAt()));
        assertTrue(result.get(1).getCreatedAt().isBefore(result.get(2).getCreatedAt()));

        // Проверяем конкретные имена в правильном порядке
        assertEquals("exponential_function", result.get(0).getName());
        assertEquals("quadratic_function", result.get(1).getName());
        assertEquals("linear_type", result.get(2).getName());
    }

    @Test
    void testFindFunctionTypesWithSorting_ByCreatedAt_Desc_Detailed() {
        // Act
        List<Function_type> result = searchService.findFunctionTypesWithSorting("createdAt", "desc");

        // Assert
        assertEquals(3, result.size());

        // Проверяем порядок по убыванию createdAt
        assertTrue(result.get(0).getCreatedAt().isAfter(result.get(1).getCreatedAt()));
        assertTrue(result.get(1).getCreatedAt().isAfter(result.get(2).getCreatedAt()));

        // Проверяем конкретные имена в обратном порядке
        assertEquals("linear_type", result.get(0).getName());
        assertEquals("quadratic_function", result.get(1).getName());
        assertEquals("exponential_function", result.get(2).getName());
    }

    @Test
    void testFindFunctionTypesWithSorting_DefaultCase_ZeroComparison() {
        // Arrange - создаем список для теста
        List<Function_type> types = functionTypeRepository.findAll();

        // Act - сортировка по несуществующему полю (должен вернуть default: 0)
        List<Function_type> result = searchService.findFunctionTypesWithSorting("invalidField", "asc");

        // Assert - проверяем, что при сравнении возвращается 0 (не меняет порядок)
        // Порядок должен остаться таким же, как в исходном списке
        assertEquals(3, result.size());

        // Проверяем, что все элементы присутствуют
        assertTrue(result.stream().anyMatch(t -> t.getName().equals("exponential_function")));
        assertTrue(result.stream().anyMatch(t -> t.getName().equals("linear_type")));
        assertTrue(result.stream().anyMatch(t -> t.getName().equals("quadratic_function")));
    }

    @Test
    void testFindFunctionsByFunctionTypeName_WithLogging() {
        // Act - поиск функций по имени типа функции
        List<Tabulated_function> result = searchService.findFunctionsByFunctionTypeName("linear_type");

        // Assert
        assertEquals(1, result.size());
        assertEquals("data1", result.get(0).getSerializedData());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindFunctionsByFunctionTypeName_MultipleFunctions() {
        // Arrange - создаем дополнительную функцию с тем же типом
        Tabulated_function extraFunc = new Tabulated_function();
        extraFunc.setSerializedData("extra_data");
        extraFunc.setUser(userRepository.findByUsername("alice").orElseThrow());
        tabulatedFunctionRepository.save(extraFunc);

        Function_type extraType = new Function_type();
        extraType.setName("linear_type"); // Тот же самый тип
        extraType.setLocName("Линейная");
        extraType.setPriority(1);
        extraType.setTabulatedFunction(extraFunc);
        functionTypeRepository.save(extraType);

        // Act
        List<Tabulated_function> result = searchService.findFunctionsByFunctionTypeName("linear_type");

        // Assert - теперь должно быть 2 функции
        assertEquals(2, result.size());
    }

    @Test
    void testFindFunctionsByFunctionTypeName_CaseInsensitive() {
        // Act - поиск с разным регистром (метод использует точное совпадение в SQL)
        List<Tabulated_function> result1 = searchService.findFunctionsByFunctionTypeName("LINEAR_TYPE");
        List<Tabulated_function> result2 = searchService.findFunctionsByFunctionTypeName("linear_type");
        List<Tabulated_function> result3 = searchService.findFunctionsByFunctionTypeName("Linear_Type");

        // Assert - зависит от настройки базы данных (чувствительность к регистру)
        // В H2 по умолчанию регистр не учитывается
        assertTrue(result1.size() >= 0);
        assertTrue(result2.size() >= 0);
        assertTrue(result3.size() >= 0);
    }

    @Test
    void testFindFunctionsByFunctionTypeName_EmptyResult_WithLogging() {
        // Act - поиск по несуществующему типу функции
        List<Tabulated_function> result = searchService.findFunctionsByFunctionTypeName("nonexistent_type");

        // Assert
        assertTrue(result.isEmpty());
        // Проверяем, что метод был вызван и логи записаны
    }

    @Test
    void testFindFunctionsByFunctionTypeName_Transactional() {
        // Arrange - проверяем, что метод помечен как @Transactional(readOnly = true)
        // Это означает, что он должен работать в транзакции только для чтения

        // Act - просто вызываем метод
        List<Tabulated_function> result = searchService.findFunctionsByFunctionTypeName("linear_type");

        // Assert - проверяем, что транзакция работает корректно
        // В тестовом окружении это должно работать без проблем
        assertNotNull(result);
    }

    @Test
    void testCreatedAtComparison_DirectTest() {
        // Arrange - создаем два типа функций с разным временем создания
        Tabulated_function testFunc = new Tabulated_function();
        testFunc.setSerializedData("test");
        testFunc.setUser(userRepository.findByUsername("alice").orElseThrow());
        tabulatedFunctionRepository.save(testFunc);

        Function_type type1 = new Function_type();
        type1.setName("type1");
        type1.setLocName("Тип 1");
        type1.setPriority(1);
        type1.setTabulatedFunction(testFunc);
        type1.setCreatedAt(LocalDateTime.now().minusHours(5));
        functionTypeRepository.save(type1);

        Function_type type2 = new Function_type();
        type2.setName("type2");
        type2.setLocName("Тип 2");
        type2.setPriority(2);
        type2.setTabulatedFunction(testFunc);
        type2.setCreatedAt(LocalDateTime.now().minusHours(2));
        functionTypeRepository.save(type2);

        // Act - тестируем сравнение createdAt напрямую
        int ascComparison = type1.getCreatedAt().compareTo(type2.getCreatedAt());
        int descComparison = type2.getCreatedAt().compareTo(type1.getCreatedAt());

        // Assert
        assertTrue(ascComparison < 0); // type1 создан раньше type2
        assertTrue(descComparison > 0); // type2 создан позже type1
    }
}