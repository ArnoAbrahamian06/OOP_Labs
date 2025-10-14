package org.example.io;

import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.operations.TabulatedDifferentialOperator;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {

    public static void main(String[] args) {
        // Создаем исходную функцию
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0}; // f(x) = x²

        LinkedListTabulatedFunction originalFunction = new LinkedListTabulatedFunction(xValues, yValues);

        // Находим производные
        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();
        TabulatedFunction firstDerivative = differentialOperator.derive(originalFunction);
        TabulatedFunction secondDerivative = differentialOperator.derive(firstDerivative);

        // Сериализация функций
        try (FileOutputStream fileOutputStream = new FileOutputStream("output/serialized linked list functions.bin");
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

            // Создаем ObjectOutputStream для сериализации
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);

            // Сериализуем все три функции
            objectOutputStream.writeObject(originalFunction);
            objectOutputStream.writeObject(firstDerivative);
            objectOutputStream.writeObject(secondDerivative);

            System.out.println("Функции успешно сериализованы в файл: output/serialized linked list functions.bin");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Десериализация функций
        try (FileInputStream fileInputStream = new FileInputStream("output/serialized linked list functions.bin");
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            // Десериализуем все три функции
            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedInputStream);
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedInputStream);

            // Выводим значения функций
            System.out.println("\nИсходная функция:");
            System.out.println(deserializedOriginal.toString());

            System.out.println("\nПервая производная:");
            System.out.println(deserializedFirstDerivative.toString());

            System.out.println("\nВторая производная:");
            System.out.println(deserializedSecondDerivative.toString());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}