package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.DTO.OpenAPI.*;
import org.example.DTO.TabulatedFunction.*;
import jakarta.validation.Valid;
import org.example.DTO.User.UserResponseDTO; // Для возврата информации о владельце
import org.example.DTO.Point.PointDTO; // Для возврата точек
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.entity.Role;
import org.example.service.TabulatedFunctionService;
import org.example.service.UserService;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.Mapper.UserMapper; // Для маппинга владельца
import org.example.Mapper.PointMapper; // Для маппинга точек
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/functions")
@Tag(name = "Tabulated Functions", description = "CRUD операции с табулированными функциями")
public class TabulatedFunctionController {

    private static final Logger log = LoggerFactory.getLogger(TabulatedFunctionController.class);

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;

    @Autowired
    private UserService userService; // Для проверки владельца

    @Autowired
    private TabulatedFunctionMapper tabulatedFunctionMapper;

    @Autowired
    private UserMapper userMapper; // Для маппинга User в UserResponseDTO

    @Autowired
    private PointMapper pointMapper; // Для маппинга Point в PointDTO

    // --- Создание функции ---
    @Operation(summary = "Создать функцию", description = "Создает новую функцию для текущего пользователя на основе списка точек.")
    @PostMapping
    public ResponseEntity<TabulatedFunctionResponseDTO> createFunction(@Valid @RequestBody FunctionCreateRequest request) {
        log.info("API: Создание новой функции: {}", request.getName());

        // 1. Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> currentUserOpt = userService.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            // Это маловероятно, если пользователь аутентифицирован, но на всякий случай
            log.error("API: Аутентифицированный пользователь '{}' не найден в базе данных!", currentUsername);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
        User currentUser = currentUserOpt.get();

        // 2. Проверяем, совпадает ли запрашиваемый владелец (request.getOwnerId()) с текущим пользователем
        if (!request.getOwnerId().equals(currentUser.getId())) {
            log.warn("API: Попытка создать функцию пользователем {}, но ownerId в запросе {} не совпадает с ID пользователя {}", currentUsername, request.getOwnerId(), currentUser.getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }

        // --- ПРЕОБРАЗОВАНИЕ DTO ---
        // Создаём внутреннее DTO из OpenAPI DTO
        TabulatedFunctionCreateDTO internalCreateDTO = new TabulatedFunctionCreateDTO();
        internalCreateDTO.setName(request.getName());
        internalCreateDTO.setUserId(request.getOwnerId()); // ownerId из OpenAPI DTO -> userId во внутреннем DTO

        // Преобразуем serializedData (предполагаем, что это строка в base64 или формате, который можно распарсить в List<PointDTO>)
        // Тут нужна логика парсинга serializedData в List<PointDTO>
        // PointDTO может быть List<PointDTO> points = parseSerializedData(request.getSerializedData());
        // internalCreateDTO.setPoints(points); // <-- УСТАНОВИТЬ ТОЧКИ
        // ПОКА ВРЕМЕННО ПУСТОЙ СПИСОК ИЛИ НУЛЬ, ЗАВИСИТ ОТ ЛОГИКИ
        internalCreateDTO.setPoints(null); // или используйте логику парсинга

        // --- /ПРЕОБРАЗОВАНИЕ DTO ---

        // 3. Преобразуем внутреннее DTO в сущность через маппер (теперь принимает TabulatedFunctionCreateDTO)
        Tabulated_function functionToSave = tabulatedFunctionMapper.toEntity(internalCreateDTO, currentUser); // <-- Передаём внутреннее DTO

        // 4. Сохраняем функцию (и связанные точки через cascade)
        Tabulated_function savedFunction = tabulatedFunctionService.save(functionToSave);

        // 5. Преобразуем сущность в ResponseDTO
        TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(savedFunction);

        log.info("API: Функция с ID {} успешно создана пользователем {}", savedFunction.getId(), currentUsername);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/functions/" + savedFunction.getId())
                .body(responseDTO);
    }

    // --- Получение функции по ID (только владельцу) ---
    @Operation(summary = "Получить функцию по ID", description = "Возвращает детальную информацию о функции, включая точки.")
    @GetMapping("/{id}")
    public ResponseEntity<TabulatedFunctionResponseDTO> getTabulatedFunctionById(@PathVariable Long id) {
        log.info("API: Запрос на получение функции с ID: {}", id);

        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            log.debug("API: Функция с ID {} не найдена", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        Tabulated_function func = funcOpt.get();

        // --- ПРОВЕРКА ВЛАДЕЛЬЦА ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!func.getUser().getUsername().equals(currentUsername)) {
            log.warn("API: Попытка доступа к функции ID {} пользователем {}, который не является владельцем", id, currentUsername);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }
        // --- /ПРОВЕРКА ВЛАДЕЛЬЦА ---

        TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(func);
        return ResponseEntity.ok(responseDTO);
    }

    // --- Обновление функции (только владельцу) ---
    @Operation(summary = "Обновить функцию", description = "Обновляет метаданные или точки функции.")
    @PutMapping("/{id}")
    public ResponseEntity<TabulatedFunctionResponseDTO> updateTabulatedFunction(@PathVariable Long id, @RequestBody FunctionUpdateRequest request) {
        log.info("API: Обновление функции с ID: {}", id);

        Optional<Tabulated_function> existingFuncOpt = tabulatedFunctionService.findById(id);
        if (existingFuncOpt.isEmpty()) {
            log.debug("API: Функция с ID {} не найдена для обновления", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        Tabulated_function existingFunc = existingFuncOpt.get();

        // --- ПРОВЕРКА ВЛАДЕЛЬЦА ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!existingFunc.getUser().getUsername().equals(currentUsername)) {
            log.warn("API: Попытка обновления функции ID {} пользователем {}, который не является владельцем", id, currentUsername);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }

        // --- ПРЕОБРАЗОВАНИЕ DTO ---
        // Преобразуем OpenAPI DTO в внутреннее DTO
        TabulatedFunctionUpdateDTO internalUpdateDTO = new TabulatedFunctionUpdateDTO();
        internalUpdateDTO.setName(request.getName());
        // internalUpdateDTO.setPoints(request.getPoints()); // Установить точки, если переданы
        // ... (логика обновления точек из request.getPoints() должна быть в маппере или сервисе)

        // Обновляем сущность, используя внутреннее DTO и маппер
        tabulatedFunctionMapper.partialUpdateFromDTO(internalUpdateDTO, existingFunc); // <-- Передаём внутреннее DTO

        Tabulated_function updatedFunction = tabulatedFunctionService.save(existingFunc);

        TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(updatedFunction);

        log.info("API: Функция с ID {} успешно обновлена пользователем {}", updatedFunction.getId(), currentUsername);
        return ResponseEntity.ok(responseDTO);
    }

    // --- Удаление функции (только владельцу) ---
    @Operation(summary = "Удалить функцию", description = "Удаляет функцию по ID. Доступно владельцу или администратору.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        log.info("API: Запрос на удаление функции с ID: {}", id);

        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            log.debug("API: Функция с ID {} не найдена для удаления", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        Tabulated_function func = funcOpt.get();

        // --- ПРОВЕРКА ВЛАДЕЛЬЦА ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!func.getUser().getUsername().equals(currentUsername)) {
            log.warn("API: Попытка удаления функции ID {} пользователем {}, который не является владельцем", id, currentUsername);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }

        tabulatedFunctionService.deleteById(id);

        log.info("API: Функция с ID {} успешно удалена пользователем {}", id, currentUsername);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // --- Получить все функции текущего пользователя ---
    @Operation(summary = "Функции текущего пользователя", description = "Возвращает список функций, созданных авторизованным пользователем.")
    @GetMapping("/owner/me") // Более явный путь для "моих" функций
    public ResponseEntity<List<TabulatedFunctionListDTO>> getFunctionsForCurrentUser() {
        log.info("API: Запрос на получение всех функций текущего пользователя");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> currentUserOpt = userService.findByUsername(currentUsername);
        if (currentUserOpt.isEmpty()) {
            // См. комментарий в createFunction
            log.error("API: Аутентифицированный пользователь '{}' не найден в базе данных!", currentUsername);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
        User currentUser = currentUserOpt.get();

        List<Tabulated_function> userFunctions = tabulatedFunctionService.findByUserId(currentUser.getId());
        List<TabulatedFunctionListDTO> responseDTOs = userFunctions.stream()
                .map(tabulatedFunctionMapper::toListDTO) // Используем toListDTO для краткой информации
                .collect(Collectors.toList());

        log.debug("API: Отправлен список {} функций пользователю {}", responseDTOs.size(), currentUsername);
        return ResponseEntity.ok(responseDTOs);
    }

    // --- Получить функции конкретного пользователя ---
    // Этот эндпоинт позволяет *любому* аутентифицированному пользователю
    // просматривать *все* функции *другого* пользователя по его ID.
    @Operation(summary = "Функции конкретного пользователя", description = "Поиск всех функций по ID создателя (для админов/модераторов).")
    @GetMapping("/owner/{userId}")
    public ResponseEntity<List<TabulatedFunctionListDTO>> getFunctionsByUserId(
            @Parameter(description = "ID пользователя") @PathVariable Long userId) {
        log.info("API: Запрос на получение функций пользователя с ID: {}", userId);

        // Проверка существования пользователя (опционально, но полезно)
        if (!userService.existsById(userId)) {
            log.warn("FunctionRetrieval: Запрошены функции для пользователя ID: {}, но пользователь не найден.", userId);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        // ПРОВЕРКА ПРАВ (например, администратор или текущий пользователь)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Проверяем, является ли текущий пользователь владельцем или администратором
        Optional<User> requestingUserOpt = userService.findByUsername(currentUsername);
        if (requestingUserOpt.isEmpty()) {
            log.error("API: Аутентифицированный пользователь '{}' не найден в базе данных!", currentUsername);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
        User requestingUser = requestingUserOpt.get();

        boolean isOwner = requestingUser.getId().equals(userId);
        boolean isAdmin = requestingUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            log.warn("FunctionRetrieval: Пользователь {} пытается получить функции другого пользователя (ID: {}) без прав администратора.", currentUsername, userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }

        List<Tabulated_function> functions = tabulatedFunctionService.findByUserId(userId);
        List<TabulatedFunctionListDTO> responseDTOs = functions.stream()
                .map(tabulatedFunctionMapper::toListDTO)
                .collect(Collectors.toList());

        log.debug("API: Отправлен список {} функций пользователю {} для владельца ID: {}", responseDTOs.size(), currentUsername, userId);
        return ResponseEntity.ok(responseDTOs);
    }

    // --- Получить функции по имени (частичное совпадение) ---
    @Operation(summary = "Поиск функций по имени", description = "Возвращает функции, имя которых содержит указанную подстроку.")
    @GetMapping("/by-name")
    public ResponseEntity<List<TabulatedFunctionListDTO>> getFunctionsByNameContaining(
            @Parameter(description = "Часть имени функции") @RequestParam String name) {
        log.info("API: Запрос на получение функций, имя которых содержит: {}", name);

        // Проверка аутентификации уже выполнена через SecurityConfig для /api/**

        List<Tabulated_function> functions = tabulatedFunctionService.findByNameContaining(name);
        List<TabulatedFunctionListDTO> responseDTOs = functions.stream()
                .map(tabulatedFunctionMapper::toListDTO)
                .collect(Collectors.toList());

        log.debug("API: Отправлен список {} функций, имя которых содержит '{}'", responseDTOs.size(), name);
        return ResponseEntity.ok(responseDTOs);
    }

    // --- Поиск функций по типу точки (например, X или Y в диапазоне - пример сложного поиска) ---
    // @GetMapping("/search/complex")
    // public ResponseEntity<List<FunctionListDTO>> searchComplexly(@RequestParam Double minX, @RequestParam Double maxX) {
    //     // ... логика поиска ...
    //     // Проверка аутентификации уже выполнена
    //     // ...
    // }
}