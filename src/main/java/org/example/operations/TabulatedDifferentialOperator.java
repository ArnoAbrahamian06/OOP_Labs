package org.example.operations;

import org.example.functions.Point;
import org.example.functions.TabulatedFunction;
import org.example.functions.factory.ArrayTabulatedFunctionFactory;
import org.example.functions.factory.TabulatedFunctionFactory;
import org.example.concurrent.SynchronizedTabulatedFunction;

import java.io.Serializable;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction>, Serializable {
    private TabulatedFunctionFactory factory;

    public TabulatedDifferentialOperator() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        Point[] points = org.example.operations.TabulatedFunctionOperationService.asPoints(function);
        int n = points.length;
        double[] xValues = new double[n];
        double[] yValues = new double[n];

        for (int i = 0; i < n; i++) {
            xValues[i] = points[i].x;
        }

        if (n == 1) {
            yValues[0] = 0.0;
            return factory.create(xValues, yValues);
        }

        // численное дифференцирование: центральные разности внутри, односторонние на краях
        // y'0 = (y1 - y0) / (x1 - x0)
        yValues[0] = (points[1].y - points[0].y) / (points[1].x - points[0].x);

        for (int i = 1; i < n - 1; i++) {
            double dx = points[i + 1].x - points[i - 1].x;
            double dy = points[i + 1].y - points[i - 1].y;
            yValues[i] = dy / dx;
        }

        // y'_{n-1} = (y_{n-1} - y_{n-2}) / (x_{n-1} - x_{n-2})
        yValues[n - 1] = (points[n - 1].y - points[n - 2].y) / (points[n - 1].x - points[n - 2].x);

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        SynchronizedTabulatedFunction syncFunction;

        if (function instanceof SynchronizedTabulatedFunction) {
            syncFunction = (SynchronizedTabulatedFunction) function;
        } else {
            syncFunction = new SynchronizedTabulatedFunction(function);
        }

        return syncFunction.doSynchronously(new SynchronizedTabulatedFunction.Operation<TabulatedFunction>() {
            @Override
            public TabulatedFunction apply(SynchronizedTabulatedFunction func) {
                return derive(func);
            }
        });
    }
}