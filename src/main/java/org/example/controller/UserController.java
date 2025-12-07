package org.example.controller;

import org.example.DTO.User.UserCreateDTO;
import org.example.DTO.User.UserDTO;
import org.example.DTO.User.UserResponseDTO;
import org.example.DTO.User.UserUpdateDTO;
import org.example.entity.User;
import org.example.Mapper.UserMapper;
import org.example.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    // GET /api/users - получить всех пользователей
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserResponseDTO> responseDTOs = users.stream()
                .map(userMapper::toUserResponseDTOFromEntity)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // GET /api/users/{id} - получить пользователя по ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            UserResponseDTO responseDTO = userMapper.toUserResponseDTOFromEntity(userOpt.get());
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/users - создать нового пользователя
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        User userToSave = userMapper.toEntity(userCreateDTO);
        User savedUser = userService.save(userToSave);
        UserResponseDTO responseDTO = userMapper.toUserResponseDTOFromEntity(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // PUT /api/users/{id} - обновить пользователя целиком
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        Optional<User> existingUserOpt = userService.findById(id);
        if (!existingUserOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOpt.get();
        userMapper.partialUpdateToEntity(userUpdateDTO, existingUser);
        User updatedUser = userService.save(existingUser);
        UserResponseDTO responseDTO = userMapper.toUserResponseDTOFromEntity(updatedUser);
        return ResponseEntity.ok(responseDTO);
    }

    // DELETE /api/users/{id} - удалить пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}