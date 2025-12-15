package org.example.DTO;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FunctionDTOTest {

    @Test
    void testFunctionDTO_NoArgsConstructor() {
        FunctionDTO function = new FunctionDTO();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusHours(1);

        function.setId(1);
        function.setUserId(100);
        function.setName("Test Function");
        function.setCreated_at(now);
        function.setUpdated_at(later);

        assertEquals(1, function.getId());
        assertEquals(100, function.getUserId());
        assertEquals("Test Function", function.getName());
        assertEquals(now, function.getCreated_at());
        assertEquals(later, function.getUpdated_at());
    }

    @Test
    void testFunctionDTO_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusHours(1);
        FunctionDTO function = new FunctionDTO(1, 100, "Test Function", now, later);

        assertEquals(1, function.getId());
        assertEquals(100, function.getUserId());
        assertEquals("Test Function", function.getName());
        assertEquals(now, function.getCreated_at());
        assertEquals(later, function.getUpdated_at());
    }

    @Test
    void testFunctionDTO_ToString() {
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 10, 30);
        LocalDateTime later = now.plusHours(1);
        FunctionDTO function = new FunctionDTO(1, 100, "Test Function", now, later);

        String result = function.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("userId=100"));
        assertTrue(result.contains("name='Test Function'"));
    }

    @Test
    void testFunctionDTO_NullValues() {
        FunctionDTO function = new FunctionDTO();

        function.setId(null);
        function.setUserId(null);
        function.setName(null);
        function.setCreated_at(null);
        function.setUpdated_at(null);

        assertNull(function.getId());
        assertNull(function.getUserId());
        assertNull(function.getName());
        assertNull(function.getCreated_at());
        assertNull(function.getUpdated_at());
    }

    @Test
    void testFunctionDTO_AllArgsConstructorWithNulls() {
        FunctionDTO function = new FunctionDTO(null, null, null, null, null);

        assertNull(function.getId());
        assertNull(function.getUserId());
        assertNull(function.getName());
        assertNull(function.getCreated_at());
        assertNull(function.getUpdated_at());
    }

    @Test
    void testFunctionDTO_SameTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        FunctionDTO function = new FunctionDTO(1, 100, "Test", now, now);

        assertEquals(now, function.getCreated_at());
        assertEquals(now, function.getUpdated_at());
        assertSame(function.getCreated_at(), function.getUpdated_at());
    }
}
