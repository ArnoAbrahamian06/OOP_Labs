package org.example.operations;

import org.example.functions.*;
import org.example.functions.factory.*;
import org.example.exceptions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabulatedFunctionOperationService {
    private TabulatedFunctionFactory factory;
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionOperationService.class);

    public  TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
        logger.debug("Создан TabulatedFunctionOperatorService с фабрикой по умолчанию: {}",
                factory.getClass().getSimpleName());
    }

    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
        logger.debug("Создан TabulatedFunctionOperatorService с фабрикой: {}",
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

    public static Point[] asPoints(TabulatedFunction tabulatedFunction){
        logger.debug("Начало преобразования функции в массив точек");

        int count = tabulatedFunction.getCount();
        logger.trace("Количество точек в функции: {}", count);

        Point[] points = new Point[count];

        count = 0;

        for (Point point : tabulatedFunction){
            Point newPoint = new Point(point.x, point.y);
            points[count] = newPoint;
            logger.trace("Точка {}: x={}, y={}", count, point.x, point.y);
            count += 1;
        }

        logger.debug("Функция успешно преобразована в {} точек", count);

        return points;
    }

    private interface BiOperation {
        double apply(double u, double v);
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        logger.info("Начало операции над функциями");
        logger.debug("Функция A: {} точек, функция B: {} точек",
                a.getCount(), b.getCount());

        if (a.getCount() != b.getCount()) {
            String errorMsg = String.format("Длина a=%d и длина b=%d различна", a.getCount(), b.getCount());
            logger.error(errorMsg);
            throw new InconsistentFunctionsException(errorMsg);
        }

        Point[] PointA = asPoints(a);
        Point[] PointB = asPoints(b);

        double xValues[] = new double[a.getCount()];
        double yValues[] = new double[a.getCount()];

        for (int i = 0; i < a.getCount(); i++) {
            if (PointA[i].x != PointB[i].x) {
                String errorMsg = String.format("Значение Xa=%.6f и Xb=%.6f различны в точке %d",
                        PointA[i].x, PointB[i].x, i);
                logger.error(errorMsg);
                throw new InconsistentFunctionsException(errorMsg);
            }
            xValues[i] = PointA[i].x;
            double result = operation.apply(PointA[i].y, PointB[i].y);
            yValues[i] = result;
            logger.trace("Точка {}: x={}, y1={}, y2={}, результат={}",
                    i, xValues[i], PointA[i].y, PointB[i].y, result);
        }

        TabulatedFunction result = factory.create(xValues, yValues);
        logger.info("Операция завершена успешно. Создана функция с {} точками",
                result.getCount());
        return result;
    }

    // Метод сложения
    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        logger.debug("Вызов операции сложения функций");

        return doOperation(a, b, new BiOperation() {
            @Override
            public double apply(double u, double v) {
                double result = u + v;
                logger.trace("Сложение: {} + {} = {}", u, v, result);
                return result;
            }
        });
    }

    // Метод вычитания
    public TabulatedFunction sub(TabulatedFunction a, TabulatedFunction b) {
        logger.debug("Вызов операции вычитания функций");

        return doOperation(a, b, new BiOperation() {
            @Override
            public double apply(double u, double v) {
                double result = u - v;
                logger.trace("Вычитание: {} - {} = {}", u, v, result);
                return result;
            }
        });
    }

    // Метод умножения
    public TabulatedFunction mult (TabulatedFunction a, TabulatedFunction b) {
        logger.debug("Вызов операции умножения функций");

        return doOperation(a, b, new BiOperation() {
            @Override
            public double apply(double u, double v) {
                double result = u * v;
                logger.trace("Умножение: {} * {} = {}", u, v, result);
                return result;
            }
        });
    }

    // Метод деления
    public TabulatedFunction div(TabulatedFunction a, TabulatedFunction b) {
        logger.debug("Вызов операции деления функций");

        return doOperation(a, b, new BiOperation() {
            @Override
            public double apply(double u, double v) {
                if (v == 0.0) {
                    logger.warn("Попытка деления на ноль: {} / {}", u, v);
                }
                double result = u / v;
                logger.trace("Деление: {} / {} = {}", u, v, result);
                return result;
            }
        });
    }
}
