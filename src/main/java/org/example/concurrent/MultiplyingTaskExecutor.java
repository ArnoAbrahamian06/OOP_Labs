package org.example.concurrent;

import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.UnitFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MultiplyingTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(MultiplyingTaskExecutor.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        logger.info("Запуск MultiplyingTaskExecutor");
        
        UnitFunction unitFunction = new UnitFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(unitFunction, 1.0, 1000.0, 1000);
        logger.info("Создана функция: {} точек, диапазон [{} - {}]", 
                function.getCount(), function.leftBound(), function.rightBound());

        List<Thread> threads = new ArrayList<>(); // Список List потоков
        int threadNumber = 10; // Число итераций
        logger.info("Создание {} потоков для выполнения задач", threadNumber);

        for (int i = 0; i < threadNumber; ++i) {
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);
            threads.add(thread);
            logger.debug("Создан поток #{}: {}", i, thread.getName());
        }

        // Запуск потока
        logger.info("Запуск всех потоков");
        for (Thread thread : threads) {
            thread.start();
            logger.debug("Запущен поток: {}", thread.getName());
        }

        // Ждём завершения КАЖДОГО потока с помощью join()
        logger.info("Ожидание завершения всех потоков");
        for (Thread thread : threads) {
            thread.join(); // текущий поток блокируется, пока thread не завершится
            logger.debug("Поток {} завершён", thread.getName());
        }

        logger.info("Все потоки завершены");
        
        // Вывод результата
        System.out.println(function);
        logger.info("Завершение MultiplyingTaskExecutor");
    }
}
