package org.example.functions.factory;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.junit.Test;

import static org.junit.Assert.*;

public class TabulatedFunctionFactoryTest {

    @Test
    public void testArrayFactoryCreatesArrayTabulatedFunction() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();
        TabulatedFunction f = factory.create(xs, ys);
        assertTrue(f instanceof ArrayTabulatedFunction);
    }

    @Test
    public void testLinkedListFactoryCreatesLinkedListTabulatedFunction() {
        double[] xs = {0.0, 1.0, 2.0};
        double[] ys = {0.0, 1.0, 4.0};
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedFunction f = factory.create(xs, ys);
        assertTrue(f instanceof LinkedListTabulatedFunction);
    }
}


