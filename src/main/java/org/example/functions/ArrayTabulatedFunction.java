package org.example.functions;

import java.util.Arrays;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable {
    private int count;
    private int capacity; // Добавленное поле для запаса памяти
    private double[] xValues;
    private double[] yValues;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        this.count = xValues.length;
        this.capacity = count + 5; // Начальный запас памяти
        this.xValues = Arrays.copyOf(xValues, capacity);
        this.yValues = Arrays.copyOf(yValues, capacity);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        this.count = count;
        this.capacity = count + 5; // Начальный запас памяти
        this.xValues = new double[capacity];
        this.yValues = new double[capacity];

        double step = (xTo - xFrom) / (count - 1);
        for (int i = 0; i < count; i++) {
            xValues[i] = xFrom + i * step;
            yValues[i] = source.apply(xValues[i]);
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getX(int index) {
        return xValues[index];
    }

    @Override
    public double getY(int index) {
        return yValues[index];
    }

    @Override
    public void setY(int index, double y) {
        yValues[index] = y;
    }

    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; i++) {
            if (xValues[i] == x) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; i++) {
            if (yValues[i] == y) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public double leftBound() {
        return xValues[0];
    }

    @Override
    public double rightBound() {
        return xValues[count - 1];
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < xValues[0]) {
            return 0;
        }
        for (int i = 1; i < count; i++) {
            if (xValues[i] >= x) {
                return i - 1;
            }
        }
        return count;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return count == 1 ? yValues[0] : interpolate(x, 0);
    }

    @Override
    protected double extrapolateRight(double x) {
        return count == 1 ? yValues[0] : interpolate(x, count - 2);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (count == 1) {
            return yValues[0];
        }
        double leftX = xValues[floorIndex];
        double rightX = xValues[floorIndex + 1];
        double leftY = yValues[floorIndex];
        double rightY = yValues[floorIndex + 1];
        return interpolate(x, leftX, rightX, leftY, rightY);
    }

    @Override
    public void insert(double x, double y) {
        int index = indexOfX(x);

        // Если x уже существует, заменяем y
        if (index != -1) {
            yValues[index] = y;
            return;
        }

        // Если массив заполнен, увеличиваем capacity
        if (count == capacity) {
            capacity += 5; // Увеличиваем запас памяти
            xValues = Arrays.copyOf(xValues, capacity);
            yValues = Arrays.copyOf(yValues, capacity);
        }

        // Находим позицию для вставки
        int insertIndex = 0;
        while (insertIndex < count && xValues[insertIndex] < x) {
            insertIndex++;
        }

        // Сдвигаем элементы вправо
        System.arraycopy(xValues, insertIndex, xValues, insertIndex + 1, count - insertIndex);
        System.arraycopy(yValues, insertIndex, yValues, insertIndex + 1, count - insertIndex);

        // Вставляем новые значения
        xValues[insertIndex] = x;
        yValues[insertIndex] = y;
        count++;
    }

    // Реализация интерфейса Remove
    @Override
    public void remove(int index) {
// Сдвигаем элементы влево, начиная с позиции после удаляемого элемента
        if (index < count - 1) {
            System.arraycopy(xValues, index + 1, xValues, index, count - index - 1);
            System.arraycopy(yValues, index + 1, yValues, index, count - index - 1);
        }

        // Очищаем последние элементы
        if (count > 0) {
            xValues[count - 1] = 0;
            yValues[count - 1] = 0;
        }

        count--;
    }
}
