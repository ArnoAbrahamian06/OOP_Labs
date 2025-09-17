package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class IdentityFunctionTest {
    private static final double DELTA = 1e-10;

    @Test
    public void testApply() {
        MathFunction id = new IdentityFunction();
        assertEquals(0.0, id.apply(0.0), DELTA);
        assertEquals(5.0, id.apply(5.0), DELTA);
        assertEquals(-3.7, id.apply(-3.7), DELTA);
    }
}