package org.example.service;

import org.example.entity.User;
import org.example.entity.Role;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);
    boolean existsById(Long id);
    User createUser(String username, String rawPassword, Role role);
    // Назначить роль существующему пользователю (только для администраторов)
    Optional<User> assignRole(Long userId, Role newRole);
    List<User> findByRole(Role role);
    Optional<User> findByUsername(String username); // Дополнительный метод для аутентификации
}