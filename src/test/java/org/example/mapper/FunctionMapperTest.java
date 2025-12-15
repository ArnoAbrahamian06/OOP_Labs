package org.example.mapper;

import org.example.DTO.FunctionDTO;
import org.example.models.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FunctionMapperTest {

    private Function function;
    private FunctionDTO functionDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusHours(1);

        function = new Function();
        function.setId(1);
        function.setUserId(100);
        function.setName("Test Function");
        function.setCreated_at(now);
        function.setUpdated_at(later);

        functionDTO = new FunctionDTO(1, 100, "Test Function", now, later);
    }

    @Test
    void testToDTO_ValidFunction() {
        FunctionDTO result = FunctionMapper.toDTO(function);

        assertNotNull(result);
        assertEquals(function.getId(), result.getId());
        assertEquals(function.getUserId(), result.getUserId());
        assertEquals(function.getName(), result.getName());
        assertEquals(function.getCreated_at(), result.getCreated_at());
        assertEquals(function.getUpdated_at(), result.getUpdated_at());
    }

    @Test
    void testToDTO_NullFunction() {
        FunctionDTO result = FunctionMapper.toDTO(null);

        assertNull(result);
    }

    @Test
    void testToEntity_ValidFunctionDTO() {
        Function result = FunctionMapper.toEntity(functionDTO);

        assertNotNull(result);
        assertEquals(functionDTO.getId(), result.getId());
        assertEquals(functionDTO.getUserId(), result.getUserId());
        assertEquals(functionDTO.getName(), result.getName());
        assertEquals(functionDTO.getCreated_at(), result.getCreated_at());
        assertEquals(functionDTO.getUpdated_at(), result.getUpdated_at());
    }

    @Test
    void testToEntity_NullFunctionDTO() {
        Function result = FunctionMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void testToEntity_WithNullValues() {
        FunctionDTO dtoWithNulls = new FunctionDTO(null, null, null, null, null);

        Function result = FunctionMapper.toEntity(dtoWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUserId());
        assertNull(result.getName());
        assertNull(result.getCreated_at());
        assertNull(result.getUpdated_at());
    }

    @Test
    void testToDTO_WithNullValues() {
        Function functionWithNulls = new Function();
        functionWithNulls.setId(null);
        functionWithNulls.setUserId(null);
        functionWithNulls.setName(null);
        functionWithNulls.setCreated_at(null);
        functionWithNulls.setUpdated_at(null);

        FunctionDTO result = FunctionMapper.toDTO(functionWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUserId());
        assertNull(result.getName());
        assertNull(result.getCreated_at());
        assertNull(result.getUpdated_at());
    }

    @Test
    void testToDTO_SameTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        function.setCreated_at(now);
        function.setUpdated_at(now);

        FunctionDTO result = FunctionMapper.toDTO(function);

        assertNotNull(result);
        assertEquals(now, result.getCreated_at());
        assertEquals(now, result.getUpdated_at());
    }

    @Test
    void testToEntity_SameTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        FunctionDTO dto = new FunctionDTO(1, 100, "Test", now, now);

        Function result = FunctionMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(now, result.getCreated_at());
        assertEquals(now, result.getUpdated_at());
    }
}
