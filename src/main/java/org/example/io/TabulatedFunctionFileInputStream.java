package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.factory.*;
import org.example.operations.TabulatedDifferentialOperator;

import java.io.*;

public class TabulatedFunctionFileInputStream {

    public static void main(String[] args) {
        // Часть 1: Чтение бинарной функции из файла
        File binaryFile = new File("input/binary function.bin");

        // Проверяем существование файла
        if (!binaryFile.exists()) {
            System.err.println("Файл 'input/binary function.bin' не существует!");
            System.err.println("Сначала запустите TabulatedFunctionFileOutputStream для создания файлов");
            return;
        }

        // Проверяем, что файл не пуст
        if (binaryFile.length() == 0) {
            System.err.println("Файл 'input/binary function.bin' пуст!");
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(binaryFile);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {

            TabulatedFunction function = FunctionsIO.readTabulatedFunction(
                    bufferedInputStream,
                    new ArrayTabulatedFunctionFactory()
            );
            System.out.println("Функция из файла:");
            System.out.println(function.toString());

        } catch (EOFException e) {
            System.err.println("Ошибка: Неожиданный конец файла. Возможно файл поврежден или имеет неверный формат.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода при чтении файла:");
            e.printStackTrace();
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

            TabulatedFunction consoleFunction = FunctionsIO.readTabulatedFunction(
                    bufferedReader,
                    new LinkedListTabulatedFunctionFactory()
            );

            TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();
            TabulatedFunction derivative = differentialOperator.derive(consoleFunction);

            System.out.println("Производная функции:");
            System.out.println(derivative.toString());

        } catch (IOException e) {
            System.err.println("Ошибка при чтении данных из консоли:");
            e.printStackTrace();
        }
    }
}