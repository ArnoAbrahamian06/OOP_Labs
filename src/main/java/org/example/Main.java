package org.example;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.MathFunction;

public class Main {
    public static void main(String[] args) {
        double[] x1 = {0, 1, 2, 3};
        double[] y1 = {0, 2, 4, 6};
        ArrayTabulatedFunction inner = new ArrayTabulatedFunction(x1, y1);

        double[] x2 = {0, 2, 4, 6};
        double[] y2 = {0, 1, 4, 9};
        ArrayTabulatedFunction outer = new ArrayTabulatedFunction(x2, y2);

        MathFunction composition = inner.andThen(outer);

        // Проверка точек из исходной области определения
        composition.apply(0.0);
    }
}