package org.example.operations;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.functions.factory.ArrayTabulatedFunctionFactory;
import org.example.functions.factory.LinkedListTabulatedFunctionFactory;
import org.example.functions.factory.TabulatedFunctionFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class TabulatedDifferentialOperatorFactoryTest {

    @Test
    public void testDefaultFactoryIsArrayAndAffectsDerive() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunction f = new ArrayTabulatedFunction(xs, ys);

        TabulatedDifferentialOperator op = new TabulatedDifferentialOperator();
        TabulatedFunctionFactory factory = op.getFactory();
        assertTrue(factory instanceof ArrayTabulatedFunctionFactory);

        TabulatedFunction df = op.derive(f);
        assertTrue(df instanceof ArrayTabulatedFunction);
    }

    @Test
    public void testConstructorInjectedFactoryIsUsed() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunction f = new ArrayTabulatedFunction(xs, ys);

        TabulatedDifferentialOperator op = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        assertTrue(op.getFactory() instanceof LinkedListTabulatedFunctionFactory);

        TabulatedFunction df = op.derive(f);
        assertTrue(df instanceof LinkedListTabulatedFunction);
    }

    @Test
    public void testSetFactorySwitchesImplementation() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunction f = new ArrayTabulatedFunction(xs, ys);

        TabulatedDifferentialOperator op = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        TabulatedFunction df1 = op.derive(f);
        assertTrue(df1 instanceof ArrayTabulatedFunction);

        op.setFactory(new LinkedListTabulatedFunctionFactory());
        assertTrue(op.getFactory() instanceof LinkedListTabulatedFunctionFactory);

        TabulatedFunction df2 = op.derive(f);
        assertTrue(df2 instanceof LinkedListTabulatedFunction);
    }
}


