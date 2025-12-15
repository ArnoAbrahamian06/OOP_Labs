package org.example.service;

import org.example.functions.TabulatedFunction;
import java.io.IOException;

public interface IOService {
    // Импорт из текстового формата (String -> TabulatedFunction)
    TabulatedFunction importFromText(String textData) throws IOException;

    // Экспорт в текстовый формат (TabulatedFunction -> String)
    String exportToText(TabulatedFunction function) throws IOException;

    // Импорт из бинарного формата (byte[] -> TabulatedFunction)
    TabulatedFunction importFromBinary(byte[] binaryData) throws IOException;

    // Экспорт в бинарном формате (TabulatedFunction -> byte[])
    byte[] exportToBinary(TabulatedFunction function) throws IOException;

    // Десериализация (byte[] -> TabulatedFunction)
    TabulatedFunction deserialize(byte[] serializedData) throws IOException, ClassNotFoundException;

    // Сериализация (TabulatedFunction -> byte[])
    byte[] serialize(TabulatedFunction function) throws IOException;
}