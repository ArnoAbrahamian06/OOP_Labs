package org.example.DTO.FunctionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

// Основной
public class FunctionTypeDTO {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Локализованное имя не может быть пустым")
    private String locName;

    @NotNull(message = "Приоритет не может быть пустым")
    @Positive(message = "Приоритет должен быть положительным числом")
    private Integer priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "ID табулированной функции не может быть пустым")
    private Long tabulatedFunctionId;

    // Конструкторы
    public FunctionTypeDTO() {}

    public FunctionTypeDTO(Long id, String name, String locName, Integer priority, Long tabulatedFunctionId) {
        this.id = id;
        this.name = name;
        this.locName = locName;
        this.priority = priority;
        this.tabulatedFunctionId = tabulatedFunctionId;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getTabulatedFunctionId() {
        return tabulatedFunctionId;
    }

    public void setTabulatedFunctionId(Long tabulatedFunctionId) {
        this.tabulatedFunctionId = tabulatedFunctionId;
    }

    @Override
    public String toString() {
        return "FunctionTypeDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", locName='" + locName + '\'' +
                ", priority=" + priority +
                ", tabulatedFunctionId=" + tabulatedFunctionId +
                '}';
    }
}