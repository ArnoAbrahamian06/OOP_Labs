package org.example.repository;

import org.example.entity.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<PointEntity, Long> { // ID типа Long, если используется автоинкремент

    // --- Методы поиска по полям ---
    // Найти все точки для конкретной табулированной функции (по ID функции)
    List<PointEntity> findByTabulatedFunctionId(Long tabulatedFunctionId);

    // Удаление точек по TabulatedID
    void deleteByTabulatedFunctionId(Long tabulatedFunctionId);

    // Найти все точки с определённым значением X
    List<PointEntity> findByX(Double x);

    // Найти все точки с определённым значением Y
    List<PointEntity> findByY(Double y);

    // Найти точки с X в заданном диапазоне
    List<PointEntity> findByXBetween(Double minX, Double maxX);

    // Найти точки с Y в заданном диапазоне
    List<PointEntity> findByYBetween(Double minY, Double maxY);

    // --- Примеры сложных запросов ---
    // Найти точки с X > определённого значения для конкретной функции
    List<PointEntity> findByTabulatedFunctionIdAndXGreaterThan(Long tabulatedFunctionId, Double xThreshold);

    // Найти точки с Y < определённого значения для конкретной функции
    List<PointEntity> findByTabulatedFunctionIdAndYLessThan(Long tabulatedFunctionId, Double yThreshold);

    // Найти точки с X в диапазоне и Y в диапазоне
    List<PointEntity> findByXBetweenAndYBetween(Double minX, Double maxX, Double minY, Double maxY);

    // --- Нативные запросы (если нужны специфичные операции) ---
    // Пример: Найти точки, где X или Y равен определённому значению
    @Query(value = "SELECT * FROM points p WHERE p.x_value = :value OR p.y_value = :value", nativeQuery = true)
    List<PointEntity> findByXOrY(@Param("value") Double value);

    // Пример: Найти точки для функции с ID, отсортированные по X
    @Query(value = "SELECT * FROM points p WHERE p.f_id = :functionId ORDER BY p.x_value ASC", nativeQuery = true)
    List<PointEntity> findByTabulatedFunctionIdOrderByXAsc(@Param("functionId") Long functionId);

    // Пример: Найти точки для функции с ID, отсортированные по Y
    @Query(value = "SELECT * FROM points p WHERE p.f_id = :functionId ORDER BY p.y_value ASC", nativeQuery = true)
    List<PointEntity> findByTabulatedFunctionIdOrderByYAsc(@Param("functionId") Long functionId);

    // Пример: Найти количество точек для конкретной функции
    @Query(value = "SELECT COUNT(*) FROM points p WHERE p.f_id = :functionId", nativeQuery = true)
    Long countByTabulatedFunctionId(@Param("functionId") Long functionId);
}