package org.example.concurrent;


import org.example.functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ReadTask.class);
    private final TabulatedFunction function;

    public ReadTask(TabulatedFunction function) {
        this.function = function;
        logger.info("Создана задача чтения для функции с {} точками", function.getCount());
    }

    @Override
    public void run() {
        logger.info("Запуск задачи чтения в потоке: {}", Thread.currentThread().getName());
        int count = function.getCount();
        
        logger.debug("Начало чтения {} точек", count);
        for (int i = 0; i < count; ++i) {
            synchronized (function) {
                double x = function.getX(i);
                double y = function.getY(i);
                logger.debug("Прочитано значение: i = {}, x = {}, y = {}", i, x, y);
            }
        }
        
        logger.info("Задача чтения завершена в потоке: {}", Thread.currentThread().getName());
    }
}

