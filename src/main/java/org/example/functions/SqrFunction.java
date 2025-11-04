package org.example.functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqrFunction implements MathFunction {
    private static final Logger log = LoggerFactory.getLogger(SqrFunction.class);

    @Override
    public double apply(double x) {
        log.info("Создан объект класса SqrFunction");
        return Math.pow(x, 2);
    }
}