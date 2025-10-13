package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;
import org.example.exceptions.*;

public class ArrayTabulatedFunctionTest {
    @Test
    public void testArrayConstructor() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(xValues, yValues);
        assertEquals(3, func.getCount());
        assertEquals(2.0, func.getX(1), 1e-9);
        assertEquals(20.0, func.getY(1), 1e-9);
        assertEquals(1, func.indexOfX(2.0), 1e-9);
        assertEquals(2, func.indexOfY(30.0), 1e-9);
        assertEquals(1.0, func.leftBound(), 1e-9);
        assertEquals(3.0, func.rightBound(), 1e-9);
        assertEquals(1, func.floorIndexOfX(2.5), 1e-9);
        assertEquals(15.0, func.extrapolateLeft(1.5), 1e-9);
        assertEquals(25.0, func.extrapolateRight(2.5), 1e-9);
        assertEquals(25.0, func.interpolate(2.5, 1), 1e-9);
    }

    @Test
    public void testInterpolateExceptions() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(InterpolationException.class,() ->  {func.interpolate(3.5, 1);});
    }

    @Test
    public void testInsert() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Вставка в начало
        function.insert(0.5, 5.0);
        assertEquals(4, function.getCount());
        assertEquals(0.5, function.getX(0), 1e-10);
        assertEquals(5.0, function.getY(0), 1e-10);

        // Вставка в середину
        function.insert(1.5, 15.0);
        assertEquals(5, function.getCount());
        assertEquals(1.5, function.getX(2), 1e-10);
        assertEquals(15.0, function.getY(2), 1e-10);

        // Вставка в конец
        function.insert(4.0, 40.0);
        assertEquals(6, function.getCount());
        assertEquals(4.0, function.getX(5), 1e-10);
        assertEquals(40.0, function.getY(5), 1e-10);

        // Замена существующего значения
        function.insert(2.0, 25.0);
        assertEquals(6, function.getCount()); // Количество не изменилось
        assertEquals(25.0, function.getY(3), 1e-10); // Значение заменено
    }

    // Тесты на исключения
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithSinglePoint() {
        double[] xValues = {1.0};
        double[] yValues = {10.0};
        new ArrayTabulatedFunction(xValues, yValues);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyArray() {
        double[] xValues = {};
        double[] yValues = {};
        new ArrayTabulatedFunction(xValues, yValues);
    }

    @Test
    public void testConstructorWithDifferentArrayLengths() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0}; // Разная длина

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFromFunctionWithCountLessThanTwo() {
        MathFunction source = new SqrFunction();
        new ArrayTabulatedFunction(source, 0, 2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetXWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        function.getX(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetXWithIndexOutOfBounds() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        function.getX(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetYWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        function.getY(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetYWithNegativeIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        function.setY(-1, 15.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFloorIndexOfXWithXLessThanLeftBound() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        function.floorIndexOfX(0.5);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveWithLessThanTwoPoints() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        function.remove(0);
        function.remove(0); // Теперь осталась только одна точка
    }
}
