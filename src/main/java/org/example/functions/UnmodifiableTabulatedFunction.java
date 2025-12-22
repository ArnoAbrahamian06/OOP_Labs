package org.example.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UnmodifiableTabulatedFunction implements TabulatedFunction, Serializable {
    private static final Logger log = LoggerFactory.getLogger(UnmodifiableTabulatedFunction.class);
    private final TabulatedFunction function;

    public UnmodifiableTabulatedFunction(TabulatedFunction function) {
        log.info("Создан объект класса UnmodifiableTabulatedFunction");
        this.function = function;
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
        throw new UnsupportedOperationException("Нельзя изменять неизменяемую функцию");
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
        return function.apply(x);
    }

    @Override
    public Iterator<Point> iterator() {
        return function.iterator();
    }
}