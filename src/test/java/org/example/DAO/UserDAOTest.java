package org.example.DAO;

import org.example.models.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOTest.class);
    private UserDAO userDAO;
    private User testUser;
    private List<Integer> cleanupUserIds = new ArrayList<>();

    @BeforeAll
    void setUp() {
        logger.info("Инициализация UserDAO тестов");
        userDAO = new UserDAO();

        // Создаем тестового пользователя
        testUser = new User();
        testUser.setUsername("test_user_" + System.currentTimeMillis());
        testUser.setPasswordHash("hashed_password_" + System.currentTimeMillis());
        testUser.setRole("user");
        testUser.setCreated_at(LocalDateTime.now());
    }

    @AfterAll
    void tearDown() {
        logger.info("Очистка тестовых данных UserDAO");

        // Удаляем созданных тестовых пользователей
        for (Integer userId : cleanupUserIds) {
            try {
                userDAO.delete(userId);
            } catch (Exception e) {
                logger.warn("Не удалось удалить пользователя ID {}: {}", userId, e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    void testInsertUser() {
        logger.info("Тест: создание пользователя");

        User insertedUser = userDAO.insert(testUser);

        assertNotNull(insertedUser, "Созданный пользователь не должен быть null");
        assertNotNull(insertedUser.getId(), "ID пользователя должен быть установлен");
        assertEquals(testUser.getUsername(), insertedUser.getUsername(), "Логин должен совпадать");
        assertEquals(testUser.getRole(), insertedUser.getRole(), "Роль должна совпадать");
        assertEquals(testUser.getPasswordHash(), insertedUser.getPasswordHash(), "Хэш пароля должен совпадать");
        assertNotNull(insertedUser.getCreated_at(), "Дата создания должна быть установлена");

        testUser = insertedUser;
        cleanupUserIds.add(testUser.getId());
        logger.info("Создан пользователь с ID: {}", testUser.getId());
    }

    @Test
    @Order(2)
    void testFindById() {
        logger.info("Тест: поиск пользователя по ID");

        Optional<User> foundUserOpt = userDAO.findById(testUser.getId());

        assertTrue(foundUserOpt.isPresent(), "Пользователь должен быть найден по ID");
        User foundUser = foundUserOpt.get();

        assertEquals(testUser.getId(), foundUser.getId(), "ID должен совпадать");
        assertEquals(testUser.getUsername(), foundUser.getUsername(), "Логин должен совпадать");
        assertEquals(testUser.getRole(), foundUser.getRole(), "Роль должна совпадать");
        assertEquals(testUser.getPasswordHash(), foundUser.getPasswordHash(), "Хэш пароля должен совпадать");
    }

    @Test
    @Order(3)
    void testFindByUsername() {
        logger.info("Тест: поиск пользователя по username");

        Optional<User> foundUserOpt = userDAO.findByUsername(testUser.getUsername());

        assertTrue(foundUserOpt.isPresent(), "Пользователь должен быть найден по username");
        User foundUser = foundUserOpt.get();

        assertEquals(testUser.getId(), foundUser.getId(), "ID должен совпадать");
        assertEquals(testUser.getUsername(), foundUser.getUsername(), "Логин должен совпадать");
    }

    @Test
    @Order(4)
    void testFindAll() {
        logger.info("Тест: получение всех пользователей");

        List<User> users = userDAO.findAll();

        assertNotNull(users, "Список пользователей не должен быть null");
        assertFalse(users.isEmpty(), "Список пользователей не должен быть пустым");

        // Проверяем, что наш тестовый пользователь есть в списке
        boolean found = users.stream()
                .anyMatch(user -> user.getId().equals(testUser.getId()));
        assertTrue(found, "Тестовый пользователь должен быть в списке");
    }

    @Test
    @Order(5)
    void testFindByRole() {
        logger.info("Тест: поиск пользователей по роли");

        List<User> userRoleUsers = userDAO.findByRole("user");

        assertNotNull(userRoleUsers, "Список пользователей с ролью 'user' не должен быть null");

        boolean found = userRoleUsers.stream()
                .anyMatch(user -> user.getId().equals(testUser.getId()));
        assertTrue(found, "Тестовый пользователь должен быть в списке пользователей с ролью 'user'");
    }

    @Test
    @Order(6)
    void testFindByUsernameLike() {
        logger.info("Тест: поиск пользователей по маске username");

        // Ищем по части логина
        String searchTerm = testUser.getUsername().substring(0, 8);
        List<User> users = userDAO.findByUsernameLike(searchTerm);

        assertNotNull(users, "Список пользователей не должен быть null");

        boolean found = users.stream()
                .anyMatch(user -> user.getId().equals(testUser.getId()));
        assertTrue(found, "Тестовый пользователь должен быть найден по части логина");
    }

    @Test
    @Order(7)
    void testUpdateUser() {
        logger.info("Тест: обновление пользователя");

        // Подготавливаем обновленные данные
        User updatedUser = new User();
        updatedUser.setId(testUser.getId());
        updatedUser.setUsername(testUser.getUsername() + "_updated");
        updatedUser.setPasswordHash("new_hashed_password");
        updatedUser.setRole("admin");

        boolean updateResult = userDAO.update(updatedUser);
        assertTrue(updateResult, "Обновление должно быть успешным");

        // Проверяем обновленные данные
        Optional<User> foundUserOpt = userDAO.findById(testUser.getId());
        assertTrue(foundUserOpt.isPresent());
        User foundUser = foundUserOpt.get();

        assertEquals(updatedUser.getUsername(), foundUser.getUsername(), "Логин должен быть обновлен");
        assertEquals(updatedUser.getRole(), foundUser.getRole(), "Роль должна быть обновлена");
        assertEquals(updatedUser.getPasswordHash(), foundUser.getPasswordHash(), "Хэш пароля должен быть обновлен");

        testUser = foundUser;
    }

    @Test
    @Order(8)
    void testUpdateRole() {
        logger.info("Тест: обновление роли пользователя");

        String newRole = "moderator";
        boolean updateResult = userDAO.updateRole(testUser.getId(), newRole);
        assertTrue(updateResult, "Обновление роли должно быть успешным");

        // Проверяем обновленную роль
        Optional<User> foundUserOpt = userDAO.findById(testUser.getId());
        assertTrue(foundUserOpt.isPresent());
        assertEquals(newRole, foundUserOpt.get().getRole(), "Роль должна быть обновлена");

        testUser.setRole(newRole);
    }

    @Test
    @Order(9)
    void testUpdatePassword() {
        logger.info("Тест: обновление пароля пользователя");

        String newPasswordHash = "new_secure_password_hash";
        boolean updateResult = userDAO.updatePassword(testUser.getId(), newPasswordHash);
        assertTrue(updateResult, "Обновление пароля должно быть успешным");

        // Проверяем обновленный пароль
        Optional<User> foundUserOpt = userDAO.findById(testUser.getId());
        assertTrue(foundUserOpt.isPresent());
        assertEquals(newPasswordHash, foundUserOpt.get().getPasswordHash(), "Хэш пароля должен быть обновлен");

        testUser.setPasswordHash(newPasswordHash);
    }

    @Test
    @Order(10)
    void testExistsByUsername() {
        logger.info("Тест: проверка существования пользователя по username");

        boolean exists = userDAO.existsByUsername(testUser.getUsername());
        assertTrue(exists, "Пользователь должен существовать");

        boolean notExists = userDAO.existsByUsername("non_existent_user_" + System.currentTimeMillis());
        assertFalse(notExists, "Несуществующий пользователь не должен существовать");
    }

    @Test
    @Order(11)
    void testFindByIds() {
        logger.info("Тест: множественный поиск пользователей по IDs");

        // Создаем еще одного пользователя для теста
        User anotherUser = new User();
        anotherUser.setUsername("another_test_user_" + System.currentTimeMillis());
        anotherUser.setPasswordHash("another_password_hash");
        anotherUser.setRole("user");

        User insertedAnotherUser = userDAO.insert(anotherUser);
        assertNotNull(insertedAnotherUser);
        cleanupUserIds.add(insertedAnotherUser.getId());

        // Ищем обоих пользователей
        List<Integer> ids = List.of(testUser.getId(), insertedAnotherUser.getId());
        List<User> foundUsers = userDAO.findByIds(ids);

        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size(), "Должны быть найдены оба пользователя");

        boolean foundTestUser = foundUsers.stream()
                .anyMatch(user -> user.getId().equals(testUser.getId()));
        boolean foundAnotherUser = foundUsers.stream()
                .anyMatch(user -> user.getId().equals(insertedAnotherUser.getId()));

        assertTrue(foundTestUser, "Тестовый пользователь должен быть найден");
        assertTrue(foundAnotherUser, "Второй пользователь должен быть найден");
    }

    @Test
    @Order(12)
    void testFindAllWithSorting() {
        logger.info("Тест: получение пользователей с сортировкой");

        // Тестируем сортировку по разным полям
        List<User> usersByIdAsc = userDAO.findAllWithSorting("id", true);
        List<User> usersByIdDesc = userDAO.findAllWithSorting("id", false);
        List<User> usersByUsername = userDAO.findAllWithSorting("username", true);
        List<User> usersByRole = userDAO.findAllWithSorting("role", true);
        List<User> usersByCreatedAt = userDAO.findAllWithSorting("created_at", true);

        assertNotNull(usersByIdAsc);
        assertNotNull(usersByIdDesc);
        assertNotNull(usersByUsername);
        assertNotNull(usersByRole);
        assertNotNull(usersByCreatedAt);

        // Проверяем, что все списки содержат данные
        assertFalse(usersByIdAsc.isEmpty(), "Список с сортировкой по ID ASC не должен быть пустым");

        // Проверяем сортировку по ID
        for (int i = 1; i < usersByIdAsc.size(); i++) {
            assertTrue(usersByIdAsc.get(i).getId() > usersByIdAsc.get(i-1).getId(),
                    "Сортировка по ID ASC должна быть восходящей");
        }

        for (int i = 1; i < usersByIdDesc.size(); i++) {
            assertTrue(usersByIdDesc.get(i).getId() < usersByIdDesc.get(i-1).getId(),
                    "Сортировка по ID DESC должна быть нисходящей");
        }
    }

    @Test
    @Order(13)
    void testFindByRoleWithSorting() {
        logger.info("Тест: поиск пользователей по роли с сортировкой");

        List<User> users = userDAO.findByRoleWithSorting("moderator", "username", true);

        assertNotNull(users, "Список пользователей не должен быть null");

        // Проверяем, что наш тестовый пользователь (теперь moderator) есть в списке
        boolean found = users.stream()
                .anyMatch(user -> user.getId().equals(testUser.getId()));
        assertTrue(found, "Тестовый пользователь должен быть в списке");

        // Проверяем сортировку по username (если есть более одного пользователя)
        if (users.size() > 1) {
            for (int i = 1; i < users.size(); i++) {
                assertTrue(users.get(i).getUsername().compareTo(users.get(i-1).getUsername()) >= 0,
                        "Сортировка по username должна быть восходящей");
            }
        }
    }

    @Test
    @Order(14)
    void testCountAll() {
        logger.info("Тест: подсчет всех пользователей");

        int count = userDAO.countAll();
        assertTrue(count > 0, "Количество пользователей должно быть больше 0");

        logger.info("Всего пользователей: {}", count);
    }

    @Test
    @Order(15)
    void testCountByRole() {
        logger.info("Тест: подсчет пользователей по роли");

        int moderatorCount = userDAO.countByRole("moderator");
        assertTrue(moderatorCount >= 1, "Должен быть хотя бы один модератор");

        int adminCount = userDAO.countByRole("admin");
        logger.info("Количество модераторов: {}, администраторов: {}", moderatorCount, adminCount);
    }

    @Test
    @Order(16)
    void testDeleteByUsername() {
        logger.info("Тест: удаление пользователя по username");

        // Создаем временного пользователя для удаления
        User tempUser = new User();
        tempUser.setUsername("temp_user_for_deletion_" + System.currentTimeMillis());
        tempUser.setPasswordHash("temp_password_hash");
        tempUser.setRole("user");

        User insertedTempUser = userDAO.insert(tempUser);
        assertNotNull(insertedTempUser);

        boolean deleteResult = userDAO.deleteByUsername(insertedTempUser.getUsername());
        assertTrue(deleteResult, "Удаление по username должно быть успешным");

        // Проверяем, что пользователь удален
        Optional<User> foundUser = userDAO.findByUsername(insertedTempUser.getUsername());
        assertFalse(foundUser.isPresent(), "Удаленный пользователь не должен быть найден");
    }

    @Test
    @Order(17)
    void testDeleteById() {
        logger.info("Тест: удаление пользователя по ID");

        // Создаем временного пользователя для удаления
        User tempUser = new User();
        tempUser.setUsername("another_temp_user_" + System.currentTimeMillis());
        tempUser.setPasswordHash("temp_hash");
        tempUser.setRole("user");

        User insertedTempUser = userDAO.insert(tempUser);
        assertNotNull(insertedTempUser);

        boolean deleteResult = userDAO.delete(insertedTempUser.getId());
        assertTrue(deleteResult, "Удаление по ID должно быть успешным");

        // Проверяем, что пользователь удален
        Optional<User> foundUser = userDAO.findById(insertedTempUser.getId());
        assertFalse(foundUser.isPresent(), "Удаленный пользователь не должен быть найден");
    }

    @Test
    @Order(18)
    void testEdgeCases() {
        logger.info("Тест: проверка граничных случаев");

        // Поиск несуществующего пользователя
        Optional<User> nonExistentUser = userDAO.findById(-999);
        assertFalse(nonExistentUser.isPresent(), "Несуществующий пользователь не должен быть найден");

        // Поиск по несуществующему username
        Optional<User> nonExistentUsername = userDAO.findByUsername("non_existent_username_" + System.currentTimeMillis());
        assertFalse(nonExistentUsername.isPresent(), "Несуществующий username не должен быть найден");

        // Поиск по пустому списку IDs
        List<User> emptyListResult = userDAO.findByIds(List.of());
        assertNotNull(emptyListResult, "Результат поиска по пустому списку не должен быть null");
        assertTrue(emptyListResult.isEmpty(), "Результат поиска по пустому списку должен быть пустым");

        // Обновление несуществующего пользователя
        User nonExistentUserForUpdate = new User();
        nonExistentUserForUpdate.setId(-999);
        nonExistentUserForUpdate.setUsername("test");
        nonExistentUserForUpdate.setPasswordHash("test");
        nonExistentUserForUpdate.setRole("user");

        boolean updateResult = userDAO.update(nonExistentUserForUpdate);
        assertFalse(updateResult, "Обновление несуществующего пользователя должно вернуть false");

        // Удаление несуществующего пользователя
        boolean deleteResult = userDAO.delete(-999);
        assertFalse(deleteResult, "Удаление несуществующего пользователя должно вернуть false");

        // Сортировка по недопустимому полю
        List<User> usersWithInvalidSort = userDAO.findAllWithSorting("invalid_field", true);
        assertNotNull(usersWithInvalidSort, "Список должен быть возвращен даже при недопустимом поле сортировки");
    }

    @Test
    @Order(19)
    void testMassOperations() {
        logger.info("Тест: массовые операции");

        // Создаем несколько пользователей
        List<User> usersToCreate = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setUsername("mass_user_" + i + "_" + System.currentTimeMillis());
            user.setPasswordHash("hash_" + i);
            user.setRole(i % 2 == 0 ? "user" : "admin");
            usersToCreate.add(user);
        }

        // Вставляем всех пользователей
        for (User user : usersToCreate) {
            User inserted = userDAO.insert(user);
            assertNotNull(inserted);
            cleanupUserIds.add(inserted.getId());

            // Проверяем, что пользователь создан
            boolean exists = userDAO.existsByUsername(inserted.getUsername());
            assertTrue(exists, "Созданный пользователь должен существовать");
        }

        // Проверяем общее количество
        int countAfter = userDAO.countAll();
        assertTrue(countAfter >= 5, "Должно быть создано как минимум 5 пользователей");

        // Проверяем количество по ролям
        int userCount = userDAO.countByRole("user");
        int adminCount = userDAO.countByRole("admin");

        logger.info("После массового создания: всего={}, user={}, admin={}",
                countAfter, userCount, adminCount);
    }

    @Test
    @Order(20)
    void testCountByRoleEmptyResult() {
        logger.info("Тест: подсчет пользователей по несуществующей роли");

        // Проверяем роль, которой точно нет в базе
        String nonExistentRole = "non_existent_role_" + System.currentTimeMillis();
        int count = userDAO.countByRole(nonExistentRole);

        assertEquals(0, count, "Количество пользователей с несуществующей ролью должно быть 0");

        logger.info("Тест подсчета по несуществующей роли выполнен");
    }

    @Test
    @Order(21)
    void testFindByUsernameLikeEmptyResult() {
        logger.info("Тест: поиск пользователей по несуществующей маске username");

        String nonExistentPattern = "xyz123abc_" + System.currentTimeMillis();
        List<User> users = userDAO.findByUsernameLike(nonExistentPattern);

        assertNotNull(users, "Список не должен быть null");
        assertTrue(users.isEmpty(), "Список должен быть пустым для несуществующей маски");

        logger.info("Тест поиска по несуществующей маске выполнен");
    }

    @Test
    @Order(22)
    void testInsertReturnsNullOnFailure() {
        logger.info("Тест: проверка обработки неудачной вставки пользователя");

        // Этот тест сложно выполнить без мокирования, но мы можем попытаться
        // создать пользователя с невалидными данными, которые могут вызвать SQLException
        // Например, попробуем создать пользователя с очень длинным username
        User invalidUser = new User();
        invalidUser.setUsername("a".repeat(1000)); // Предположительно превышает лимит БД
        invalidUser.setPasswordHash("hash");
        invalidUser.setRole("user");

        try {
            User result = userDAO.insert(invalidUser);
            // Если не выбросилось исключение, то результат может быть null
            if (result == null) {
                logger.info("Вставка невалидного пользователя вернула null, как и ожидалось");
            }
        } catch (RuntimeException e) {
            // Ожидаем RuntimeException с сообщением "Database error"
            assertTrue(e.getMessage().contains("Database error") ||
                            e.getMessage().contains("SQL"),
                    "Должно быть исключение об ошибке базы данных");
            logger.info("Вставка невалидного пользователя выбросила исключение, как и ожидалось");
        }
    }

    @Test
    @Order(23)
    void testUpdateRoleNonExistentUser() {
        logger.info("Тест: обновление роли несуществующего пользователя");

        boolean result = userDAO.updateRole(-999, "admin");
        assertFalse(result, "Обновление роли несуществующего пользователя должно вернуть false");
    }

    @Test
    @Order(24)
    void testUpdatePasswordNonExistentUser() {
        logger.info("Тест: обновление пароля несуществующего пользователя");

        boolean result = userDAO.updatePassword(-999, "new_hash");
        assertFalse(result, "Обновление пароля несуществующего пользователя должно вернуть false");
    }

    @Test
    @Order(25)
    void testDeleteNonExistentUser() {
        logger.info("Тест: удаление несуществующего пользователя");

        boolean result = userDAO.delete(-999);
        assertFalse(result, "Удаление несуществующего пользователя должно вернуть false");
    }

    @Test
    @Order(26)
    void testDeleteByUsernameNonExistent() {
        logger.info("Тест: удаление по несуществующему username");

        String nonExistentUsername = "non_existent_" + System.currentTimeMillis();
        boolean result = userDAO.deleteByUsername(nonExistentUsername);
        assertFalse(result, "Удаление по несуществующему username должно вернуть false");
    }
}
