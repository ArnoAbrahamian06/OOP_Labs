package org.example.concurrent;

import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.UnitFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MultiplyingTaskExecutor {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        UnitFunction unitFunction = new UnitFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(unitFunction, 1.0, 1000.0, 1000);

        List<Thread> threads = new ArrayList<>(); // Список List потоков
        int threadNumber = 10; // Число итераций

        for (int i = 0; i < threadNumber; ++i) {
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);
            threads.add(thread);
        }

        // Запуск потокв
        for (Thread thread : threads) {
            thread.start();
        }

        // Ждём завершения КАЖДОГО потока с помощью join()
        for (Thread thread : threads) {
            thread.join(); // текущий поток блокируется, пока thread не завершится
        }

        // Вывод результата
        System.out.println(function);
    }
}
