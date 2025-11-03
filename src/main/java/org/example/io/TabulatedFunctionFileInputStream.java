package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.factory.*;
import org.example.operations.TabulatedDifferentialOperator;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabulatedFunctionFileInputStream {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(TabulatedFunctionFileInputStream.class);
        logger.info("Запуск TabulatedFunctionFileInputStream");

        // Часть 1: Чтение бинарной функции из файла
        File binaryFile = new File("input/binary function.bin");

        // Проверяем существование файла
        if (!binaryFile.exists()) {
            logger.error("Файл '{}' не существует! Сначала запустите TabulatedFunctionFileOutputStream для создания файлов",
                binaryFile.getAbsolutePath());
            return;
        }

        // Проверяем, что файл не пуст
        if (binaryFile.length() == 0) {
            logger.error("Файл {} пуст!", binaryFile.getAbsolutePath());
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(binaryFile);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            logger.info("Начало чтения функции из бинарного файла: {}", binaryFile.getName());

            TabulatedFunction function = FunctionsIO.readTabulatedFunction(
                    bufferedInputStream,
                    new ArrayTabulatedFunctionFactory()
            );

            logger.info("Функция успешно загружена из файла, количество точек: {}", function.getCount());

            System.out.println("Функция из файла:");
            System.out.println(function.toString());

        } catch (EOFException e) {
            logger.error("Неожиданный конец файла {}. Файл поврежден или имеет неверный формат",
                    binaryFile.getAbsolutePath(), e);
        } catch (IOException e) {
            logger.error("Ошибка ввода-вывода при чтении файла {}", binaryFile.getAbsolutePath(), e);
        }

        // Часть 2: Чтение функции из консоли
        System.out.println("\nВведите размер и значения функции");
        System.out.println("Формат:");
        System.out.println("количество_точек");
        System.out.println("x1 y1");
        System.out.println("x2 y2");
        System.out.println("...");

        try (InputStreamReader inputStreamReader = new InputStreamReader(System.in);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            logger.debug("Открыты потоки для чтения из консоли");

            TabulatedFunction consoleFunction = FunctionsIO.readTabulatedFunction(
                    bufferedReader,
                    new LinkedListTabulatedFunctionFactory()
            );

            logger.info("Функция успешно прочитана из консоли, количество точек: {}",
                    consoleFunction.getCount());

            TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();
            logger.debug("Создан оператор дифференцирования");

            TabulatedFunction derivative = differentialOperator.derive(consoleFunction);
            logger.info("Вычислена производная функции, количество точек: {}", derivative.getCount());


            System.out.println("Производная функции:");
            System.out.println(derivative.toString());

        } catch (IOException e) {
            logger.error("Ошибка при чтении данных из консоли", e);
        }
        logger.info("Завершение работы TabulatedFunctionFileInputStream");
    }
}