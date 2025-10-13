package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;
import java.io.*;


public class TabulatedFunctionFileWriter {

    public static void main(String[] args) {
        // Создаем директорию output, если она не существует
        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Создаем две табулированные функции
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0}; // f(x) = x²

        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        // Используем одну конструкцию try-with-resources для обоих потоков
        try (FileWriter arrayWriter = new FileWriter("output/array function.txt");
             FileWriter linkedListWriter = new FileWriter("output/linked list function.txt");
             BufferedWriter bufferedArrayWriter = new BufferedWriter(arrayWriter);
             BufferedWriter bufferedLinkedListWriter = new BufferedWriter(linkedListWriter)) {

            // Записываем функции в соответствующие файлы
            FunctionsIO.writeTabulatedFunction(bufferedArrayWriter, arrayFunction);
            FunctionsIO.writeTabulatedFunction(bufferedLinkedListWriter, linkedListFunction);

            System.out.println("Функции успешно записаны в файлы:");
            System.out.println("- output/array function.txt");
            System.out.println("- output/linked list function.txt");

        } catch (IOException e) {
            // Обрабатываем исключение - выводим стектрейс в поток ошибок
            e.printStackTrace();
        }
    }
}