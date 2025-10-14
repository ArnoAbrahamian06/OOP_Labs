package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.factory.*;
import java.io.*;


public class TabulatedFunctionFileReader {

    public static void main(String[] args) {
        // Создаем директорию input, если она не существует
        File inputDir = new File("input");
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }

        // Создаем фабрики для разных типов функций
        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();

        // Используем одну конструкцию try-with-resources для обоих потоков
        try (FileReader fileReader1 = new FileReader("input/function.txt");
             FileReader fileReader2 = new FileReader("input/function.txt");
             BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
             BufferedReader bufferedReader2 = new BufferedReader(fileReader2)) {

            // Читаем функции разных типов из одного файла
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(bufferedReader1, arrayFactory);
            TabulatedFunction linkedListFunction = FunctionsIO.readTabulatedFunction(bufferedReader2, linkedListFactory);

            // Выводим функции в консоль
            System.out.println("ArrayTabulatedFunction:");
            System.out.println(arrayFunction.toString());
            System.out.println();

            System.out.println("LinkedListTabulatedFunction:");
            System.out.println(linkedListFunction.toString());

        } catch (IOException e) {
            // Обрабатываем исключение - выводим стектрейс в поток ошибок
            e.printStackTrace();
        }
    }
}