package org.example.Mapper;

import org.example.DTO.User.*;
import org.example.DTO.TabulatedFunction.TabulatedFunctionDTO;
import org.example.entity.User;
import org.example.entity.Role;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final TabulatedFunctionMapper tabulatedFunctionMapper;

    public UserMapper(TabulatedFunctionMapper tabulatedFunctionMapper) {
        this.tabulatedFunctionMapper = tabulatedFunctionMapper;
    }

    // Преобразование UserCreateDTO -> User
    public User toEntity(UserCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setPasswordHash(dto.getPassword()); // ВНИМАНИЕ: хешируйте в сервисе!
        entity.setCreatedAt(LocalDateTime.now()); // или @PrePersist
        entity.setRole(Role.USER); // Значение по умолчанию
        return entity;
    }

    // Преобразование UserUpdateDTO -> User (частичное обновление)
    public User partialUpdateToEntity(UserUpdateDTO dto, User existingUser) {
        if (dto == null) {
            return existingUser;
        }

        if (dto.getUsername() != null) {
            existingUser.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            existingUser.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            existingUser.setPasswordHash(dto.getPassword());
        }

        return existingUser;
    }

    // Преобразование User -> UserDTO
    public UserDTO toUserDTO(User entity) {
        if (entity == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setRole(entity.getRole());
        dto.setCreatedAt(entity.getCreatedAt());

        // Преобразование списка Tabulated_function
        if (entity.getTabulated_functions() != null) {
            dto.setTabulatedFunctions(entity.getTabulated_functions().stream()
                    .map(tabulatedFunctionMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // Преобразование UserDTO -> UserResponseDTO
    public UserResponseDTO toUserResponseDTO(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(dto.getId());
        responseDTO.setUsername(dto.getUsername());
        responseDTO.setEmail(dto.getEmail());
        responseDTO.setRole(dto.getRole() != null ? dto.getRole().toString() : null);
        responseDTO.setCreatedAt(dto.getCreatedAt());
        return responseDTO;
    }

    // Преобразование User -> UserResponseDTO
    public UserResponseDTO toUserResponseDTOFromEntity(User entity) {
        if (entity == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setRole(entity.getRole() != null ? entity.getRole().toString() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    // Преобразование UserDTO -> User
    public User toUserFromDTO(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User entity = new User();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setRole(dto.getRole());
        entity.setCreatedAt(dto.getCreatedAt());
        // Не устанавливаем passwordHash и tabulatedFunctions из DTO
        return entity;
    }
}