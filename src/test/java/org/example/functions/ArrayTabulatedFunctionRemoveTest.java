package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayTabulatedFunctionRemoveTest {

    @Test
    public void testRemoveFromBeginning() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(2, function.getCount());
        assertEquals(2.0, function.getX(0), 1e-9);
        assertEquals(5.0, function.getY(0), 1e-9);
        assertEquals(3.0, function.getX(1), 1e-9);
        assertEquals(6.0, function.getY(1), 1e-9);
    }

    @Test
    public void testRemoveFromMiddle() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-9);
        assertEquals(4.0, function.getY(0), 1e-9);
        assertEquals(3.0, function.getX(1), 1e-9);
        assertEquals(6.0, function.getY(1), 1e-9);
    }

    @Test
    public void testRemoveFromEnd() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(2);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-9);
        assertEquals(4.0, function.getY(0), 1e-9);
        assertEquals(2.0, function.getX(1), 1e-9);
        assertEquals(5.0, function.getY(1), 1e-9);
    }

    @Test
    public void testRemoveSingleElement() {
        double[] xValues = {1.0};
        double[] yValues = {2.0};
        //ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
        //function.remove(0);

        //assertEquals(0, function.getCount());
    }

    @Test
    public void testRemoveInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () ->
                function.remove(5)); // Неверный индекс
    }

    @Test
    public void testRemoveFromEmptyArray() {
        double[] xValues = {};
        double[] yValues = {};
        //ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
        //function.remove(0); // Попытка удаления из пустого массива
    }
}