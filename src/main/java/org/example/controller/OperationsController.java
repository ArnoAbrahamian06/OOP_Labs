package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.DTO.OpenAPI.ErrorResponse;
import org.example.DTO.OpenAPI.BinaryOperationRequest;
import org.example.DTO.OpenAPI.DerivativeRequest;
import org.example.DTO.TabulatedFunction.TabulatedFunctionResponseDTO;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.entity.Tabulated_function;
import org.example.service.TabulatedFunctionService;
// Сервисы операций
import org.example.service.ArithmeticService;
import org.example.service.DifferentiationService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
// ...
@RestController
@RequestMapping("/api/functions")
@Tag(name = "Math Operations", description = "Математические операции: дифференцирование и арифметика")
public class OperationsController {

    private static final Logger log = LoggerFactory.getLogger(OperationsController.class);

    private final TabulatedFunctionService tabulatedFunctionService;
    private final TabulatedFunctionMapper tabulatedFunctionMapper;

    @Autowired(required = false)
    private ArithmeticService arithmeticService;

    @Autowired(required = false)
    private DifferentiationService differentiationService;

    @Autowired
    public OperationsController(TabulatedFunctionService tabulatedFunctionService,
                                TabulatedFunctionMapper tabulatedFunctionMapper) {
        this.tabulatedFunctionService = tabulatedFunctionService;
        this.tabulatedFunctionMapper = tabulatedFunctionMapper;
    }

    @Operation(summary = "Бинарная операция", description = "Выполняет операцию (сложение, вычитание, умножение, деление) между текущей функцией и другой.")
    @PostMapping("/{id}/operations/binary")
    public ResponseEntity<?> performBinaryOperation(
            @Parameter(description = "ID первой функции") @PathVariable Long id,
            @Valid @RequestBody BinaryOperationRequest request) {
        log.info("API: Запрос бинарной операции {} для функций ID: {} и ID: {}", request.getOperation(), id, request.getSecondFunctionId());
        Optional<Tabulated_function> func1Opt = tabulatedFunctionService.findById(id);
        Optional<Tabulated_function> func2Opt = tabulatedFunctionService.findById(request.getSecondFunctionId());

        if (func1Opt.isEmpty() || func2Opt.isEmpty()) {
            log.warn("BinaryOperation: Не найдена одна из функций. ID1: {}, ID2: {}", id, request.getSecondFunctionId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NotFound", "Одна из функций не найдена", "id"));
        }

        if (arithmeticService == null) {
            log.error("BinaryOperation: Сервис 'arithmeticService' не реализован (конфигурация).");
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new ErrorResponse("NotImplemented", "Сервис арифметики не реализован", null));
        }

        // Передаём ID первой функции и весь request в сервис
        Optional<Tabulated_function> resultOpt = arithmeticService.performOperation(id, request);

        if (resultOpt.isEmpty()) {
            log.warn("BinaryOperation: Не удалось выполнить операцию {} над функциями ID1: {} и ID2: {}. Вероятно, несовместимость данных (домены).", request.getOperation(), id, request.getSecondFunctionId());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("OperationError", "Не удалось выполнить операцию", null));
        }

        // Возвращаем DTO результата
        TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(resultOpt.get());
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "Вычислить производную", description = "Создает новую функцию, являющуюся производной от указанной.")
    @PostMapping("/{id}/derive")
    public ResponseEntity<?> computeDerivative(@PathVariable Long id,
                                               @RequestBody(required = false) DerivativeRequest request) {
        log.info("API: Запрос производной (порядок: {}) для функции ID: {}", request.getOrder(), id);
        // Если request == null, используем значения по умолчанию
        if (request == null) {
            log.debug("Differentiation: Request body пуст. Используются значения по умолчанию (order=1, method=CENTER).");
            request = new DerivativeRequest(); // Создаём с значениями по умолчанию (order = 1, method = CENTER)
        }

        if (differentiationService == null) {
            log.error("Differentiation: Сервис 'differentiationService' не реализован (конфигурация).");
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new ErrorResponse("NotImplemented", "Сервис дифференцирования не реализован", null));
        }

        // Передаём ID функции и весь request в сервис
        Optional<Tabulated_function> derivativeOpt = differentiationService.computeDerivative(id, request);

        if (derivativeOpt.isEmpty()) {
            log.warn("Differentiation: Не удалось вычислить производную для функции ID: {}. Параметры: order={}. Вероятно, недостаточно точек или невалидный запрос.", id, request.getOrder());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("OperationError", "Не удалось вычислить производную", null));
        }

        // Возвращаем DTO результата
        TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(derivativeOpt.get());
        return ResponseEntity.ok(responseDTO);
    }
}