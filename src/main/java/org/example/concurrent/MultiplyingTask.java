package org.example.concurrent;

import org.example.functions.TabulatedFunction;

public class MultiplyingTask implements Runnable {
    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        int count = function.getCount();
        for (int i = 0; i < count; ++i) {
            function.setY(i, 2 * function.getY(i));
        }
        System.out.println(Thread.currentThread().getName() +
                " finished.");
    }
}
