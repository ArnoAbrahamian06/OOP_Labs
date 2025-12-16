package org.example.Mapper;

import org.example.DTO.TabulatedFunction.*;
import org.example.DTO.Point.PointDTO;
import org.example.DTO.Point.PointUpdateDTO;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.entity.PointEntity;
import org.example.functions.TabulatedFunction;
import org.example.functions.factory.TabulatedFunctionFactory;
import org.example.repository.PointRepository;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TabulatedFunctionMapper {

    // Внедряем PointMapper (предполагаем, что он будет создан)
    private final PointMapper pointMapper;

    private final TabulatedFunctionFactory externalFunctionFactory;

    // КОНСТРУКТОР с внедрением зависимостей
    public TabulatedFunctionMapper(PointMapper pointMapper,
                                   TabulatedFunctionFactory externalFunctionFactory) {
        this.pointMapper = pointMapper;
        this.externalFunctionFactory = externalFunctionFactory; // Присваиваем внедрённый бин
    }

    // Преобразование TabulatedFunctionCreateDTO -> Tabulated_function
    public Tabulated_function toEntity(TabulatedFunctionCreateDTO dto, User user) {
        if (dto == null || user == null) {
            return null;
        }

        Tabulated_function entity = new Tabulated_function();
        entity.setName(dto.getName());
        entity.setUser(user);

        if (dto.getPoints() != null) {
            for (PointDTO pointDTO : dto.getPoints()) {
                // Используем toEntityFromDTO, который преобразует PointDTO в Point
                PointEntity point = pointMapper.toEntityFromDTO(pointDTO);
                // Устанавливаем связь с Tabulated_function вручную
                point.setTabulatedFunction(entity);
                // Используем вспомогательный метод для корректного добавления
                entity.addPoint(point);
            }
        }
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
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        // Преобразование списка Point в PointDTO
        if (entity.getPoints() != null) {
            dto.setPoints(entity.getPoints().stream()
                    .map(pointMapper::toDTO)
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
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }
        dto.setPointsCount(entity.getPoints() != null ? entity.getPoints().size() : 0);
        return dto;
    }

    // Преобразование Tabulated_function -> TabulatedFunctionResponseDTO
    public TabulatedFunctionResponseDTO toResponseDTO(Tabulated_function entity) {
        if (entity == null) {
            return null;
        }

        TabulatedFunctionResponseDTO dto = new TabulatedFunctionResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
        }
        // Преобразование списка Point в PointResponseDTO
        if (entity.getPoints() != null) {
            dto.setPoints(entity.getPoints().stream()
                    .map(pointMapper::toDTO)
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
        entity.setName(dto.getName());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }


    // Преобразование TabulatedFunctionUpdateDTO -> Tabulated_function (частичное обновление)
    public Tabulated_function partialUpdateFromDTO(TabulatedFunctionUpdateDTO dto, Tabulated_function existingEntity) {
        if (dto == null || existingEntity == null) {
            return existingEntity;
        }

        if (dto.getName() != null) {
            existingEntity.setName(dto.getName());
        }

        // Обновление points, если переданы
        if (dto.getPoints() != null) {
            // 1. Получаем текущие точки из сущности
            List<PointEntity> currentPoints = existingEntity.getPoints();
            // 2. Получаем DTO точек, которые должны быть в сущности
            List<PointDTO> requestedPointsDTO = dto.getPoints();

            // 3. Создаем множества ID для эффективного поиска
            Set<Long> currentPointIds = currentPoints.stream()
                    .map(PointEntity::getId)
                    .filter(id -> id != null) // Игнорируем точки без ID (новые)
                    .collect(Collectors.toSet());

            Set<Long> requestedPointIds = requestedPointsDTO.stream()
                    .map(PointDTO::getId)
                    .filter(id -> id != null) // Игнорируем DTO новых точек без ID
                    .collect(Collectors.toSet());

            // 4. Определяем точки для удаления (ID есть в current, но нет в requested)
            List<PointEntity> pointsToDelete = currentPoints.stream()
                    .filter(point -> point.getId() != null && !requestedPointIds.contains(point.getId()))
                    .collect(Collectors.toList());

            // 5. Определяем DTO новых точек (ID есть в requested, но нет в current)
            List<PointDTO> newPointDTOs = requestedPointsDTO.stream()
                    .filter(dtoP -> dtoP.getId() == null) // Новые точки
                    .collect(Collectors.toList());

            // 6. Определяем DTO точек для обновления (ID есть и там, и там)
            List<PointDTO> updatedPointDTOs = requestedPointsDTO.stream()
                    .filter(dtoP -> dtoP.getId() != null && currentPointIds.contains(dtoP.getId()))
                    .collect(Collectors.toList());

            // 7. Применяем изменения
            // Удаляем точки
            for (PointEntity point : pointsToDelete) {
                existingEntity.removePoint(point); // Используем вспомогательный метод
            }

            // Добавляем новые точки
            for (PointDTO newPointDTO : newPointDTOs) {
                // Преобразуем PointDTO в Point.
                PointEntity newPoint = pointMapper.toEntityFromDTO(newPointDTO); // Используем toEntityFromDTO
                newPoint.setTabulatedFunction(existingEntity); // Устанавливаем связь
                existingEntity.addPoint(newPoint); // Используем вспомогательный метод
            }

            // Обновляем существующие точки
            for (PointDTO updatedPointDTO : updatedPointDTOs) {
                PointEntity pointToUpdate = currentPoints.stream()
                        .filter(p -> p.getId().equals(updatedPointDTO.getId()))
                        .findFirst()
                        .orElse(null);
                if (pointToUpdate != null) {
                    // Преобразуем PointDTO в PointUpdateDTO для обновления
                    PointUpdateDTO pointUpdateDTO = new PointUpdateDTO();
                    pointUpdateDTO.setX(updatedPointDTO.getX());
                    pointUpdateDTO.setY(updatedPointDTO.getY());
                    // Не устанавливаем tabulatedFunctionId

                    pointMapper.partialUpdateFromDTO(pointUpdateDTO, pointToUpdate); // Обновляем поля точки
                }
            }
        }

        // updatedAt обновляется в @PreUpdate
        return existingEntity;
    }

    /**
     * Преобразует Spring-сущность Tabulated_function в внешний интерфейс org.example.functions.TabulatedFunction.
     * Использует внедрённую TabulatedFunctionFactory для создания *строго-неизменяемой* версии экземпляра.
     *
     * @param springFunc Spring-сущность Tabulated_function.
     * @return Внешний интерфейс org.example.functions.TabulatedFunction (строго-неизменяемая версия) или null, если преобразование невозможно.
     */
    public TabulatedFunction toExternalTabulatedFunction(org.example.entity.Tabulated_function springFunc) {
        if (springFunc == null) {
            return null;
        }

        List<org.example.entity.PointEntity> springPoints = springFunc.getPoints();
        if (springPoints == null || springPoints.isEmpty()) {
            return null;
        }

        int size = springPoints.size();
        double[] xValues = new double[size];
        double[] yValues = new double[size];

        for (int i = 0; i < size; i++) {
            org.example.entity.PointEntity springPoint = springPoints.get(i);
            xValues[i] = springPoint.getX();
            yValues[i] = springPoint.getY();
        }

        // ИСПОЛЬЗУЕМ: createStrictUnmodifiable
        TabulatedFunction externalFunc = externalFunctionFactory.createStrictUnmodifiable(xValues, yValues);
        return externalFunc;
    }

    /**
     * Преобразует внешний интерфейс org.example.functions.TabulatedFunction в Spring-сущность Tabulated_function.
     * Результат *не* содержит связь с User. Её нужно установить отдельно.
     *
     * @param externalFunc Внешний интерфейс org.example.functions.TabulatedFunction.
     * @return Spring-сущность org.example.entity.Tabulated_function или null, если преобразование невозможно.
     */
    public org.example.entity.Tabulated_function toSpringTabulatedFunction(org.example.functions.TabulatedFunction externalFunc) {
        if (externalFunc == null) {
            return null;
        }

        int count = externalFunc.getCount();
        if (count <= 0) {
            return null;
        }

        org.example.entity.Tabulated_function springFunc = new org.example.entity.Tabulated_function();
        springFunc.setName("Result_" + System.currentTimeMillis());

        for (int i = 0; i < count; i++) {
            double x = externalFunc.getX(i);
            double y = externalFunc.getY(i);

            org.example.entity.PointEntity springPoint = new org.example.entity.PointEntity();
            springPoint.setX(x);
            springPoint.setY(y);
            springFunc.addPoint(springPoint);
        }

        return springFunc;
    }
}