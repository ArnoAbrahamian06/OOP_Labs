package org.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import org.example.DTO.TabulatedFunction.TabulatedFunctionListDTO;
import org.example.DTO.User.UserResponseDTO;
import org.example.DTO.Point.PointDTO;
import org.example.entity.Role;
import org.example.entity.PointEntity;
import org.example.entity.User;

import org.example.service.SearchService;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.Mapper.UserMapper;
import org.example.Mapper.PointMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Поиск пользователей и точек")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    // Внедряем мапперы для преобразования Entity -> DTO
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TabulatedFunctionMapper functionMapper;

    @Autowired
    private PointMapper pointMapper;

    // --- Поиск пользователей ---

    // Одиночный поиск пользователя по username (возвращаем UserResponseDTO)
    @Operation(summary = "Найти пользователя по username")
    @GetMapping("/users/single/{username}")
    public ResponseEntity<UserResponseDTO> findSingleUser(@PathVariable String username) {
        log.info("API: Поиск пользователя по username: {}", username);
        return searchService.findSingleUserByUsername(username)
                .map(userMapper::toUserResponseDTOFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Множественный поиск пользователей по роли (возвращаем List<UserResponseDTO>)
    @Operation(summary = "Найти пользователей по роли")
    @GetMapping("/users/multiple/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> findUsersByRole(@PathVariable Role role) {
        log.info("API: Множественный поиск пользователей по роли: {}", role);
        // Получаем Entity из сервиса
        List<User> users = searchService.findMultipleUsersByRole(role);

        // Преобразуем Entity в DTO
        List<UserResponseDTO> userDTOs = users.stream()
                .map(userMapper::toUserResponseDTOFromEntity) // Используем маппер
                .collect(Collectors.toList());

        // Возвращаем ResponseEntity с DTO
        return ResponseEntity.ok(userDTOs);
    }

    // --- Поиск функций ---

    // Поиск функций по ID пользователя (возвращаем List<TabulatedFunctionListDTO>)
    @Operation(summary = "Поиск функций по ID пользователя", description = "Возвращает список всех табулированных функций, принадлежащих указанному пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список функций найден (может быть пустым)"),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден")
    })
    @GetMapping("/functions/by-user/{userId}")
    public ResponseEntity<List<TabulatedFunctionListDTO>> findFunctionsByUserId(
            @Parameter(description = "ID пользователя-владельца функций", example = "1") @PathVariable Long userId) {
        log.info("API: Поиск функций по ID пользователя: {}", userId);
        List<TabulatedFunctionListDTO> results = searchService.findFunctionsByUserId(userId).stream()
                .map(functionMapper::toListDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    // Поиск функций по имени (содержит) (возвращаем List<TabulatedFunctionListDTO>)
    @Operation(summary = "Поиск функций по имени", description = "Возвращает список функций, название которых содержит указанную подстроку (без учета регистра).")
    @ApiResponse(responseCode = "200", description = "Успешный поиск")
    @GetMapping("/functions/by-name")
    public ResponseEntity<List<TabulatedFunctionListDTO>> findFunctionsByNameContaining(
            @Parameter(description = "Часть имени функции для поиска", example = "Sin") @RequestParam String name) {
        log.info("API: Поиск функций по имени (содержит): {}", name);
        List<TabulatedFunctionListDTO> results = searchService.findFunctionsByNameContaining(name).stream()
                .map(functionMapper::toListDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    // Поиск функций с минимальным количеством точек (возвращаем List<TabulatedFunctionListDTO>)
    @Operation(summary = "Функции с мин. кол-вом точек", description = "Находит функции, у которых количество точек больше или равно заданному.")
    @GetMapping("/functions/with-min-points/{minPoints}")
    public ResponseEntity<List<TabulatedFunctionListDTO>> findFunctionsWithMinPoints(@PathVariable int minPoints) {
        log.info("API: Поиск функций с минимум {} точками", minPoints);
        List<TabulatedFunctionListDTO> results = searchService.findFunctionsWithMinPoints(minPoints).stream()
                .map(functionMapper::toListDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    // --- Поиск точек ---

    // Поиск точек по ID функции (возвращаем List<PointDTO>)
    @Operation(summary = "Все точки функции", description = "Возвращает список всех точек для указанной функции.")
    @GetMapping("/points/by-function/{functionId}")
    public ResponseEntity<List<PointDTO>> findPointsByFunctionId(@PathVariable Long functionId) {
        log.info("API: Поиск точек по ID функции: {}", functionId);
        List<PointDTO> results = searchService.findPointsByFunctionId(functionId).stream()
                .map(pointMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    // Поиск точек в диапазоне X/Y (возвращаем List<PointDTO>)
    @Operation(summary = "Поиск точек в диапазоне", description = "Находит точки, попадающие в прямоугольную область (minX..maxX, minY..maxY).")
    @GetMapping("/points/in-range")
    public ResponseEntity<List<PointDTO>> findPointsInXYRange(
            @RequestParam Double minX,
            @RequestParam Double maxX,
            @RequestParam Double minY,
            @RequestParam Double maxY) {
        log.info("API: Поиск точек в диапазоне X: [{} - {}], Y: [{} - {}]", minX, maxX, minY, maxY);
        List<PointDTO> results = searchService.findPointsInXYRange(minX, maxX, minY, maxY).stream()
                .map(pointMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    // Расширенный поиск точек с фильтрацией и сортировкой (возвращаем List<PointDTO>)
    @Operation(summary = "Расширенный поиск точек", description = "Поиск точек по диапазонам координат с пагинацией и сортировкой.")
    @GetMapping("/points/advanced")
    public ResponseEntity<List<PointDTO>> advancedPointSearch(
            @Parameter(description = "ID функции") @RequestParam(required = false) Long functionId,
            @Parameter(description = "Мин. X") @RequestParam(required = false) Double minX,
            @Parameter(description = "Макс. X") @RequestParam(required = false) Double maxX,
            @Parameter(description = "Мин. Y") @RequestParam(required = false) Double minY,
            @Parameter(description = "Макс. Y") @RequestParam(required = false) Double maxY,
            @Parameter(description = "Поле сортировки") @RequestParam(defaultValue = "x") String sortBy,
            @Parameter(description = "Направление (asc/desc)") @RequestParam(defaultValue = "asc") String direction) {

        log.info("API: Расширенный поиск точек (fId={}, minX={}, sortBy={}, direction={})", functionId, minX, sortBy, direction);

        // SearchService возвращает Entity
        List<PointEntity> points = searchService.advancedPointSearch(functionId, minX, maxX, minY, maxY, sortBy, direction);

        // Маппим Entity в DTO
        List<PointDTO> dtos = points.stream()
                .map(pointMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}