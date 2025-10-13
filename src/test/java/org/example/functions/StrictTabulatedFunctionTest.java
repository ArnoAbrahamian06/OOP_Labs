package org.example.functions;

import org.junit.Test;

import static org.junit.Assert.*;

public class StrictTabulatedFunctionTest {

    @Test
    public void testApplyExactArray() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunction base = new ArrayTabulatedFunction(xs, ys);
        TabulatedFunction strict = new StrictTabulatedFunction(base);

        assertEquals(0.0, strict.apply(0.0), 1e-10);
        assertEquals(1.0, strict.apply(1.0), 1e-10);
        assertEquals(4.0, strict.apply(2.0), 1e-10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testApplyInterpolatingArrayThrows() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction strict = new StrictTabulatedFunction(new ArrayTabulatedFunction(xs, ys));
        strict.apply(1.0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testApplyExtrapolatingArrayThrowsLeft() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction strict = new StrictTabulatedFunction(new ArrayTabulatedFunction(xs, ys));
        strict.apply(-1.0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testApplyExtrapolatingArrayThrowsRight() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction strict = new StrictTabulatedFunction(new ArrayTabulatedFunction(xs, ys));
        strict.apply(3.0);
    }

    @Test
    public void testDelegateMethodsArray() {
        double[] xs = {1.0, 2.0, 3.0};
        double[] ys = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction base = new ArrayTabulatedFunction(xs, ys);
        StrictTabulatedFunction strict = new StrictTabulatedFunction(base);

        assertEquals(base.getCount(), strict.getCount());
        assertEquals(base.getX(1), strict.getX(1), 1e-10);
        assertEquals(base.getY(2), strict.getY(2), 1e-10);
        strict.setY(1, 25.0);
        assertEquals(25.0, base.getY(1), 1e-10);
        assertEquals(base.indexOfX(2.0), strict.indexOfX(2.0));
        assertEquals(base.indexOfY(30.0), strict.indexOfY(30.0));
        assertEquals(base.leftBound(), strict.leftBound(), 1e-10);
        assertEquals(base.rightBound(), strict.rightBound(), 1e-10);
    }

    @Test
    public void testApplyExactLinkedList() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunction base = new LinkedListTabulatedFunction(xs, ys);
        TabulatedFunction strict = new StrictTabulatedFunction(base);

        assertEquals(0.0, strict.apply(0.0), 1e-10);
        assertEquals(1.0, strict.apply(1.0), 1e-10);
        assertEquals(4.0, strict.apply(2.0), 1e-10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testApplyInterpolatingLinkedListThrows() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction strict = new StrictTabulatedFunction(new LinkedListTabulatedFunction(xs, ys));
        strict.apply(1.0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testApplyExtrapolatingLinkedListThrowsLeft() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction strict = new StrictTabulatedFunction(new LinkedListTabulatedFunction(xs, ys));
        strict.apply(-1.0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testApplyExtrapolatingLinkedListThrowsRight() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction strict = new StrictTabulatedFunction(new LinkedListTabulatedFunction(xs, ys));
        strict.apply(3.0);
    }

    @Test
    public void testIteratorWhileArray() {
        double[] xs = {1.0, 2.0, 3.0};
        double[] ys = {10.0, 20.0, 30.0};
        TabulatedFunction strict = new StrictTabulatedFunction(new ArrayTabulatedFunction(xs, ys));

        int i = 0;
        java.util.Iterator<Point> it = strict.iterator();
        while (it.hasNext()) {
            Point p = it.next();
            assertEquals(xs[i], p.x, 1e-10);
            assertEquals(ys[i], p.y, 1e-10);
            i++;
        }
        assertEquals(3, i);
    }

    @Test
    public void testIteratorForEachLinkedList() {
        double[] xs = {5.0, 6.0, 7.0, 8.0};
        double[] ys = {50.0, 60.0, 70.0, 80.0};
        TabulatedFunction strict = new StrictTabulatedFunction(new LinkedListTabulatedFunction(xs, ys));

        int i = 0;
        for (Point p : strict) {
            assertEquals(xs[i], p.x, 1e-10);
            assertEquals(ys[i], p.y, 1e-10);
            i++;
        }
        assertEquals(4, i);
    }
}


