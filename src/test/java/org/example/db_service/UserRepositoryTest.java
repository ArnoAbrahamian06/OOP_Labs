package org.example.db_service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends BaseTest {

    private final UserRepository userRepository = new UserRepository();

    @Test
    void testInsertAndFindById() throws Exception {

        User user = TestDataGenerator.generateUser();

        User insertedUser = userRepository.insert(user);
        User foundUser = userRepository.findById(insertedUser.getId());

        assertNotNull(insertedUser.getId());
        assertNotNull(foundUser);
        assertEquals(insertedUser.getId(), foundUser.getId());
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertEquals(user.getLogin(), foundUser.getLogin());
        assertEquals(user.getRole(), foundUser.getRole());
        assertNotNull(foundUser.getCreatedTime());
    }

    @Test
    void testFindByEmail() throws Exception {
        User user = TestDataGenerator.generateUser();
        User insertedUser = userRepository.insert(user);

        User foundUser = userRepository.findByEmail(user.getEmail());

        assertNotNull(foundUser);
        assertEquals(insertedUser.getId(), foundUser.getId());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void testFindByLogin() throws Exception {
        User user = TestDataGenerator.generateUser();
        User insertedUser = userRepository.insert(user);

        User foundUser = userRepository.findByLogin(user.getLogin());

        assertNotNull(foundUser);
        assertEquals(insertedUser.getId(), foundUser.getId());
        assertEquals(user.getLogin(), foundUser.getLogin());
    }

    @Test
    void testFindByRole() throws Exception {
        String targetRole = "admin";
        User user1 = TestDataGenerator.generateUserWithSpecificRole(targetRole);
        User user2 = TestDataGenerator.generateUserWithSpecificRole(targetRole);
        User user3 = TestDataGenerator.generateUserWithSpecificRole("user"); // Другая роль

        userRepository.insert(user1);
        userRepository.insert(user2);
        userRepository.insert(user3);

        List<User> adminUsers = userRepository.findByRole(targetRole);

        assertEquals(2, adminUsers.size());
        assertTrue(adminUsers.stream().allMatch(u -> targetRole.equals(u.getRole())));
    }

    @Test
    void testFindAll() throws Exception {
        // Given
        int userCount = 5;
        for (int i = 0; i < userCount; i++) {
            userRepository.insert(TestDataGenerator.generateUser());
        }

        List<User> allUsers = userRepository.findAll();

        assertTrue(allUsers.size() >= userCount);
    }

    @Test
    void testFindWithPagination() throws Exception {
        // Given
        for (int i = 0; i < 10; i++) {
            userRepository.insert(TestDataGenerator.generateUser());
        }

        List<User> firstPage = userRepository.findWithPagination(5, 0);
        List<User> secondPage = userRepository.findWithPagination(5, 5);

        assertEquals(5, firstPage.size());
        assertTrue(secondPage.size() <= 5);

        // Проверяем, что страницы не пересекаются
        if (!firstPage.isEmpty() && !secondPage.isEmpty()) {
            assertNotEquals(firstPage.get(0).getId(), secondPage.get(0).getId());
        }
    }

    @Test
    void testUpdateEmail() throws Exception {
        User user = TestDataGenerator.generateUser();
        User insertedUser = userRepository.insert(user);
        String newEmail = "new_" + user.getEmail();

        boolean updated = userRepository.updateEmail(insertedUser.getId(), newEmail);
        User updatedUser = userRepository.findById(insertedUser.getId());

        assertTrue(updated);
        assertNotNull(updatedUser);
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(user.getLogin(), updatedUser.getLogin()); // Логин не должен измениться
    }

    @Test
    void testUpdatePassword() throws Exception {
        User user = TestDataGenerator.generateUser();
        User insertedUser = userRepository.insert(user);
        String newPasswordHash = "new_hash_" + System.currentTimeMillis();

        boolean updated = userRepository.updatePassword(insertedUser.getId(), newPasswordHash);
        User updatedUser = userRepository.findById(insertedUser.getId());

        assertTrue(updated);
        assertNotNull(updatedUser);
        assertEquals(newPasswordHash, updatedUser.getPasswordHash());
    }

    @Test
    void testUpdateRole() throws Exception {
        User user = TestDataGenerator.generateUser();
        User insertedUser = userRepository.insert(user);
        String newRole = "moderator";

        boolean updated = userRepository.updateRole(insertedUser.getId(), newRole);
        User updatedUser = userRepository.findById(insertedUser.getId());

        assertTrue(updated);
        assertNotNull(updatedUser);
        assertEquals(newRole, updatedUser.getRole());
    }

    @Test
    void testUpdateUserMultipleFields() throws Exception {
        User user = TestDataGenerator.generateUser();
        User insertedUser = userRepository.insert(user);
        String newEmail = "multi_update@example.com";
        String newLogin = "multi_update_login";

        boolean updated = userRepository.updateUser(insertedUser.getId(), newEmail, newLogin);
        User updatedUser = userRepository.findById(insertedUser.getId());

        assertTrue(updated);
        assertNotNull(updatedUser);
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(newLogin, updatedUser.getLogin());
    }

    @Test
    void testDeleteById() throws Exception {
        User user = TestDataGenerator.generateUser();
        User insertedUser = userRepository.insert(user);

        boolean deleted = userRepository.deleteById(insertedUser.getId());
        User foundUser = userRepository.findById(insertedUser.getId());

        assertTrue(deleted);
        assertNull(foundUser);
    }

    @Test
    void testDeleteByEmail() throws Exception {
        User user = TestDataGenerator.generateUser();
        User insertedUser = userRepository.insert(user);

        boolean deleted = userRepository.deleteByEmail(user.getEmail());
        User foundUser = userRepository.findByEmail(user.getEmail());

        assertTrue(deleted);
        assertNull(foundUser);
    }

    @Test
    void testUniqueConstraints() throws Exception {
        User user1 = TestDataGenerator.generateUser();
        userRepository.insert(user1);

        // Попытка создать пользователя с тем же email
        User user2 = TestDataGenerator.generateUser();
        user2.setEmail(user1.getEmail()); // Дублирующий email

        assertThrows(Exception.class, () -> userRepository.insert(user2));

        // Попытка создать пользователя с тем же логином
        User user3 = TestDataGenerator.generateUser();
        user3.setLogin(user1.getLogin()); // Дублирующий логин

        assertThrows(Exception.class, () -> userRepository.insert(user3));
    }
}