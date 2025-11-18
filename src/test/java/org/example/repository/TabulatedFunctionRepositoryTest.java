package org.example.repository;

import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.entity.Function_type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class TabulatedFunctionRepositoryTest {

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionTypeRepository functionTypeRepository;

    private User testUser;
    private Tabulated_function testFunction;

    @BeforeEach
    void setUp() {
        // Генерация тестовых данных
        testUser = new User("functionuser", "function@example.com", "password123");
        userRepository.save(testUser);

        testFunction = new Tabulated_function("serialized_data_here", testUser);
    }

    @Test
    void testSaveTabulatedFunction() {
        // Сохранение
        Tabulated_function savedFunction = tabulatedFunctionRepository.save(testFunction);

        // Проверки
        assertNotNull(savedFunction.getId());
        assertEquals("serialized_data_here", savedFunction.getSerializedData());
        assertEquals(testUser.getId(), savedFunction.getUser().getId());
    }

    @Test
    void testFindByUser() {
        // Подготовка
        Tabulated_function savedFunction = tabulatedFunctionRepository.save(testFunction);

        // Поиск по пользователю
        List<Tabulated_function> functions = tabulatedFunctionRepository.findByUser(testUser);

        // Проверки
        assertEquals(1, functions.size());
        assertEquals(savedFunction.getId(), functions.get(0).getId());
    }

    @Test
    void testFindByUserId() {
        // Подготовка
        Tabulated_function savedFunction = tabulatedFunctionRepository.save(testFunction);

        // Поиск по ID пользователя
        List<Tabulated_function> functions = tabulatedFunctionRepository.findByUserId(testUser.getId());

        // Проверки
        assertEquals(1, functions.size());
        assertEquals(savedFunction.getId(), functions.get(0).getId());
    }

    @Test
    void testFindBySerializedDataContaining() {
        // Подготовка
        tabulatedFunctionRepository.save(testFunction);

        // Поиск по содержанию данных
        List<Tabulated_function> functions = tabulatedFunctionRepository.findBySerializedDataContaining("serialized");

        // Проверки
        assertEquals(1, functions.size());
        assertTrue(functions.get(0).getSerializedData().contains("serialized"));
    }

    @Test
    void testTabulatedFunctionWithFunctionTypes() {
        // Подготовка - создаем функцию с типами
        Tabulated_function function = new Tabulated_function("data_with_types", testUser);

        Function_type type1 = new Function_type("linear", "Линейная", 1, function);
        Function_type type2 = new Function_type("quadratic", "Квадратичная", 2, function);

        function.addFunctionType(type1);
        function.addFunctionType(type2);

        // Сохранение
        Tabulated_function savedFunction = tabulatedFunctionRepository.save(function);

        // Проверки
        assertEquals(2, savedFunction.getFunctionTypes().size());
        assertEquals("linear", savedFunction.getFunctionTypes().get(0).getName());
    }

    @Test
    void testDeleteTabulatedFunction() {
        // Подготовка
        Tabulated_function savedFunction = tabulatedFunctionRepository.save(testFunction);
        Long functionId = savedFunction.getId();

        // Проверяем что функция существует
        assertTrue(tabulatedFunctionRepository.findById(functionId).isPresent());

        // Удаление
        tabulatedFunctionRepository.deleteById(functionId);

        // Проверяем что функция удалена
        assertFalse(tabulatedFunctionRepository.findById(functionId).isPresent());
    }

    @Test
    void testFindWithMinFunctionTypes() {
        // Подготовка - создаем функции с разным количеством типов
        Tabulated_function functionWithOneType = new Tabulated_function("data1", testUser);
        functionWithOneType.addFunctionType(new Function_type("type1", "Тип1", 1, functionWithOneType));

        Tabulated_function functionWithThreeTypes = new Tabulated_function("data2", testUser);
        functionWithThreeTypes.addFunctionType(new Function_type("type1", "Тип1", 1, functionWithThreeTypes));
        functionWithThreeTypes.addFunctionType(new Function_type("type2", "Тип2", 2, functionWithThreeTypes));
        functionWithThreeTypes.addFunctionType(new Function_type("type3", "Тип3", 3, functionWithThreeTypes));

        tabulatedFunctionRepository.save(functionWithOneType);
        tabulatedFunctionRepository.save(functionWithThreeTypes);

        // Поиск функций с минимум 2 типами
        List<Tabulated_function> functions = tabulatedFunctionRepository.findWithMinFunctionTypes(2);

        // Проверки
        assertEquals(1, functions.size());
        assertEquals(3, functions.get(0).getFunctionTypes().size());
    }
}