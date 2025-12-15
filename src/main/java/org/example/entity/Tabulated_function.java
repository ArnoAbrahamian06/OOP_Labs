package org.example.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tabulated_functions", indexes = {
        @Index(name = "idx_tf_user_id", columnList = "user_id"),
        @Index(name = "idx_tf_created_at", columnList = "created_at")
})
public class Tabulated_function {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "tabulatedFunction", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE) // Удаление точек при удалении функции
    private List<PointEntity> points = new ArrayList<>();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Tabulated_function() {}

    public Tabulated_function(String name, User user) {
        this.name = name;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Геттеры
    public Long getId() { return id; }
    public User getUser() { return user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getName(){ return name; }
    public List<PointEntity> getPoints() { return points; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUser(User user) { this.user = user; }
    public void setName(String name) { this.name = name; }
    public void setPoints(List<PointEntity> points) { this.points = points; }

    // Вспомогательные методы для корректного управления связью с Point
    public void addPoint(PointEntity point) {
        points.add(point);
        point.setTabulatedFunction(this); // Устанавливаем обратную связь
    }

    public void removePoint(PointEntity point) {
        points.remove(point);
        point.setTabulatedFunction(null); // Убираем обратную связь
    }

    @Override
    public String toString() {
        return "Tabulated_function{" +
                "id=" + id +
                ", name='" + name +
                '}';
    }
}