package org.example.repository;

import org.example.entity.User;
import org.example.entity.Role;
import org.example.entity.Tabulated_function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
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
    void testPrePersistOnCreate_SetsDefaultValues() {
        // Arrange - создаем пользователя без установки createdAt и role
        User user = new User();
        user.setUsername("testuser_prepersist");
        user.setEmail("prepersist@example.com");
        user.setPasswordHash("password");
        user.setCreatedAt(null);  // явно сбрасываем
        user.setRole(null);       // явно сбрасываем

        // Act - сохраняем
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getCreatedAt(), "createdAt должен быть установлен @PrePersist");
        assertEquals(Role.USER, savedUser.getRole(), "role должен быть установлен в USER по умолчанию @PrePersist");
    }

    @Test
    void testSetId() {
        // Arrange
        User user = new User();
        // Act
        user.setId(999L);
        // Assert
        assertEquals(999L, user.getId());
    }

    @Test
    void testSetTabulatedFunctions() {
        // Arrange
        User user = new User();
        Tabulated_function  func1 = new Tabulated_function();
        Tabulated_function func2 = new Tabulated_function();

        List<Tabulated_function> functions = new ArrayList<>();
        functions.add(func1);
        functions.add(func2);

        // Act
        user.setTabulated_functions(functions);

        // Assert
        assertEquals(2, user.getTabulated_functions().size());
        assertTrue(user.getTabulated_functions().contains(func1));
        assertTrue(user.getTabulated_functions().contains(func2));
    }

    @Test
    void testRemoveTabulatedFunction() {
        // Arrange
        User user = new User();
        Tabulated_function func1 = new Tabulated_function();
        Tabulated_function func2 = new Tabulated_function();

        user.addTabulated_function(func1);
        user.addTabulated_function(func2);

        // Act
        user.removeTabulated_function(func1);

        // Assert
        assertEquals(1, user.getTabulated_functions().size());
        assertFalse(user.getTabulated_functions().contains(func1));
        assertTrue(user.getTabulated_functions().contains(func2));
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
        // Создаем пользователя с текущим временем
        User user = new User("testuser", "test@example.com", "password");
        userRepository.save(user);

        // Используем время на 1 секунду раньше
        LocalDateTime timeFilter = LocalDateTime.now().minusSeconds(1);

        // Ищем пользователей созданных после этого времени
        List<User> users = userRepository.findByCreatedAtAfter(timeFilter);

        // Должен найти созданного пользователя
        assertEquals(1, users.size());
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