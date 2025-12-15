package org.example.service;

import org.example.DTO.OpenAPI.BinaryOperationRequest;
import org.example.entity.Tabulated_function;
import org.example.enums.BinaryOperationType;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.operations.TabulatedFunctionOperationService;
import org.example.functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ArithmeticService {

    private static final Logger log = LoggerFactory.getLogger(ArithmeticService.class);

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;

    @Autowired
    private TabulatedFunctionMapper tabulatedFunctionMapper;

    // Внедряем или создаём TabulatedFunctionOperationService
    // Внедрение зависимости возможно, если TabulatedFunctionOperationService сам будет Spring-бином
    // или если его зависимости (TabulatedFunctionFactory) будут настроены.
    // Для простоты, создадим его здесь.
    private final TabulatedFunctionOperationService operationService = new TabulatedFunctionOperationService();

    /**
     * Выполняет бинарную операцию над двумя табулированными функциями (Spring-сущности).
     * Использует TabulatedFunctionOperationService для выполнения операции.
     *
     * @param functionId1 ID первой функции (Spring-сущность).
     * @param request     Запрос с ID второй функции и типом операции.
     * @return Optional с результатом операции (новая Spring-сущность Tabulated_function) или Optional.empty() в случае ошибки.
     */
    public Optional<Tabulated_function> performOperation(Long functionId1, BinaryOperationRequest request) {
        log.info("Выполнение арифметической операции {} над Spring-сущностями {} и {}", request.getOperation(), functionId1, request.getSecondFunctionId());

        // 1. Получить Spring-сущности
        Optional<Tabulated_function> func1Opt = tabulatedFunctionService.findById(functionId1);
        Optional<Tabulated_function> func2Opt = tabulatedFunctionService.findById(request.getSecondFunctionId());

        if (func1Opt.isEmpty() || func2Opt.isEmpty()) {
            log.warn("Не удалось найти Spring-сущность(и) для операции. func1: {}, func2: {}", func1Opt.isPresent(), func2Opt.isPresent());
            return Optional.empty();
        }

        Tabulated_function springFunc1 = func1Opt.get();
        Tabulated_function springFunc2 = func2Opt.get();

        // 2. Преобразовать Spring-сущности в TabulatedFunction (внешний интерфейс)
        TabulatedFunction externalFunc1 = tabulatedFunctionMapper.toExternalTabulatedFunction(springFunc1); // Предполагаем, что метод есть
        TabulatedFunction externalFunc2 = tabulatedFunctionMapper.toExternalTabulatedFunction(springFunc2); // Предполагаем, что метод есть

        if (externalFunc1 == null || externalFunc2 == null) {
            log.error("Ошибка преобразования Spring-сущности в TabulatedFunction.");
            return Optional.empty();
        }

        // 3. Выполнить операцию с помощью TabulatedFunctionOperationService
        TabulatedFunction externalResultFunc = null;
        try {
            switch (request.getOperation()) {
                case ADD:
                    externalResultFunc = operationService.add(externalFunc1, externalFunc2);
                    break;
                case SUB:
                    externalResultFunc = operationService.sub(externalFunc1, externalFunc2);
                    break;
                case MULT:
                    externalResultFunc = operationService.mult(externalFunc1, externalFunc2);
                    break;
                case DIV:
                    externalResultFunc = operationService.div(externalFunc1, externalFunc2);
                    break;
                default:
                    log.warn("Неизвестная операция: {}", request.getOperation());
                    return Optional.empty();
            }
        } catch (org.example.exceptions.InconsistentFunctionsException e) {
            log.error("Функции несовместимы для операции: {}", e.getMessage());
            return Optional.empty();
        }

        if (externalResultFunc == null) {
            log.error("Внешняя библиотека вернула null результат для операции {}.", request.getOperation());
            return Optional.empty();
        }

        // 4. Преобразовать результат (TabulatedFunction) обратно в Spring-сущность
        Tabulated_function resultSpringFunc = tabulatedFunctionMapper.toSpringTabulatedFunction(externalResultFunc); // Предполагаем, что метод есть
        if (resultSpringFunc == null) {
            log.error("Ошибка преобразования результата TabulatedFunction обратно в Spring-сущность.");
            return Optional.empty();
        }

        // 5. Установить владельца результата (например, владельца первой функции)
        resultSpringFunc.setUser(springFunc1.getUser());

        // 6. Сохранить результат как Spring-сущность
        Tabulated_function savedResult = tabulatedFunctionService.save(resultSpringFunc);

        log.info("Операция {} выполнена успешно через внешнюю библиотеку. Результат сохранён с ID: {}", request.getOperation(), savedResult.getId());
        return Optional.of(savedResult);
    }
}