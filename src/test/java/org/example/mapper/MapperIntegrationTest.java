package org.example.mapper;

import org.example.DTO.PointDTO;
import org.example.DTO.FunctionDTO;
import org.example.DTO.UserDTO;
import org.example.models.Point;
import org.example.models.Function;
import org.example.models.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

// Дополнительные тесты для проверки целостности маппинга
class MapperIntegrationTest {

    @Test
    void testPointMapper_BidirectionalMapping() {
        // Создаем оригинальный DTO
        PointDTO originalDTO = new PointDTO(1, 100, 2.5, 3.7);

        // Конвертируем в Entity
        Point entity = PointMapper.toEntity(originalDTO);

        // Конвертируем обратно в DTO
        PointDTO resultDTO = PointMapper.toDTO(entity);

        // Проверяем, что значения сохранились
        assertEquals(originalDTO.getId(), resultDTO.getId());
        assertEquals(originalDTO.getFunctionId(), resultDTO.getFunctionId());
        assertEquals(originalDTO.getXValue(), resultDTO.getXValue());
        assertEquals(originalDTO.getYValue(), resultDTO.getYValue());
    }

    @Test
    void testFunctionMapper_BidirectionalMapping() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusHours(1);

        FunctionDTO originalDTO = new FunctionDTO(1, 100, "Test Function", now, later);

        Function entity = FunctionMapper.toEntity(originalDTO);
        FunctionDTO resultDTO = FunctionMapper.toDTO(entity);

        assertEquals(originalDTO.getId(), resultDTO.getId());
        assertEquals(originalDTO.getUserId(), resultDTO.getUserId());
        assertEquals(originalDTO.getName(), resultDTO.getName());
        assertEquals(originalDTO.getCreated_at(), resultDTO.getCreated_at());
        assertEquals(originalDTO.getUpdated_at(), resultDTO.getUpdated_at());
    }

    @Test
    void testUserMapper_BidirectionalMapping() {
        LocalDateTime now = LocalDateTime.now();
        UserDTO originalDTO = new UserDTO(1, "testUser", "ADMIN", now);
        originalDTO.setPasswordHash("password123");

        User entity = UserMapper.toEntity(originalDTO);
        UserDTO resultDTO = UserMapper.toDTO(entity);

        // Проверяем все поля, кроме пароля (т.к. toDTO не включает пароль)
        assertEquals(originalDTO.getId(), resultDTO.getId());
        assertEquals(originalDTO.getUsername(), resultDTO.getUsername());
        assertEquals(originalDTO.getRole(), resultDTO.getRole());
        assertEquals(originalDTO.getCreated_at(), resultDTO.getCreated_at());

        // Проверяем, что пароль сохранился в Entity
        assertEquals(originalDTO.getPasswordHash(), entity.getPasswordHash());
    }
}