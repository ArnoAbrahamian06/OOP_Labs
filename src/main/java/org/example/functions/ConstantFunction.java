package org.example.functions;

public class ConstantFunction implements MathFunction {
    private final double constant;

    public ConstantFunction(double x) {
        this.constant = x;
    }

    @Override
    public double apply(double x){
        return constant;
    }
}
