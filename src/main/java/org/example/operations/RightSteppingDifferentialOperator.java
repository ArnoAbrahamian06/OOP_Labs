package org.example.operations;

import org.example.functions.MathFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator {

    private static final Logger logger = LoggerFactory.getLogger(RightSteppingDifferentialOperator.class);

    public RightSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        logger.debug("Вычисление правой разностной производной для функции");

        return new MathFunction() {
            @Override
            public double apply(double x) {
                // Правая разностная производная: (f(x + step) - f(x)) / step
                double f_x_plus_step = function.apply(x + step);
                double f_x = function.apply(x);
                double derivative = (f_x_plus_step - f_x) / step;
                logger.trace("Левая производная в точке x={}: (f({}) - f({})) / {} = {}",
                        x, x + step, x, step, derivative);

                return derivative;
            }
        };
    }
}
