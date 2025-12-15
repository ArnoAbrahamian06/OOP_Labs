package org.example.service;

import org.example.entity.Tabulated_function;
import java.util.List;
import java.util.Optional;

public interface TabulatedFunctionService {
    List<Tabulated_function> findAll();
    Optional<Tabulated_function> findById(Long id);
    Tabulated_function save(Tabulated_function tabulatedFunction);
    void deleteById(Long id);
    // Найти функции по имени (частичное совпадение)
    List<Tabulated_function> findByNameContaining(String name);

    // Найти функции по точному имени
    List<Tabulated_function> findByName(String name);
    boolean existsById(Long id);

    // Найти функцию по имени и пользователю (например, для проверки уникальности)
    Optional<Tabulated_function> findByNameAndUserId(String name, Long userId);
    List<Tabulated_function> findByUserId(Long userId); // Дополнительный метод для получения функций пользователя
}