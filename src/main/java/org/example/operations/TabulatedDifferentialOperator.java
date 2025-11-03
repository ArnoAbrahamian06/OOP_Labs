package org.example.operations;

import org.example.functions.Point;
import org.example.functions.TabulatedFunction;
import org.example.functions.factory.ArrayTabulatedFunctionFactory;
import org.example.functions.factory.TabulatedFunctionFactory;
import org.example.concurrent.SynchronizedTabulatedFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction>, Serializable {
    private TabulatedFunctionFactory factory;
    private static final Logger logger = LoggerFactory.getLogger(TabulatedDifferentialOperator.class);

    public TabulatedDifferentialOperator() {
        this.factory = new ArrayTabulatedFunctionFactory();
        logger.debug("Создан TabulatedDifferentialOperator с фабрикой по умолчанию: {}",
                factory.getClass().getSimpleName());
    }

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
        logger.debug("Создан TabulatedDifferentialOperator с фабрикой: {}",
                factory.getClass().getSimpleName());
    }

    public TabulatedFunctionFactory getFactory() {
        logger.trace("Получение фабрики: {}",
                factory.getClass().getSimpleName());
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        String oldFactory = this.factory.getClass().getSimpleName();
        String newFactory = factory.getClass().getSimpleName();

        logger.debug("Изменение фабрики: {} -> {}", oldFactory, newFactory);
        this.factory = factory;
        logger.info("Фабрика изменена на: {}", newFactory);
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        logger.info("Начало вычисления производной табличной функции. Точек: {}", function.getCount());
        Point[] points = org.example.operations.TabulatedFunctionOperationService.asPoints(function);
        int n = points.length;
        double[] xValues = new double[n];
        double[] yValues = new double[n];

        for (int i = 0; i < n; i++) {
            xValues[i] = points[i].x;
        }

        if (n == 1) {
            logger.warn("Функция содержит только одну точку. Производная будет нулевой");
            yValues[0] = 0.0;
            TabulatedFunction result = factory.create(xValues, yValues);
            logger.debug("Создана производная для функции с одной точкой");
            return result;
        }

        logger.debug("Вычисление производной для {} точек", n);

        // численное дифференцирование: центральные разности внутри, односторонние на краях
        // y'0 = (y1 - y0) / (x1 - x0)
        yValues[0] = (points[1].y - points[0].y) / (points[1].x - points[0].x);
        logger.trace("Производная в точке 0: x={}, y'={}", points[0].x, yValues[0]);


        for (int i = 1; i < n - 1; i++) {
            double dx = points[i + 1].x - points[i - 1].x;
            double dy = points[i + 1].y - points[i - 1].y;
            yValues[i] = dy / dx;
            logger.trace("Производная в точке {}: x={}, y'={}", i, points[i].x, yValues[i]);
        }

        // y'_{n-1} = (y_{n-1} - y_{n-2}) / (x_{n-1} - x_{n-2})
        yValues[n - 1] = (points[n - 1].y - points[n - 2].y) / (points[n - 1].x - points[n - 2].x);
        logger.trace("Производная в точке {}: x={}, y'={}", n-1, points[n-1].x, yValues[n-1]);

        TabulatedFunction result = factory.create(xValues, yValues);
        logger.info("Производная успешно вычислена. Создана функция с {} точками", result.getCount());

        return result;
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        logger.info("Начало синхронного вычисления производной");

        SynchronizedTabulatedFunction syncFunction;

        if (function instanceof SynchronizedTabulatedFunction) {
            syncFunction = (SynchronizedTabulatedFunction) function;
            logger.debug("Функция уже является SynchronizedTabulatedFunction");
        } else {
            logger.debug("Создание SynchronizedTabulatedFunction для: {}",
                    function.getClass().getSimpleName());
            syncFunction = new SynchronizedTabulatedFunction(function);
        }

        return syncFunction.doSynchronously(new SynchronizedTabulatedFunction.Operation<TabulatedFunction>() {
            @Override
            public TabulatedFunction apply(SynchronizedTabulatedFunction func) {
                logger.debug("Применение операции derive в синхронизированном контексте");
                return derive(func);
            }
        });
    }
}