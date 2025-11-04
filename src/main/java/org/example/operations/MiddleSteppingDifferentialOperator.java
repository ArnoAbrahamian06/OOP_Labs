package org.example.operations;

import org.example.functions.MathFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiddleSteppingDifferentialOperator extends SteppingDifferentialOperator{

    private static final Logger logger = LoggerFactory.getLogger(MiddleSteppingDifferentialOperator.class);

    public MiddleSteppingDifferentialOperator(double step){
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction function) {
        logger.debug("Вычисление средней разностной производной для функции");

        return new MathFunction() {
            @Override
            public double apply(double x) {
                // Средняя разностная производная: (f(x + step) - f(x - step)) / (2 * step)
                double f_x_plus_step = function.apply(x + step);
                double f_x_minus_step = function.apply(x - step);
                double derivative = (f_x_plus_step - f_x_minus_step) / (2 * step);
                logger.trace("Левая производная в точке x={}: (f({}) - f({})) / {} = {}",
                        x, x + step, x - step, 2 * step, derivative);

                return derivative;
            }
        };
    }
}
