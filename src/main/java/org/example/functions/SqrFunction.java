package org.example.functions;

import java.util.function.Function;
import org.example.functions.MathFunction;

public class SqrFunction implements MathFunction {
    @Override
    public double apply(double x) {
        return Math.pow(x, 2);
    }
}