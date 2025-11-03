package org.example.concurrent;

import org.example.functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiplyingTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MultiplyingTask.class);
    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
        logger.info("Создана задача MultiplyingTask для функции с {} точками", function.getCount());
    }

    @Override
    public void run() {
        logger.info("Запуск задачи умножения в потоке: {}", Thread.currentThread().getName());
        int count = function.getCount();
        logger.debug("Начало обработки {} точек", count);
        
        for (int i = 0; i < count; ++i) {
            synchronized (function) {  // Блок синхронизации
                double oldValue = function.getY(i);
                function.setY(i, 2 * oldValue);
                logger.trace("Умножено значение Y[{}]: {} -> {}", i, oldValue, function.getY(i));
            }
        }
        
        logger.info("Задача умножения завершена в потоке: {}", Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getName() + " finished.");
    }
}
