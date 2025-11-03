package org.example.concurrent;

import org.example.functions.Point;
import org.example.functions.TabulatedFunction;
import org.example.operations.TabulatedFunctionOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedTabulatedFunction.class);
    private final TabulatedFunction function;

    public SynchronizedTabulatedFunction(TabulatedFunction function) {
        this.function = Objects.requireNonNull(function, "функция не должна быть равна null");
        logger.debug("Создан SynchronizedTabulatedFunction для функции: {}", function.getClass().getSimpleName());
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
        logger.debug("Установка значения Y[{}] = {}", index, value);
        function.setY(index, value);
        logger.debug("Значение Y[{}] успешно установлено", index);
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
        logger.debug("Вычисление функции для x = {}", x);
        double result = function.apply(x);
        logger.debug("Результат применения функции для x = {}: {}", x, result);
        return result;
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