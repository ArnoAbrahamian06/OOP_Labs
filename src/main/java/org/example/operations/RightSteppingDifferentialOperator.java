package org.example.operations;

import org.example.functions.MathFunction;

public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator {

    public RightSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                // Правая разностная производная: (f(x + step) - f(x)) / step
                double f_x_plus_step = function.apply(x + step);
                double f_x = function.apply(x);
                return (f_x_plus_step - f_x) / step;
            }
        };
    }
}
