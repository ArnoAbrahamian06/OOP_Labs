package org.example.db_service;

import org.example.DAO.PointRepository;
import org.example.models.Point;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunctionTypeRepositoryTest extends BaseTest {

    private final PointRepository functionTypeRepository = new PointRepository();

    @Test
    void testInsertAndFindById() throws Exception {
        // Given
        Point type = TestDataGenerator.generateFunctionType();

        // When
        Point insertedType = functionTypeRepository.insert(type);
        Point foundType = functionTypeRepository.findById(insertedType.getId());

        // Then
        assertNotNull(insertedType.getId());
        assertNotNull(foundType);
        assertEquals(insertedType.getId(), foundType.getId());
        assertEquals(type.getName(), foundType.getName());
        assertEquals(type.getLocalizedName(), foundType.getLocalizedName());
        assertEquals(type.getPriority(), foundType.getPriority());
        assertNotNull(foundType.getCreatedTime());
        assertNotNull(foundType.getUpdatedTime());
    }

    @Test
    void testFindByName() throws Exception {
        // Given
        Point type = TestDataGenerator.generateFunctionType();
        Point insertedType = functionTypeRepository.insert(type);

        // When
        Point foundType = functionTypeRepository.findByName(type.getName());

        // Then
        assertNotNull(foundType);
        assertEquals(insertedType.getId(), foundType.getId());
        assertEquals(type.getName(), foundType.getName());
    }

    @Test
    void testFindByLocalizedName() throws Exception {
        // Given
        Point type = TestDataGenerator.generateFunctionType();
        Point insertedType = functionTypeRepository.insert(type);

        // When
        Point foundType = functionTypeRepository.findByLocalizedName(type.getLocalizedName());

        // Then
        assertNotNull(foundType);
        assertEquals(insertedType.getId(), foundType.getId());
        assertEquals(type.getLocalizedName(), foundType.getLocalizedName());
    }

    @Test
    void testFindByPriorityGreaterThan() throws Exception {
        // Given
        int minPriority = 5;

        // Создаем типы с разным приоритетом
        Point highPriority1 = TestDataGenerator.generateFunctionTypeWithHighPriority();
        Point highPriority2 = TestDataGenerator.generateFunctionTypeWithHighPriority();
        Point lowPriority = TestDataGenerator.generateFunctionType();
        lowPriority.setPriority(2);

        functionTypeRepository.insert(highPriority1);
        functionTypeRepository.insert(highPriority2);
        functionTypeRepository.insert(lowPriority);

        // When
        List<Point> highPriorityTypes = functionTypeRepository.findByPriorityGreaterThan(minPriority);

        // Then
        assertTrue(highPriorityTypes.stream().allMatch(t -> t.getPriority() > minPriority));
    }

    @Test
    void testUpdatePriority() throws Exception {
        // Given
        Point type = TestDataGenerator.generateFunctionType();
        Point insertedType = functionTypeRepository.insert(type);
        int newPriority = type.getPriority() + 10;

        // When
        boolean updated = functionTypeRepository.updatePriority(insertedType.getId(), newPriority);
        Point updatedType = functionTypeRepository.findById(insertedType.getId());

        // Then
        assertTrue(updated);
        assertNotNull(updatedType);
        assertEquals(newPriority, updatedType.getPriority());
    }

    @Test
    void testUpdateLocalizedName() throws Exception {
        // Given
        Point type = TestDataGenerator.generateFunctionType();
        Point insertedType = functionTypeRepository.insert(type);
        String newLocalizedName = "Новое локализованное имя";

        // When
        boolean updated = functionTypeRepository.updateLocalizedName(insertedType.getId(), newLocalizedName);
        Point updatedType = functionTypeRepository.findById(insertedType.getId());

        // Then
        assertTrue(updated);
        assertNotNull(updatedType);
        assertEquals(newLocalizedName, updatedType.getLocalizedName());
    }

    @Test
    void testUpdateMultipleFields() throws Exception {
        // Given
        Point type = TestDataGenerator.generateFunctionType();
        Point insertedType = functionTypeRepository.insert(type);

        String newName = "updated_name";
        String newLocalizedName = "Обновленное имя";
        int newPriority = 99;

        // When
        boolean updated = functionTypeRepository.updateFunctionType(
                insertedType.getId(), newName, newLocalizedName, newPriority);
        Point updatedType = functionTypeRepository.findById(insertedType.getId());

        // Then
        assertTrue(updated);
        assertNotNull(updatedType);
        assertEquals(newName, updatedType.getName());
        assertEquals(newLocalizedName, updatedType.getLocalizedName());
        assertEquals(newPriority, updatedType.getPriority());
    }

    @Test
    void testDeleteById() throws Exception {
        // Given
        Point type = TestDataGenerator.generateFunctionType();
        Point insertedType = functionTypeRepository.insert(type);

        // When
        boolean deleted = functionTypeRepository.deleteById(insertedType.getId());
        Point foundType = functionTypeRepository.findById(insertedType.getId());

        // Then
        assertTrue(deleted);
        assertNull(foundType);
    }

    @Test
    void testDeleteByName() throws Exception {
        // Given
        Point type = TestDataGenerator.generateFunctionType();
        Point insertedType = functionTypeRepository.insert(type);

        // When
        boolean deleted = functionTypeRepository.deleteByName(type.getName());
        Point foundType = functionTypeRepository.findByName(type.getName());

        // Then
        assertTrue(deleted);
        assertNull(foundType);
    }

    @Test
    void testUniqueConstraints() throws Exception {
        // Given
        Point type1 = TestDataGenerator.generateFunctionType();
        functionTypeRepository.insert(type1);

        // When & Then - попытка создать тип с тем же именем
        Point type2 = TestDataGenerator.generateFunctionType();
        type2.setName(type1.getName());

        assertThrows(Exception.class, () -> functionTypeRepository.insert(type2));

        // When & Then - попытка создать тип с тем же локализованным именем
        Point type3 = TestDataGenerator.generateFunctionType();
        type3.setLocalizedName(type1.getLocalizedName());

        assertThrows(Exception.class, () -> functionTypeRepository.insert(type3));
    }
}