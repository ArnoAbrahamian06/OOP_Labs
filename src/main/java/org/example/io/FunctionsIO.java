package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.factory.TabulatedFunctionFactory;
import org.example.functions.Point;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FunctionsIO {

    private static final Logger logger = LoggerFactory.getLogger(FunctionsIO.class);

    private FunctionsIO() {
        throw new UnsupportedOperationException();
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream)
            throws IOException, ClassNotFoundException {
        logger.debug("Начало десериализации функции из потока");
        ObjectInputStream objectInputStream = new ObjectInputStream(stream);
        TabulatedFunction function = (TabulatedFunction) objectInputStream.readObject();
        logger.debug("Функция успешно десериализована, количество точек: {}", function.getCount());
        return function;
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function){
        logger.debug("Запись табличной функции в текстовый формат, точек: {}", function.getCount());
        PrintWriter printWriter = new PrintWriter(writer);

        // Записываем количество точек
        printWriter.println(function.getCount());
        logger.trace("Записано количество точек: {}", function.getCount());

        // Записываем точки в цикле for-each
        int pointCount = 0;
        for (Point point : function) {
            printWriter.printf("%f %f\n", point.x, point.y);
            pointCount++;
            logger.trace("Записана точка #{}: ({}, {})", pointCount, point.x, point.y);
        }

        // Сбрасываем буфер, но не закрываем поток
        printWriter.flush();
        logger.debug("Успешно записано {} точек в текстовый формат", pointCount);
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        logger.debug("Запись табличной функции в бинарный формат, точек: {}", function.getCount());
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(function.getCount());
        logger.trace("Записано количество точек: {}", function.getCount());

        int pointCount = 0;
        for (Point point : function) {
            dataOutputStream.writeDouble(point.x);
            dataOutputStream.writeDouble(point.y);
            pointCount++;
            logger.trace("Записана точка #{}: ({}, {})", pointCount, point.x, point.y);
        }

        dataOutputStream.flush();
        logger.debug("Успешно записано {} точек в бинарный формат", pointCount);
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory)
            throws IOException {
        logger.debug("Чтение табличной функции из текстового потока");
        if (reader == null || factory == null) {
            String errorMsg = "Reader и factory не могут быть null";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Создаем форматтер для чисел с запятой в качестве разделителя
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

        // Читаем количество точек
        String countLine = reader.readLine();
        if (countLine == null) {
            String errorMsg = "Неожиданный конец потока - невозможно прочитать количество точек";
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }

        int count;
        try {
            count = Integer.parseInt(countLine.trim());
            logger.trace("Прочитано количество точек: {}", count);
        } catch (NumberFormatException e) {
            String errorMsg = "Неверный формат количества точек: " + countLine;
            logger.error(errorMsg, e);
            throw new IOException(errorMsg, e);
        }

        if (count < 2) {
            String errorMsg = "Количество точек должно быть не менее 2: " + count;
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }

        double[] xValues = new double[count];
        double[] yValues = new double[count];
        logger.debug("Созданы массивы для {} точек", count);

        // Читаем точки
        for (int i = 0; i < count; i++) {
            String pointLine = reader.readLine();
            if (pointLine == null) {
                String errorMsg = String.format("Неожиданный конец потока при чтении точки %d", i);
                logger.error(errorMsg);
                throw new IOException(errorMsg);
            }

            // Разбиваем строку по пробелу
            String[] parts = pointLine.trim().split(" ");
            if (parts.length != 2) {
                String errorMsg = String.format("Неверный формат точки: '%s'. Ожидалось 2 значения через пробел", pointLine);
                logger.error(errorMsg);
                throw new IOException(errorMsg);
            }

            try {
                // Парсим числа с запятой в качестве разделителя
                Number xNumber = numberFormat.parse(parts[0]);
                Number yNumber = numberFormat.parse(parts[1]);
                xValues[i] = xNumber.doubleValue();
                yValues[i] = yNumber.doubleValue();
                logger.trace("Прочитана точка #{}: ({}, {})", i, xValues[i], yValues[i]);
            } catch (ParseException e) {
                String errorMsg = "Неверный формат числа в точке: " + pointLine;
                logger.error(errorMsg, e);
                throw new IOException(errorMsg, e);
            }
        }
        logger.debug("Успешно создана функция из {} точек", count);
        return factory.create(xValues, yValues);
    }

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException {
        logger.debug("Чтение табличной функции из бинарного потока");
        DataInputStream stream = new DataInputStream(inputStream);
        int length = stream.readInt();
        logger.trace("Прочитано количество точек: {}", length);

        double[] xValues = new double[length];
        double[] yValues = new double[length];

        for (int i = 0; i < length; ++i) {
            xValues[i] = stream.readDouble();
            yValues[i] = stream.readDouble();
            logger.trace("Прочитана точка #{}: ({}, {})", i, xValues[i], yValues[i]);
        }
        logger.debug("Успешно создана функция из {} точек", length);
        return factory.create(xValues, yValues);
    }

    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        logger.debug("Сериализация функции, количество точек: {}", function.getCount());
        if (stream == null || function == null) {
            String errorMsg = "Поток или функция не может быть null";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Создаем ObjectOutputStream для сериализации объекта
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);

        // Сериализуем функцию
        objectOutputStream.writeObject(function);
        logger.trace("Функция сериализована в ObjectOutputStream");

        // Сбрасываем буфер, но не закрываем поток
        objectOutputStream.flush();
        logger.debug("Сериализация завершена успешно");
    }
}
