package org.example.Mapper;

import org.example.DTO.FunctionType.*;
import org.example.entity.Function_type;
import org.example.entity.Tabulated_function;
import org.springframework.stereotype.Component;

@Component
public class FunctionTypeMapper {

    private final TabulatedFunctionMapper tabulatedFunctionMapper;

    public FunctionTypeMapper(TabulatedFunctionMapper tabulatedFunctionMapper) {
        this.tabulatedFunctionMapper = tabulatedFunctionMapper;
    }

    // Преобразование FunctionTypeCreateDTO -> Function_type
    public Function_type toEntity(FunctionTypeCreateDTO dto, Tabulated_function tabulatedFunction) {
        if (dto == null || tabulatedFunction == null) {
            return null;
        }

        Function_type entity = new Function_type();
        entity.setName(dto.getName());
        entity.setLocName(dto.getLocName());
        entity.setPriority(dto.getPriority());
        entity.setTabulatedFunction(tabulatedFunction);
        // createdAt и updatedAt устанавливаются в @PrePersist
        return entity;
    }

    // Преобразование Function_type -> FunctionTypeDTO
    public FunctionTypeDTO toDTO(Function_type entity) {
        if (entity == null) {
            return null;
        }

        FunctionTypeDTO dto = new FunctionTypeDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocName(entity.getLocName());
        dto.setPriority(entity.getPriority());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getTabulatedFunction() != null) {
            dto.setTabulatedFunctionId(entity.getTabulatedFunction().getId());
        }
        return dto;
    }

    // Преобразование Function_type -> FunctionTypeListDTO
    public FunctionTypeListDTO toListDTO(Function_type entity) {
        if (entity == null) {
            return null;
        }

        FunctionTypeListDTO dto = new FunctionTypeListDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocName(entity.getLocName());
        dto.setPriority(entity.getPriority());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getTabulatedFunction() != null) {
            dto.setTabulatedFunctionId(entity.getTabulatedFunction().getId());
            String data = entity.getTabulatedFunction().getSerializedData();
            if (data != null) {
                dto.setTabulatedFunctionPreview(data.length() > 20 ? data.substring(0, 20) + "..." : data);
            }
        }
        return dto;
    }

    // Преобразование Function_type -> FunctionTypeResponseDTO
    public FunctionTypeResponseDTO toResponseDTO(Function_type entity) {
        if (entity == null) {
            return null;
        }

        FunctionTypeResponseDTO dto = new FunctionTypeResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocName(entity.getLocName());
        dto.setPriority(entity.getPriority());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getTabulatedFunction() != null) {
            dto.setTabulatedFunctionId(entity.getTabulatedFunction().getId());
            dto.setTabulatedFunctionSerializedData(entity.getTabulatedFunction().getSerializedData());
        }
        return dto;
    }

    // Преобразование Function_type -> FunctionTypeWithFunctionDTO
    public FunctionTypeWithFunctionDTO toWithFunctionDTO(Function_type entity) {
        if (entity == null) {
            return null;
        }

        FunctionTypeWithFunctionDTO dto = new FunctionTypeWithFunctionDTO();
        dto.setFunctionType(toResponseDTO(entity));
        dto.setTabulatedFunction(tabulatedFunctionMapper.toResponseDTO(entity.getTabulatedFunction()));
        return dto;
    }

    // Преобразование FunctionTypeDTO -> Function_type
    public Function_type toEntityFromDTO(FunctionTypeDTO dto) {
        if (dto == null) {
            return null;
        }

        Function_type entity = new Function_type();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLocName(dto.getLocName());
        entity.setPriority(dto.getPriority());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }

    // Преобразование FunctionTypeUpdateDTO -> Function_type (частичное обновление)
    public Function_type partialUpdateFromDTO(FunctionTypeUpdateDTO dto, Function_type existingEntity) {
        if (dto == null || existingEntity == null) {
            return existingEntity;
        }

        if (dto.getName() != null) {
            existingEntity.setName(dto.getName());
        }
        if (dto.getLocName() != null) {
            existingEntity.setLocName(dto.getLocName());
        }
        if (dto.getPriority() != null) {
            existingEntity.setPriority(dto.getPriority());
        }
        // updatedAt обновляется в @PreUpdate
        return existingEntity;
    }
}