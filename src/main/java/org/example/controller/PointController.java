package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.DTO.Point.PointBatchUpdateItemDTO;
import org.example.DTO.Point.PointCreateDTO;
import org.example.service.PointService;
import org.example.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points")
@Tag(name = "Point Management", description = "APIs для управления точками")
public class PointController {

    @Autowired
    private PointService pointService;



    @Operation(summary = "Массовое создание точек", description = "Создает несколько точек за один запрос.")
    @ApiResponse(responseCode = "200", description = "Точки успешно созданы",
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Неверный размер списка или отсутствуют обязательные поля",
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
            content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/create/batch")
    public ResponseEntity<String> batchCreatePoints(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Список точек для создания",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PointCreateDTO.class))
            )
            @RequestBody List<PointCreateDTO> creates) {

        if (creates == null || creates.isEmpty() || creates.size() > Constants.MAX_BATCH_CREATE_SIZE) {
            return ResponseEntity.badRequest()
                    .body("Количество создаваемых точек должно быть от 1 до " + Constants.MAX_BATCH_CREATE_SIZE);
        }

        try {
            pointService.batchCreatePoints(creates);
            return ResponseEntity.ok("Создано " + creates.size() + " точек.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при массовом создании: " + e.getMessage());
        }
    }

    @Operation(summary = "Массовое обновление точек", description = "Обновляет координаты X и Y для списка точек по их ID.")
    @ApiResponse(responseCode = "200", description = "Точки успешно обновлены",
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "400", description = "Неверный размер списка или отсутствует ID точки",
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
            content = @Content(schema = @Schema(implementation = String.class)))
    @PutMapping("/update/batch")
    public ResponseEntity<String> batchUpdatePoints(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Список точек с новыми значениями X и Y",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PointBatchUpdateItemDTO.class))
            )
            @RequestBody List<PointBatchUpdateItemDTO> updates) {

        if (updates == null || updates.isEmpty() || updates.size() > Constants.MAX_BATCH_UPDATE_SIZE) {
            return ResponseEntity.badRequest()
                    .body("Количество обновлений должно быть от 1 до " + Constants.MAX_BATCH_UPDATE_SIZE);
        }

        try {
            pointService.batchUpdatePoints(updates);
            return ResponseEntity.ok("Обновлено " + updates.size() + " точек.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка при массовом обновлении: " + e.getMessage());
        }
    }
}