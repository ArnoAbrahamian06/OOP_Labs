package org.example.DTO.Point;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO для обновления точки в массовой операции")
public class PointBatchUpdateItemDTO {

    @Schema(description = "ID точки", example = "1", required = true)
    @NotNull(message = "ID точки не может быть пустым")
    private Long id;

    @Schema(description = "Новое значение X", example = "5.5", required = true)
    @NotNull(message = "Координата X не может быть пустой")
    private Double x;

    @Schema(description = "Новое значение Y", example = "-3.2", required = true)
    @NotNull(message = "Координата Y не может быть пустой")
    private Double y;

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

    @Override
    public String toString() {
        return "PointBatchUpdateItemDTO{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}