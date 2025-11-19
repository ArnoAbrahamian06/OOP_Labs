package org.example.repository;

import org.example.entity.User;
import org.example.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Генерация тестовых данных
        testUser = new User("testuser", "test@example.com", "hashedpassword123");
        testUser.setRole(Role.USER);
    }

    @Test
    void testSaveUser() {
        // Сохранение
        User savedUser = userRepository.save(testUser);

        // Проверки
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals(Role.USER, savedUser.getRole());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void testFindByUsername() {
        // Подготовка
        userRepository.save(testUser);

        // Поиск
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Проверки
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByEmail() {
        // Подготовка
        userRepository.save(testUser);

        // Поиск
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Проверки
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void testFindByRole() {
        // Подготовка - создаем несколько пользователей
        User adminUser = new User("admin", "admin@example.com", "adminpass");
        adminUser.setRole(Role.ADMIN);

        userRepository.save(testUser);
        userRepository.save(adminUser);

        // Поиск по роли
        List<User> users = userRepository.findByRole("USER");
        List<User> admins = userRepository.findByRole("ADMIN");

        // Проверки
        assertEquals(1, users.size());
        assertEquals(1, admins.size());
        assertEquals("testuser", users.get(0).getUsername());
        assertEquals("admin", admins.get(0).getUsername());
    }

    @Test
    void testExistsByUsername() {
        // Подготовка
        userRepository.save(testUser);

        // Проверки существования
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testFindByCreatedAtAfter() {
        // Подготовка
        User oldUser = new User("olduser", "old@example.com", "pass");
        userRepository.save(oldUser);

        // Ждем немного чтобы время отличалось
        try { Thread.sleep(100); } catch (InterruptedException e) {}

        LocalDateTime cutoff = LocalDateTime.now();

        User newUser = new User("newuser", "new@example.com", "pass");
        userRepository.save(newUser);

        // Поиск пользователей созданных после cutoff
        List<User> recentUsers = userRepository.findByCreatedAtAfter(cutoff);

        // Проверки
        assertEquals(1, recentUsers.size());
    }

    @Test
    void testDeleteUser() {
        // Подготовка
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();

        // Проверяем что пользователь существует
        assertTrue(userRepository.findById(userId).isPresent());

        // Удаление
        userRepository.deleteById(userId);

        // Проверяем что пользователь удален
        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    void testUpdateUser() {
        // Подготовка
        User savedUser = userRepository.save(testUser);

        // Обновление
        savedUser.setEmail("updated@example.com");
        savedUser.setRole(Role.ADMIN);
        User updatedUser = userRepository.save(savedUser);

        // Проверки
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals(Role.ADMIN, updatedUser.getRole());
    }
}