package org.example.functions;

import java.util.Arrays;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction {
    private int count; // Количество x и y
    private double[] xValues; // Массив точек x
    private double[] yValues; // Массив точек y

    ArrayTabulatedFunction (double[] xValues, double[] yValues) {  // Конструктор через массивы
        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
    }

    ArrayTabulatedFunction (MathFunction source, double xFrom, double xTo, int count) {  // Конструктор через x0 и  x конечное с шагом
        if (xFrom > xTo) { // Свап в случае
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        this.count = count;
        this.xValues = new double[count];
        this.yValues = new double[count];

        double step = (xTo - xFrom) /  count; // Шаг
        xValues[0] = xFrom;   // Первый x
        yValues[0] = source.apply(xFrom);
        xValues[count-1] = xTo;  // Первый y
        yValues[count-1] = source.apply(xTo);
        for (int i = 1; i < count; i++) {
            xValues[i] = xValues[i-1] + step;
            yValues[i] = source.apply(xValues[i]);
        }
    }

    @Override
    public int getCount() {
        return count;
    }
    @Override
    public double getX(int index) {
        return xValues[index];
    }
    @Override
    public double getY(int index) {
        return yValues[index];
    }
    @Override
    public void setY(int index, double y) {
        yValues[index] = y;
    }
    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; i++) {
            if (xValues[i] == x) {
                return i;
            }
        }
        return -1;
    }
    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; i++) {
            if (yValues[i] == y) {
                return i;
            }
        }
        return -1;
    }
    @Override
    public double leftBound() {
        return xValues[0];
    }
    @Override
    public double rightBound() {
        return xValues[count - 1];
    }

    @Override
    protected int floorIndexOfX(double x){
        if (x < xValues[0]) {
            return 0;
        }
        for (int i = 1; i < count; i++) {
            if (xValues[i] >= x) {
                return i - 1;
            }
        }
        return count;
    }
    @Override
    protected double extrapolateLeft(double x) {
        return count == 1 ? yValues[0] : interpolate(x, 0);
    }

    @Override
    protected double extrapolateRight(double x) {
        return count == 1 ? yValues[0] : interpolate(x, count - 2);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (count == 1) {
            return yValues[0];
        }
        double leftX = xValues[floorIndex];
        double rightX = xValues[floorIndex + 1];
        double leftY = yValues[floorIndex];
        double rightY = yValues[floorIndex + 1];
        return interpolate(x, leftX, rightX, leftY, rightY);
    }
}
