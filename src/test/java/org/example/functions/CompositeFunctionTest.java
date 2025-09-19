package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class CompositeFunctionTest {
    private static final double DELTA = 1e-10;

    @Test
    public void testApply() {
        MathFunction id = new IdentityFunction();
        MathFunction sin = Math::sin;;

        // Композиция sin(identity(x)) = sin(x)
        MathFunction sinId = new CompositeFunction(id, sin);
        assertEquals(Math.sin(5.0), sinId.apply(5.0), DELTA);

        // Композиция identity(sin(x)) = sin(x)
        MathFunction idSin = new CompositeFunction(sin, id);
        assertEquals(Math.sin(5.0), idSin.apply(5.0), DELTA);

        // Многократная композиция
        MathFunction complex = new CompositeFunction(sinId, idSin);
        assertEquals(Math.sin(Math.sin(5.0)), complex.apply(5.0), DELTA);
    }
}