package org.example.operations;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.functions.factory.ArrayTabulatedFunctionFactory;
import org.example.functions.factory.LinkedListTabulatedFunctionFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class TabulatedDifferentialOperatorTest {

    @Test
    public void testDeriveLinearFunction_ArrayFactory() {
        double[] xs = {0.0, 1.0, 2.0, 3.0};
        double[] ys = {0.0, 2.0, 4.0, 6.0}; // y = 2x -> derivative = 2
        TabulatedFunction f = new ArrayTabulatedFunction(xs, ys);
        TabulatedDifferentialOperator op = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        TabulatedFunction df = op.derive(f);

        assertArrayEquals(xs, new double[]{df.getX(0), df.getX(1), df.getX(2), df.getX(3)}, 1e-10);
        for (int i = 0; i < xs.length; i++) {
            assertEquals(2.0, df.getY(i), 1e-10);
        }
    }

    @Test
    public void testDeriveQuadraticFunction_LinkedFactory() {
        double[] xs = {0.0, 1.0, 2.0, 3.0};
        double[] ys = {0.0, 1.0, 4.0, 9.0}; // y = x^2 -> derivative = 2x
        TabulatedFunction f = new LinkedListTabulatedFunction(xs, ys);
        TabulatedDifferentialOperator op = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction df = op.derive(f);

        assertArrayEquals(xs, new double[]{df.getX(0), df.getX(1), df.getX(2), df.getX(3)}, 1e-10);
        assertEquals(1.0, df.getY(0), 1e-10); // forward diff at 0 ~ 1
        assertEquals(2.0, df.getY(1), 1e-10); // central (x=1) ~ 2
        assertEquals(4.0, df.getY(2), 1e-10); // central (x=2) ~ 4
        assertEquals(5.0, df.getY(3), 1e-10); // backward at 3 ~ 5
    }

    @Test
    public void testDefaultConstructorUsesArrayFactory() {
        double[] xs = {0.0, 1.0};
        double[] ys = {0.0, 1.0};
        TabulatedFunction f = new ArrayTabulatedFunction(xs, ys);
        TabulatedDifferentialOperator op = new TabulatedDifferentialOperator();
        TabulatedFunction df = op.derive(f);
        assertTrue(df instanceof org.example.functions.ArrayTabulatedFunction);
    }
}


