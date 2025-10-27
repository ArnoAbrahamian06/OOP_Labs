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
        List<MultiplyingTask> tasks = new ArrayList<>();
        int threadNumber = 10; // Число итераций

        for (int i = 0; i < threadNumber; ++i) {
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);
            threads.add(thread);
            tasks.add(task);
        }

        // Запуск потокв
        for (Thread thread : threads) {
            thread.start();
        }

        // Ожидаем завершения всех задач без фиксированного таймаута
        while (!tasks.isEmpty()) {
            // Создаём копию списка для безопасного удаления
            List<MultiplyingTask> completedTasks = new ArrayList<>();
            for (MultiplyingTask task : tasks) {
                if (task.isCompleted()) {
                    completedTasks.add(task);
                }
            }
            // Удаляем завершённые задачи из основного списка
            tasks.removeAll(completedTasks);

            // Небольшая пауза, чтобы не грузить CPU
            Thread.sleep(10);
        }
        // Вывод результата
        System.out.println(function);
    }
}
