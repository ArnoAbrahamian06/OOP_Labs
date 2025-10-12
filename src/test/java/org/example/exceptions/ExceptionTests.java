package org.example.exceptions;

import org.example.exceptions.*;
import org.example.functions.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExceptionTests {

    @Test
    public void testDifferentLengthOfArraysException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0}; // разная длина

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });
    }

    @Test
    public void testArrayIsNotSortedException() {
        double[] xValues = {1.0, 3.0, 2.0}; // не отсортирован

        assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(xValues);
        });
    }

    @Test
    public void testArrayIsNotSortedExceptionWithEqualValues() {
        double[] xValues = {1.0, 2.0, 2.0}; // равные значения

        AbstractTabulatedFunction.checkSorted(xValues);
    }


    @Test
    public void testConstructorExceptions() {
        // Тест для DifferentLengthOfArraysException в конструкторе
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });

        // Тест для ArrayIsNotSortedException в конструкторе
        double[] unsortedX = {1.0, 3.0, 2.0};
        double[] yValues2 = {1.0, 2.0, 3.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new LinkedListTabulatedFunction(unsortedX, yValues2);
        });
    }
}