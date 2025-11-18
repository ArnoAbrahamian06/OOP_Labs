package org.example.repository;

import org.example.entity.Function_type;
import org.example.entity.Tabulated_function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FunctionTypeRepository extends JpaRepository<Function_type, Long> {

    // Поиск по имени
    @Query(value = "SELECT * FROM functions_types WHERE name = :name", nativeQuery = true)
    List<Function_type> findByName(@Param("name") String name);

    // Поиск по локализованному имени
    @Query(value = "SELECT * FROM functions_types WHERE loc_name = :locName", nativeQuery = true)
    List<Function_type> findByLocName(@Param("locName") String locName);

    // Поиск по приоритету
    @Query(value = "SELECT * FROM functions_types WHERE priority = :priority", nativeQuery = true)
    List<Function_type> findByPriority(@Param("priority") Integer priority);

    // Поиск по tabulated function
    @Query(value = "SELECT * FROM functions_types WHERE tabulated_function_id = :#{#tabulatedFunction.id}", nativeQuery = true)
    List<Function_type> findByTabulatedFunction(@Param("tabulatedFunction") Tabulated_function tabulatedFunction);

    // Поиск по ID tabulated function
    @Query(value = "SELECT * FROM functions_types WHERE tabulated_function_id = :tabulatedFunctionId", nativeQuery = true)
    List<Function_type> findByTabulatedFunctionId(@Param("tabulatedFunctionId") Long tabulatedFunctionId);

    // Поиск по имени (без учета регистра)
    @Query(value = "SELECT * FROM functions_types WHERE LOWER(name) = LOWER(:name)", nativeQuery = true)
    List<Function_type> findByNameIgnoreCase(@Param("name") String name);

    // Поиск по диапазону приоритетов
    @Query(value = "SELECT * FROM functions_types WHERE priority BETWEEN :minPriority AND :maxPriority", nativeQuery = true)
    List<Function_type> findByPriorityBetween(@Param("minPriority") Integer minPriority, @Param("maxPriority") Integer maxPriority);

    // Поиск по части имени
    @Query(value = "SELECT * FROM functions_types WHERE name LIKE %:name%", nativeQuery = true)
    List<Function_type> findByNameContaining(@Param("name") String name);

    // Подсчет типов функций по tabulated function
    @Query(value = "SELECT COUNT(*) FROM functions_types WHERE tabulated_function_id = :tfId", nativeQuery = true)
    Long countByTabulatedFunctionId(@Param("tfId") Long tabulatedFunctionId);
}