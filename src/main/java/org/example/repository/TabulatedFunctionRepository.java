package org.example.repository;

import org.example.entity.Tabulated_function;
import org.example.entity.User;
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

    // Поиск функций, содержащих определенные данные
    List<Tabulated_function> findBySerializedDataContaining(String data);

    // Поиск функций, созданных после указанной даты
    List<Tabulated_function> findByCreatedAtAfter(java.time.LocalDateTime date);

    @Query(value = "SELECT tf.* FROM tabulated_functions tf " +
            "WHERE (SELECT COUNT(*) FROM functions_types ft WHERE ft.tabulated_function_id = tf.id) > :minTypes",
            nativeQuery = true)
    List<Tabulated_function> findWithMinFunctionTypes(@Param("minTypes") int minTypes);

    // Нативный SQL для поиска функций по имени типа функции
    @Query(value = "SELECT tf.* FROM tabulated_functions tf " +
            "JOIN functions_types ft ON tf.id = ft.tabulated_function_id " +
            "WHERE ft.name = :functionTypeName", nativeQuery = true)
    List<Tabulated_function> findByFunctionTypeName(@Param("functionTypeName") String functionTypeName);
}