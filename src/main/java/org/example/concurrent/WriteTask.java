package org.example.concurrent;

import org.example.functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WriteTask.class);
    private TabulatedFunction function;
    private double value;

    public WriteTask(TabulatedFunction function, double value) {
        this.function = function;
        this.value = value;
        logger.info("Создана задача записи для функции с {} точками, значение = {}", 
                function.getCount(), value);
    }

    public WriteTask(TabulatedFunction function) {
        logger.warn("Создан WriteTask без указания значения");
    }

    @Override
    public void run() {
        logger.info("Запуск задачи записи в потоке: {}", Thread.currentThread().getName());
        int count = function.getCount();
        logger.debug("Начало записи значения {} в {} точек", value, count);
        
        for (int i = 0; i < count; i++) {
            synchronized (function) {
                function.setY(i, value);
                logger.debug("Записано значение {} в индекс {}", value, i);
                System.out.printf("Writing for index %d complete", i);
            }
        }
        
        logger.info("Задача записи завершена в потоке: {}", Thread.currentThread().getName());
    }
}
