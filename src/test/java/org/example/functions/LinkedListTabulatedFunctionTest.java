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
}
