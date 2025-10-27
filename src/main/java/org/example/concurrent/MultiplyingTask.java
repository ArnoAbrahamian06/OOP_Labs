package org.example.concurrent;

import org.example.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable {
    private final TabulatedFunction function;
    private volatile boolean completed = false;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        int count = function.getCount();
        for (int i = 0; i < count; ++i) {
            synchronized (function) {  // Блок синхронизации
                function.setY(i, 2 * function.getY(i));
            }
        }
        System.out.println(Thread.currentThread().getName() +
                " finished.");
        completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }
}
