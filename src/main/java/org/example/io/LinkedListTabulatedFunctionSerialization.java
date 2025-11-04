package org.example.io;

import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.operations.TabulatedDifferentialOperator;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedListTabulatedFunctionSerialization {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(LinkedListTabulatedFunctionSerialization.class);
        logger.info("Запуск LinkedListTabulatedFunctionSerialization");
        // Создаем исходную функцию
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0}; // f(x) = x²

        LinkedListTabulatedFunction originalFunction = new LinkedListTabulatedFunction(xValues, yValues);
        logger.debug("Данные функции - x: {}, y: {}", xValues, yValues);

        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();
        logger.debug("Создан оператор для вычисления производных");

        // Находим производные
        logger.info("Вычисление первой производной...");
        TabulatedFunction firstDerivative = differentialOperator.derive(originalFunction);
        logger.debug("Первая производная вычислена: {} точек", firstDerivative.getCount());

        logger.info("Вычисление второй производной...");
        TabulatedFunction secondDerivative = differentialOperator.derive(firstDerivative);
        logger.debug("Вторая производная вычислена: {} точек", secondDerivative.getCount());

        // Сериализация функций
        String filename = "output/serialized linked list functions.bin";
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

            // Создаем ObjectOutputStream для сериализации
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);

            // Сериализуем все три функции
            objectOutputStream.writeObject(originalFunction);
            logger.debug("Сериализована исходная функция");
            objectOutputStream.writeObject(firstDerivative);
            logger.debug("Сериализована первая производная");
            objectOutputStream.writeObject(secondDerivative);
            logger.debug("Сериализована вторая производная");

            logger.info("Все функции успешно сериализованы в файл: {}", filename);

        } catch (IOException e) {
            logger.error("Ошибка ввода-вывода при сериализации в файл: {}", e.getMessage(), e);
        }

        // Десериализация функций
        try (FileInputStream fileInputStream = new FileInputStream(filename);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            logger.info("Начало десериализации из файла: {}", filename);

            // Десериализуем все три функции
            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(bufferedInputStream);
            logger.debug("Десериализована исходная функция: {} точек", deserializedOriginal.getCount());
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedInputStream);
            logger.debug("Десериализована первая производная: {} точек", deserializedFirstDerivative.getCount());
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedInputStream);
            logger.debug("Десериализована вторая производная: {} точек", deserializedSecondDerivative.getCount());

            logger.info("Все функции успешно десериализованы");

            // Выводим значения функций
            System.out.println("\nИсходная функция:");
            System.out.println(deserializedOriginal.toString());

            System.out.println("\nПервая производная:");
            System.out.println(deserializedFirstDerivative.toString());

            System.out.println("\nВторая производная:");
            System.out.println(deserializedSecondDerivative.toString());

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Ошибка при десериализации из файла: {}", e.getMessage(), e);
        }
        logger.info("Завершение работы LinkedListTabulatedFunctionSerialization");
    }
}