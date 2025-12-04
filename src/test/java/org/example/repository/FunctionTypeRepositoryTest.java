package org.example.repository;

import org.example.entity.Function_type;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class FunctionTypeRepositoryTest {

    @Autowired
    private FunctionTypeRepository functionTypeRepository;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Tabulated_function testFunction;
    private Function_type testFunctionType;

    @BeforeEach
    void setUp() {
        // Генерация тестовых данных
        testUser = new User("typeuser", "type@example.com", "password123");
        userRepository.save(testUser);

        testFunction = new Tabulated_function("function_data", testUser);
        tabulatedFunctionRepository.save(testFunction);

        testFunctionType = new Function_type("test_type", "Тестовый тип", 1, testFunction);
    }

    @Test
    void testSaveFunctionType() {
        // Сохранение
        Function_type savedType = functionTypeRepository.save(testFunctionType);

        // Проверки
        assertNotNull(savedType.getId());
        assertEquals("test_type", savedType.getName());
        assertEquals("Тестовый тип", savedType.getLocName());
        assertEquals(1, savedType.getPriority());
        assertEquals(testFunction.getId(), savedType.getTabulatedFunction().getId());
    }

    @Test
    void testSetId() {
        // Arrange
        Function_type type = new Function_type();

        // Act
        type.setId(999L);

        // Assert
        assertEquals(999L, type.getId());
    }

    @Test
    void testOnUpdateViaReflection() throws Exception {
        // Arrange
        Function_type type = new Function_type("test", "Тест", 1, testFunction);

        // Сохраняем сущность
        functionTypeRepository.save(type);

        // Получаем protected метод onUpdate
        Method onUpdateMethod = Function_type.class.getDeclaredMethod("onUpdate");
        onUpdateMethod.setAccessible(true);

        // Запоминаем текущее время
        LocalDateTime beforeCall = type.getUpdatedAt();

        // Ждем немного
        Thread.sleep(10);

        // Act - вызываем onUpdate через рефлексию
        onUpdateMethod.invoke(type);

        // Assert - проверяем, что updatedAt изменился
        assertNotNull(type.getUpdatedAt());
        assertNotEquals(beforeCall, type.getUpdatedAt());
    }


    @Test
    void testFindByName() {
        // Подготовка
        functionTypeRepository.save(testFunctionType);

        // Поиск по имени
        List<Function_type> types = functionTypeRepository.findByName("test_type");

        // Проверки
        assertEquals(1, types.size());
        assertEquals("Тестовый тип", types.get(0).getLocName());
    }

    @Test
    void testFindByLocName() {
        // Подготовка
        functionTypeRepository.save(testFunctionType);

        // Поиск по локализованному имени
        List<Function_type> types = functionTypeRepository.findByLocName("Тестовый тип");

        // Проверки
        assertEquals(1, types.size());
        assertEquals("test_type", types.get(0).getName());
    }

    @Test
    void testFindByPriority() {
        // Подготовка
        functionTypeRepository.save(testFunctionType);

        // Поиск по приоритету
        List<Function_type> types = functionTypeRepository.findByPriority(1);

        // Проверки
        assertEquals(1, types.size());
        assertEquals("test_type", types.get(0).getName());
    }

    @Test
    void testFindByTabulatedFunction() {
        // Подготовка
        Function_type savedType = functionTypeRepository.save(testFunctionType);

        // Поиск по tabulated function
        List<Function_type> types = functionTypeRepository.findByTabulatedFunction(testFunction);

        // Проверки
        assertEquals(1, types.size());
        assertEquals(savedType.getId(), types.get(0).getId());
    }

    @Test
    void testFindByTabulatedFunctionId() {
        // Подготовка
        functionTypeRepository.save(testFunctionType);

        // Поиск по ID tabulated function
        List<Function_type> types = functionTypeRepository.findByTabulatedFunctionId(testFunction.getId());

        // Проверки
        assertEquals(1, types.size());
        assertEquals("test_type", types.get(0).getName());
    }

    @Test
    void testFindByNameIgnoreCase() {
        // Подготовка
        functionTypeRepository.save(testFunctionType);

        // Поиск без учета регистра
        List<Function_type> types = functionTypeRepository.findByNameIgnoreCase("TEST_TYPE");

        // Проверки
        assertEquals(1, types.size());
        assertEquals("test_type", types.get(0).getName());
    }

    @Test
    void testFindByPriorityBetween() {
        // Подготовка - создаем несколько типов с разными приоритетами
        Function_type lowPriority = new Function_type("low", "Низкий", 1, testFunction);
        Function_type mediumPriority = new Function_type("medium", "Средний", 5, testFunction);
        Function_type highPriority = new Function_type("high", "Высокий", 10, testFunction);

        functionTypeRepository.save(lowPriority);
        functionTypeRepository.save(mediumPriority);
        functionTypeRepository.save(highPriority);

        // Поиск по диапазону приоритетов
        List<Function_type> types = functionTypeRepository.findByPriorityBetween(2, 8);

        // Проверки
        assertEquals(1, types.size());
        assertEquals("medium", types.get(0).getName());
    }

    @Test
    void testFindByNameContaining() {
        // Подготовка
        functionTypeRepository.save(testFunctionType);

        // Поиск по части имени
        List<Function_type> types = functionTypeRepository.findByNameContaining("test");

        // Проверки
        assertEquals(1, types.size());
        assertTrue(types.get(0).getName().contains("test"));
    }

    @Test
    void testDeleteFunctionType() {
        // Подготовка
        Function_type savedType = functionTypeRepository.save(testFunctionType);
        Long typeId = savedType.getId();

        // Проверяем что тип существует
        assertTrue(functionTypeRepository.findById(typeId).isPresent());

        // Удаление
        functionTypeRepository.deleteById(typeId);

        // Проверяем что тип удален
        assertFalse(functionTypeRepository.findById(typeId).isPresent());
    }

    @Test
    void testCountByTabulatedFunctionId() {
        // Подготовка - создаем несколько типов для одной функции
        Function_type type1 = new Function_type("type1", "Тип1", 1, testFunction);
        Function_type type2 = new Function_type("type2", "Тип2", 2, testFunction);
        Function_type type3 = new Function_type("type3", "Тип3", 3, testFunction);

        functionTypeRepository.save(type1);
        functionTypeRepository.save(type2);
        functionTypeRepository.save(type3);

        // Подсчет типов для функции
        Long count = functionTypeRepository.countByTabulatedFunctionId(testFunction.getId());

        // Проверки
        assertEquals(3, count);
    }
}