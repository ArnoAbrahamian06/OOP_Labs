package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.example.entity.User;
import org.example.entity.Role;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <-- Импортируем для @PreAuthorize
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin") // Базовый путь для админ-операций
@Tag(name = "Admin Management", description = "Административная панель для управления пользователями")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    // Создать нового пользователя (только для ADMIN)
    @Operation(summary = "Создать нового пользователя", description = "Создает пользователя с указанной ролью. Доступно только для ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "403", description = "Нет прав доступа")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/create")
    public ResponseEntity<User> createUser(
            @Parameter(description = "Имя пользователя") @RequestParam String username,
            @Parameter(description = "Пароль") @RequestParam String password,
            @Parameter(description = "Роль (USER, ADMIN, MODERATOR)") @RequestParam Role role) {
        log.info("API: Запрос на создание пользователя '{}' с ролью '{}' от администратора", username, role);

        try {
            User createdUser = userService.createUser(username, password, role);
            log.info("API: Пользователь '{}' успешно создан администратором", username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            log.warn("API: Ошибка при создании пользователя '{}': {}", username, e.getMessage());
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }
    }

    // Назначить роль пользователю (только для ADMIN)
    @Operation(summary = "Назначить роль пользователю", description = "Изменяет роль пользователя (например, повышает до ADMIN). Требует прав администратора.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Роль успешно изменена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/assign-role")
    public ResponseEntity<User> assignRole(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "Новая роль (USER, MODERATOR, ADMIN)") @RequestParam Role newRole) {
        log.info("API: Запрос на назначение роли '{}' пользователю с ID '{}' от администратора", newRole, userId);

        Optional<User> updatedUserOpt = userService.assignRole(userId, newRole);
        if (updatedUserOpt.isPresent()) {
            log.info("API: Роль '{}' успешно назначена пользователю с ID '{}'", newRole, userId);
            return ResponseEntity.ok(updatedUserOpt.get());
        } else {
            log.warn("API: Не удалось назначить роль. Пользователь с ID '{}' не найден.", userId);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // Получить всех пользователей (только для ADMIN)
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех зарегистрированных пользователей.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("API: Запрос на получение всех пользователей от администратора");
        List<User> users = userService.findAll();
        log.debug("API: Отправлен список {} пользователей администратору", users.size());
        return ResponseEntity.ok(users);
    }

    // Получить пользователей по роли (только для ADMIN)
    @Operation(summary = "Найти пользователей по роли", description = "Фильтрация пользователей по конкретной роли.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        log.info("API: Запрос на получение пользователей с ролью '{}' от администратора", role);
        List<User> users = userService.findByRole(role);
        log.debug("API: Отправлен список {} пользователей с ролью '{}' администратору", users.size(), role);
        return ResponseEntity.ok(users);
    }
}