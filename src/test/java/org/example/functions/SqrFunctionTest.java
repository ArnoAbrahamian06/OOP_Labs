package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;


public class SqrFunctionTest {

    @Test
    public void testApply() {
        MathFunction sqr = new SqrFunction();

        // Тестирование нулевого значения
        assertEquals(0.0, sqr.apply(0.0), 0.0001);

        // Тестирование положительных значений
        assertEquals(1.0, sqr.apply(1.0), 0.0001);
        assertEquals(4.0, sqr.apply(2.0), 0.0001);
        assertEquals(9.0, sqr.apply(3.0), 0.0001);

        // Тестирование отрицательных значений
        assertEquals(4.0, sqr.apply(-2.0), 0.0001);
        assertEquals(9.0, sqr.apply(-3.0), 0.0001);

        // Тестирование дробных значений
        assertEquals(6.25, sqr.apply(2.5), 0.0001);
        assertEquals(2.25, sqr.apply(-1.5), 0.0001);

        // Тестирование больших значений
        assertEquals(1.0E10, sqr.apply(100000.0), 0.0001);
    }

    @Test
    public void testApplyWithSpecialValues() {
        MathFunction sqr = new SqrFunction();

        // Тестирование NaN
        assertEquals(Double.NaN, sqr.apply(Double.NaN), 0.0001);

        // Тестирование бесконечности
        assertEquals(Double.POSITIVE_INFINITY, sqr.apply(Double.POSITIVE_INFINITY), 0.0001);
        assertEquals(Double.POSITIVE_INFINITY, sqr.apply(Double.NEGATIVE_INFINITY), 0.0001);
    }
}