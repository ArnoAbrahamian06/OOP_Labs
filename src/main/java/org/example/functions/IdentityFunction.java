package org.example.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentityFunction implements MathFunction {
    private static final Logger log = LoggerFactory.getLogger(IdentityFunction.class);
    @Override
    public double apply(double x) {
        log.info("создан объект класса ConstantFunction");
        return x;
    }
}

