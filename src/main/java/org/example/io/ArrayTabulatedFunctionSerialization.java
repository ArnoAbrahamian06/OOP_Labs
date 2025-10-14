package org.example.io;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.operations.TabulatedDifferentialOperator;
import java.io.*;


public class ArrayTabulatedFunctionSerialization {

    public static void main(String[] args) {
        // Создаем директорию output, если она не существует
        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Создаем исходную функцию f(x) = x^2
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0, 25.0};
        ArrayTabulatedFunction originalFunction = new ArrayTabulatedFunction(xValues, yValues);

        // Создаем оператор для вычисления производных
        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();

        // Вычисляем первую и вторую производные
        TabulatedFunction firstDerivative = differentialOperator.derive(originalFunction);
        TabulatedFunction secondDerivative = differentialOperator.derive(firstDerivative);

        // СЕРИАЛИЗАЦИЯ - записываем функции в файл
        try (FileOutputStream fileOutputStream = new FileOutputStream("output/serialized array functions.bin");
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

            // Сериализуем все три функции
            FunctionsIO.serialize(bufferedOutputStream, originalFunction);
            FunctionsIO.serialize(bufferedOutputStream, firstDerivative);
            FunctionsIO.serialize(bufferedOutputStream, secondDerivative);

            System.out.println("Функции успешно сериализованы в файл: output/serialized array functions.bin");

        } catch (IOException e) {
            // Обрабатываем исключение - выводим стектрейс в поток ошибок
            e.printStackTrace();
        }

        // ДЕСЕРИАЛИЗАЦИЯ - читаем функции из файла
        try (FileInputStream fileInputStream = new FileInputStream("output/serialized array functions.bin");
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            // Десериализуем все три функции в том же порядке
            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedInputStream);

            // Выводим значения функций в консоль
            System.out.println("\nИсходная функция:");
            System.out.println(deserializedOriginal.toString());

            System.out.println("\nПервая производная:");
            System.out.println(deserializedFirstDerivative.toString());

            System.out.println("\nВторая производная:");
            System.out.println(deserializedSecondDerivative.toString());

        } catch (IOException | ClassNotFoundException e) {
            // Обрабатываем исключения - выводим стектрейс в поток ошибок
            e.printStackTrace();
        }
    }
}