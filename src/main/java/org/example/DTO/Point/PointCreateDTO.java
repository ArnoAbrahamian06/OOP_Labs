package org.example.DTO.Point;

import jakarta.validation.constraints.NotNull;

public class PointCreateDTO {

    @NotNull(message = "Координата X не может быть пустой")
    private Double x;

    @NotNull(message = "Координата Y не может быть пустой")
    private Double y;

    @NotNull(message = "ID функции не может быть пустым")
    private Long tabulatedFunctionId; // ID функции, к которой привязывается точка

    // Геттеры и сеттеры
    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Long getTabulatedFunctionId() {
        return tabulatedFunctionId;
    }

    public void setTabulatedFunctionId(Long tabulatedFunctionId) {
        this.tabulatedFunctionId = tabulatedFunctionId;
    }

    @Override
    public String toString() {
        return "PointCreateDTO{" +
                "x=" + x +
                ", y=" + y +
                ", tabulatedFunctionId=" + tabulatedFunctionId +
                '}';
    }
}