package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TabulatedFunctionFileWriter {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(TabulatedFunctionFileWriter.class);
        logger.info("Запуск TabulatedFunctionFileWriter");

        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0}; // f(x) = x^2

        logger.info("Создание arrayFunction");
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        logger.info("Создание linkedListFunction");
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);
        logger.debug("Данные функций - x: {}, y: {}", xValues, yValues);

        try (FileWriter fileWriter1 = new FileWriter("output/array_function.txt");
             FileWriter fileWriter2 = new FileWriter("output/linked_list_function.txt");
             BufferedWriter bufferedWriter1 = new BufferedWriter(fileWriter1);
             BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2)) {
            logger.info("Начало записывания функций в файлы: 'output/array_function.txt' и " +
                    "'output/linked_list_function.txt'");

            // Записываем функции в соответствующие файлы
            logger.info("Запись ArrayTabulatedFunction в файл array_function.txt");
            FunctionsIO.writeTabulatedFunction(bufferedWriter1, arrayFunction);
            logger.debug("ArrayTabulatedFunction записана. Количество точек: {}", arrayFunction.getCount());

            logger.info("Запись LinkedListTabulatedFunction в файл linked_list_function.txt");
            FunctionsIO.writeTabulatedFunction(bufferedWriter2, linkedListFunction);
            logger.debug("LinkedListTabulatedFunction записана. Количество точек: {}", linkedListFunction.getCount());

            logger.info("Обе функции успешно записаны в текстовые файлы");

        }
        catch (IOException e) {
            logger.error("Ошибка ввода-вывода при записи функций в текстовые файлы", e);
        }
        logger.info("Завершение работы TabulatedFunctionFileWriter");
    }
}