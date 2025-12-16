package org.example.service;

import org.example.DTO.OpenAPI.DerivativeRequest;
import org.example.entity.Tabulated_function;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.operations.TabulatedDifferentialOperator; // Внешняя библиотека
import org.example.functions.TabulatedFunction; // Внешний интерфейс
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class DifferentiationService {

    private static final Logger log = LoggerFactory.getLogger(DifferentiationService.class);

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;

    @Autowired
    private TabulatedFunctionMapper tabulatedFunctionMapper; // Для конвертации


    private final TabulatedDifferentialOperator differentialOperator =
            new TabulatedDifferentialOperator(); // Использует фабрику по умолчанию

    /**
     * Вычисляет n-ую производную табулированной функции.
     * Использует TabulatedDifferentialOperator для вычисления *одной* производной.
     * ПРИМЕЧАНИЕ: DerivativeMethod из запроса ИГНОРИРУЕТСЯ, так как TabulatedDifferentialOperator
     * реализует только один метод (центральные разности внутри, односторонние на краях).
     *
     * @param functionId ID функции (Spring-сущность).
     * @param request Запрос с порядком производной и методом.
     * @return Optional с функцией-производной (Spring-сущность) или Optional.empty() в случае ошибки.
     */
    public Optional<Tabulated_function> computeDerivative(Long functionId, DerivativeRequest request) {
        log.info("Вычисление {}-ой производной функции {}",
                request.getOrder(), functionId);

        Optional<Tabulated_function> originalSpringFunctionOpt = tabulatedFunctionService.findById(functionId);
        if (originalSpringFunctionOpt.isEmpty()) {
            log.warn("Функция с ID {} не найдена для вычисления производной.", functionId);
            return Optional.empty();
        }

        Tabulated_function originalSpringFunction = originalSpringFunctionOpt.get();

        if (originalSpringFunction.getPoints().size() < 2) {
            log.warn("Недостаточно точек для вычисления производной функции {}.", functionId);
            return Optional.empty(); // Нельзя вычислить производную без минимум двух точек
        }

        Tabulated_function currentSpringFunction = originalSpringFunction;

        // Вычисляем производную 'order' раз
        for (int i = 0; i < request.getOrder(); i++) {
            log.debug("Вычисление {}-ой производной...", i + 1);

            // 1. Преобразовать Spring-сущность в TabulatedFunction (внешний интерфейс)
            TabulatedFunction externalFunc = tabulatedFunctionMapper.toExternalTabulatedFunction(currentSpringFunction);
            if (externalFunc == null) {
                log.error("Ошибка преобразования Spring-сущности в TabulatedFunction на итерации {}.", i);
                return Optional.empty();
            }

            // 2. Выполнить дифференцирование с помощью TabulatedDifferentialOperator
            // ПРИМЕЧАНИЕ: метод из request.getMethod() ИГНОРИРУЕТСЯ!
            TabulatedFunction externalDerivativeFunc = differentialOperator.derive(externalFunc);

            if (externalDerivativeFunc == null) {
                log.error("Внешняя библиотека вернула null результат для производной на итерации {}.", i);
                return Optional.empty();
            }

            // 3. Преобразовать результат (TabulatedFunction) обратно в Spring-сущность
            Tabulated_function resultSpringFunc = tabulatedFunctionMapper.toSpringTabulatedFunction(externalDerivativeFunc);
            if (resultSpringFunc == null) {
                log.error("Ошибка преобразования результата TabulatedFunction обратно в Spring-сущность на итерации {}.", i);
                return Optional.empty();
            }

            // 4. Установить владельца и имя для результата
            resultSpringFunc.setUser(currentSpringFunction.getUser()); // Наследуем владельца
            resultSpringFunc.setName(currentSpringFunction.getName() + "_derivative_" + (i + 1)); // Генерируем имя

            // 5. Обновить currentSpringFunction для следующей итерации
            currentSpringFunction = resultSpringFunc;
        }

        log.info("Вычисление {}-ой производной завершено. Результат: {}", request.getOrder(), currentSpringFunction.getName());
        return Optional.of(currentSpringFunction);
    }
}