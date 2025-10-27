package org.example.concurrent;

import org.example.functions.ConstantFunction;
import org.example.functions.TabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;

public class ReadWriteTaskExecutor {

    public static void main(String[] args) {
        ConstantFunction constantNegative = new ConstantFunction(-1);
        TabulatedFunction tabulatedFunction = new LinkedListTabulatedFunction(constantNegative, 1.0, 1000.0, 1000);

        ReadTask readTask = new ReadTask(tabulatedFunction);
        WriteTask writeTask = new WriteTask(tabulatedFunction, 0.5);

        Thread readerThread = new Thread(readTask);
        Thread writerThread = new Thread(writeTask);

        readerThread.start();
        writerThread.start();

        try {
            readerThread.join();
            writerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}