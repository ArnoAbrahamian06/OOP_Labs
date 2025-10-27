package org.example.concurrent;

import org.example.functions.TabulatedFunction;

public class WriteTask implements Runnable {
    private TabulatedFunction function;
    private double value;

    public WriteTask(TabulatedFunction function, double value) {
        this.function = function;
        this.value = value;
    }

    public WriteTask(TabulatedFunction function) {
    }

    @Override
    public void run() {
        int count = function.getCount();
        for (int i = 0; i < count; i++) {
            synchronized (function) {
                function.setY(i, value);
                System.out.printf("Writing for index %d complete", i);
            }
        }
    }
}
