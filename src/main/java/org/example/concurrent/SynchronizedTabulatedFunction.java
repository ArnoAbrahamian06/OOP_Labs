package org.example.concurrent;

import org.example.functions.Point;
import org.example.functions.TabulatedFunction;
import org.example.operations.TabulatedFunctionOperationService;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction function;

    public SynchronizedTabulatedFunction(TabulatedFunction function) {
        this.function = Objects.requireNonNull(function, "функция не должна быть равна null");
    }

    @Override
    public synchronized int getCount() {
        return function.getCount();
    }

    @Override
    public synchronized double getX(int index) {
        return function.getX(index);
    }

    @Override
    public synchronized double getY(int index) {
        return function.getY(index);
    }

    @Override
    public synchronized void setY(int index, double value) {
        function.setY(index, value);
    }

    @Override
    public synchronized int indexOfX(double x) {
        return function.indexOfX(x);
    }

    @Override
    public synchronized int indexOfY(double y) {
        return function.indexOfY(y);
    }

    @Override
    public synchronized double leftBound() {
        return function.leftBound();
    }

    @Override
    public synchronized double rightBound() {
        return function.rightBound();
    }

    @Override
    public synchronized double apply(double x) {
        return function.apply(x);
    }

    @Override
    public boolean equals(Object obj) {
        synchronized (this) {
            return function.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        synchronized (this) {
            return function.hashCode();
        }
    }

    @Override
    public String toString() {
        synchronized (this) {
            return function.toString();
        }
    }

    @Override
    public Iterator<Point> iterator() {
        Point[] pointsCopy;
        synchronized (this) {
            pointsCopy = TabulatedFunctionOperationService.asPoints(function);
        }
        return new Iterator<Point>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < pointsCopy.length;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return pointsCopy[index++];
            }
        };
    }
}