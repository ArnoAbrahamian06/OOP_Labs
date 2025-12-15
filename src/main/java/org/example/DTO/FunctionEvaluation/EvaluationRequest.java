package org.example.DTO.FunctionEvaluation;

import jakarta.validation.constraints.NotNull;

// Запрос для оценки функции
public class EvaluationRequest {

    @NotNull(message = "Значение X для оценки не может быть null")
    private Double xValue;

    // Геттеры и Сеттеры
    public Double getXValue() {
        return xValue;
    }

    public void setXValue(Double xValue) {
        this.xValue = xValue;
    }
}