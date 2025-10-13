package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class AbstractTabulatedFunctionTest {

    @Test
    public void testInterpolate() {
        MockTabulatedFunction function = new MockTabulatedFunction(1, 2, 1, 2);

        // Тестирование интерполяции
        double result = function.interpolate(1.5, 1, 2, 1, 2);
        assertEquals(1.5, result, 1e-9);
    }

    @Test
    public void testApply() {
        MockTabulatedFunction function = new MockTabulatedFunction(1, 3, 1, 9);

        // Тестирование значения внутри интервала
        assertEquals(5.0, function.apply(2.0), 1e-9);

        // Тестирование экстраполяции слева
        assertEquals(-3.0, function.apply(0.0), 1e-9);

        // Тестирование экстраполяции справа
        assertEquals(13.0, function.apply(4.0), 1e-9);

        // Тестирование существующих значений
        assertEquals(1.0, function.apply(1.0), 1e-9);
        assertEquals(9.0, function.apply(3.0), 1e-9);
    }

    @Test
    public void testArrayTabulatedFunctionToString() {
        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        String expected = "ArrayTabulatedFunction size = 3\n[0.0; 0.0]\n[0.5; 0.25]\n[1.0; 1.0]";
        assertEquals(expected, function.toString());
    }

    @Test
    public void testLinkedListTabulatedFunctionToString() {
        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        String expected = "LinkedListTabulatedFunction size = 3\n[0.0; 0.0]\n[0.5; 0.25]\n[1.0; 1.0]";
        assertEquals(expected, function.toString());
    }
}