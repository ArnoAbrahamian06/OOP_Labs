package org.example.DTO.TabulatedFunction;

import jakarta.validation.constraints.NotBlank;

// Для обнавления
public class TabulatedFunctionUpdateDTO {

    @NotBlank(message = "Данные функции не могут быть пустыми")
    private String serializedData;

    // Геттеры и сеттеры
    public String getSerializedData() {
        return serializedData;
    }

    public void setSerializedData(String serializedData) {
        this.serializedData = serializedData;
    }

    @Override
    public String toString() {
        return "TabulatedFunctionUpdateDTO{" +
                "serializedData='" + serializedData + '\'' +
                '}';
    }
}