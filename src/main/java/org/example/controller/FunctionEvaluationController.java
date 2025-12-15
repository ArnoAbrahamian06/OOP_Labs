package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.DTO.FunctionEvaluation.EvaluationRequest;
import org.example.DTO.FunctionEvaluation.EvaluationResponse;
import org.example.DTO.OpenAPI.ErrorResponse;
import org.example.service.FunctionEvaluationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/functions")
@Tag(name = "Function Evaluation", description = "Вычисление значений функций (интерполяция/экстраполяция)")
public class FunctionEvaluationController {

    private static final Logger log = LoggerFactory.getLogger(FunctionEvaluationController.class);

    private final FunctionEvaluationService evaluationService;

    @Autowired
    public FunctionEvaluationController(FunctionEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    /**
     * POST /api/functions/{id}/evaluate
     * Оценка функции (интерполяция/экстраполяция) в заданной точке X.
     * * @param id ID функции
     * @param request Объект с X-значением
     * @return Результат оценки Y или сообщение об ошибке
     */
    @Operation(summary = "Оценить функцию в точке X", description = "Возвращает значение Y для заданного X.")
    @PostMapping("/{id}/evaluate")
    public ResponseEntity<Object> evaluateFunction(@PathVariable Long id,
                                                   @Valid @RequestBody EvaluationRequest request) {

        log.info("API: Запрос на оценку функции ID: {} в точке X: {}", id, request.getXValue());

        // 1. Вызываем сервис для оценки
        Optional<EvaluationResponse> result = evaluationService.evaluate(id, request.getXValue());

        // 2. Явная проверка наличия результата (if/else)
        if (result.isPresent()) {
            EvaluationResponse response = result.get();
            log.info("API: Успешный результат оценки функции ID: {}. X: {} -> Y: {}", id, response.getX(), response.getY());

            // Успех: ResponseEntity<EvaluationResponse>
            return ResponseEntity.ok(response);

        } else {
            log.warn("API: Ошибка оценки функции ID: {}. Возвращаем BAD_REQUEST.", id);

            // Ошибка: ResponseEntity<ErrorResponse>
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("EvaluationError", "Не удалось оценить функцию по ID: " + id,
                            "Проверьте, существует ли функция и допустимо ли значение X (например, не найдена функция).")
            );
        }
    }
}