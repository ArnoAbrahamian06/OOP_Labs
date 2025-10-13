package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class StrictUnmodifiableCompositionTest {

    // Unmodifiable(Strict(Array))
    @Test
    public void testExactApply_UnmodifiableStrict_Array() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunction f = new UnmodifiableTabulatedFunction(new StrictTabulatedFunction(new ArrayTabulatedFunction(xs, ys)));
        assertEquals(0.0, f.apply(0.0), 1e-10);
        assertEquals(1.0, f.apply(1.0), 1e-10);
        assertEquals(4.0, f.apply(2.0), 1e-10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoInterpolation_UnmodifiableStrict_Array() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction f = new UnmodifiableTabulatedFunction(new StrictTabulatedFunction(new ArrayTabulatedFunction(xs, ys)));
        f.apply(1.0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoMutation_UnmodifiableStrict_Array() {
        double[] xs = {0.0, 1.0};
        double[] ys = {0.0, 1.0};
        TabulatedFunction f = new UnmodifiableTabulatedFunction(new StrictTabulatedFunction(new ArrayTabulatedFunction(xs, ys)));
        f.setY(0, 10.0);
    }

    // Strict(Unmodifiable(Array))
    @Test
    public void testExactApply_StrictUnmodifiable_Array() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunction f = new StrictTabulatedFunction(new UnmodifiableTabulatedFunction(new ArrayTabulatedFunction(xs, ys)));
        assertEquals(0.0, f.apply(0.0), 1e-10);
        assertEquals(1.0, f.apply(1.0), 1e-10);
        assertEquals(4.0, f.apply(2.0), 1e-10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoInterpolation_StrictUnmodifiable_Array() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction f = new StrictTabulatedFunction(new UnmodifiableTabulatedFunction(new ArrayTabulatedFunction(xs, ys)));
        f.apply(1.0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoMutation_StrictUnmodifiable_Array() {
        double[] xs = {0.0, 1.0};
        double[] ys = {0.0, 1.0};
        TabulatedFunction f = new StrictTabulatedFunction(new UnmodifiableTabulatedFunction(new ArrayTabulatedFunction(xs, ys)));
        f.setY(0, 10.0);
    }

    // Unmodifiable(Strict(LinkedList))
    @Test
    public void testExactApply_UnmodifiableStrict_Linked() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunction f = new UnmodifiableTabulatedFunction(new StrictTabulatedFunction(new LinkedListTabulatedFunction(xs, ys)));
        assertEquals(0.0, f.apply(0.0), 1e-10);
        assertEquals(1.0, f.apply(1.0), 1e-10);
        assertEquals(4.0, f.apply(2.0), 1e-10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoInterpolation_UnmodifiableStrict_Linked() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction f = new UnmodifiableTabulatedFunction(new StrictTabulatedFunction(new LinkedListTabulatedFunction(xs, ys)));
        f.apply(1.0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoMutation_UnmodifiableStrict_Linked() {
        double[] xs = {0.0, 1.0};
        double[] ys = {0.0, 1.0};
        TabulatedFunction f = new UnmodifiableTabulatedFunction(new StrictTabulatedFunction(new LinkedListTabulatedFunction(xs, ys)));
        f.setY(0, 10.0);
    }

    // Strict(Unmodifiable(LinkedList))
    @Test
    public void testExactApply_StrictUnmodifiable_Linked() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunction f = new StrictTabulatedFunction(new UnmodifiableTabulatedFunction(new LinkedListTabulatedFunction(xs, ys)));
        assertEquals(0.0, f.apply(0.0), 1e-10);
        assertEquals(1.0, f.apply(1.0), 1e-10);
        assertEquals(4.0, f.apply(2.0), 1e-10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoInterpolation_StrictUnmodifiable_Linked() {
        double[] xs = {0.0, 2.0};
        double[] ys = {0.0, 4.0};
        TabulatedFunction f = new StrictTabulatedFunction(new UnmodifiableTabulatedFunction(new LinkedListTabulatedFunction(xs, ys)));
        f.apply(1.0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoMutation_StrictUnmodifiable_Linked() {
        double[] xs = {0.0, 1.0};
        double[] ys = {0.0, 1.0};
        TabulatedFunction f = new StrictTabulatedFunction(new UnmodifiableTabulatedFunction(new LinkedListTabulatedFunction(xs, ys)));
        f.setY(0, 10.0);
    }
}


