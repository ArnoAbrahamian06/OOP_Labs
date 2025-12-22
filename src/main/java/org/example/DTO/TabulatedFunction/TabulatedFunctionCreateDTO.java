package org.example.DTO.TabulatedFunction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.DTO.Point.PointDTO;
import java.util.List;

// Для создания (внутреннее DTO)
public class TabulatedFunctionCreateDTO {

    @NotBlank(message = "Имя функции не может быть пустым")
    @Size(min = 1, max = 100, message = "Имя функции должно быть от 1 до 100 символов")
    private String name;

    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;

    private List<PointDTO> points;

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<PointDTO> getPoints() {
        return points;
    }

    public void setPoints(List<PointDTO> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "TabulatedFunctionCreateDTO{" +
                "name='" + name + '\'' +
                ", userId=" + userId +
                ", points=" + points +
                '}';
    }
}