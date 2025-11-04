package org.example.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewtonFunction implements MathFunction {
    private static final Logger log = LoggerFactory.getLogger(NewtonFunction.class);
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
                if (Math.abs(fx) < tolerance) {
                    log.debug("Метод Ньютона сошёлся при почти нулевой производной в x={} (|f(x)|<tol)", current);
                    return current; // Мы уже в корне
                }
                log.warn("Производная нулевая при x={}, f(x)={}, продолжение невозможно", current, fx);
                return 0;
            }
            double next = current - fx / fpx;

            if (Math.abs(next - current) < tolerance) {
                log.debug("Метод Ньютона сошёлся за {} итераций: корень ≈ {} (старт={})", i + 1, next, initialGuess);
                return next; // Возвращаем найденный корень
            }
            current = next;
        }
        log.warn("Метод Ньютона не сошёлся за {} итераций, последнее x={} (старт={})", maxIterations, current, initialGuess);
        return 0;
    }
}