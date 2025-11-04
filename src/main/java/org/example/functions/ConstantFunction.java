package org.example.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantFunction implements MathFunction {
    private final double constant;
    private static final Logger log = LoggerFactory.getLogger(ConstantFunction.class);

    public ConstantFunction(double x) {
        this.constant = x;
        log.info("создан объект класса ConstantFunction");
    }

    @Override
    public double apply(double x){
        return constant;
    }
}
