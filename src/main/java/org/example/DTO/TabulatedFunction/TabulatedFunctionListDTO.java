package org.example.DTO.TabulatedFunction;

import java.time.LocalDateTime;

// Для частичного вывода
public class TabulatedFunctionListDTO {

    private Long id;
    private String serializedData;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private int functionTypesCount; // количество типов функций

    // Конструкторы
    public TabulatedFunctionListDTO() {}

    public TabulatedFunctionListDTO(Long id, String serializedData, LocalDateTime createdAt,
                                    Long userId, String username, int functionTypesCount) {
        this.id = id;
        this.serializedData = serializedData;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
        this.functionTypesCount = functionTypesCount;
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

    public int getFunctionTypesCount() {
        return functionTypesCount;
    }

    public void setFunctionTypesCount(int functionTypesCount) {
        this.functionTypesCount = functionTypesCount;
    }

    @Override
    public String toString() {
        return "TabulatedFunctionListDTO{" +
                "id=" + id +
                ", serializedData='" + serializedData + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", functionTypesCount=" + functionTypesCount +
                '}';
    }
}