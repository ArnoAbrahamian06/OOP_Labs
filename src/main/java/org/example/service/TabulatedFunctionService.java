package org.example.service;

import org.example.entity.Tabulated_function;
import java.util.List;
import java.util.Optional;

public interface TabulatedFunctionService {
    List<Tabulated_function> findAll();
    Optional<Tabulated_function> findById(Long id);
    Tabulated_function save(Tabulated_function tabulatedFunction);
    void deleteById(Long id);
    List<Tabulated_function> findByUserId(Long userId); // Дополнительный метод для получения функций пользователя
}