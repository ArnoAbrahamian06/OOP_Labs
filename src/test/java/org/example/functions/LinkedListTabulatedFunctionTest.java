package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class LinkedListTabulatedFunctionTest {
    private static final double DELTA = 1e-10;

    @Test
    public void testConstructorFromArrays() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(3, function.getCount(), DELTA);
        assertEquals(1.0, function.leftBound(), DELTA);
        assertEquals(3.0, function.rightBound(), DELTA);
        assertEquals(20.0, function.getY(1), DELTA);
    }

    @Test
    public void testConstructorFromFunction() {
        MathFunction source = new SqrFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(source, 0, 2, 3);

        assertEquals(3, function.getCount(), DELTA);
        assertEquals(0.0, function.getX(0), DELTA);
        assertEquals(1.0, function.getX(1), DELTA);
        assertEquals(4.0, function.getY(2), DELTA);
    }

    @Test
    public void testGetNode() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Node node = function.getNode(1); // Доступ через рефлексию или изменение модификатора на protected
        assertEquals(2.0, node.x, DELTA);
        assertEquals(20.0, node.y, DELTA);
    }

    @Test
    public void testApply() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(10.0, function.apply(1.0), DELTA);
        assertEquals(25.0, function.apply(2.5), DELTA); // Интерполяция
        assertEquals(5.0, function.apply(0.5), DELTA);  // Экстраполяция слева
        assertEquals(40.0, function.apply(4.0), DELTA); // Экстраполяция справа
    }


    @Test
    public void testFloorNodeOfX() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction func = new LinkedListTabulatedFunction(xValues, yValues);

        // x ближе к началу
        Node node1 = func.floorNodeOfX(0.5);
        assertEquals(0.0, node1.x, 1e-9);

        // x ближе к концу
        Node node2 = func.floorNodeOfX(3.5);
        assertEquals(3.0, node2.x, 1e-9);

        // x посередине
        Node node3 = func.floorNodeOfX(2.2);
        assertEquals(2.0, node3.x, 1e-9);
    }

    @Test
    public void testApplyOptimized() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        LinkedListTabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // Точное совпадение
        assertEquals(0.0, listFunc.apply(0.0), 1e-9);
        assertEquals(1.0, listFunc.apply(1.0), 1e-9);
        assertEquals(4.0, listFunc.apply(2.0), 1e-9);

        // Интерполяция
        assertEquals(0.5, listFunc.apply(0.5), 1e-9);
        assertEquals(2.5, listFunc.apply(1.5), 1e-9);

        // Экстраполяция
        assertEquals(-1.0, listFunc.apply(-1.0), 1e-9);
        assertEquals(7.0, listFunc.apply(3.0), 1e-9);
    }

    @Test
    public void testApplyWithSingleNode() {
        double[] xValues = {5.0};
        double[] yValues = {10.0};
        LinkedListTabulatedFunction func1 = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(10.0, func1.apply(5.0), 1e-9);
    }

    @Test
    public void testApplyComparisonWithArray() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};

        LinkedListTabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues, yValues);
        ArrayTabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);

        // Проверяем, что результаты совпадают
        for (double x = -0.5; x <= 3.5; x += 0.1) {
            assertEquals(arrayFunc.apply(x), listFunc.apply(x), 1e-9);
        }
    }
}


