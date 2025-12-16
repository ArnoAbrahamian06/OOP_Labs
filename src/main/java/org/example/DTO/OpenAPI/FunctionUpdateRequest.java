package org.example.DTO.OpenAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.DTO.Point.PointUpdateDTO;
import java.util.List;

public class FunctionUpdateRequest {

    @Max(value = 100, message = "Имя функции не должно превышать 100 символов")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Точки не могут быть пустыми")
    @Size(min = 2, message = "Должно быть минимум 2 точки")
    @JsonProperty("points")
    private List<PointUpdateDTO> points;

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PointUpdateDTO> getPoints() {
        return points;
    }

    public void setPoints(List<PointUpdateDTO> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "FunctionUpdateRequest{" +
                "name='" + name + '\'' +
                ", points=" + points +
                '}';
    }
}