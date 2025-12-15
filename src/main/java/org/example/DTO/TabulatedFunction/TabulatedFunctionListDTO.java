package org.example.DTO.TabulatedFunction;

import java.time.LocalDateTime;

// Для частичного вывода
public class TabulatedFunctionListDTO {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private Long userId;
    private String username;
    private int pointsCount; // количество точек функции

    // Конструкторы
    public TabulatedFunctionListDTO() {}

    public TabulatedFunctionListDTO(Long id, String name, LocalDateTime createdAt,
                                    Long userId, String username, int pointsCount) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.userId = userId;
        this.username = username;
        this.pointsCount = pointsCount;
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

    public int getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(int pointsCount) {
        this.pointsCount = pointsCount;
    }

    @Override
    public String toString() {
        return "TabulatedFunctionListDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", pointsCount=" + pointsCount +
                '}';
    }
}