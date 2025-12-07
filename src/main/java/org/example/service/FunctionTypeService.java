package org.example.service;

import org.example.entity.Function_type;
import java.util.List;
import java.util.Optional;

public interface FunctionTypeService {
    List<Function_type> findAll();
    Optional<Function_type> findById(Long id);
    Function_type save(Function_type functionType);
    void deleteById(Long id);
    List<Function_type> findByTabulatedFunctionId(Long tabulatedFunctionId); // Дополнительный метод для поиска всех Function_type
}