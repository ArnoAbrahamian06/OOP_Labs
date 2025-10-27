package org.example.concurrent;

import org.example.functions.Point;
import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {

    private TabulatedFunction innerFunction;
    private SynchronizedTabulatedFunction syncFunction;
    private final double[] xValues = {1.0, 2.0, 3.0, 4.0};
    private final double[] yValues = {10.0, 20.0, 30.0, 40.0};

    @BeforeEach
    void setUp() {
        innerFunction = new ArrayTabulatedFunction(xValues, yValues);
        syncFunction = new SynchronizedTabulatedFunction(innerFunction);
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