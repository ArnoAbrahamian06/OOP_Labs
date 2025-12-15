package org.example.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "points", indexes = {
        @Index(name = "idx_point_function_id", columnList = "function_id"),
        @Index(name = "idx_point_x", columnList = "x"),
        @Index(name = "idx_point_y", columnList = "y")
})
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоинкремент
    private Long id;

    @Column(name = "x", nullable = false)
    private double x;

    @Column(name = "y", nullable = false)
    private double y;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tabulated_function tabulatedFunction;

    // Обязательные конструкторы
    public PointEntity() {}

    public PointEntity(double x, double y, Tabulated_function tabulatedFunction) {
        this.x = x;
        this.y = y;
        this.tabulatedFunction = tabulatedFunction;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Tabulated_function getTabulatedFunction() {
        return tabulatedFunction;
    }

    public void setTabulatedFunction(Tabulated_function tabulatedFunction) {
        this.tabulatedFunction = tabulatedFunction;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", tabulatedFunctionId=" + (tabulatedFunction != null ? tabulatedFunction.getId() : null) +
                '}';
    }
}