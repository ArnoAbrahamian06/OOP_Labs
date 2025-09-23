package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class LinkedListTabulatedFunctionRemovableTest {

    @Test
    public void testRemoveFromBeginning() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(0); // Удаляем первый элемент

        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(0), 1e-10);
        assertEquals(20.0, function.getY(0), 1e-10);
        assertEquals(4.0, function.getX(2), 1e-10);
        assertEquals(40.0, function.getY(2), 1e-10);
    }

    @Test
    public void testRemoveFromMiddle() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(2); // Удаляем третий элемент (индекс 2)

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(4.0, function.getX(2), 1e-10);
        assertEquals(2.0, function.getX(1), 1e-10);
    }

    @Test
    public void testRemoveFromEnd() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(3); // Удаляем последний элемент

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(3.0, function.getX(2), 1e-10);
        assertEquals(30.0, function.getY(2), 1e-10);
    }

    @Test
    public void testRemoveSingleElement() {
        double[] xValues = {1.0};
        double[] yValues = {10.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(0, function.getCount());
        assertEquals(0, function.getCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveWithInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(5); // Неверный индекс
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveWithNegativeIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(-1); // Отрицательный индекс
    }

    @Test
    public void testRemoveAndCheckCircularity() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1); // Удаляем средний элемент

        // Проверяем количество элементов
        assertEquals(2, function.getCount());

        // Проверяем оставшиеся значения
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(10.0, function.getY(0), 1e-10);
        assertEquals(3.0, function.getX(1), 1e-10);
        assertEquals(30.0, function.getY(1), 1e-10);

        // Проверяем границы
        assertEquals(1.0, function.leftBound(), 1e-10);  // Первый элемент
        assertEquals(3.0, function.rightBound(), 1e-10); // Последний элемент

        // Проверяем циклические связи через поведение
        // При получении элементов по кругу не должно быть исключений
        for (int i = 0; i < function.getCount(); i++) {
            function.getX(i);
            function.getY(i);
        }

        // Проверяем, что можно пройти полный круг
        // (это косвенная проверка цикличности)
        assertEquals(1.0, function.getX(0), 1e-10); // Начало
        assertEquals(3.0, function.getX(1), 1e-10); // Конец
        // Если бы мы могли получить элемент с индексом 2, он должен быть равен индексу 0
    }

    @Test
    public void testRemoveAllElements() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(0);
        function.remove(0); // Теперь удаляем оставшийся элемент (новый индекс 0)

        assertEquals(0, function.getCount());
        assertEquals(0, function.getCount());
    }
}