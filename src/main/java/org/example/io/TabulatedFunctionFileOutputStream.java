package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(TabulatedFunctionFileOutputStream.class);
        logger.info("Запуск TabulatedFunctionFileOutputStream");

        // Создаём табулированные функции
        double[] x = {0.0, 1.0, 2.0, 3.0};
        double[] y = {0.0, 1.0, 4.0, 9.0}; // f(x) = x^2

        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(x, y);
        TabulatedFunction linkedFunc = new LinkedListTabulatedFunction(x, y);
        logger.debug("Данные функций - x: {}, y: {}", x, y);


        try (
                BufferedOutputStream bufOut1 = new BufferedOutputStream(new FileOutputStream("output/array function.bin"));
                BufferedOutputStream bufOut2 = new BufferedOutputStream(new FileOutputStream("output/linked list function.bin"))
        ) {
            logger.info("Начало записи функций в бинарные файлы");

            FunctionsIO.writeTabulatedFunction(bufOut1, arrayFunc);
            logger.info("Функция arrayFunc записана в файл output/array function.bin");

            FunctionsIO.writeTabulatedFunction(bufOut2, linkedFunc);
            logger.info("Функция linkedFunc записана в файл output/linked list function.bin");

            logger.info("Все файлы успешно записаны в папку output/");
        } catch (IOException e) {
            logger.error("Ошибка ввода-вывода при записи функций в файлы", e);
        }
        logger.info("Завеершение работы TabulatedFunctionFileOutputStream");
    }
}