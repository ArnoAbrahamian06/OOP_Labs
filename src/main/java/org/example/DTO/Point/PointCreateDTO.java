package org.example.DTO.Point;

import jakarta.validation.constraints.NotNull;

public class PointCreateDTO {

    @NotNull(message = "Координата X не может быть пустой")
    private Double x;

    @NotNull(message = "Координата Y не может быть пустой")
    private Double y;

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


    @Override
    public String toString() {
        return "PointCreateDTO{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}