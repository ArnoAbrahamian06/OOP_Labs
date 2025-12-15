package org.example.service;

import org.example.DTO.FunctionEvaluation.EvaluationResponse;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.entity.Tabulated_function;
import org.example.functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FunctionEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(FunctionEvaluationService.class);

    private final TabulatedFunctionService tabulatedFunctionService;
    private final TabulatedFunctionMapper tabulatedFunctionMapper;

    @Autowired
    public FunctionEvaluationService(TabulatedFunctionService tabulatedFunctionService,
                                     TabulatedFunctionMapper tabulatedFunctionMapper) {
        this.tabulatedFunctionService = tabulatedFunctionService;
        this.tabulatedFunctionMapper = tabulatedFunctionMapper;
    }

    /**
     * Оценивает значение функции Y в заданной точке X.
     * Логика интерполяции/экстраполяции инкапсулирована в доменном объекте TabulatedFunction.
     *
     * @param functionId ID функции в базе данных.
     * @param x Значение аргумента.
     * @return Optional с объектом EvaluationResponse (X, Y) или Optional.empty()
     */
    public Optional<EvaluationResponse> evaluate(Long functionId, double x) {
        log.info("SERVICE: Запрос на оценку функции ID: {} в точке X: {}", functionId, x);

        // 1. Загрузка сущности
        Optional<Tabulated_function> functionEntityOpt = tabulatedFunctionService.findById(functionId);
        if (functionEntityOpt.isEmpty()) {
            log.warn("SERVICE: Функция ID: {} не найдена", functionId);
            return Optional.empty();
        }

        Tabulated_function functionEntity = functionEntityOpt.get();

        // 2. Преобразование в доменный объект
        TabulatedFunction coreFunction = tabulatedFunctionMapper.toExternalTabulatedFunction(functionEntity);

        if (coreFunction == null) {
            log.error("SERVICE: Не удалось преобразовать сущность ID: {} в доменный объект.", functionId);
            return Optional.empty();
        }

        // 3. Вычисление значения Y
        try {
            // Метод apply(x) в TabulatedFunction должен содержать всю логику
            // интерполяции и экстраполяции.
            double y = coreFunction.apply(x);

            log.info("SERVICE: Успешная оценка функции ID: {}. X: {} -> Y: {}", functionId, x, y);
            return Optional.of(new EvaluationResponse(x, y));

        } catch (Exception e) {
            log.error("SERVICE: Ошибка при оценке функции ID: {} в точке X: {}: {}",
                    functionId, x, e.getMessage());
            return Optional.empty();
        }
    }
}