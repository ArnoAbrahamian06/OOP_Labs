package org.example.DTO.OpenAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.DTO.Point.PointCreateDTO;
import java.util.List;

public class FunctionCreateRequest {

    @NotNull(message = "ID владельца не может быть пустым")
    @JsonProperty("ownerId")
    private Long ownerId;

    @NotBlank(message = "Имя функции не может быть пустым")
    @Size(min = 1, max = 100, message = "Имя функции должно быть от 1 до 100 символов")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Точки не могут быть пустыми")
    @Size(min = 2, message = "Должно быть минимум 2 точки")
    @JsonProperty("points")
    private List<PointCreateDTO> points;

    // Геттеры и сеттеры
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PointCreateDTO> getPoints() {
        return points;
    }

    public void setPoints(List<PointCreateDTO> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "FunctionCreateRequest{" +
                "ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", points=" + points +
                '}';
    }
}