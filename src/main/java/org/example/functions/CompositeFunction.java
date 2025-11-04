package org.example.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeFunction implements MathFunction {
    private final MathFunction firstFunction;
    private final MathFunction secondFunction;
    private static final Logger log = LoggerFactory.getLogger(CompositeFunction.class);


    public CompositeFunction(MathFunction first, MathFunction second) {
        this.firstFunction = first;
        this.secondFunction = second;
        log.info("Создан CompositeFunction");
        log.trace("firstFunction класс: {}", firstFunction.getClass().getName());
        log.trace("secondFunction класс: {}", secondFunction.getClass().getName());
    }

    @Override
    public double apply(double x) {
        return secondFunction.apply(firstFunction.apply(x));
    }
}