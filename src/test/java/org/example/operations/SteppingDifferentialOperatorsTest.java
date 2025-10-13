package org.example.operations;

import org.example.functions.MathFunction;
import org.example.functions.SqrFunction;

import org.junit.Test;
import static org.junit.Assert.*;

public class SteppingDifferentialOperatorsTest {

    // Тесты для LeftSteppingDifferentialOperator
    @Test
    public void testLeftSteppingDifferentialOperator() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.001);
        SqrFunction sqrFunction = new SqrFunction();

        MathFunction derivative = operator.derive(sqrFunction);

        // Для f(x) = x² производная f'(x) = 2x
        // Левая разностная производная: (x² - (x-step)²)/step = (x² - (x² - 2x*step + step²))/step = 2x - step
        assertEquals(0.0, derivative.apply(0.0), 1e-3);   // f'(0) = 0
        assertEquals(1.999, derivative.apply(1.0), 1e-3); // f'(1) ≈ 2 - step = 2 - 0.001 = 1.999
        assertEquals(3.999, derivative.apply(2.0), 1e-3); // f'(2) ≈ 4 - step = 4 - 0.001 = 3.999
    }

    @Test
    public void testLeftSteppingWithLinearFunction() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.001);
        MathFunction linear = x -> 3 * x + 2; // f(x) = 3x + 2

        MathFunction derivative = operator.derive(linear);

        // Для линейной функции производная постоянна и равна 3
        assertEquals(3.0, derivative.apply(0.0), 1e-5);
        assertEquals(3.0, derivative.apply(1.0), 1e-5);
        assertEquals(3.0, derivative.apply(10.0), 1e-5);
    }

    // Тесты для RightSteppingDifferentialOperator
    @Test
    public void testRightSteppingDifferentialOperator() {
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(0.001);
        SqrFunction sqrFunction = new SqrFunction();

        MathFunction derivative = operator.derive(sqrFunction);

        // Для f(x) = x² производная f'(x) = 2x
        // Правая разностная производная: ((x+step)² - x²)/step = (x² + 2x*step + step² - x²)/step = 2x + step
        assertEquals(0.001, derivative.apply(0.0), 1e-3); // f'(0) ≈ 0 + step = 0.001
        assertEquals(2.001, derivative.apply(1.0), 1e-3); // f'(1) ≈ 2 + step = 2.001
        assertEquals(4.001, derivative.apply(2.0), 1e-3); // f'(2) ≈ 4 + step = 4.001
    }

    @Test
    public void testRightSteppingWithConstantFunction() {
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(0.001);
        MathFunction constant = x -> 5.0; // f(x) = 5

        MathFunction derivative = operator.derive(constant);

        // Производная константы должна быть 0
        assertEquals(0.0, derivative.apply(0.0), 1e-5);
        assertEquals(0.0, derivative.apply(1.0), 1e-5);
        assertEquals(0.0, derivative.apply(-5.0), 1e-5);
    }

    // Тесты для MiddleSteppingDifferentialOperator
    @Test
    public void testMiddleSteppingDifferentialOperator() {
        MiddleSteppingDifferentialOperator operator = new MiddleSteppingDifferentialOperator(0.001);
        SqrFunction sqrFunction = new SqrFunction();

        MathFunction derivative = operator.derive(sqrFunction);

        // Для f(x) = x² производная f'(x) = 2x
        // Средняя разностная производная: ((x+step)² - (x-step)²)/(2*step) = (4x*step)/(2*step) = 2x
        assertEquals(0.0, derivative.apply(0.0), 1e-5); // f'(0) = 0
        assertEquals(2.0, derivative.apply(1.0), 1e-5); // f'(1) = 2
        assertEquals(4.0, derivative.apply(2.0), 1e-5); // f'(2) = 4
    }

    @Test
    public void testMiddleSteppingWithCubicFunction() {
        MiddleSteppingDifferentialOperator operator = new MiddleSteppingDifferentialOperator(0.001);
        MathFunction cubic = x -> x * x * x; // f(x) = x³

        MathFunction derivative = operator.derive(cubic);

        // Производная f'(x) = 3x²
        assertEquals(0.0, derivative.apply(0.0), 1e-3);   // f'(0) = 0
        assertEquals(3.0, derivative.apply(1.0), 1e-3);   // f'(1) = 3
        assertEquals(12.0, derivative.apply(2.0), 1e-3);  // f'(2) = 12
    }

    // Общие тесты для всех операторов
    @Test
    public void testAllOperatorsWithSameStep() {
        double step = 0.001;
        SqrFunction sqrFunction = new SqrFunction();
        double x = 2.0;

        // Аналитические ожидания для каждого метода
        double expectedAnalytic = 4.0;           // Точная производная: 2x
        double expectedLeft = 4.0 - step;        // Левая разность: 2x - step
        double expectedRight = 4.0 + step;       // Правая разность: 2x + step
        double expectedMiddle = 4.0;             // Средняя разность: 2x

        LeftSteppingDifferentialOperator left = new LeftSteppingDifferentialOperator(step);
        RightSteppingDifferentialOperator right = new RightSteppingDifferentialOperator(step);
        MiddleSteppingDifferentialOperator middle = new MiddleSteppingDifferentialOperator(step);

        double leftResult = left.derive(sqrFunction).apply(x);
        double rightResult = right.derive(sqrFunction).apply(x);
        double middleResult = middle.derive(sqrFunction).apply(x);

        // Проверяем каждый метод с правильными ожиданиями
        assertEquals(expectedLeft, leftResult, 1e-12);
        assertEquals(expectedRight, rightResult, 1e-12);
        assertEquals(expectedMiddle, middleResult, 1e-12);

        // Дополнительная проверка: средняя разность должна быть самой точной
        assertEquals(expectedAnalytic, middleResult, 1e-12);
    }

    @Test
    public void testStepValidationInAllOperators() {
        // Проверяем, что все операторы используют одну и ту же валидацию
        double[] invalidSteps = {0.0, -1.0, Double.POSITIVE_INFINITY, Double.NaN};

        for (double invalidStep : invalidSteps) {
            try {
                new LeftSteppingDifferentialOperator(invalidStep);
                fail("LeftSteppingDifferentialOperator некорректный шаг: " + invalidStep);
            } catch (IllegalArgumentException e) {
                // Ожидаемое поведение
            }

            try {
                new RightSteppingDifferentialOperator(invalidStep);
                fail("RightSteppingDifferentialOperator некорректный шаг: " + invalidStep);
            } catch (IllegalArgumentException e) {
                // Ожидаемое поведение
            }

            try {
                new MiddleSteppingDifferentialOperator(invalidStep);
                fail("MiddleSteppingDifferentialOperator некорректный шаг: " + invalidStep);
            } catch (IllegalArgumentException e) {
                // Ожидаемое поведение
            }
        }
    }

    @Test
    public void testGettersAndSetters() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.01);
        assertEquals(0.01, operator.getStep(), 1e-10);

        operator.setStep(0.001);
        assertEquals(0.001, operator.getStep(), 1e-10);

        // Проверяем, что сеттер также валидирует значение
        try {
            operator.setStep(0.0);
            fail("setStep получает некорректное значение");
        } catch (IllegalArgumentException e) {
            assertEquals(0.001, operator.getStep(), 1e-10); // Шаг не должен измениться
        }
    }
}