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
}
