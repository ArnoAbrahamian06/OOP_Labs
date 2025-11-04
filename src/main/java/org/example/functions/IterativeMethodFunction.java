package org.example.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IterativeMethodFunction implements MathFunction {
    private static final Logger log = LoggerFactory.getLogger(IterativeMethodFunction.class);
    private final MathFunction phi;
    private final int maxIterations;
    private final double tolerance;

    public IterativeMethodFunction(MathFunction phi, int maxIterations, double tolerance) {
        this.phi = phi;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
    }

    // Конструктор с дефолтными параметрами
    public IterativeMethodFunction(MathFunction phi) {
        this(phi, 1000, 1e-10);
    }

    @Override
    public double apply(double x0) {
        double xCurrent = x0;
        double xNext;
        int iterations = 0;

        do {
            xNext = phi.apply(xCurrent);
            if (Math.abs(xNext - xCurrent) < tolerance) {
        log.debug("Итерационный метод сошёлся за {} итераций: x*={} (старт={})", iterations, xNext, x0);
                return xNext;
            }
            xCurrent = xNext;
            iterations++;
        } while (iterations < maxIterations);

        log.warn("Итерационный метод не сошёлся за maxIterations={}, последнее x={} (старт={})", maxIterations, xNext, x0);
        return xNext;
    }
}