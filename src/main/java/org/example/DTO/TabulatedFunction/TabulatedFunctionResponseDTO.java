package org.example.DTO.TabulatedFunction;

import org.example.DTO.FunctionType.FunctionTypeDTO;

import java.time.LocalDateTime;
import java.util.List;

// Для полного вывода
public class TabulatedFunctionResponseDTO {

    private Long id;
    private String serializedData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username; // имя пользователя для удобства
    private List<FunctionTypeDTO> functionTypes;

    // Конструкторы
    public TabulatedFunctionResponseDTO() {}

    public TabulatedFunctionResponseDTO(Long id, String serializedData, LocalDateTime createdAt,
                                        LocalDateTime updatedAt, Long userId, String username) {
        this.id = id;
        this.serializedData = serializedData;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<FunctionTypeDTO> getFunctionTypes() {
        return functionTypes;
    }

    public void setFunctionTypes(List<FunctionTypeDTO> functionTypes) {
        this.functionTypes = functionTypes;
    }

    @Override
    public String toString() {
        return "TabulatedFunctionResponseDTO{" +
                "id=" + id +
                ", serializedData='" + serializedData + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}