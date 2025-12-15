package org.example.DTO.TabulatedFunction;

import org.example.DTO.Point.PointDTO;
import java.time.LocalDateTime;
import java.util.List;

// Для полного вывода
public class TabulatedFunctionResponseDTO {

    private Long id;
    private String name; // Только name
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username; // имя пользователя для удобства
    private List<PointDTO> points; // Список точек

    // Конструкторы
    public TabulatedFunctionResponseDTO() {}

    public TabulatedFunctionResponseDTO(Long id, String name, LocalDateTime createdAt,
                                        LocalDateTime updatedAt, Long userId, String username) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<PointDTO> getPoints() {
        return points;
    }

    public void setPoints(List<PointDTO> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "TabulatedFunctionResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}