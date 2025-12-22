package org.example.service.Implementation;

import org.example.DTO.Point.PointCreateDTO;
import org.example.entity.PointEntity;
import org.example.entity.Tabulated_function;
import org.example.repository.PointRepository;
import org.example.service.PointService;
import org.example.service.TabulatedFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.DTO.Point.PointBatchUpdateItemDTO;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PointServiceImpl implements PointService {

    private static final Logger log = LoggerFactory.getLogger(PointServiceImpl.class);

    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;


    @Override
    public List<PointEntity> findAll() {
        log.info("Fetching all points from the database.");
        return pointRepository.findAll();
    }

    @Override
    public Optional<PointEntity> findById(Long id) {
        log.debug("Attempting to find point with ID: {}", id);
        Optional<PointEntity> point = pointRepository.findById(id);
        point.ifPresentOrElse(
                p -> log.info("Found point with ID: {}", id),
                () -> log.warn("Point with ID {} not found.", id)
        );
        return point;
    }

    @Override
    public PointEntity save(PointEntity point) {
        log.info("Saving point: x={}, y={}, functionId={}",
                point.getX(), point.getY(),
                point.getTabulatedFunction() != null ? point.getTabulatedFunction().getId() : "null");
        PointEntity savedPoint = pointRepository.save(point);
        log.debug("Point saved successfully with ID: {}", savedPoint.getId());
        return savedPoint;
    }

    @Override
    public void deleteById(Long id) {
        log.warn("Deleting point with ID: {}", id);
        pointRepository.deleteById(id);
        log.info("Point with ID {} deleted successfully.", id);
    }

    // --- РЕАЛИЗАЦИЯ ДОПОЛНИТЕЛЬНЫХ МЕТОДОВ ---
    @Override
    public List<PointEntity> findByTabulatedFunctionId(Long tabulatedFunctionId) {
        log.debug("Fetching points for TabulatedFunction ID: {}", tabulatedFunctionId);
        return pointRepository.findByTabulatedFunctionId(tabulatedFunctionId);
    }

    @Override
    public List<PointEntity> findByX(Double x) {
        log.debug("Fetching points with X value: {}", x);
        return pointRepository.findByX(x);
    }

    @Override
    public List<PointEntity> findByY(Double y) {
        log.debug("Fetching points with Y value: {}", y);
        return pointRepository.findByY(y);
    }

    @Override
    public List<PointEntity> findByXBetween(Double minX, Double maxX) {
        log.debug("Fetching points with X between {} and {}", minX, maxX);
        return pointRepository.findByXBetween(minX, maxX);
    }

    @Override
    public List<PointEntity> findByYBetween(Double minY, Double maxY) {
        log.debug("Fetching points with Y between {} and {}", minY, maxY);
        return pointRepository.findByYBetween(minY, maxY);
    }

    @Override
    public List<PointEntity> findByTabulatedFunctionIdAndXGreaterThan(Long tabulatedFunctionId, Double xThreshold) {
        log.debug("Fetching points for function ID {} with X greater than {}", tabulatedFunctionId, xThreshold);
        return pointRepository.findByTabulatedFunctionIdAndXGreaterThan(tabulatedFunctionId, xThreshold);
    }

    @Override
    public List<PointEntity> findByTabulatedFunctionIdAndYLessThan(Long tabulatedFunctionId, Double yThreshold) {
        log.debug("Fetching points for function ID {} with Y less than {}", tabulatedFunctionId, yThreshold);
        return pointRepository.findByTabulatedFunctionIdAndYLessThan(tabulatedFunctionId, yThreshold);
    }

    @Override
    public List<PointEntity> findByXBetweenAndYBetween(Double minX, Double maxX, Double minY, Double maxY) {
        log.debug("Fetching points in rectangle X:[{} - {}], Y:[{} - {}]", minX, maxX, minY, maxY);
        return pointRepository.findByXBetweenAndYBetween(minX, maxX, minY, maxY);
    }

    @Override
    public List<PointEntity> findByXOrY(Double value) {
        log.debug("Fetching points where X or Y equals {}", value);
        return pointRepository.findByXOrY(value);
    }

    @Override
    public List<PointEntity> findByTabulatedFunctionIdOrderByXAsc(Long tabulatedFunctionId) {
        log.debug("Fetching points for function ID {} ordered by X ascending", tabulatedFunctionId);
        return pointRepository.findByTabulatedFunctionIdOrderByXAsc(tabulatedFunctionId);
    }

    @Override
    public List<PointEntity> findByTabulatedFunctionIdOrderByYAsc(Long tabulatedFunctionId) {
        log.debug("Fetching points for function ID {} ordered by Y ascending", tabulatedFunctionId);
        return pointRepository.findByTabulatedFunctionIdOrderByYAsc(tabulatedFunctionId);
    }

    @Override
    public Long countByTabulatedFunctionId(Long tabulatedFunctionId) {
        log.info("Counting points for TabulatedFunction ID: {}", tabulatedFunctionId);
        Long count = pointRepository.countByTabulatedFunctionId(tabulatedFunctionId);
        log.debug("Found {} points for function ID {}", count, tabulatedFunctionId);
        return count;
    }

    @Override
    @Transactional
    public List<PointEntity> saveAll(List<PointEntity> points) {
        log.info("saving points for all points: {}", points);
        return pointRepository.saveAll(points);
    }

    @Override
    @Transactional
    public void deleteByTabulatedFunctionId(Long tabulatedFunctionId) {
        log.info("Deleting points for TabulatedFunction ID: {}", tabulatedFunctionId);
        pointRepository.deleteByTabulatedFunctionId(tabulatedFunctionId);
    }

    @Override
    @Transactional
    public void batchUpdatePoints(List<PointBatchUpdateItemDTO> updates) {
        log.info("Начало массового обновления {} точек.", updates.size());

        if (updates.isEmpty()) {
            log.warn("Список обновлений пуст.");
            return;
        }

        List<Long> ids = updates.stream().map(PointBatchUpdateItemDTO::getId).collect(Collectors.toList());

        // Загружаем только те точки, ID которых есть в списке
        List<PointEntity> entitiesToUpdate = pointRepository.findAllById(ids);

        if (entitiesToUpdate.size() != ids.size()) {
            log.error("Не все точки найдены по предоставленным ID. Запрошено: {}, Найдено: {}", ids.size(), entitiesToUpdate.size());
            throw new RuntimeException("Некоторые точки не найдены для обновления.");
        }

        // Создаем мапу ID -> Entity для быстрого доступа
        Map<Long, PointEntity> entityMap = entitiesToUpdate.stream()
                .collect(Collectors.toMap(PointEntity::getId, e -> e));

        // Применяем обновления
        for (PointBatchUpdateItemDTO updateDto : updates) {
            PointEntity entity = entityMap.get(updateDto.getId());
            if (entity != null) {
                entity.setX(updateDto.getX());
                entity.setY(updateDto.getY());
                log.debug("Обновлена точка с ID {}: x={}, y={}", entity.getId(), entity.getX(), entity.getY());
            }
        }

        // Сохраняем все обновленные сущности
        pointRepository.saveAll(entitiesToUpdate);
        log.info("Массовое обновление {} точек завершено успешно.", entitiesToUpdate.size());
    }

    @Override
    @Transactional
    public void batchCreatePoints(List<PointCreateDTO> creates) {
        log.info("Начало массового создания {} точек.", creates.size());

        if (creates.isEmpty()) {
            log.warn("Список создаваемых точек пуст.");
            return;
        }

        // Сгруппируем DTO по ID функции, чтобы минимизировать обращения к БД
        Map<Long, List<PointCreateDTO>> groupedByFunctionId = creates.stream()
                .collect(Collectors.groupingBy(PointCreateDTO::getTabulatedFunctionId));

        List<PointEntity> entitiesToSave = new ArrayList<>();

        for (Map.Entry<Long, List<PointCreateDTO>> entry : groupedByFunctionId.entrySet()) {
            Long functionId = entry.getKey();

            // Проверим, существует ли функция
            Tabulated_function tabulatedFunction = tabulatedFunctionService.findById(functionId)
                    .orElseThrow(() -> new RuntimeException("Функция с ID " + functionId + " не найдена."));

            for (PointCreateDTO createDto : entry.getValue()) {
                PointEntity point = toEntityFromCreateDTO(createDto, tabulatedFunction);
                entitiesToSave.add(point);
                log.debug("Подготовлена точка к созданию: x={}, y={}, functionId={}", point.getX(), point.getY(), functionId);
            }
        }

        // Сохраняем все точки за один раз
        pointRepository.saveAll(entitiesToSave);
        log.info("Массовое создание {} точек завершено успешно.", entitiesToSave.size());
    }

    // Вспомогательный метод (можно вынести в Mapper, если используется часто)
    private PointEntity toEntityFromCreateDTO(PointCreateDTO dto, Tabulated_function tabulatedFunction) {
        if (dto == null || tabulatedFunction == null) {
            return null;
        }
        PointEntity entity = new PointEntity();
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        entity.setTabulatedFunction(tabulatedFunction);
        return entity;
    }
}