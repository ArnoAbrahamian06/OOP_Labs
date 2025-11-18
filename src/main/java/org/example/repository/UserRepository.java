package org.example.repository;

import org.example.entity.User;
import org.example.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Поиск по username
    @Query(value = "SELECT * FROM users WHERE username = :username", nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    // Поиск по email
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email);

    // Поиск по роли
    @Query(value = "SELECT * FROM users WHERE role = :role", nativeQuery = true)
    List<User> findByRole(@Param("role") String role);

    // Проверка существования по username
    @Query(value = "SELECT COUNT(*) > 0 FROM users WHERE username = :username", nativeQuery = true)
    boolean existsByUsername(@Param("username") String username);

    // Проверка существования по email
    @Query(value = "SELECT COUNT(*) > 0 FROM users WHERE email = :email", nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);

    // Поиск пользователей, созданных после указанной даты
    @Query(value = "SELECT * FROM users WHERE created_at > :date", nativeQuery = true)
    List<User> findByCreatedAtAfter(@Param("date") LocalDateTime date);

    // Поиск по части username
    @Query(value = "SELECT * FROM users WHERE username LIKE %:username%", nativeQuery = true)
    List<User> findByUsernameContaining(@Param("username") String username);

    // Подсчет пользователей по роли
    @Query(value = "SELECT COUNT(*) FROM users WHERE role = :role", nativeQuery = true)
    Long countByRole(@Param("role") String role);
}