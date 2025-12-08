package org.example.db_service;

import org.example.DAO.PointRepository;
import org.example.DAO.FunctionRepository;
import org.example.DAO.UserRepository;
import org.example.models.Point;
import org.example.models.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionRepositoryTest extends BaseTest {

    private final FunctionRepository functionRepository = new FunctionRepository();
    private final UserRepository userRepository = new UserRepository();
    private final PointRepository typeRepository = new PointRepository();

    private User testUser;
    private Point testType;

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        // Создаем тестового пользователя и тип функции для использования в тестах
        testUser = userRepository.insert(TestDataGenerator.generateUser());
        testType = typeRepository.insert(TestDataGenerator.generateFunctionType());
    }

    @Test
    void testInsertAndFindById() throws Exception {
        // Given
        Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());

        // When
        Function insertedFunction = functionRepository.insert(function);
        Function foundFunction = functionRepository.findByUserId(testUser.getId()).get(0);

        // Then
        assertNotNull(insertedFunction.getId());
        assertNotNull(foundFunction);
        assertEquals(insertedFunction.getId(), foundFunction.getId());
        assertEquals(testUser.getId(), foundFunction.getUserId());
        assertEquals(testType.getId(), foundFunction.getFunctionTypeId());
        assertArrayEquals(function.getSerializedData(), foundFunction.getSerializedData());
        assertNotNull(foundFunction.getCreatedTime());
        assertNotNull(foundFunction.getUpdatedTime());
    }

    @Test
    void testFindByUserId() throws Exception {
        // Given
        int functionCount = 3;
        for (int i = 0; i < functionCount; i++) {
            Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
            functionRepository.insert(function);
        }

        // Создаем функции для другого пользователя
        User anotherUser = userRepository.insert(TestDataGenerator.generateUser());
        for (int i = 0; i < 2; i++) {
            Function function = TestDataGenerator.generateTabulatedFunction(anotherUser.getId(), testType.getId());
            functionRepository.insert(function);
        }

        // When
        List<Function> userFunctions = functionRepository.findByUserId(testUser.getId());

        // Then
        assertEquals(functionCount, userFunctions.size());
        assertTrue(userFunctions.stream().allMatch(f -> testUser.getId().equals(f.getUserId())));
    }

    @Test
    void testFindByFunctionTypeId() throws Exception {
        // Given
        Point anotherType = typeRepository.insert(TestDataGenerator.generateFunctionType());

        int type1Count = 3;
        int type2Count = 2;

        for (int i = 0; i < type1Count; i++) {
            Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
            functionRepository.insert(function);
        }

        for (int i = 0; i < type2Count; i++) {
            Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), anotherType.getId());
            functionRepository.insert(function);
        }

        // When
        List<Function> type1Functions = functionRepository.findByFunctionTypeId(testType.getId());

        // Then
        assertEquals(type1Count, type1Functions.size());
        assertTrue(type1Functions.stream().allMatch(f -> testType.getId().equals(f.getFunctionTypeId())));
    }

    @Test
    void testFindByCreatedTimeAfter() throws Exception {
        // Given
        Function function1 = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
        functionRepository.insert(function1);

        // Ждем немного, чтобы время изменилось
        Thread.sleep(100);

        Timestamp cutoffTime = Timestamp.valueOf(LocalDateTime.now());

        Thread.sleep(100);

        Function function2 = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
        functionRepository.insert(function2);

        // When
        List<Function> recentFunctions = functionRepository.findByCreatedTimeAfter(cutoffTime);

        // Then
        assertFalse(recentFunctions.isEmpty());
        assertTrue(recentFunctions.stream().anyMatch(f -> f.getId().equals(function2.getId())));
    }

    @Test
    void testFindWithPagination() throws Exception {
        // Given
        int totalFunctions = 8;
        for (int i = 0; i < totalFunctions; i++) {
            Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
            functionRepository.insert(function);
        }

        // When
        List<Function> firstPage = functionRepository.findWithPagination(5, 0);
        List<Function> secondPage = functionRepository.findWithPagination(5, 5);

        // Then
        assertEquals(5, firstPage.size());
        assertEquals(3, secondPage.size()); // Осталось только 3 функции

        // Проверяем, что страницы не пересекаются
        List<Long> firstPageIds = firstPage.stream().map(Function::getId).toList();
        List<Long> secondPageIds = secondPage.stream().map(Function::getId).toList();

        assertTrue(firstPageIds.stream().noneMatch(secondPageIds::contains));
    }

    @Test
    void testFindWithUserAndTypeInfo() throws Exception {
        // Given
        Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
        functionRepository.insert(function);

        // When
        List<Function> functionsWithInfo = functionRepository.findWithUserAndTypeInfo();

        // Then
        assertFalse(functionsWithInfo.isEmpty());

        Function functionWithInfo = functionsWithInfo.get(0);
        assertNotNull(functionWithInfo.getUserUsername());
        assertNotNull(functionWithInfo.getUserEmail());
        assertNotNull(functionWithInfo.getFunctionTypeName());
        assertNotNull(functionWithInfo.getFunctionTypeLocalized());

        assertEquals(testUser.getUsername(), functionWithInfo.getUserUsername());
        assertEquals(testUser.getEmail(), functionWithInfo.getUserEmail());
        assertEquals(testType.getName(), functionWithInfo.getFunctionTypeName());
        assertEquals(testType.getLocalizedName(), functionWithInfo.getFunctionTypeLocalized());
    }

    @Test
    void testUpdateSerializedData() throws Exception {
        // Given
        Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
        Function insertedFunction = functionRepository.insert(function);

        byte[] newData = "новые данные".getBytes();

        // When
        boolean updated = functionRepository.updateSerializedData(insertedFunction.getId(), newData);
        List<Function> updatedFunctions = functionRepository.findByUserId(testUser.getId());

        // Then
        assertTrue(updated);
        assertFalse(updatedFunctions.isEmpty());

        Function updatedFunction = updatedFunctions.get(0);
        assertArrayEquals(newData, updatedFunction.getSerializedData());
    }

    @Test
    void testUpdateFunctionType() throws Exception {
        // Given
        Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
        Function insertedFunction = functionRepository.insert(function);

        Point newType = typeRepository.insert(TestDataGenerator.generateFunctionType());

        // When
        boolean updated = functionRepository.updateFunctionType(insertedFunction.getId(), newType.getId());
        List<Function> updatedFunctions = functionRepository.findByUserId(testUser.getId());

        // Then
        assertTrue(updated);
        assertFalse(updatedFunctions.isEmpty());

        Function updatedFunction = updatedFunctions.get(0);
        assertEquals(newType.getId(), updatedFunction.getFunctionTypeId());
    }

    @Test
    void testUpdateUserFunctions() throws Exception {
        // Given
        int functionCount = 3;
        for (int i = 0; i < functionCount; i++) {
            Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
            functionRepository.insert(function);
        }

        // Ждем немного перед обновлением
        Thread.sleep(100);

        // When
        boolean updated = functionRepository.updateUserFunctions(testUser.getId());
        List<Function> userFunctions = functionRepository.findByUserId(testUser.getId());

        // Then
        assertTrue(updated);
        assertEquals(functionCount, userFunctions.size());
        // Все функции должны иметь обновленное время
        assertTrue(userFunctions.stream().allMatch(f ->
                f.getUpdatedTime().isAfter(f.getCreatedTime())));
    }

    @Test
    void testDeleteById() throws Exception {
        // Given
        Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
        Function insertedFunction = functionRepository.insert(function);

        // When
        boolean deleted = functionRepository.deleteById(insertedFunction.getId());
        List<Function> remainingFunctions = functionRepository.findByUserId(testUser.getId());

        // Then
        assertTrue(deleted);
        assertTrue(remainingFunctions.isEmpty());
    }

    @Test
    void testDeleteByUserId() throws Exception {
        // Given
        int functionCount = 3;
        for (int i = 0; i < functionCount; i++) {
            Function function = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
            functionRepository.insert(function);
        }

        // Создаем функции для другого пользователя
        User anotherUser = userRepository.insert(TestDataGenerator.generateUser());
        Function otherFunction = TestDataGenerator.generateTabulatedFunction(anotherUser.getId(), testType.getId());
        functionRepository.insert(otherFunction);

        // When
        boolean deleted = functionRepository.deleteByUserId(testUser.getId());
        List<Function> testUserFunctions = functionRepository.findByUserId(testUser.getId());
        List<Function> anotherUserFunctions = functionRepository.findByUserId(anotherUser.getId());

        // Then
        assertTrue(deleted);
        assertTrue(testUserFunctions.isEmpty());
        assertEquals(1, anotherUserFunctions.size()); // Функции другого пользователя должны остаться
    }

    @Test
    void testDeleteByFunctionTypeId() throws Exception {
        // Given
        Point anotherType = typeRepository.insert(TestDataGenerator.generateFunctionType());

        // Создаем функции разных типов
        for (int i = 0; i < 2; i++) {
            Function function1 = TestDataGenerator.generateTabulatedFunction(testUser.getId(), testType.getId());
            Function function2 = TestDataGenerator.generateTabulatedFunction(testUser.getId(), anotherType.getId());
            functionRepository.insert(function1);
            functionRepository.insert(function2);
        }

        // When
        boolean deleted = functionRepository.deleteByFunctionTypeId(testType.getId());
        List<Function> type1Functions = functionRepository.findByFunctionTypeId(testType.getId());
        List<Function> type2Functions = functionRepository.findByFunctionTypeId(anotherType.getId());

        // Then
        assertTrue(deleted);
        assertTrue(type1Functions.isEmpty());
        assertEquals(2, type2Functions.size()); // Функции другого типа должны остаться
    }

    @Test
    void testForeignKeyConstraints() throws Exception {
        // Given
        Long nonExistentUserId = 999999L;
        Integer nonExistentTypeId = 999999;

        Function functionWithBadUser = TestDataGenerator.generateTabulatedFunction(nonExistentUserId, testType.getId());
        Function functionWithBadType = TestDataGenerator.generateTabulatedFunction(testUser.getId(), nonExistentTypeId);

        // When & Then - должны получить ошибку из-за foreign key constraint
        assertThrows(Exception.class, () -> functionRepository.insert(functionWithBadUser));
        assertThrows(Exception.class, () -> functionRepository.insert(functionWithBadType));
    }
}