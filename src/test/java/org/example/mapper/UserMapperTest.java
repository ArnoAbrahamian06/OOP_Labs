package org.example.mapper;

import org.example.DTO.UserDTO;
import org.example.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        user = new User();
        user.setId(1);
        user.setUsername("testUser");
        user.setRole("ADMIN");
        user.setPasswordHash("hashedPassword123");
        user.setCreated_at(now);

        userDTO = new UserDTO(1, "testUser", "ADMIN", now);
        userDTO.setPasswordHash("hashedPassword123");
    }

    @Test
    void testToDTO_ValidUser() {
        UserDTO result = UserMapper.toDTO(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.getCreated_at(), result.getCreated_at());
        // Примечание: метод toDTO не устанавливает passwordHash в DTO
        assertNull(result.getPasswordHash());
    }

    @Test
    void testToDTO_NullUser() {
        UserDTO result = UserMapper.toDTO(null);

        assertNull(result);
    }

    @Test
    void testToEntity_ValidUserDTO() {
        User result = UserMapper.toEntity(userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getRole(), result.getRole());
        assertEquals(userDTO.getPasswordHash(), result.getPasswordHash());
        assertEquals(userDTO.getCreated_at(), result.getCreated_at());
    }

    @Test
    void testToEntity_NullUserDTO() {
        User result = UserMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void testToEntity_WithNullValues() {
        UserDTO dtoWithNulls = new UserDTO(null, null, null, null);
        dtoWithNulls.setPasswordHash(null);

        User result = UserMapper.toEntity(dtoWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUsername());
        assertNull(result.getRole());
        assertNull(result.getPasswordHash());
        assertNull(result.getCreated_at());
    }

    @Test
    void testToDTO_WithNullValues() {
        User userWithNulls = new User();
        userWithNulls.setId(null);
        userWithNulls.setUsername(null);
        userWithNulls.setRole(null);
        userWithNulls.setPasswordHash(null);
        userWithNulls.setCreated_at(null);

        UserDTO result = UserMapper.toDTO(userWithNulls);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUsername());
        assertNull(result.getRole());
        assertNull(result.getPasswordHash());
        assertNull(result.getCreated_at());
    }

    @Test
    void testToDTO_UserWithoutPassword() {
        user.setPasswordHash(null);

        UserDTO result = UserMapper.toDTO(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertNull(result.getPasswordHash());
    }

    @Test
    void testToEntity_UserDTOWithoutPassword() {
        userDTO.setPasswordHash(null);

        User result = UserMapper.toEntity(userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        assertNull(result.getPasswordHash());
    }

    @Test
    void testToEntity_UserDTOWithEmptyPassword() {
        userDTO.setPasswordHash("");

        User result = UserMapper.toEntity(userDTO);

        assertNotNull(result);
        assertEquals("", result.getPasswordHash());
    }

    @Test
    void testToEntity_UserDTOWithWhitespacePassword() {
        userDTO.setPasswordHash("   ");

        User result = UserMapper.toEntity(userDTO);

        assertNotNull(result);
        assertEquals("   ", result.getPasswordHash());
    }

    @Test
    void testToDTO_ConstructorExcludesPassword() {
        // Проверяем, что конструктор UserDTO не включает пароль
        LocalDateTime now = LocalDateTime.now();
        UserDTO newDTO = new UserDTO(2, "newUser", "USER", now);

        assertNull(newDTO.getPasswordHash());

        // После установки пароля через сеттер
        newDTO.setPasswordHash("newPassword");
        assertEquals("newPassword", newDTO.getPasswordHash());
    }
}
