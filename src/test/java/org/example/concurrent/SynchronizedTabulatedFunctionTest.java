package org.example.concurrent;

import org.example.functions.Point;
import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.LinkedListTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {

    private TabulatedFunction innerFunction;
    private SynchronizedTabulatedFunction syncFunction;

    private SynchronizedTabulatedFunction syncFunction2;
    private TabulatedFunction originalFunction;
    private final double[] xValues = {1.0, 2.0, 3.0, 4.0};
    private final double[] yValues = {10.0, 20.0, 30.0, 40.0};

    @BeforeEach
    void setUp() {
        innerFunction = new ArrayTabulatedFunction(xValues, yValues);
        syncFunction = new SynchronizedTabulatedFunction(innerFunction);

        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        originalFunction = new LinkedListTabulatedFunction(xValues, yValues);
        syncFunction2 = new SynchronizedTabulatedFunction(originalFunction);
    }

    @Test
    void testDoSynchronouslyWithReturnValue() {
        // Operation с возвращаемым значением Integer
        SynchronizedTabulatedFunction.Operation<Integer> countOperation =
                new SynchronizedTabulatedFunction.Operation<Integer>() {
                    @Override
                    public Integer apply(SynchronizedTabulatedFunction function) {
                        return function.getCount();
                    }
                };

        Integer result = syncFunction2.doSynchronously(countOperation);
        assertEquals(4, (int) result, "Количество точек должно совпадать");
    }

    @Test
    void testDoSynchronouslyWithLambdaReturnValue() {
        // Operation с возвращаемым значением Double через лямбда
        Double result = syncFunction2.doSynchronously(func -> {
            double sum = 0;
            for (int i = 0; i < func.getCount(); i++) {
                sum += func.getY(i);
            }
            return sum;
        });

        assertEquals(30.0, result, 1e-9, "Сумма Y должна быть правильной");
    }

    @Test
    void testDoSynchronouslyWithVoidOperation() {
        // Operation с void возвращаемым типом (Void)
        SynchronizedTabulatedFunction.Operation<Void> modifyOperation =
                new SynchronizedTabulatedFunction.Operation<Void>() {
                    @Override
                    public Void apply(SynchronizedTabulatedFunction function) {
                        for (int i = 0; i < function.getCount(); i++) {
                            function.setY(i, function.getY(i) * 2);
                        }
                        return null;
                    }
                };

        Void result = syncFunction2.doSynchronously(modifyOperation);
        assertNull(result, "Результат Void операции должен быть null");

        // Проверяем, что значения действительно изменились
        assertEquals(2.0, originalFunction.getY(0), 1e-9, "Y[0] должен быть удвоен");
        assertEquals(8.0, originalFunction.getY(1), 1e-9, "Y[1] должен быть удвоен");
    }

    @Test
    void testDoSynchronouslyWithVoidLambda() {
        // Void операция через лямбду
        Void result = syncFunction2.doSynchronously(func -> {
            func.setY(0, 100.0);
            func.setY(1, 200.0);
            return null;
        });

        assertNull(result, "Результат Void лямбды должен быть null");
        assertEquals(100.0, originalFunction.getY(0), 1e-9, "Y[0] должен быть изменен");
        assertEquals(200.0, originalFunction.getY(1), 1e-9, "Y[1] должен быть изменен");
    }

    @Test
    void testDoSynchronouslyWithStringReturn() {
        // Operation с возвращаемым значением String
        String result = syncFunction2.doSynchronously(func -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < func.getCount(); i++) {
                sb.append(String.format("(%.1f, %.1f) ", func.getX(i), func.getY(i)));
            }
            return sb.toString().trim();
        });

        String expected = "(1,0, 1,0) (2,0, 4,0) (3,0, 9,0) (4,0, 16,0)";
        assertEquals(expected, result, "Строковое представление должно быть правильным");
    }

    @Test
    void testDoSynchronouslyWithBooleanReturn() {
        // Operation с возвращаемым значением Boolean
        Boolean result = syncFunction2.doSynchronously(func -> {
            return func.getCount() > 0 && func.getY(0) == 1.0;
        });

        assertTrue(result, "Булев результат должен быть true");
    }

    @Test
    void testDoSynchronouslyWithComplexObjectReturn() {
        // Operation с возвращаемым значением сложного объекта
        Point[] result = syncFunction2.doSynchronously(func -> {
            Point[] points = new Point[func.getCount()];
            for (int i = 0; i < func.getCount(); i++) {
                points[i] = new Point(func.getX(i), func.getY(i));
            }
            return points;
        });

        assertNotNull(result, "Массив точек не должен быть null");
        assertEquals(4, result.length, "Должно быть 4 точки");
        assertEquals(1.0, result[0].x, 1e-9, "Первая точка X");
        assertEquals(1.0, result[0].y, 1e-9, "Первая точка Y");
    }

    @Test
    void testDoSynchronouslyWithStateModification() {
        // Проверяем, что состояние сохраняется между вызовами
        syncFunction2.doSynchronously(func -> {
            func.setY(0, 50.0);
            return null;
        });

        Double result = syncFunction2.doSynchronously(func -> func.getY(0));
        assertEquals(50.0, result, 1e-9, "Измененное состояние должно сохраняться");
    }

    @Test
    void testDoSynchronouslyMultipleCalls() {
        // Множественные вызовы doSynchronously
        Integer count1 = syncFunction2.doSynchronously(TabulatedFunction::getCount);
        Double leftBound = syncFunction2.doSynchronously(TabulatedFunction::leftBound);
        Double rightBound = syncFunction2.doSynchronously(TabulatedFunction::rightBound);

        assertEquals(4, (int) count1, "Count должен быть 4");
        assertEquals(1.0, leftBound, 1e-9, "Левая граница");
        assertEquals(4.0, rightBound, 1e-9, "Правая граница");
    }

    @Test
    void testConstructorWithNull() {
        Exception exception = assertThrows(NullPointerException.class,
                () -> new SynchronizedTabulatedFunction(null));
        assertEquals("функция не должна быть равна null", exception.getMessage());
    }

    @Test
    void testGetCount() {
        assertEquals(4, syncFunction.getCount());
    }

    @Test
    void testGetX() {
        assertEquals(1.0, syncFunction.getX(0));
        assertEquals(2.0, syncFunction.getX(1));
        assertEquals(3.0, syncFunction.getX(2));
        assertEquals(4.0, syncFunction.getX(3));
    }

    @Test
    void testGetXWithInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> syncFunction.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> syncFunction.getX(10));
    }

    @Test
    void testGetY() {
        assertEquals(10.0, syncFunction.getY(0));
        assertEquals(20.0, syncFunction.getY(1));
        assertEquals(30.0, syncFunction.getY(2));
        assertEquals(40.0, syncFunction.getY(3));
    }

    @Test
    void testSetY() {
        syncFunction.setY(1, 25.0);
        assertEquals(25.0, syncFunction.getY(1));
        assertEquals(25.0, innerFunction.getY(1)); // Проверяем, что изменение прошло во внутреннюю функцию
    }

    @Test
    void testSetYWithInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> syncFunction.setY(-1, 5.0));
        assertThrows(IllegalArgumentException.class, () -> syncFunction.setY(10, 5.0));
    }

    @Test
    void testIndexOfX() {
        assertEquals(0, syncFunction.indexOfX(1.0));
        assertEquals(1, syncFunction.indexOfX(2.0));
        assertEquals(2, syncFunction.indexOfX(3.0));
        assertEquals(3, syncFunction.indexOfX(4.0));
        assertEquals(-1, syncFunction.indexOfX(5.0)); // Несуществующее значение
    }

    @Test
    void testIndexOfY() {
        assertEquals(0, syncFunction.indexOfY(10.0));
        assertEquals(1, syncFunction.indexOfY(20.0));
        assertEquals(2, syncFunction.indexOfY(30.0));
        assertEquals(3, syncFunction.indexOfY(40.0));
        assertEquals(-1, syncFunction.indexOfY(50.0)); // Несуществующее значение
    }

    @Test
    void testLeftBound() {
        assertEquals(1.0, syncFunction.leftBound());
    }

    @Test
    void testRightBound() {
        assertEquals(4.0, syncFunction.rightBound());
    }

    @Test
    void testApply() {
        assertEquals(10.0, syncFunction.apply(1.0));
        assertEquals(20.0, syncFunction.apply(2.0));
        assertEquals(30.0, syncFunction.apply(3.0));
        assertEquals(40.0, syncFunction.apply(4.0));

        // Тестирование интерполяции (если она поддерживается внутренней функцией)
        double interpolated = syncFunction.apply(1.5);
        assertTrue(interpolated > 10.0 && interpolated < 20.0);
    }

    @Test
    void testApplyOutsideBounds() {
        // Поведение может зависеть от реализации внутренней функции
        // Тестируем, что метод не падает с исключением
        assertDoesNotThrow(() -> syncFunction.apply(0.0));
        assertDoesNotThrow(() -> syncFunction.apply(5.0));
    }

    // Тесты для итератора
    @Test
    void testIteratorHasNext() {
        Iterator<Point> iterator = syncFunction.iterator();
        assertTrue(iterator.hasNext());

        // Пройдем по всем элементам
        for (int i = 0; i < syncFunction.getCount(); i++) {
            iterator.next();
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorNext() {
        Iterator<Point> iterator = syncFunction.iterator();

        // Проверяем первую точку
        Point point1 = iterator.next();
        assertEquals(1.0, point1.x);
        assertEquals(10.0, point1.y);

        // Проверяем вторую точку
        Point point2 = iterator.next();
        assertEquals(2.0, point2.x);
        assertEquals(20.0, point2.y);

        // Проверяем третью точку
        Point point3 = iterator.next();
        assertEquals(3.0, point3.x);
        assertEquals(30.0, point3.y);

        // Проверяем четвертую точку
        Point point4 = iterator.next();
        assertEquals(4.0, point4.x);
        assertEquals(40.0, point4.y);
    }

    @Test
    void testIteratorNextThrowsException() {
        Iterator<Point> iterator = syncFunction.iterator();

        // Проходим все элементы
        for (int i = 0; i < syncFunction.getCount(); i++) {
            iterator.next();
        }

        // Должно выбросить исключение при попытке получить следующий элемент
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testIteratorOrder() {
        Iterator<Point> iterator = syncFunction.iterator();

        for (int i = 0; i < syncFunction.getCount(); i++) {
            Point point = iterator.next();
            assertEquals(syncFunction.getX(i), point.x);
            assertEquals(syncFunction.getY(i), point.y);
        }
    }

    @Test
    void testIteratorIndependence() {
        Iterator<Point> iterator1 = syncFunction.iterator();
        Iterator<Point> iterator2 = syncFunction.iterator();

        // Итераторы должны работать независимо
        Point point1FromIterator1 = iterator1.next();
        Point point1FromIterator2 = iterator2.next();

        assertEquals(point1FromIterator1.x, point1FromIterator2.x);
        assertEquals(point1FromIterator1.y, point1FromIterator2.y);

        // Изменение функции не должно влиять на существующие итераторы (они работают с копией)
        syncFunction.setY(0, 15.0);

        Point point2FromIterator1 = iterator1.next();
        Point point2FromIterator2 = iterator2.next();

        // Итераторы должны возвращать исходные значения, а не измененные
        assertEquals(2.0, point2FromIterator1.x);
        assertEquals(20.0, point2FromIterator1.y);
        assertEquals(2.0, point2FromIterator2.x);
        assertEquals(20.0, point2FromIterator2.y);
    }

    @Test
    void testMultipleIterators() {
        Iterator<Point> iterator1 = syncFunction.iterator();
        Iterator<Point> iterator2 = syncFunction.iterator();

        // Оба итератора должны работать независимо
        int count1 = 0;
        while (iterator1.hasNext()) {
            iterator1.next();
            count1++;
        }

        int count2 = 0;
        while (iterator2.hasNext()) {
            iterator2.next();
            count2++;
        }

        assertEquals(4, count1);
        assertEquals(4, count2);
    }
}