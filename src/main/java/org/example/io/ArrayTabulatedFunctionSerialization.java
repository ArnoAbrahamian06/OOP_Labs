package org.example.io;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.operations.TabulatedDifferentialOperator;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayTabulatedFunctionSerialization {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ArrayTabulatedFunctionSerialization.class);
        logger.info("Запуск ArrayTabulatedFunctionSerialization");

        // Создаем директорию output, если она не существует
        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Создаем исходную функцию f(x) = x^2
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0, 25.0};
        ArrayTabulatedFunction originalFunction = new ArrayTabulatedFunction(xValues, yValues);
        logger.debug("Данные функции - x: {}, y: {}", xValues, yValues);

        // Создаем оператор для вычисления производных
        TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();
        logger.debug("Создан оператор для вычисления производных");

        // Вычисляем первую и вторую производные
        logger.info("Вычисление первой производной...");
        TabulatedFunction firstDerivative = differentialOperator.derive(originalFunction);
        logger.debug("Первая производная вычислена: {} точек", firstDerivative.getCount());

        logger.info("Вычисление второй производной...");
        TabulatedFunction secondDerivative = differentialOperator.derive(firstDerivative);
        logger.debug("Вторая производная вычислена: {} точек", secondDerivative.getCount());

        // СЕРИАЛИЗАЦИЯ - записываем функции в файл
        String filename = "output/serialized array functions.bin";
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

            // Сериализуем все три функции
            FunctionsIO.serialize(bufferedOutputStream, originalFunction);
            logger.debug("Сериализована исходная функция");
            FunctionsIO.serialize(bufferedOutputStream, firstDerivative);
            logger.debug("Сериализована первая производная");
            FunctionsIO.serialize(bufferedOutputStream, secondDerivative);
            logger.debug("Сериализована вторая производная");

            logger.info("Все функции успешно сериализованы в файл: {}", filename);

        } catch (IOException e) {
            // Обрабатываем исключение - выводим стектрейс в поток ошибок
            logger.error("Ошибка ввода-вывода при сериализации в файл: {}", e.getMessage(), e);
        }

        // ДЕСЕРИАЛИЗАЦИЯ - читаем функции из файла
        try (FileInputStream fileInputStream = new FileInputStream(filename);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            logger.info("Начало десериализации из файла: {}", filename);

            // Десериализуем все три функции в том же порядке
            TabulatedFunction deserializedOriginal = FunctionsIO.deserialize(bufferedInputStream);
            logger.debug("Десериализована исходная функция: {} точек", deserializedOriginal.getCount());
            TabulatedFunction deserializedFirstDerivative = FunctionsIO.deserialize(bufferedInputStream);
            logger.debug("Десериализована первая производная: {} точек", deserializedFirstDerivative.getCount());
            TabulatedFunction deserializedSecondDerivative = FunctionsIO.deserialize(bufferedInputStream);
            logger.debug("Десериализована вторая производная: {} точек", deserializedSecondDerivative.getCount());

            logger.info("Все функции успешно десериализованы");

            // Выводим значения функций в консоль
            System.out.println("\nИсходная функция:");
            System.out.println(deserializedOriginal.toString());

            System.out.println("\nПервая производная:");
            System.out.println(deserializedFirstDerivative.toString());

            System.out.println("\nВторая производная:");
            System.out.println(deserializedSecondDerivative.toString());

        } catch (IOException | ClassNotFoundException e) {
            // Обрабатываем исключения - выводим стектрейс в поток ошибок
            logger.error("Ошибка при десериализации из файла: {}", e.getMessage(), e);
        }
        logger.info("Завершение работы ArrayTabulatedFunctionSerialization");
    }
}