package org.example.functions;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayTabulatedFunctionIteratorTest {

    @Test
    public void testIteratorWithWhileLoop() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        int expectedCount = 0;
        double[] expectedX = {1.0, 2.0, 3.0, 4.0};
        double[] expectedY = {10.0, 20.0, 30.0, 40.0};

        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(expectedX[expectedCount], point.x, 1e-10);
            assertEquals(expectedY[expectedCount], point.y, 1e-10);
            expectedCount++;
        }
        assertEquals(4, expectedCount);
    }

    @Test
    public void testIteratorWithForEachLoop() {
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {5.0, 15.0, 25.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        double[] expectedX = {0.5, 1.5, 2.5};
        double[] expectedY = {5.0, 15.0, 25.0};
        int index = 0;

        for (Point point : function) {
            assertEquals(expectedX[index], point.x, 1e-10);
            assertEquals(expectedY[index], point.y, 1e-10);
            index++;
        }
        assertEquals(3, index);
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorThrowsExceptionWhenNoMoreElements() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        iterator.next(); // 1.0, 10.0
        iterator.next(); // 2.0, 20.0
        iterator.next(); // Должен бросить NoSuchElementException
    }

    @Test
    public void testMultipleIteratorsAreIndependent() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator1 = function.iterator();
        Iterator<Point> iterator2 = function.iterator();

        // Перемещаем первый итератор
        Point point1 = iterator1.next();
        assertEquals(1.0, point1.x, 1e-10);
        assertEquals(10.0, point1.y, 1e-10);

        // Второй итератор должен начинать с начала
        Point point2 = iterator2.next();
        assertEquals(1.0, point2.x, 1e-10);
        assertEquals(10.0, point2.y, 1e-10);

        // Продолжаем первый итератор
        point1 = iterator1.next();
        assertEquals(2.0, point1.x, 1e-10);
        assertEquals(20.0, point1.y, 1e-10);

        // Второй итератор на следующем элементе
        point2 = iterator2.next();
        assertEquals(2.0, point2.x, 1e-10);
        assertEquals(20.0, point2.y, 1e-10);
    }
}