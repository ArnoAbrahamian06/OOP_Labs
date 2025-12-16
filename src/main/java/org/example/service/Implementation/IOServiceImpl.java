package org.example.service.Implementation;

import org.example.service.IOService;
import org.example.functions.TabulatedFunction;
import org.example.functions.factory.TabulatedFunctionFactory;
import org.example.io.FunctionsIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;

@Service
public class IOServiceImpl implements IOService {

    private static final Logger log = LoggerFactory.getLogger(IOServiceImpl.class); // НОВОЕ ПОЛЕ

    private final TabulatedFunctionFactory factory;

    // Фабрика внедряется через конструктор
    @Autowired
    public IOServiceImpl(TabulatedFunctionFactory factory) {
        this.factory = factory;
        log.info("IOServiceImpl создан с использованием фабрики: {}", factory.getClass().getSimpleName());
    }

    @Override
    public TabulatedFunction importFromText(String textData) throws IOException {
        log.info("importFromText: Начало импорта функции из текстовых данных");
        try (StringReader stringReader = new StringReader(textData);
             BufferedReader bufferedReader = new BufferedReader(stringReader)) {

            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedReader, factory);
            log.info("importFromText: Функция успешно импортирована из текста. Точек: {}", function.getCount());
            return function;
        } catch (IOException e) {
            log.error("importFromText: Ошибка ввода-вывода или парсинга при импорте из текста", e);
            throw e;
        }
    }

    @Override
    public String exportToText(TabulatedFunction function) throws IOException {
        log.info("exportToText: Начало экспорта функции в текстовый формат. Точек: {}", function.getCount());
        try (StringWriter stringWriter = new StringWriter();
             BufferedWriter bufferedWriter = new BufferedWriter(stringWriter)) {
            FunctionsIO.writeTabulatedFunction(bufferedWriter, function);
            bufferedWriter.flush();
            log.info("exportToText: Функция успешно экспортирована в текстовый формат");
            return stringWriter.toString();
        } catch (IOException e) {
            log.error("exportToText: Ошибка ввода-вывода при экспорте в текст", e);
            throw e;
        }
    }

    @Override
    public TabulatedFunction importFromBinary(byte[] binaryData) throws IOException {
        log.info("importFromBinary: Начало импорта функции из бинарных данных. Размер: {} байт", binaryData.length);
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(binaryData);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(byteInputStream)) {
            TabulatedFunction function = FunctionsIO.readTabulatedFunction(bufferedInputStream, factory);
            log.info("importFromBinary: Функция успешно импортирована из бинарных данных. Точек: {}", function.getCount());
            return function;
        } catch (IOException e) {
            log.error("importFromBinary: Ошибка ввода-вывода или формата при импорте из бинарного файла", e);
            throw e;
        }
    }

    @Override
    public byte[] exportToBinary(TabulatedFunction function) throws IOException {
        log.info("exportToBinary: Начало экспорта функции в бинарный формат. Точек: {}", function.getCount());
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteOutputStream)) {
            FunctionsIO.writeTabulatedFunction(bufferedOutputStream, function);
            bufferedOutputStream.flush();
            byte[] result = byteOutputStream.toByteArray();
            log.info("exportToBinary: Функция успешно экспортирована. Размер данных: {} байт", result.length);
            return result;
        } catch (IOException e) {
            log.error("exportToBinary: Ошибка ввода-вывода при экспорте в бинарный файл", e);
            throw e;
        }
    }

    @Override
    public TabulatedFunction deserialize(byte[] serializedData) throws IOException, ClassNotFoundException {
        log.info("deserialize: Начало десериализации объекта. Размер: {} байт", serializedData.length);
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(serializedData);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(byteInputStream)) {
            TabulatedFunction function = FunctionsIO.deserialize(bufferedInputStream);
            log.info("deserialize: Объект успешно десериализован. Точек: {}", function.getCount());
            return function;
        } catch (IOException | ClassNotFoundException e) {
            log.error("deserialize: Ошибка десериализации объекта", e);
            throw e;
        }
    }

    @Override
    public byte[] serialize(TabulatedFunction function) throws IOException {
        log.info("serialize: Начало сериализации объекта. Точек: {}", function.getCount());
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteOutputStream)) {
            FunctionsIO.serialize(bufferedOutputStream, function);
            bufferedOutputStream.flush();
            byte[] result = byteOutputStream.toByteArray();
            log.info("serialize: Объект успешно сериализован. Размер данных: {} байт", result.length);
            return result;
        } catch (IOException e) {
            log.error("serialize: Ошибка сериализации объекта", e);
            throw e;
        }
    }
}