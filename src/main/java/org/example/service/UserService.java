package org.example.service;

import org.example.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteById(Long id);
    Optional<User> findByUsername(String username); // Дополнительный метод для аутентификации
    Optional<User> findByEmail(String email);       // Дополнительный метод для проверки уникальности
}