package org.example.DTO.Point;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для создания точки")
public class PointCreateDTO {

    @Schema(description = "Координата X точки", example = "5.5", required = true)
    @NotNull(message = "Координата X не может быть пустой")
    private Double x;

    @Schema(description = "Координата Y точки", example = "-3.2", required = true)
    @NotNull(message = "Координата Y не может быть пустой")
    private Double y;

    @Schema(description = "ID табулированной функции, к которой привязывается точка", example = "1", required = true)
    @NotNull(message = "ID функции не может быть пустым")
    private Long tabulatedFunctionId;

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