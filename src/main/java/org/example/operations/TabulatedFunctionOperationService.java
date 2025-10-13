package org.example.operations;

import org.example.functions.*;
import org.example.functions.factory.*;
import org.example.exceptions.*;

public class TabulatedFunctionOperationService {
    private TabulatedFunctionFactory factory;

    public  TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public static Point[] asPoints(TabulatedFunction tabulatedFunction){
        int Count = tabulatedFunction.getCount();

        Point[] points = new Point[Count];

        Count = 0;

        for (Point point : tabulatedFunction){
            Point newPoint = new Point(point.x, point.y);
            points[Count] = newPoint;
            Count += 1;
        }

        return points;
    }

    private interface BiOperation {
        double apply(double u, double v);
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {

        if (a.getCount() != b.getCount()) {
            throw new InconsistentFunctionsException("Длинна a=" + a.getCount() + " и длинна b=" + b.getCount() + " Различна");
        }

        Point[] Pointa = asPoints(a);
        Point[] Pointb = asPoints(b);

        double xValues[] = new double[a.getCount()];
        double yValues[] = new double[a.getCount()];

        for (int i = 0; i < a.getCount(); i++) {
            if (Pointa[i].x != Pointb[i].x) {
                throw new InconsistentFunctionsException("Значение Xa=" + Pointa[i].x + " и Xb=" + Pointb[i].x + " различны");
            }
            xValues[i] = Pointa[i].x;
            yValues[i] = operation.apply(Pointa[i].y, Pointb[i].y);
        }

        return factory.create(xValues, yValues);
    }

    // Метод сложения
    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, new BiOperation() {
            @Override
            public double apply(double u, double v) {
                return u + v;
            }
        });
    }

    public TabulatedFunction sub(TabulatedFunction a, TabulatedFunction b) {
        return doOperation(a, b, new BiOperation() {
            @Override
            public double apply(double u, double v) {
                return u - v;
            }
        });
    }
}
