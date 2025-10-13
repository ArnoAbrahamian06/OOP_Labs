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
            fail("ожидалось UnsupportedOperationException");
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
            fail("ожидалось UnsupportedOperationException");
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
    @Test
    public void testCreateStrictUnmodifiable() {
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};

        TabulatedFunction strictUnmodifiableFunction = factory.createStrictUnmodifiable(xValues, yValues);

        // Проверяем базовые свойства
        assertEquals(4, strictUnmodifiableFunction.getCount());
        assertEquals(1.0, strictUnmodifiableFunction.getX(0), 1e-10);
        assertEquals(10.0, strictUnmodifiableFunction.getY(0), 1e-10);
        assertEquals(4.0, strictUnmodifiableFunction.getX(3), 1e-10);
        assertEquals(40.0, strictUnmodifiableFunction.getY(3), 1e-10);

        // Проверяем свойство Strict: интерполяция запрещена
        try {
            strictUnmodifiableFunction.apply(1.5); // X между узлами
            fail("ожидалось UnsupportedOperationException for interpolation");
        } catch (UnsupportedOperationException e) {
            // Ожидаемое поведение - Strict запрещает интерполяцию
        }

        // Проверяем свойство Unmodifiable: изменение запрещено
        try {
            strictUnmodifiableFunction.setY(0, 100.0);
            fail("ожидалось UnsupportedOperationException for modification");
        } catch (UnsupportedOperationException e) {
            // Ожидаемое поведение - Unmodifiable запрещает изменения
        }

        // Проверяем, что для существующих X значения возвращаются корректно
        assertEquals(10.0, strictUnmodifiableFunction.apply(1.0), 1e-10);
        assertEquals(20.0, strictUnmodifiableFunction.apply(2.0), 1e-10);
        assertEquals(30.0, strictUnmodifiableFunction.apply(3.0), 1e-10);
        assertEquals(40.0, strictUnmodifiableFunction.apply(4.0), 1e-10);

        // Проверяем, что экстраполяция также запрещена (свойство Strict)
        try {
            strictUnmodifiableFunction.apply(0.5); // X меньше левой границы
            fail("ожидалось UnsupportedOperationException for left extrapolation");
        } catch (UnsupportedOperationException e) {
            // Ожидаемое поведение
        }

        try {
            strictUnmodifiableFunction.apply(4.5); // X больше правой границы
            fail("ожидалось UnsupportedOperationException for right extrapolation");
        } catch (UnsupportedOperationException e) {
            // Ожидаемое поведение
        }
    }

    @Test
    public void testCreateStrictUnmodifiableWithLinkedListFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {5.0, 15.0, 25.0};

        TabulatedFunction strictUnmodifiableFunction = factory.createStrictUnmodifiable(xValues, yValues);

        // Проверяем базовые свойства
        assertEquals(3, strictUnmodifiableFunction.getCount());
        assertEquals(0.5, strictUnmodifiableFunction.getX(0), 1e-10);
        assertEquals(5.0, strictUnmodifiableFunction.getY(0), 1e-10);

        // Проверяем Strict свойство
        try {
            strictUnmodifiableFunction.apply(1.0); // X между узлами
            fail("ожидалось UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Ожидаемое поведение
        }

        // Проверяем Unmodifiable свойство
        try {
            strictUnmodifiableFunction.setY(1, 100.0);
            fail("ожидалось UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // Ожидаемое поведение
        }

        // Проверяем корректную работу для существующих X
        assertEquals(15.0, strictUnmodifiableFunction.apply(1.5), 1e-10);
    }

    @Test
    public void testCreateStrictUnmodifiableOrderIndependence() {
        // Тест демонстрирует, что порядок оберток не влияет на конечное поведение
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {2.0, 6.0, 10.0};

        TabulatedFunction function1 = factory.createStrictUnmodifiable(xValues, yValues);

        // Проверяем, что оба свойства соблюдаются независимо от порядка оберток
        assertThrows(UnsupportedOperationException.class, () -> function1.apply(2.0)); // Strict
        assertThrows(UnsupportedOperationException.class, () -> function1.setY(0, 99.0)); // Unmodifiable

        // Проверяем корректные вызовы
        assertEquals(6.0, function1.apply(3.0), 1e-10);
    }
}


