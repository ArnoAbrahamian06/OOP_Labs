package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;


public class ConstantFunctionTest {
    @Test
    public void testConstant(){
        ConstantFunction con = new ConstantFunction(3.0);
        assertEquals(3.0, con.apply(5.0), 1e-6);
    }

    @Test
    public void testZero(){
        ZeroFunction zero = new ZeroFunction();
        assertEquals(0.0, zero.apply(5.0), 1e-6);
    }

    @Test
    public void testUnit(){
        UnitFunction unit = new UnitFunction();
        assertEquals(1.0, unit.apply(5.0), 1e-6);
    }
}
