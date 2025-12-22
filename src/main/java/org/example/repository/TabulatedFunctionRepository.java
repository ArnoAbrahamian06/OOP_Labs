package org.example.repository;

import org.example.entity.Tabulated_function;
import org.example.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TabulatedFunctionRepository extends JpaRepository<Tabulated_function, Long> {

    // Поиск всех функций пользователя
    List<Tabulated_function> findByUser(User user);

    // Поиск по ID пользователя
    List<Tabulated_function> findByUserId(Long userId);

    // Поиск функций по имени
    List<Tabulated_function> findByNameContaining(String name);

    // Поиск функций по точному имени
    List<Tabulated_function> findByName(String name);

    // Поиск функций, созданных после указанной даты
    List<Tabulated_function> findByCreatedAtAfter(java.time.LocalDateTime date);

    // Поиск функции по имени и ID пользователя (для проверки уникальности)
    Optional<Tabulated_function> findByNameAndUserId(String name, Long userId);

    // Нативный SQL для поиска функций по имени типа функции
    @Query(value = "SELECT tf.* FROM tabulated_functions tf " +
            "JOIN functions_types ft ON tf.id = ft.tabulated_f_id " +
            "WHERE ft.name = :functionTypeName", nativeQuery = true)
    List<Tabulated_function> findByFunctionTypeName(@Param("functionTypeName") String functionTypeName);
}