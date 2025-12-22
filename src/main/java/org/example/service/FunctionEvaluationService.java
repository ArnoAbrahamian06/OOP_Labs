package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.DTO.FunctionEvaluation.EvaluationResponse;
import org.example.entity.Tabulated_function;
import org.example.functions.TabulatedFunction;
import org.example.Mapper.TabulatedFunctionMapper;

import java.util.Optional;

@Service
public class FunctionEvaluationService {
    private static final Logger log = LoggerFactory.getLogger(FunctionEvaluationService.class);

    private final TabulatedFunctionService tabulatedFunctionService;
    private final TabulatedFunctionMapper tabulatedFunctionMapper;

    @Autowired
    public FunctionEvaluationService(
            TabulatedFunctionService tabulatedFunctionService,
            TabulatedFunctionMapper tabulatedFunctionMapper) {
        this.tabulatedFunctionService = tabulatedFunctionService;
        this.tabulatedFunctionMapper = tabulatedFunctionMapper;
    }

    public Optional<EvaluationResponse> evaluate(Long id, double x) {
        log.info("SERVICE: Запрос на оценку функции ID: {} в точке X: {}", id, x);

        try {
            // 1. Получаем сущность из БД
            Tabulated_function entity = tabulatedFunctionService.findById(id)
                    .orElseThrow(() -> {
                        log.error("SERVICE: Функция с ID {} не найдена", id);
                        return new IllegalArgumentException("Функция не найдена");
                    });

            log.debug("SERVICE: Функция '{}' найдена, точек: {}",
                    entity.getName(), entity.getPoints().size());

            // 2. Создаем вычислимую функцию (без Strict декоратора)
            TabulatedFunction computableFunction = tabulatedFunctionMapper.toComputableFunction(entity);

            log.debug("SERVICE: Тип созданной функции: {}",
                    computableFunction.getClass().getSimpleName());

            // 3. Проверяем границы (для логов)
            double leftBound = computableFunction.leftBound();
            double rightBound = computableFunction.rightBound();
            log.debug("SERVICE: Границы функции: [{}, {}], запрашиваемое X: {}",
                    leftBound, rightBound, x);

            // 4. Вычисляем значение
            double y = computableFunction.apply(x);

            log.info("SERVICE: Успешное вычисление: f({}) = {}", x, y);
            return Optional.of(new EvaluationResponse(x, y));

        } catch (IllegalArgumentException e) {
            log.error("SERVICE: Ошибка валидации: {}", e.getMessage());
            return Optional.empty();
        } catch (UnsupportedOperationException e) {
            log.error("SERVICE: Функция не поддерживает вычисление в точке X={} (ограничения Strict)", x);
            return Optional.empty();
        } catch (Exception e) {
            log.error("SERVICE: Непредвиденная ошибка при оценке функции ID: {} в точке X: {}. Причина: {}",
                    id, x, e.getMessage(), e);
            return Optional.empty();
        }
    }
}