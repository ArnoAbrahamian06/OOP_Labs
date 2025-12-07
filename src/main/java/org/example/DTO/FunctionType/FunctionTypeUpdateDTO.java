package org.example.DTO.FunctionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Для обнавления
public class FunctionTypeUpdateDTO {

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Локализованное имя не может быть пустым")
    private String locName;

    @NotNull(message = "Приоритет не может быть пустым")
    @Positive(message = "Приоритет должен быть положительным числом")
    private Integer priority;

    // Геттеры и сеттеры
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

    @Override
    public String toString() {
        return "FunctionTypeUpdateDTO{" +
                "name='" + name + '\'' +
                ", locName='" + locName + '\'' +
                ", priority=" + priority +
                '}';
    }
}