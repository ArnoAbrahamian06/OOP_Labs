package org.example.DTO.TabulatedFunction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Для Создания
public class TabulatedFunctionCreateDTO {

    @NotBlank(message = "Данные функции не могут быть пустыми")
    private String serializedData;

    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;

    // Геттеры и сеттеры
    public String getSerializedData() {
        return serializedData;
    }

    public void setSerializedData(String serializedData) {
        this.serializedData = serializedData;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TabulatedFunctionCreateDTO{" +
                "serializedData='" + serializedData + '\'' +
                ", userId=" + userId +
                '}';
    }
}