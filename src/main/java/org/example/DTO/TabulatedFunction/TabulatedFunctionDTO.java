package org.example.DTO.TabulatedFunction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.DTO.Point.PointDTO;
import java.time.LocalDateTime;
import java.util.List;

// Основной (внутреннее DTO)
public class TabulatedFunctionDTO {

    private Long id;

    @NotBlank(message = "Имя функции не может быть пустым")
    private String name;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "Пользователь не может быть пустым")
    private Long userId; // ID пользователя

    private List<PointDTO> points; // Список точек

    // Конструкторы
    public TabulatedFunctionDTO() {}

    public TabulatedFunctionDTO(Long id, String name, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
        return "TabulatedFunctionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
}