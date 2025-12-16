package org.example.operations;

import org.example.functions.MathFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator{

    private static final Logger logger = LoggerFactory.getLogger(LeftSteppingDifferentialOperator.class);

    public LeftSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        logger.debug("Вычисление левой разностной производной для функции");

        return new MathFunction() {
            @Override
            public double apply(double x) {
                // Левая разностная производная: (f(x) - f(x - step)) / step
                double f_x = function.apply(x);
                double f_x_minus_step = function.apply(x - step);
                double derivative = (f_x - f_x_minus_step) / step;
                logger.debug("Левая производная в точке x={}: (f({}) - f({})) / {} = {}",
                        x, x, x - step, step, derivative);

                return derivative;
            }
        };
    }
}
