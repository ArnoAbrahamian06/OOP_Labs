package org.example.functions.factory;

import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.springframework.stereotype.Component;

@Component
public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        return new LinkedListTabulatedFunction(xValues, yValues);
    }
}


