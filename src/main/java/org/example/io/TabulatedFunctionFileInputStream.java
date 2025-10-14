package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.factory.*;
import org.example.operations.TabulatedDifferentialOperator;

import java.io.*;

public class TabulatedFunctionFileInputStream {
    public static void main(String[] args) {
        try (
                FileInputStream arrayFunctionReader = new FileInputStream("input/binary function.bin");
                BufferedInputStream bufferedArrayFunctionReader = new BufferedInputStream(arrayFunctionReader);
        ) {
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(bufferedArrayFunctionReader, new ArrayTabulatedFunctionFactory());
            System.out.println(arrayFunction);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStreamReader streamReader = new InputStreamReader(System.in);
        System.out.print("Введите размер и значения функции: ");
        BufferedReader reader = new BufferedReader(streamReader);
        try {
            TabulatedFunction linkedListFunctionFromConsole = FunctionsIO.readTabulatedFunction(reader, new LinkedListTabulatedFunctionFactory());
            System.out.println(new TabulatedDifferentialOperator().derive(linkedListFunctionFromConsole));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}