package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.example.DTO.OpenAPI.*;
import org.example.DTO.TabulatedFunction.*;
import org.example.DTO.Point.PointCreateDTO;
import org.example.DTO.Point.PointUpdateDTO;
import org.example.DTO.User.UserResponseDTO;
import org.example.entity.PointEntity;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.entity.Role;
import org.example.service.TabulatedFunctionService;
import org.example.service.UserService;
import org.example.service.PointService;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.Mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.ArrayList;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/functions")
@Tag(name = "Tabulated Functions", description = "CRUD операции с табулированными функциями")
@Validated
@SecurityRequirement(name = "bearerAuth")
public class TabulatedFunctionController {

    private static final Logger log = LoggerFactory.getLogger(TabulatedFunctionController.class);

    private final TabulatedFunctionService tabulatedFunctionService;
    private final UserService userService;
    private final PointService pointService;
    private final TabulatedFunctionMapper tabulatedFunctionMapper;
    private final UserMapper userMapper;

    @Autowired
    public TabulatedFunctionController(
            TabulatedFunctionService tabulatedFunctionService,
            UserService userService,
            PointService pointService,
            TabulatedFunctionMapper tabulatedFunctionMapper,
            UserMapper userMapper) {
        this.tabulatedFunctionService = tabulatedFunctionService;
        this.userService = userService;
        this.pointService = pointService;
        this.tabulatedFunctionMapper = tabulatedFunctionMapper;
        this.userMapper = userMapper;
    }

    // --- Создание функции ---
    @Operation(summary = "Создать функцию", description = "Создает новую функцию для текущего пользователя на основе списка точек.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Функция успешно создана",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TabulatedFunctionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Запрещено: попытка создать функцию для другого пользователя",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неавторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<?> createFunction(
            @Valid @RequestBody FunctionCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Создание новой функции: {}", request.getName());

        try {
            // 1. Проверка аутентификации
            if (currentUser == null) {
                log.warn("API: Попытка создания функции без аутентификации");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized", "Необходима аутентификация", null));
            }
            log.info("текущий пользователь: {}", currentUser);

            // 2. Проверка прав доступа (принудительно устанавливаем ID текущего пользователя)
            if (!request.getOwnerId().equals(currentUser.getId())) {
                log.warn("API: Пользователь {} (ID: {}) пытается создать функцию для другого пользователя (ID: {})",
                        currentUser.getUsername(), currentUser.getId(), request.getOwnerId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Forbidden", "Нельзя создать функцию для другого пользователя",
                                "requestedOwnerId=" + request.getOwnerId() + ", currentUserId=" + currentUser.getId()));
            }

            // 3. Валидация данных
            if (request.getPoints() == null || request.getPoints().size() < 2) {
                log.warn("API: Недостаточно точек для создания функции: {}",
                        request.getPoints() != null ? request.getPoints().size() : 0);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("InvalidData", "Должно быть минимум 2 точки", "points"));
            }

            // 4. Преобразование DTO в сущность
            Tabulated_function function = new Tabulated_function();
            function.setName(request.getName());
            function.setUser(currentUser);

            // 5. Создание и связывание точек с функцией
            List<PointEntity> points = new ArrayList<>();
            for (PointCreateDTO pointDto : request.getPoints()) {
                PointEntity point = new PointEntity();
                point.setX(pointDto.getX());
                point.setY(pointDto.getY());
                point.setTabulatedFunction(function);
                points.add(point);
            }
            function.setPoints(points);

            // 6. Сохранение функции (точки сохранятся каскадно благодаря CascadeType.ALL)
            Tabulated_function savedFunction = tabulatedFunctionService.save(function);

            if (savedFunction == null || savedFunction.getId() == null) {
                log.error("API: Не удалось создать функцию. Сохраненная функция: {}", savedFunction);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("InternalError", "Не удалось создать функцию", null));
            }

            // 7. Формирование ответа
            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(savedFunction);
            log.info("API: Пользователь {} создал новую функцию (ID: {})",
                    currentUser.getUsername(), savedFunction.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", "/api/functions/" + savedFunction.getId())
                    .body(responseDTO);

        } catch (IllegalArgumentException e) {
            log.error("API: Ошибка валидации при создании функции: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("InvalidData", e.getMessage(), null));
        } catch (Exception e) {
            log.error("API: Ошибка при создании функции: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при создании функции", e.getMessage()));
        }
    }

    // --- Получение функции по ID (только владельцу) ---
    @Operation(summary = "Получить функцию по ID", description = "Возвращает детальную информацию о функции, включая точки.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Функция успешно найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TabulatedFunctionResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Запрещено: доступ только владельцу",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Функция не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getTabulatedFunctionById(
            @Parameter(description = "ID функции", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Запрос на получение функции с ID: {}", id);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Необходима аутентификация", null));
        }

        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            log.debug("API: Функция с ID {} не найдена", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NotFound", "Функция с ID " + id + " не найдена", null));
        }

        Tabulated_function func = funcOpt.get();

        if (!func.getUser().getId().equals(currentUser.getId())) {
            log.warn("API: Пользователь {} (ID: {}) пытается получить доступ к функции ID {} владельца {} (ID: {})",
                    currentUser.getUsername(), currentUser.getId(),
                    id, func.getUser().getUsername(), func.getUser().getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Forbidden", "Доступ запрещен: вы не являетесь владельцем этой функции",
                            "functionId=" + id + ", ownerId=" + func.getUser().getId()));
        }

        try {
            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(func);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("API: Ошибка при преобразовании функции ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при обработке данных функции", e.getMessage()));
        }
    }

    // --- Обновление функции (только владельцу) ---
    @Operation(summary = "Обновить функцию", description = "Обновляет метаданные и/или точки функции.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Функция успешно обновлена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TabulatedFunctionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Запрещено: доступ только владельцу",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Функция не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTabulatedFunction(
            @Parameter(description = "ID функции", required = true) @PathVariable Long id,
            @Valid @RequestBody FunctionUpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Обновление функции с ID: {}", id);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Необходима аутентификация", null));
        }

        Optional<Tabulated_function> existingFuncOpt = tabulatedFunctionService.findById(id);
        if (existingFuncOpt.isEmpty()) {
            log.debug("API: Функция с ID {} не найдена для обновления", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NotFound", "Функция с ID " + id + " не найдена", null));
        }

        Tabulated_function existingFunc = existingFuncOpt.get();

        if (!existingFunc.getUser().getId().equals(currentUser.getId())) {
            log.warn("API: Пользователь {} (ID: {}) пытается обновить функцию ID {} владельца {} (ID: {})",
                    currentUser.getUsername(), currentUser.getId(),
                    id, existingFunc.getUser().getUsername(), existingFunc.getUser().getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Forbidden", "Нельзя обновить функцию другого пользователя",
                            "functionId=" + id + ", ownerId=" + existingFunc.getUser().getId()));
        }

        try {
            if (request.getPoints() != null && request.getPoints().size() < 2) {
                log.warn("API: Попытка обновить функцию ID {} с недостаточным количеством точек: {}",
                        id, request.getPoints().size());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("InvalidData", "Должно быть минимум 2 точки",
                                "points.size=" + request.getPoints().size()));
            }

            // Обновляем имя
            if (request.getName() != null) {
                existingFunc.setName(request.getName());
            }

            // Обновляем точки, если они переданы
            if (request.getPoints() != null) {
                log.info("API: Начало обновления точек для функции ID: {}. Количество новых точек: {}",
                        id, request.getPoints().size());

                // 1. Получаем текущую коллекцию точек
                List<PointEntity> existingPoints = existingFunc.getPoints();
                log.debug("API: Текущее количество точек в функции ID {}: {}",
                        id, existingPoints.size());

                // 2. Создаем маппинг для быстрого поиска
                log.debug("API: Создание маппинга новых точек");
                Map<String, PointEntity> newPointsMap = request.getPoints().stream()
                        .collect(Collectors.toMap(
                                dto -> String.format("%.6f_%.6f", dto.getX(), dto.getY()), // Форматируем double в строку
                                dto -> {
                                    PointEntity point = new PointEntity();
                                    point.setX(dto.getX());
                                    point.setY(dto.getY());
                                    point.setTabulatedFunction(existingFunc);
                                    log.trace("API: Создана новая точка: x={}, y={}", dto.getX(), dto.getY());
                                    return point;
                                },
                                (p1, p2) -> p1
                        ));
                log.debug("API: Создано {} уникальных точек в маппинге", newPointsMap.size());

                // 3. Ищем точки для удаления (те, которых нет в новых данных)
                log.debug("API: Поиск точек для удаления");
                List<PointEntity> pointsToRemove = new ArrayList<>();
                for (PointEntity existingPoint : existingPoints) {
                    String key = String.format("%.6f_%.6f", existingPoint.getX(), existingPoint.getY()); // Форматируем double в строку
                    if (!newPointsMap.containsKey(key)) {
                        pointsToRemove.add(existingPoint);
                        log.debug("API: Точка для удаления: x={}, y={}",
                                existingPoint.getX(), existingPoint.getY());
                    }
                }
                log.info("API: Найдено точек для удаления: {}", pointsToRemove.size());

                // 4. Удаляем старые точки из коллекции
                log.debug("API: Удаление точек из коллекции");
                int beforeRemoveCount = existingPoints.size();
                existingPoints.removeAll(pointsToRemove);
                log.info("API: Удалено точек: {}. Осталось точек после удаления: {}",
                        pointsToRemove.size(), existingPoints.size());

                // 5. Добавляем новые точки
                log.debug("API: Добавление новых точек");
                int addedPoints = 0;
                int skippedPoints = 0;

                for (PointEntity newPoint : newPointsMap.values()) {
                    // Проверяем, не существует ли уже такая точка (сравниваем double через допуск)
                    boolean exists = existingPoints.stream()
                            .anyMatch(p ->
                                    Math.abs(p.getX() - newPoint.getX()) < 0.000001 &&
                                            Math.abs(p.getY() - newPoint.getY()) < 0.000001
                            );
                    if (!exists) {
                        existingPoints.add(newPoint);
                        addedPoints++;
                        log.trace("API: Добавлена новая точка: x={}, y={}",
                                newPoint.getX(), newPoint.getY());
                    } else {
                        skippedPoints++;
                        log.trace("API: Точка уже существует, пропущена: x={}, y={}",
                                newPoint.getX(), newPoint.getY());
                    }
                }

                log.info("API: Добавлено новых точек: {}, пропущено дубликатов: {}",
                        addedPoints, skippedPoints);
                log.info("API: Общее количество точек после обновления: {}", existingPoints.size());

                // 6. Обновляем ссылку на коллекцию (Hibernate требует это)
                log.debug("API: Обновление ссылки на коллекцию точек");
                existingFunc.setPoints(existingPoints);

                log.info("API: Обновление точек для функции ID {} завершено. Итоговое количество точек: {}",
                        id, existingFunc.getPoints().size());
            }

            // Сохраняем обновленную функцию
            Tabulated_function updatedFunction = tabulatedFunctionService.save(existingFunc);

            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(updatedFunction);
            log.info("API: Функция с ID {} успешно обновлена пользователем {}. Обновлено: имя={}, точки={}",
                    updatedFunction.getId(), currentUser.getUsername(),
                    request.getName() != null,
                    request.getPoints() != null);

            return ResponseEntity.ok(responseDTO);

        } catch (IllegalArgumentException e) {
            log.warn("API: Неверные данные при обновлении функции ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("InvalidData", e.getMessage(), null));
        } catch (Exception e) {
            log.error("API: Ошибка при обновлении функции ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при обновлении функции", e.getMessage()));
        }
    }

    // --- Удаление функции (только владельцу) ---
    @Operation(summary = "Удалить функцию", description = "Удаляет функцию по ID. Доступно только владельцу.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Функция успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Запрещено: доступ только владельцу",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Функция не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFunction(
            @Parameter(description = "ID функции", required = true) @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Запрос на удаление функции с ID: {}", id);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Необходима аутентификация", null));
        }

        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            log.debug("API: Функция с ID {} не найдена для удаления", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NotFound", "Функция с ID " + id + " не найдена", null));
        }

        Tabulated_function func = funcOpt.get();

        if (!func.getUser().getId().equals(currentUser.getId())) {
            log.warn("API: Пользователь {} (ID: {}) пытается удалить функцию ID {} владельца {} (ID: {})",
                    currentUser.getUsername(), currentUser.getId(),
                    id, func.getUser().getUsername(), func.getUser().getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Forbidden", "Нельзя удалить функцию другого пользователя",
                            "functionId=" + id + ", ownerId=" + func.getUser().getId()));
        }

        try {
            // Сначала удаляем точки
            pointService.deleteByTabulatedFunctionId(id);
            tabulatedFunctionService.deleteById(id);
            log.info("API: Функция с ID {} успешно удалена пользователем {}", id, currentUser.getUsername());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("API: Ошибка при удалении функции ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при удалении функции", e.getMessage()));
        }
    }

    // --- Получить все функции текущего пользователя ---
    @Operation(summary = "Функции текущего пользователя", description = "Возвращает список функций, созданных авторизованным пользователем.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список функций успешно получен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TabulatedFunctionListDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Неавторизован",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/owner/me")
    public ResponseEntity<?> getFunctionsForCurrentUser(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Запрос на получение всех функций текущего пользователя");

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Необходима аутентификация", null));
        }

        try {
            List<Tabulated_function> userFunctions = tabulatedFunctionService.findByUserId(currentUser.getId());
            List<TabulatedFunctionListDTO> responseDTOs = userFunctions.stream()
                    .map(tabulatedFunctionMapper::toListDTO)
                    .collect(Collectors.toList());

            log.debug("API: Отправлен список {} функций пользователю {}", responseDTOs.size(), currentUser.getUsername());
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("API: Ошибка при получении функций пользователя {}: {}",
                    currentUser.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при получении списка функций", e.getMessage()));
        }
    }

    // --- Получить функции конкретного пользователя (для админов) ---
    @Operation(summary = "Функции конкретного пользователя", description = "Поиск всех функций по ID создателя (только для администраторов).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список функций успешно получен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TabulatedFunctionListDTO.class)))),
            @ApiResponse(responseCode = "403", description = "Запрещено: только для администраторов",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/owner/{userId}")
    public ResponseEntity<?> getFunctionsByUserId(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Long userId,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Запрос на получение функций пользователя с ID: {}", userId);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Необходима аутентификация", null));
        }

        if (!currentUser.getId().equals(userId) && currentUser.getRole() != Role.ADMIN) {
            log.warn("API: Пользователь {} (ID: {}, роль: {}) пытается получить функции пользователя ID {} без прав",
                    currentUser.getUsername(), currentUser.getId(), currentUser.getRole(), userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Forbidden", "Доступ запрещен: только администраторы или сам пользователь",
                            "requestedUserId=" + userId + ", currentUserRole=" + currentUser.getRole()));
        }

        if (!userService.existsById(userId)) {
            log.warn("API: Запрошены функции для несуществующего пользователя ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("NotFound", "Пользователь с ID " + userId + " не найден", null));
        }

        try {
            List<Tabulated_function> functions = tabulatedFunctionService.findByUserId(userId);
            List<TabulatedFunctionListDTO> responseDTOs = functions.stream()
                    .map(tabulatedFunctionMapper::toListDTO)
                    .collect(Collectors.toList());

            log.debug("API: Отправлен список {} функций пользователю {} для владельца ID: {}",
                    responseDTOs.size(), currentUser.getUsername(), userId);
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("API: Ошибка при получении функций пользователя ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при получении списка функций", e.getMessage()));
        }
    }

    // --- Получить функции по имени (частичное совпадение) ---
    @Operation(summary = "Поиск функций по имени", description = "Возвращает функции, имя которых содержит указанную подстроку.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список функций успешно получен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TabulatedFunctionListDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/by-name")
    public ResponseEntity<?> getFunctionsByNameContaining(
            @Parameter(description = "Часть имени функции", required = true, example = "sin")
            @RequestParam String name,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Запрос на получение функций, имя которых содержит: {}", name);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Необходима аутентификация", null));
        }

        if (name == null || name.trim().isEmpty()) {
            log.warn("API: Пустой параметр поиска имени");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("InvalidParameter", "Параметр 'name' не может быть пустым", null));
        }

        try {
            List<Tabulated_function> functions = tabulatedFunctionService.findByNameContaining(name.trim());
            List<TabulatedFunctionListDTO> responseDTOs = functions.stream()
                    .map(tabulatedFunctionMapper::toListDTO)
                    .collect(Collectors.toList());

            log.debug("API: Отправлен список {} функций, имя которых содержит '{}'", responseDTOs.size(), name);
            return ResponseEntity.ok(responseDTOs);
        } catch (Exception e) {
            log.error("API: Ошибка при поиске функций по имени '{}': {}", name, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при поиске функций", e.getMessage()));
        }
    }
}