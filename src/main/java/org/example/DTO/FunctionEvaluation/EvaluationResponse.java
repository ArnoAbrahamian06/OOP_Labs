package org.example.DTO.FunctionEvaluation;

// Ответ с результатом оценки функции
public class EvaluationResponse {

    private double x;
    private double y;

    // Конструкторы
    public EvaluationResponse() {}

    public EvaluationResponse(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Геттеры и Сеттеры
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
}