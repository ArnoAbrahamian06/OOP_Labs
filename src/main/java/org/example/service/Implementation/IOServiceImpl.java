package org.example.service.Implementation;

import org.example.functions.TabulatedFunction;
import org.example.functions.factory.ArrayTabulatedFunctionFactory;
import org.example.io.FunctionsIO;
import org.example.service.IOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class IOServiceImpl implements IOService {

    private static final Logger log = LoggerFactory.getLogger(IOServiceImpl.class);

    @Override
    public TabulatedFunction importFromText(String textData) throws IOException {
        log.debug("Импорт из текста: {}", textData.substring(0, Math.min(50, textData.length())) + "...");
        try (BufferedReader reader = new BufferedReader(new StringReader(textData))) {
            return FunctionsIO.readTabulatedFunction(reader, new ArrayTabulatedFunctionFactory());
        }
    }

    @Override
    public String exportToText(TabulatedFunction function) throws IOException {
        log.debug("Экспорт в текст для функции с {} точками", function.getCount());
        StringWriter writer = new StringWriter();
        try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            FunctionsIO.writeTabulatedFunction(bufferedWriter, function);
            bufferedWriter.flush();
        }
        return writer.toString();
    }

    @Override
    public TabulatedFunction importFromBinary(byte[] binaryData) throws IOException {
        log.debug("Импорт из бинарных данных, размер: {} байт", binaryData.length);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(binaryData);
             BufferedInputStream bis = new BufferedInputStream(bais)) {
            return FunctionsIO.readTabulatedFunction(bis, new ArrayTabulatedFunctionFactory());
        }
    }

    @Override
    public byte[] exportToBinary(TabulatedFunction function) throws IOException {
        log.debug("Экспорт в бинарный формат для функции с {} точками", function.getCount());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BufferedOutputStream bos = new BufferedOutputStream(baos)) {
            FunctionsIO.writeTabulatedFunction(bos, function);
            bos.flush();
        }
        return baos.toByteArray();
    }

    @Override
    public TabulatedFunction deserialize(byte[] serializedData) throws IOException, ClassNotFoundException {
        log.debug("Десериализация данных, размер: {} байт", serializedData.length);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
             BufferedInputStream bis = new BufferedInputStream(bais)) {
            return FunctionsIO.deserialize(bis);
        }
    }

    @Override
    public byte[] serialize(TabulatedFunction function) throws IOException {
        log.debug("Сериализация функции с {} точками", function.getCount());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BufferedOutputStream bos = new BufferedOutputStream(baos)) {
            FunctionsIO.serialize(bos, function);
            bos.flush();
        }
        return baos.toByteArray();
    }
}