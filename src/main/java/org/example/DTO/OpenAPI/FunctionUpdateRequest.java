package org.example.DTO.OpenAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public class FunctionUpdateRequest {

    @Max(value = 100, message = "Имя функции не должно превышать 100 символов")
    @JsonProperty("name")
    private String name;

    @JsonProperty("serializedData")
    private String serializedData;

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerializedData() {
        return serializedData;
    }

    public void setSerializedData(String serializedData) {
        this.serializedData = serializedData;
    }

    @Override
    public String toString() {
        return "FunctionUpdateRequest{" +
                "name='" + name + '\'' +
                ", serializedData='" + serializedData + '\'' +
                '}';
    }
}