package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.factory.*;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TabulatedFunctionFileReader {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(TabulatedFunctionFileReader.class);
        logger.info("Запуск TabulatedFunctionFileReader");

        // Создаем директорию input, если она не существует
        File inputDir = new File("input");
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }

        // Создаем фабрики для разных типов функций
        TabulatedFunctionFactory arrayFactory = new ArrayTabulatedFunctionFactory();
        logger.debug("Создание фабрики arrayFactory");

        TabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();
        logger.debug("Создание фабрики linkedListFactory");

        // Используем одну конструкцию try-with-resources для обоих потоков
        try (FileReader fileReader1 = new FileReader("input/function.txt");
             FileReader fileReader2 = new FileReader("input/function.txt");
             BufferedReader bufferedReader1 = new BufferedReader(fileReader1);
             BufferedReader bufferedReader2 = new BufferedReader(fileReader2)) {
            logger.info("Начало чтения функций из файла function.txt");

            // Читаем функции разных типов из одного файла
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(bufferedReader1, arrayFactory);
            logger.debug("ArrayTabulatedFunction успешно прочитана, количество точек: {}",
                    arrayFunction.getCount());

            TabulatedFunction linkedListFunction = FunctionsIO.readTabulatedFunction(bufferedReader2, linkedListFactory);
            logger.debug("LinkedListTabulatedFunction успешно прочитана, количество точек: {}",
                    linkedListFunction.getCount());

            // Выводим функции в консоль
            System.out.println("ArrayTabulatedFunction:");
            System.out.println(arrayFunction.toString());
            System.out.println();

            System.out.println("LinkedListTabulatedFunction:");
            System.out.println(linkedListFunction.toString());

            logger.info("Обе функции успешно прочитаны и выведены в консоль");

        } catch (IOException e) {
            // Обрабатываем исключение - выводим стектрейс в поток ошибок
            logger.error("Ошибка ввода-вывода при чтении файла input/function.txt", e);
        }
        logger.info("Завершение работы TabulatedFunctionFileReader");
    }
}