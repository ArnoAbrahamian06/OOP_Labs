package org.example.operations;

import org.example.functions.MathFunction;

public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator{

    public LeftSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                // Левая разностная производная: (f(x) - f(x - step)) / step
                double f_x = function.apply(x);
                double f_x_minus_step = function.apply(x - step);
                return (f_x - f_x_minus_step) / step;
            }
        };
    }
}
