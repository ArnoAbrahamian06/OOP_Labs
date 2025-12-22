package org.example.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class StrictTabulatedFunction implements TabulatedFunction, Serializable {
    private static final Logger log = LoggerFactory.getLogger(StrictTabulatedFunction.class);
    private final TabulatedFunction function;

    public StrictTabulatedFunction(TabulatedFunction function) {
        this.function = function;
        log.info("Создан объект класса StrictTabulatedFunction");
    }

    @Override
    public int getCount() {
        return function.getCount();
    }

    @Override
    public double getX(int index) {
        return function.getX(index);
    }

    @Override
    public double getY(int index) {
        return function.getY(index);
    }

    @Override
    public void setY(int index, double value) {
        function.setY(index, value);
    }

    @Override
    public int indexOfX(double x) {
        return function.indexOfX(x);
    }

    @Override
    public int indexOfY(double y) {
        return function.indexOfY(y);
    }

    @Override
    public double leftBound() {
        return function.leftBound();
    }

    @Override
    public double rightBound() {
        return function.rightBound();
    }

    @Override
    public double apply(double x) {
        int index = function.indexOfX(x);
        if (index == -1) {
            throw new UnsupportedOperationException();
        }
        return function.getY(index);
    }

    @Override
    public java.util.Iterator<Point> iterator() {
        return function.iterator();
    }
}


