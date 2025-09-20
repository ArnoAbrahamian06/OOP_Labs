package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class IterativeMethodFunctionTest {
    private static final double DELTA = 1e-10;

    @Test
    public void testImmediateConvergence() {
        MathFunction identity = new IdentityFunction();
        IterativeMethodFunction solver = new IterativeMethodFunction(identity);

        double result = solver.apply(5.0);
        assertEquals(5.0, result, DELTA);
    }

    @Test
    public void testLinearConvergence() {
        MathFunction linearPhi = x -> 0.5 * x + 1;
        IterativeMethodFunction solver = new IterativeMethodFunction(linearPhi);

        double result = solver.apply(0.0);
        double expected = 2.0;
        assertEquals(expected, result, DELTA);
    }
}