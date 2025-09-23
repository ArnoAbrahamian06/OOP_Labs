package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

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
}
