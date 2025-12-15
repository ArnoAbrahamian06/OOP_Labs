package org.example.repository;

import org.example.entity.User;
import org.example.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск по username
    Optional<User> findByUsername(String username);

    // Поиск по роли
    List<User> findByRole(Role role);

    // Проверка существования по username
    boolean existsByUsername(String username);

    // Поиск пользователей, созданных после указанной даты
    List<User> findByCreatedAtAfter(LocalDateTime date);

    // Поиск по части username
    List<User> findByUsernameContaining(String username);

    // Подсчет пользователей по роли
    Long countByRole(Role role);
}