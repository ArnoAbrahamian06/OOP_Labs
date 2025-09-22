package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class CompositeTabulatedFunctionTest {

    // Тестовые математические функции
    private static class SquareFunction implements MathFunction {
        @Override
        public double apply(double x) {
            return x * x;
        }
    }

    // 1. Тест композиции ArrayTabulatedFunction с ArrayTabulatedFunction
    @Test
    public void testArrayAndArrayComposition() {
        double[] x1 = {0, 1, 2, 3};
        double[] y1 = {0, 2, 4, 6};
        ArrayTabulatedFunction inner = new ArrayTabulatedFunction(x1, y1);

        double[] x2 = {0, 2, 4, 6};
        double[] y2 = {0, 1, 4, 9};
        ArrayTabulatedFunction outer = new ArrayTabulatedFunction(x2, y2);

        MathFunction composition = inner.andThen(outer);

        // Проверка точек из исходной области определения
        assertEquals(0.0, composition.apply(0.0), 1e-9);
        assertEquals(1.0, composition.apply(1.0), 1e-9);
        assertEquals(4.0, composition.apply(2.0), 1e-9);

        // Проверка интерполируемых точек
        assertEquals(2.5, composition.apply(1.5), 1e-9);
    }

    // 2. Тест смешанной композиции (Array + LinkedList)
    @Test
    public void testArrayAndLinkedListComposition() {
        double[] x1 = {0, 0.5, 1.0};
        double[] y1 = {0, Math.PI/2, Math.PI};
        ArrayTabulatedFunction inner = new ArrayTabulatedFunction(x1, y1);

        double[] x2 = {0, Math.PI/2, Math.PI};
        double[] y2 = {0, 1, 0};
        LinkedListTabulatedFunction outer = new LinkedListTabulatedFunction(x2, y2);

        MathFunction composition = inner.andThen(outer);

        // Проверка значений sin(x) в точках
        assertEquals(0.0, composition.apply(0.0), 1e-9);
        assertEquals(1.0, composition.apply(0.5), 1e-9);
        assertEquals(0.0, composition.apply(1.0), 1e-9);
    }

    // 3. Тест композиции табулированной функции с обычной математической функцией
    @Test
    public void testTabulatedAndMathFunctionComposition() {
        double[] x = {1, 2, 3, 4};
        double[] y = {1, 4, 9, 16};
        ArrayTabulatedFunction tabulated = new ArrayTabulatedFunction(x, y);

        MathFunction sqrtFunction = new MathFunction() {
            @Override
            public double apply(double x) {
                return Math.sqrt(x);
            }
        };

        MathFunction composition = tabulated.andThen(sqrtFunction);

        // sqrt(x^2) = |x|, но в нашем случае x положительный
        assertEquals(1.0, composition.apply(1.0), 1e-9);
        assertEquals(2.0, composition.apply(2.0), 1e-9);
        assertEquals(3.0, composition.apply(3.0), 1e-9);
    }


    // 4. Тест граничных случаев и экстраполяции
    @Test
    public void testEdgeCasesAndExtrapolation() {
        double[] x = {0, 1};
        double[] y = {10, 20};
        ArrayTabulatedFunction inner = new ArrayTabulatedFunction(x, y);

        MathFunction outer = new SquareFunction();

        MathFunction composition = inner.andThen(outer);

        // Левая экстраполяция: inner(-1) = 0 (экстраполяция слева), outer(10) = 0
        assertEquals(0.0, composition.apply(-1.0), 1e-9);

        // Правая экстраполяция: inner(2) = 30 (экстраполяция справа), outer(30) = 900
        assertEquals(900.0, composition.apply(2.0), 1e-9);
    }
}