package org.example.Mapper;

import org.example.DTO.Point.*;
import org.example.entity.PointEntity;
import org.example.entity.Tabulated_function;
import org.springframework.stereotype.Component;

@Component
public class PointMapper {

    // Преобразование PointCreateDTO -> Point
    public PointEntity toEntity(PointCreateDTO dto, Tabulated_function tabulatedFunction) {
        if (dto == null || tabulatedFunction == null) {
            return null;
        }

        PointEntity entity = new PointEntity();
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        entity.setTabulatedFunction(tabulatedFunction);
        // ID устанавливается автоматически JPA
        return entity;
    }

    // Преобразование Point -> PointDTO
    public PointDTO toDTO(PointEntity entity) {
        if (entity == null) {
            return null;
        }

        PointDTO dto = new PointDTO();
        dto.setId(entity.getId());
        dto.setX(entity.getX());
        dto.setY(entity.getY());
        if (entity.getTabulatedFunction() != null) {
            dto.setTabulatedFunctionId(entity.getTabulatedFunction().getId());
        }
        return dto;
    }

    // Преобразование Point -> PointResponseDTO
    public PointResponseDTO toResponseDTO(PointEntity entity) {
        if (entity == null) {
            return null;
        }

        PointResponseDTO dto = new PointResponseDTO();
        dto.setId(entity.getId());
        dto.setX(entity.getX());
        dto.setY(entity.getY());
        if (entity.getTabulatedFunction() != null) {
            dto.setTabulatedFunctionId(entity.getTabulatedFunction().getId());
        }
        return dto;
    }

    // Преобразование PointDTO -> Point
    public PointEntity toEntityFromDTO(PointDTO dto) {
        if (dto == null) {
            return null;
        }

        PointEntity entity = new PointEntity();
        entity.setId(dto.getId());
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        // tabulatedFunction устанавливается отдельно
        return entity;
    }

    // Преобразование PointUpdateDTO -> Point (частичное обновление)
    public PointEntity partialUpdateFromDTO(PointUpdateDTO dto, PointEntity existingEntity) {
        if (dto == null || existingEntity == null) {
            return existingEntity;
        }

        if (dto.getX() != null) {
            existingEntity.setX(dto.getX());
        }
        if (dto.getY() != null) {
            existingEntity.setY(dto.getY());
        }
        // tabulatedFunctionId не обновляется
        return existingEntity;
    }
}