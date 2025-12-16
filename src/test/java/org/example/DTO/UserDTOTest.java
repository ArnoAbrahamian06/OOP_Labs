package org.example.DTO;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    @Test
    void testUserDTO_NoArgsConstructor() {
        UserDTO user = new UserDTO();

        user.setId(1);
        user.setUsername("testUser");
        user.setPasswordHash("hashedPassword123");
        user.setRole("ADMIN");
        LocalDateTime now = LocalDateTime.now();
        user.setCreated_at(now);

        assertEquals(1, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("hashedPassword123", user.getPasswordHash());
        assertEquals("ADMIN", user.getRole());
        assertEquals(now, user.getCreated_at());
    }

    @Test
    void testUserDTO_AllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        UserDTO user = new UserDTO(1, "testUser", "ADMIN", now);

        assertEquals(1, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("ADMIN", user.getRole());
        assertEquals(now, user.getCreated_at());
    }

    @Test
    void testUserDTO_ToString() {
        LocalDateTime now = LocalDateTime.now();
        UserDTO user = new UserDTO(1, "testUser", "ADMIN", now);

        String result = user.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("username='testUser'"));
        assertTrue(result.contains("role='ADMIN'"));
    }

    @Test
    void testUserDTO_DefaultConstructorValues() {
        UserDTO user = new UserDTO();

        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPasswordHash());
        assertNull(user.getRole());
        assertNull(user.getCreated_at());
    }
}
