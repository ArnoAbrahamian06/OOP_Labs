package org.example.functions;

public class NewtonFunction implements MathFunction {
    private MathFunction function;      // Функция f(x)
    private MathFunction derivative;    // Производная f'(x)
    private double tolerance;           // Допустимая погрешность
    private int maxIterations;          // Максимальное число итераций

    public NewtonFunction(MathFunction function, MathFunction derivative,
                          double tolerance, int maxIterations) {
        this.function = function;
        this.derivative = derivative;
        this.tolerance = tolerance;
        this.maxIterations = maxIterations;
    }

    @Override
    public double apply(double initialGuess) {
        double current = initialGuess;

        for (int i = 0; i < maxIterations; i++) {
            double fx = function.apply(current);
            double fpx = derivative.apply(current);

            // Проверка на нулевую производную с более точным условием
            if (Math.abs(fpx) < 1e-10) {
                // Если производная почти нулевая, пробуем немного сместить точку
                if (Math.abs(fx) < tolerance) {
                    return current; // Мы уже в корне
                }
            }

            double next = current - fx / fpx;

            if (Math.abs(next - current) < tolerance) {
                return next; // Возвращаем найденный корень
            }
            current = next;
        }
        return 0;
    }
}