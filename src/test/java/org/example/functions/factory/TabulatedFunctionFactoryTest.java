package org.example.functions.factory;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.TabulatedFunction;

import org.junit.Test;

import static org.junit.Assert.*;

public class TabulatedFunctionFactoryTest {

    @Test
    public void testArrayFactoryCreateUnmodifiable() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};

        TabulatedFunction unmodifiableFunction = factory.createUnmodifiable(xValues, yValues);

        // Проверяем, что данные корректны
        assertEquals(4, unmodifiableFunction.getCount());
        assertEquals(1.0, unmodifiableFunction.getX(0), 1e-10);
        assertEquals(10.0, unmodifiableFunction.getY(0), 1e-10);
        assertEquals(4.0, unmodifiableFunction.getX(3), 1e-10);
        assertEquals(40.0, unmodifiableFunction.getY(3), 1e-10);

        // Проверяем, что попытка изменить функцию бросает исключение
        try {
            unmodifiableFunction.setY(0, 100.0);
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Ожидаемое поведение
        }
    }
    @Test
    public void testArrayFactoryCreateStrict() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};

        TabulatedFunction strictFunction = factory.createStrict(xValues, yValues);

        // Проверяем, что данные корректны
        assertEquals(4, strictFunction.getCount());
        assertEquals(1.0, strictFunction.getX(0), 1e-10);
        assertEquals(10.0, strictFunction.getY(0), 1e-10);
        assertEquals(4.0, strictFunction.getX(3), 1e-10);
        assertEquals(40.0, strictFunction.getY(3), 1e-10);

        // Проверяем, что изменение Y разрешено (Strict не запрещает изменения)
        strictFunction.setY(0, 100.0);
        assertEquals(100.0, strictFunction.getY(0), 1e-10);

        // Проверяем, что интерполяция запрещена
        try {
            strictFunction.apply(1.5); // X между узлами
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Ожидаемое поведение для Strict
        }
    }

    @Test
    public void testArrayFactoryCreatesArrayTabulatedFunction() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        TabulatedFunction f = factory.create(xs, ys);
        assertTrue(f instanceof ArrayTabulatedFunction);
    }

    @Test
    public void testLinkedListFactoryCreatesLinkedListTabulatedFunction() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedFunction f = factory.create(xs, ys);
        assertTrue(f instanceof LinkedListTabulatedFunction);
    }
}


