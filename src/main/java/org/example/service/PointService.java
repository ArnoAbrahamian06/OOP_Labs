package org.example.service;

import org.example.DTO.Point.PointBatchUpdateItemDTO;
import org.example.DTO.Point.PointCreateDTO;
import org.example.DTO.Point.PointUpdateDTO;
import org.example.entity.PointEntity;
import java.util.List;
import java.util.Optional;

public interface PointService {
    List<PointEntity> findAll();
    Optional<PointEntity> findById(Long id);
    PointEntity save(PointEntity point);
    void deleteById(Long id);

    /**
     * Массовое обновление точек.
     * @param updates список DTO с новыми значениями X и Y для обновления.
     */
    void batchUpdatePoints(List<PointBatchUpdateItemDTO> updates);

    /**
     * Массовое создание точек.
     * @param creates список DTO с координатами X и Y и ID функции.
     */
    void batchCreatePoints(List<PointCreateDTO> creates);

    List<PointEntity> saveAll(List<PointEntity> points);
    void deleteByTabulatedFunctionId(Long tabulatedFunctionId);

    // --- Дополнительные методы ---
    // Найти все точки для конкретной табулированной функции (по ID функции)
    List<PointEntity> findByTabulatedFunctionId(Long tabulatedFunctionId);

    // Найти все точки с определённым значением X
    List<PointEntity> findByX(Double x);

    // Найти все точки с определённым значением Y
    List<PointEntity> findByY(Double y);

    // Найти точки с X в заданном диапазоне
    List<PointEntity> findByXBetween(Double minX, Double maxX);

    // Найти точки с Y в заданном диапазоне
    List<PointEntity> findByYBetween(Double minY, Double maxY);

    // Найти точки с X > определённого значения для конкретной функции
    List<PointEntity> findByTabulatedFunctionIdAndXGreaterThan(Long tabulatedFunctionId, Double xThreshold);

    // Найти точки с Y < определённого значения для конкретной функции
    List<PointEntity> findByTabulatedFunctionIdAndYLessThan(Long tabulatedFunctionId, Double yThreshold);

    // Найти точки с X в диапазоне и Y в диапазоне
    List<PointEntity> findByXBetweenAndYBetween(Double minX, Double maxX, Double minY, Double maxY);

    // Найти точки, где X или Y равен определённому значению
    List<PointEntity> findByXOrY(Double value);

    // Найти точки для функции с ID, отсортированные по X
    List<PointEntity> findByTabulatedFunctionIdOrderByXAsc(Long tabulatedFunctionId);

    // Найти точки для функции с ID, отсортированные по Y
    List<PointEntity> findByTabulatedFunctionIdOrderByYAsc(Long tabulatedFunctionId);

    // Найти количество точек для конкретной функции
    Long countByTabulatedFunctionId(Long tabulatedFunctionId);
}