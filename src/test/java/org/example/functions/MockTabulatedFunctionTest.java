package org.example.functions;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class MockTabulatedFunctionTest {

    @Test
    public void testConstructorAndGetters() {
        MockTabulatedFunction f = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);
        assertEquals(2, f.getCount());
        assertEquals(1.0, f.getX(0), 1e-10);
        assertEquals(3.0, f.getX(1), 1e-10);
        assertEquals(2.0, f.getY(0), 1e-10);
        assertEquals(6.0, f.getY(1), 1e-10);
        assertEquals(1.0, f.leftBound(), 1e-10);
        assertEquals(3.0, f.rightBound(), 1e-10);
    }

    @Test
    public void testSetY() {
        MockTabulatedFunction f = new MockTabulatedFunction(0.0, 10.0, 0.0, 10.0);
        f.setY(0, 5.0);
        f.setY(1, 15.0);
        assertEquals(5.0, f.getY(0), 1e-10);
        assertEquals(15.0, f.getY(1), 1e-10);
    }

    @Test
    public void testIndexOfXAndY() {
        MockTabulatedFunction f = new MockTabulatedFunction(2.0, 4.0, 8.0, 16.0);
        assertEquals(0, f.indexOfX(2.0));
        assertEquals(1, f.indexOfX(4.0));
        assertEquals(-1, f.indexOfX(5.0));

        assertEquals(0, f.indexOfY(8.0));
        assertEquals(1, f.indexOfY(16.0));
        assertEquals(-1, f.indexOfY(9.0));
    }

    @Test
    public void testApplyExactInterpolationAndExtrapolation() {
        // line through (0,0) and (2,4): y = 2x
        MockTabulatedFunction f = new MockTabulatedFunction(0.0, 2.0, 0.0, 4.0);

        // exact
        assertEquals(0.0, f.apply(0.0), 1e-10);
        assertEquals(4.0, f.apply(2.0), 1e-10);

        // interpolation inside [0,2]
        assertEquals(2.0, f.apply(1.0), 1e-10);

        // extrapolation left and right
        assertEquals(-2.0, f.apply(-1.0), 1e-10);
        assertEquals(6.0, f.apply(3.0), 1e-10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorThrows() {
        MockTabulatedFunction f = new MockTabulatedFunction(0.0, 1.0, 0.0, 1.0);
        Iterator<Point> it = f.iterator();
        it.hasNext();
    }
}


