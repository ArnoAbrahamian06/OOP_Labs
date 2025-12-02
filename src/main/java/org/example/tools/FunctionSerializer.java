package org.example.tools;

import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.io.FunctionsIO;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Утилита для генерации сериализованного (base64) представления табулированной функции.
 * Используется для подготовки тестовых данных Postman/Newman.
 */
public final class FunctionSerializer {

    private FunctionSerializer() {
    }

    public static void main(String[] args) throws IOException {
        double[] x = {0.0, 1.0, 2.0, 3.0};
        double[] y = {0.0, 1.0, 4.0, 9.0};
        TabulatedFunction function = new ArrayTabulatedFunction(x, y);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BufferedOutputStream bos = new BufferedOutputStream(baos)) {
            FunctionsIO.serialize(bos, function);
            bos.flush();
        }

        String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
        System.out.println(base64);
    }
}

