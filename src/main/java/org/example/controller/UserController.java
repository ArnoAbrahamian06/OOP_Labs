package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.DTO.User.UserCreateDTO;
import org.example.DTO.User.UserResponseDTO;
import org.example.DTO.User.UserUpdateDTO;
import org.example.entity.User;
import org.example.Mapper.UserMapper;
import org.example.service.UserService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "Управление профилем пользователя")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "Получить список всех пользователей")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("API: Запрос списка всех пользователей");
        List<UserResponseDTO> responseDTOs = userService.findAll().stream()
                .map(userMapper::toUserResponseDTOFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        log.info("API: Запрос пользователя по ID: {}", id);
        return userService.findById(id)
                .map(userMapper::toUserResponseDTOFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать пользователя (Регистрация)")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        log.info("API: Создание нового пользователя с username: {}", userCreateDTO.getUsername());
        // Маппер создает сущность с дефолтной ролью и датой
        User userToSave = userMapper.toEntity(userCreateDTO);
        User savedUser = userService.save(userToSave);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toUserResponseDTOFromEntity(savedUser));
    }

    @Operation(summary = "Обновить данные пользователя")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("API: Обновление пользователя с ID: {}", id);
        return userService.findById(id).map(existingUser -> {
            // Частичное обновление полей через маппер
            userMapper.partialUpdateToEntity(userUpdateDTO, existingUser);
            User updatedUser = userService.save(existingUser);
            return ResponseEntity.ok(userMapper.toUserResponseDTOFromEntity(updatedUser));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("API: Удаление пользователя с ID: {}", id);
        if (userService.existsById(id)) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("DeleteMapping: Запрошен пользователь ID: {} для удаления, но он не найден.", id);
            return ResponseEntity.notFound().build();
        }
    }
}