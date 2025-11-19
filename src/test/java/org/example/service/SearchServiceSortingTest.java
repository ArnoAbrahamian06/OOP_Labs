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
}