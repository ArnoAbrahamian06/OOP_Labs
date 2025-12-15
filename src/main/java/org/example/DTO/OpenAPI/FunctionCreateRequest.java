package org.example.DTO.OpenAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FunctionCreateRequest {

    @NotNull(message = "ID владельца не может быть пустым")
    @JsonProperty("ownerId")
    private Long ownerId;

    @NotNull(message = "ID типа функции не может быть пустым")
    @Min(value = 1, message = "ID типа функции должен быть положительным")
    @JsonProperty("typeId")
    private Integer typeId;

    @NotBlank(message = "Имя функции не может быть пустым")
    @Max(value = 100, message = "Имя функции не должно превышать 100 символов")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "Сериализованные данные не могут быть пустыми")
    @JsonProperty("serializedData")
    private String serializedData;

    // Геттеры и сеттеры
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

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
        return "FunctionCreateRequest{" +
                "ownerId=" + ownerId +
                ", typeId=" + typeId +
                ", name='" + name + '\'' +
                ", serializedData='" + serializedData + '\'' +
                '}';
    }
}