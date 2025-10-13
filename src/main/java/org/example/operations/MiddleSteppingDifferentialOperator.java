package org.example.operations;

import org.example.functions.MathFunction;

public class MiddleSteppingDifferentialOperator extends SteppingDifferentialOperator{

    public MiddleSteppingDifferentialOperator(double step){
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                // Средняя разностная производная: (f(x + step) - f(x - step)) / (2 * step)
                double f_x_plus_step = function.apply(x + step);
                double f_x_minus_step = function.apply(x - step);
                return (f_x_plus_step - f_x_minus_step) / (2 * step);
            }
        };
    }
}
