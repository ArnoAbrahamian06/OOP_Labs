package org.example;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.MathFunction;
import org.example.functions.SqrFunction;

public class Main {
    public static void main(String[] args) {
        MathFunction function = new SqrFunction();
        System.out.println(function.apply(7));
    }
}