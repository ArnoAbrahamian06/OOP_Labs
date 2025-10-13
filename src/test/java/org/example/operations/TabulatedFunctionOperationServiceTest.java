package org.example.operations;


import org.example.functions.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class TabulatedFunctionOperationServiceTest {

    private TabulatedFunction arrayFunction;
    private TabulatedFunction linkedListFunction;

    @Before
    public void setUp() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};

        arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);
    }

    @Test
    public void testAsPointsWithArrayTabulatedFunction() {
        Point[] points = TabulatedFunctionOperationService.asPoints(arrayFunction);

        assertEquals(4, points.length);

        // Проверяем корректность преобразования
        assertEquals(1.0, points[0].x, 1e-10);
        assertEquals(10.0, points[0].y, 1e-10);

        assertEquals(2.0, points[1].x, 1e-10);
        assertEquals(20.0, points[1].y, 1e-10);

        assertEquals(3.0, points[2].x, 1e-10);
        assertEquals(30.0, points[2].y, 1e-10);

        assertEquals(4.0, points[3].x, 1e-10);
        assertEquals(40.0, points[3].y, 1e-10);
    }

    @Test
    public void testAsPointsWithLinkedListTabulatedFunction() {
        Point[] points = TabulatedFunctionOperationService.asPoints(linkedListFunction);

        assertEquals(4, points.length);

        // Проверяем корректность преобразования
        assertEquals(1.0, points[0].x, 1e-10);
        assertEquals(10.0, points[0].y, 1e-10);

        assertEquals(2.0, points[1].x, 1e-10);
        assertEquals(20.0, points[1].y, 1e-10);

        assertEquals(3.0, points[2].x, 1e-10);
        assertEquals(30.0, points[2].y, 1e-10);

        assertEquals(4.0, points[3].x, 1e-10);
        assertEquals(40.0, points[3].y, 1e-10);
    }


    @Test
    public void testAsPointsPreservesOrder() {
        double[] xValues = {3.0, 1.0, 2.0}; // Не отсортированный массив
        double[] yValues = {30.0, 10.0, 20.0};
    }

    @Test
    public void testAsPointsWithDifferentImplementationsReturnSameResult() {
        Point[] arrayPoints = TabulatedFunctionOperationService.asPoints(arrayFunction);
        Point[] linkedListPoints = TabulatedFunctionOperationService.asPoints(linkedListFunction);

        assertEquals(arrayPoints.length, linkedListPoints.length);

        for (int i = 0; i < arrayPoints.length; i++) {
            assertEquals("Point " + i + " x differs", arrayPoints[i].x, linkedListPoints[i].x, 1e-10);
            assertEquals("Point " + i + " y differs", arrayPoints[i].y, linkedListPoints[i].y, 1e-10);
        }
    }
}