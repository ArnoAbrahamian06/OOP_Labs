package org.example.operations;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.functions.factory.ArrayTabulatedFunctionFactory;
import org.example.functions.factory.LinkedListTabulatedFunctionFactory;
import org.example.concurrent.SynchronizedTabulatedFunction;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class TabulatedDifferentialOperatorTest {

    private TabulatedDifferentialOperator operator;
    private final double[] xValues = {1.0, 2.0, 3.0, 4.0};
    private final double[] yValues = {1.0, 4.0, 9.0, 16.0};

    @Before
    public void setUp() {
        operator = new TabulatedDifferentialOperator();
    }

    @Test
    public void testDeriveSynchronouslyWithRegularFunction() {
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derived = operator.deriveSynchronously(function);

        assertNotNull("Производная не должна быть null", derived);
        assertEquals("Количество точек должно совпадать", 4, derived.getCount());

        // Проверяем вычисленные значения производной
        assertEquals(3.0, derived.getY(0), 1e-9);  // (4-1)/(2-1) = 3
        assertEquals(4.0, derived.getY(1), 1e-9);  // (9-1)/(3-1) = 4
        assertEquals(6.0, derived.getY(2), 1e-9);  // (16-4)/(4-2) = 6
        assertEquals(7.0, derived.getY(3), 1e-9);  // (16-9)/(4-3) = 7
    }

    @Test
    public void testDeriveSynchronouslyWithSynchronizedFunction() {
        TabulatedFunction innerFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        TabulatedFunction derived = operator.deriveSynchronously(syncFunction);

        assertNotNull("Производная не должна быть null", derived);
        assertEquals("Количество точек должно совпадать", 4, derived.getCount());

        // Проверяем, что результат вычислен корректно
        assertEquals(3.0, derived.getY(0), 1e-9);
        assertEquals(4.0, derived.getY(1), 1e-9);
        assertEquals(6.0, derived.getY(2), 1e-9);
        assertEquals(7.0, derived.getY(3), 1e-9);
    }

    @Test
    public void testDeriveSynchronouslyWithLinkedListFunction() {
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        TabulatedFunction derived = operator.deriveSynchronously(function);

        assertNotNull("Производная не должна быть null", derived);
        assertEquals("Количество точек должно совпадать", 4, derived.getCount());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testDeriveSynchronouslyWithSinglePoint() {
        double[] singleX = {1.0};
        double[] singleY = {5.0};
        TabulatedFunction function = new ArrayTabulatedFunction(singleX, singleY);

        TabulatedFunction derived = operator.deriveSynchronously(function);

        assertNotNull("Производная не должна быть null", derived);
        assertEquals("Для одной точки должна быть одна точка", 1, derived.getCount());
        assertEquals(0.0, derived.getY(0), 1e-9);  // Для одной точки производная = 0
    }

    @Test
    public void testDeriveSynchronouslyWithTwoPoints() {
        double[] twoX = {1.0, 2.0};
        double[] twoY = {2.0, 4.0};
        TabulatedFunction function = new ArrayTabulatedFunction(twoX, twoY);

        TabulatedFunction derived = operator.deriveSynchronously(function);

        assertNotNull("Производная не должна быть null", derived);
        assertEquals("Для двух точек должно быть две точки", 2, derived.getCount());
        assertEquals(2.0, derived.getY(0), 1e-9);  // (4-2)/(2-1) = 2
        assertEquals(2.0, derived.getY(1), 1e-9);  // (4-2)/(2-1) = 2
    }

    @Test
    public void testDeriveSynchronouslyWithDifferentFactory() {
        TabulatedDifferentialOperator customOperator =
                new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction derived = customOperator.deriveSynchronously(function);

        assertNotNull("Производная не должна быть null", derived);
        assertEquals("Количество точек должно совпадать", 4, derived.getCount());
    }

    @Test
    public void testDeriveSynchronouslyResultEqualsDerive() {
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivedSync = operator.deriveSynchronously(function);
        TabulatedFunction derivedNormal = operator.derive(function);

        assertNotNull("Обе производные не должны быть null", derivedSync);
        assertNotNull("Обе производные не должны быть null", derivedNormal);

        assertEquals("Количество точек должно совпадать",
                derivedNormal.getCount(), derivedSync.getCount());

        // Проверяем, что значения совпадают
        for (int i = 0; i < derivedNormal.getCount(); i++) {
            assertEquals("Значения производной должны совпадать",
                    derivedNormal.getY(i), derivedSync.getY(i), 1e-9);
        }
    }

    @Test
    public void testDeriveSynchronouslyWithLinearFunction() {
        double[] linearX = {0.0, 1.0, 2.0, 3.0};
        double[] linearY = {2.0, 4.0, 6.0, 8.0};  // y = 2x + 2

        TabulatedFunction function = new ArrayTabulatedFunction(linearX, linearY);
        TabulatedFunction derived = operator.deriveSynchronously(function);

        assertNotNull("Производная не должна быть null", derived);

        // Для линейной функции производная должна быть постоянной и равной 2
        for (int i = 0; i < derived.getCount(); i++) {
            assertEquals("Производная линейной функции должна быть постоянной",
                    2.0, derived.getY(i), 1e-9);
        }
    }

    @Test
    public void testDeriveSynchronouslyMultipleCalls() {
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        // Многократные вызовы должны работать стабильно
        for (int i = 0; i < 10; i++) {
            TabulatedFunction derived = operator.deriveSynchronously(function);
            assertNotNull("Производная не должна быть null при вызове " + i, derived);
            assertEquals("Количество точек должно совпадать при вызове " + i,
                    4, derived.getCount());
        }
    }

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


