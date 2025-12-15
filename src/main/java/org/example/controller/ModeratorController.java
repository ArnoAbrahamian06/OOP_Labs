package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.entity.User;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/moderator")
@Tag(name = "Moderator Panel", description = "Инструменты для модерации контента и просмотра статистики")
@PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
public class ModeratorController {

    private static final Logger log = LoggerFactory.getLogger(ModeratorController.class);

    @Autowired
    private UserService userService;

    // Пример: получить всех пользователей (доступно модератору и админу)
    @Operation(summary = "Получить всех пользователей (для модератора)", description = "Возвращает полный список зарегистрированных пользователей. Доступно только для ролей MODERATOR и ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsersForModerator() {
        log.info("API: Запрос на получение всех пользователей от модератора/админа");
        List<User> users = userService.findAll();
        log.debug("API: Отправлен список {} пользователей модератору/админу", users.size());
        return ResponseEntity.ok(users);
    }

    // Пример: найти пользователя по ID (доступно модератору и админу)
    @Operation(summary = "Получить пользователя по ID (для модератора)", description = "Возвращает детальную информацию о пользователе по его ID. Позволяет модератору просматривать профили любых пользователей.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserByIdForModerator(
            @Parameter(description = "ID искомого пользователя", example = "10") @PathVariable Long id) {
        log.debug("API: Модератор/админ запрашивает пользователя с ID: {}", id);
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            log.debug("API: Отправлен пользователь '{}' модератору/админу", userOpt.get().getUsername());
            return ResponseEntity.ok(userOpt.get());
        } else {
            log.debug("API: Пользователь с ID {} не найден (модератор/админ)", id);
            return ResponseEntity.notFound().build();
        }
    }

    // удалить пользователя
    // @DeleteMapping("/users/{id}")
    // public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    //     // ...
    // }

}