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
        double[] y1 = {0, Math.PI / 2, Math.PI};
        ArrayTabulatedFunction inner = new ArrayTabulatedFunction(x1, y1);

        double[] x2 = {0, Math.PI / 2, Math.PI};
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


    @Test
    public void testLinkedListAndLinkedListComposition() {
        double[] x1 = {-2, -1, 0, 1, 2};
        double[] y1 = {4, 1, 0, 1, 4};
        LinkedListTabulatedFunction inner = new LinkedListTabulatedFunction(x1, y1);

        double[] x2 = {0, 1, 4, 9};
        double[] y2 = {0, 1, 2, 3};
        LinkedListTabulatedFunction outer = new LinkedListTabulatedFunction(x2, y2);

        MathFunction composition = inner.andThen(outer);

        // Проверка известных значений
        assertEquals(0.0, composition.apply(0.0), 1e-9);
        assertEquals(1.0, composition.apply(1.0), 1e-9);
        assertEquals(2.0, composition.apply(2.0), 1e-9);

        // Проверка экстраполяции
        assertEquals(2.6, composition.apply(3.0), 1e-9); // 3^2=9 -> sqrt(9)=3
    }

    // 5. Тест композиции математической функции с табулированной
    @Test
    public void testMathFunctionAndTabulatedComposition() {
        MathFunction sinFunction = Math::sin;

        double[] x = {0, Math.PI / 6, Math.PI / 2, Math.PI};
        double[] y = {0, 0.5, 1.0, 0};
        LinkedListTabulatedFunction tabulated = new LinkedListTabulatedFunction(x, y);

        MathFunction composition = sinFunction.andThen(tabulated);

        // Табулированная функция описывает sin(x), поэтому композиция sin(sin(x))
        assertEquals(0.0, composition.apply(0.0), 1e-9);
        assertEquals(0.5, composition.apply(Math.PI / 6), 1e-1);
    }


    @Test
    public void testConstantFunctions() {
        // Постоянная табулированная функция
        double[] x = {-5, 0, 5};
        double[] y = {7, 7, 7};
        LinkedListTabulatedFunction constantTabulated = new LinkedListTabulatedFunction(x, y);

        MathFunction increment = xVal -> xVal + 2;

        MathFunction composition = constantTabulated.andThen(increment);

        // Для любого x constantTabulated(x) = 7, затем 7 + 2 = 9
        assertEquals(9.0, composition.apply(-10.0), 1e-9);
        assertEquals(9.0, composition.apply(0.0), 1e-9);
        assertEquals(9.0, composition.apply(10.0), 1e-9);
    }


    @Test
    public void testPerformanceWithLargeArrays() {
        int size = 1000;
        double[] x = new double[size];
        double[] y = new double[size];

        for (int i = 0; i < size; i++) {
            x[i] = i;
            y[i] = i * i;
        }

        ArrayTabulatedFunction inner = new ArrayTabulatedFunction(x, y);
        LinkedListTabulatedFunction outer = new LinkedListTabulatedFunction(x, y);

        MathFunction composition = inner.andThen(outer);

        // Проверка в средней точке
        assertEquals(498252998.0, composition.apply(500.0), 1e-9); // (500^2)^2 = 500^4
    }


}