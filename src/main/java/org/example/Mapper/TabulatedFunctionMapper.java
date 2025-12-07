package org.example.Mapper;

import org.example.DTO.TabulatedFunction.*;
import org.example.DTO.FunctionType.FunctionTypeDTO;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TabulatedFunctionMapper {

    private final FunctionTypeMapper functionTypeMapper;
    private final UserMapper userMapper;

    public TabulatedFunctionMapper(FunctionTypeMapper functionTypeMapper, UserMapper userMapper) {
        this.functionTypeMapper = functionTypeMapper;
        this.userMapper = userMapper;
    }

    // Преобразование TabulatedFunctionCreateDTO -> Tabulated_function
    public Tabulated_function toEntity(TabulatedFunctionCreateDTO dto, User user) {
        if (dto == null || user == null) {
            return null;
        }

        Tabulated_function entity = new Tabulated_function();
        entity.setSerializedData(dto.getSerializedData());
        entity.setUser(user);
        // createdAt и updatedAt устанавливаются в @PrePersist
        return entity;
    }

    // Преобразование Tabulated_function -> TabulatedFunctionDTO
    public TabulatedFunctionDTO toDTO(Tabulated_function entity) {
        if (entity == null) {
            return null;
        }

        TabulatedFunctionDTO dto = new TabulatedFunctionDTO();
        dto.setId(entity.getId());
        dto.setSerializedData(entity.getSerializedData());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }

        if (entity.getFunctionTypes() != null) {
            dto.setFunctionTypes(entity.getFunctionTypes().stream()
                    .map(functionTypeMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // Преобразование Tabulated_function -> TabulatedFunctionListDTO
    public TabulatedFunctionListDTO toListDTO(Tabulated_function entity) {
        if (entity == null) {
            return null;
        }

        TabulatedFunctionListDTO dto = new TabulatedFunctionListDTO();
        dto.setId(entity.getId());
        dto.setSerializedData(entity.getSerializedData());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }
        dto.setFunctionTypesCount(entity.getFunctionTypes() != null ? entity.getFunctionTypes().size() : 0);
        return dto;
    }

    // Преобразование Tabulated_function -> TabulatedFunctionResponseDTO
    public TabulatedFunctionResponseDTO toResponseDTO(Tabulated_function entity) {
        if (entity == null) {
            return null;
        }

        TabulatedFunctionResponseDTO dto = new TabulatedFunctionResponseDTO();
        dto.setId(entity.getId());
        dto.setSerializedData(entity.getSerializedData());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }

        if (entity.getFunctionTypes() != null) {
            dto.setFunctionTypes(entity.getFunctionTypes().stream()
                    .map(functionTypeMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // Преобразование TabulatedFunctionDTO -> Tabulated_function
    public Tabulated_function toEntityFromDTO(TabulatedFunctionDTO dto) {
        if (dto == null) {
            return null;
        }

        Tabulated_function entity = new Tabulated_function();
        entity.setId(dto.getId());
        entity.setSerializedData(dto.getSerializedData());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }

    // Преобразование TabulatedFunctionUpdateDTO -> Tabulated_function (частичное обновление)
    public Tabulated_function partialUpdateFromDTO(TabulatedFunctionUpdateDTO dto, Tabulated_function existingEntity) {
        if (dto == null || existingEntity == null) {
            return existingEntity;
        }

        if (dto.getSerializedData() != null) {
            existingEntity.setSerializedData(dto.getSerializedData());
        }
        // updatedAt обновляется в @PreUpdate
        return existingEntity;
    }
}