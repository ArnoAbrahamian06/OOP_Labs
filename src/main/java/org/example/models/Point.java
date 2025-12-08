package org.example.models;

import java.time.LocalDateTime;

public class Point {
    private Integer id;
    private Integer functionId;
    private Double xValue;
    private Double yValue;

    // Конструкторы
    public Point() {
    }

    public Point(Integer functionId, Double xValue, Double yValue) {
        this.functionId = functionId;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    // Геттеры и сеттеры
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public Double getXValue() {
        return xValue;
    }

    public void setXValue(Double xValue) {
        this.xValue = xValue;
    }

    public Double getYValue() {
        return yValue;
    }

    public void setYValue(Double yValue) {
        this.yValue = yValue;
    }

    @Override
    public String toString() {
        return "PointDTO{" +
                "id=" + id +
                ", functionId=" + functionId +
                ", xValue=" + xValue +
                ", yValue=" + yValue +
                '}';
    }
}