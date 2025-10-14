package org.example.io;

import org.example.functions.TabulatedFunction;
import org.example.functions.factory.TabulatedFunctionFactory;
import org.example.functions.Point;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class FunctionsIO {

    private FunctionsIO() {
        throw new UnsupportedOperationException();
    }

    public static TabulatedFunction deserialize(BufferedInputStream stream)
            throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(stream);
        TabulatedFunction function = (TabulatedFunction) objectInputStream.readObject();
        return function;
    }

    public static void writeTabulatedFunction(BufferedWriter writer, TabulatedFunction function){
        PrintWriter printWriter = new PrintWriter(writer);

        // Записываем количество точек
        printWriter.println(function.getCount());

        // Записываем точки в цикле for-each
        for (Point point : function) {
            printWriter.printf("%f %f\n", point.x, point.y);
        }

        // Сбрасываем буфер, но не закрываем поток
        printWriter.flush();
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        dataOutputStream.writeInt(function.getCount());

        for (Point point : function) {
            dataOutputStream.writeDouble(point.x);
            dataOutputStream.writeDouble(point.y);
        }

        dataOutputStream.flush();
    }

    public static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory)
            throws IOException {
        if (reader == null || factory == null) {
            throw new IllegalArgumentException("Reader and factory cannot be null");
        }

        // Создаем форматтер для чисел с запятой в качестве разделителя
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

        // Читаем количество точек
        String countLine = reader.readLine();
        if (countLine == null) {
            throw new IOException("Unexpected end of stream - cannot read count");
        }

        int count;
        try {
            count = Integer.parseInt(countLine.trim());
        } catch (NumberFormatException e) {
            throw new IOException("Invalid count format: " + countLine, e);
        }

        if (count < 2) {
            throw new IOException("Count must be at least 2: " + count);
        }

        double[] xValues = new double[count];
        double[] yValues = new double[count];

        // Читаем точки
        for (int i = 0; i < count; i++) {
            String pointLine = reader.readLine();
            if (pointLine == null) {
                throw new IOException("Unexpected end of stream at point " + i);
            }

            // Разбиваем строку по пробелу
            String[] parts = pointLine.trim().split(" ");
            if (parts.length != 2) {
                throw new IOException("Invalid point format: " + pointLine +
                        ". Expected two values separated by space");
            }

            try {
                // Парсим числа с запятой в качестве разделителя
                Number xNumber = numberFormat.parse(parts[0]);
                Number yNumber = numberFormat.parse(parts[1]);
                xValues[i] = xNumber.doubleValue();
                yValues[i] = yNumber.doubleValue();
            } catch (ParseException e) {
                throw new IOException("Invalid number format in point: " + pointLine, e);
            }
        }

        return factory.create(xValues, yValues);
    }

    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory) throws IOException {
        DataInputStream stream = new DataInputStream(inputStream);
        int length = stream.readInt();
        double[] xValues = new double[length];
        double[] yValues = new double[length];

        for (int i = 0; i < length; ++i) {
            xValues[i] = stream.readDouble();
            yValues[i] = stream.readDouble();
        }
        return factory.create(xValues, yValues);
    }

    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        if (stream == null || function == null) {
            throw new IllegalArgumentException("Поток или функция не может быть 0");
        }

        // Создаем ObjectOutputStream для сериализации объекта
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);

        // Сериализуем функцию
        objectOutputStream.writeObject(function);

        // Сбрасываем буфер, но не закрываем поток
        objectOutputStream.flush();
    }
}
