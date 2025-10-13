package org.example.functions;

import org.example.exceptions.ArrayIsNotSortedException;
import org.example.exceptions.DifferentLengthOfArraysException;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {

    public abstract int getCount();
    public abstract double getX(int index);
    public abstract double getY(int index);
    public abstract void setY(int index, double y);
    public abstract int indexOfX(double x);
    public abstract double leftBound();
    public abstract double rightBound();

    // Защищённые абстрактные методы для интерполяции и экстраполяции
    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x, int floorIndex);

    // Общий метод интерполяции по значениям
    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        if (Math.abs(rightX - leftX) < 1e-10) {
            throw new IllegalArgumentException("Интервал интерполяции не может быть нулевым: leftX = " + leftX + ", rightX = " + rightX);
        }
        return leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
    }

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues){
        if(xValues.length != yValues.length){
            throw new DifferentLengthOfArraysException("Длинна X и Y различна");
        }
    }
    public static void checkSorted(double[] xValues){
        for(int i = 0; i < xValues.length - 1; i++){
            if(xValues[i] > xValues[i+1]){
                throw new ArrayIsNotSortedException("Массив X не отсортирован");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" size = ");
        sb.append(getCount());

        for (Point point : this) {
            sb.append("\n[");
            sb.append(point.x);
            sb.append("; ");
            sb.append(point.y);
            sb.append("]");
        }

        return sb.toString();
    }

    // Реализация метода apply из MathFunction
    @Override
    public double apply(double x) {
        if (x < leftBound()) {
            return extrapolateLeft(x);
        } else if (x > rightBound()) {
            return extrapolateRight(x);
        } else {
            int index = indexOfX(x);
            if (index != -1) {
                return getY(index);
            } else {
                int floorIndex = floorIndexOfX(x);
                return interpolate(x, floorIndex);
            }
        }
    }
}