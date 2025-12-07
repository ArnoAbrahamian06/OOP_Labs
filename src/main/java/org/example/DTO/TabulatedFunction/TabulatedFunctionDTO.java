package org.example.DTO.TabulatedFunction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.DTO.FunctionType.FunctionTypeDTO;

import java.time.LocalDateTime;
import java.util.List;

// Основной
public class TabulatedFunctionDTO {

    private Long id;

    @NotBlank(message = "Данные функции не могут быть пустыми")
    private String serializedData;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "Пользователь не может быть пустым")
    private Long userId; // ID пользователя

    private List<FunctionTypeDTO> functionTypes;

    // Конструкторы
    public TabulatedFunctionDTO() {}

    public TabulatedFunctionDTO(Long id, String serializedData, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.serializedData = serializedData;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerializedData() {
        return serializedData;
    }

    public void setSerializedData(String serializedData) {
        this.serializedData = serializedData;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<FunctionTypeDTO> getFunctionTypes() {
        return functionTypes;
    }

    public void setFunctionTypes(List<FunctionTypeDTO> functionTypes) {
        this.functionTypes = functionTypes;
    }

    @Override
    public String toString() {
        return "TabulatedFunctionDTO{" +
                "id=" + id +
                ", serializedData='" + serializedData + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}