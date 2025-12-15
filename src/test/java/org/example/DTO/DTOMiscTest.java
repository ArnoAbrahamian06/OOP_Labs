package org.example.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


// Дополнительный тестовый класс для демонстрации использования Mockito с DTO
class DTOMiscTest {

    @Test
    void testUserDTOMock() {
        // Пример использования Mockito для создания мока DTO
        UserDTO mockUser = mock(UserDTO.class);

        when(mockUser.getId()).thenReturn(100);
        when(mockUser.getUsername()).thenReturn("mockUser");

        assertEquals(100, mockUser.getId());
        assertEquals("mockUser", mockUser.getUsername());

        verify(mockUser, times(1)).getId();
        verify(mockUser, times(1)).getUsername();
    }

    @Test
    void testPointDTOWithMockito() {
        PointDTO mockPoint = mock(PointDTO.class);

        when(mockPoint.getXValue()).thenReturn(10.5);
        when(mockPoint.getYValue()).thenReturn(20.5);

        assertEquals(10.5, mockPoint.getXValue());
        assertEquals(20.5, mockPoint.getYValue());
    }
}