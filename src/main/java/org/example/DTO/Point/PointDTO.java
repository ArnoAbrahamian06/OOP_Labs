package org.example.DTO.Point;

import jakarta.validation.constraints.NotNull;

public class PointDTO {

    private Long id; // ID точки

    @NotNull(message = "Координата X не может быть пустой")
    private Double x;

    @NotNull(message = "Координата Y не может быть пустой")
    private Double y;

    @NotNull(message = "ID функции не может быть пустым")
    private Long tabulatedFunctionId; // ID функции, к которой привязана точка

    // Конструкторы
    public PointDTO() {}

    public PointDTO(Long id, Double x, Double y, Long tabulatedFunctionId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.tabulatedFunctionId = tabulatedFunctionId;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return "PointDTO{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", tabulatedFunctionId=" + tabulatedFunctionId +
                '}';
    }
}