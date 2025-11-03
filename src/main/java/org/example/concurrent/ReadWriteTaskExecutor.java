package org.example.concurrent;

import org.example.functions.ConstantFunction;
import org.example.functions.TabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadWriteTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ReadWriteTaskExecutor.class);

    public static void main(String[] args) {
        logger.info("Запуск ReadWriteTaskExecutor");
        
        ConstantFunction constantNegative = new ConstantFunction(-1);
        TabulatedFunction tabulatedFunction = new LinkedListTabulatedFunction(constantNegative, 1.0, 1000.0, 1000);
        logger.info("Создана функция: {} точек, диапазон [{} - {}]", 
                tabulatedFunction.getCount(), tabulatedFunction.leftBound(), tabulatedFunction.rightBound());

        ReadTask readTask = new ReadTask(tabulatedFunction);
        WriteTask writeTask = new WriteTask(tabulatedFunction, 0.5);

        Thread readerThread = new Thread(readTask);
        Thread writerThread = new Thread(writeTask);
        logger.info("Созданы потоки: читатель = {}, писатель = {}", 
                readerThread.getName(), writerThread.getName());

        logger.info("Запуск потоков чтения и записи");
        readerThread.start();
        writerThread.start();

        try {
            logger.debug("Ожидание завершения потоков");
            readerThread.join();
            logger.debug("Поток чтения завершён");
            writerThread.join();
            logger.debug("Поток записи завершён");
        } catch (InterruptedException e) {
            logger.error("Прерывание потока", e);
            Thread.currentThread().interrupt();
        }
        
        logger.info("Завершение ReadWriteTaskExecutor");
    }
}