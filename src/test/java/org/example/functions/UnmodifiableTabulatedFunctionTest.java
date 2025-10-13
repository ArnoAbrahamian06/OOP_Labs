package org.example.functions;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.Iterator;

public class UnmodifiableTabulatedFunctionTest {
    private TabulatedFunction arrayFunction;
    private TabulatedFunction linkedListFunction;
    private UnmodifiableTabulatedFunction unmodifiableArray;
    private UnmodifiableTabulatedFunction unmodifiableLinkedList;

    @Before
    public void setUp() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};

        arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        unmodifiableArray = new UnmodifiableTabulatedFunction(arrayFunction);
        unmodifiableLinkedList = new UnmodifiableTabulatedFunction(linkedListFunction);
    }

    @Test
    public void testGetCount() {
        assertEquals(4, unmodifiableArray.getCount());
        assertEquals(4, unmodifiableLinkedList.getCount());
    }

    @Test
    public void testGetX() {
        assertEquals(1.0, unmodifiableArray.getX(0), 1e-10);
        assertEquals(3.0, unmodifiableArray.getX(2), 1e-10);
        assertEquals(1.0, unmodifiableLinkedList.getX(0), 1e-10);
        assertEquals(3.0, unmodifiableLinkedList.getX(2), 1e-10);
    }

    @Test
    public void testGetY() {
        assertEquals(10.0, unmodifiableArray.getY(0), 1e-10);
        assertEquals(30.0, unmodifiableArray.getY(2), 1e-10);
        assertEquals(10.0, unmodifiableLinkedList.getY(0), 1e-10);
        assertEquals(30.0, unmodifiableLinkedList.getY(2), 1e-10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetYThrowsExceptionForArray() {
        unmodifiableArray.setY(0, 100.0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetYThrowsExceptionForLinkedList() {
        unmodifiableLinkedList.setY(0, 100.0);
    }

    @Test
    public void testIndexOfX() {
        assertEquals(0, unmodifiableArray.indexOfX(1.0));
        assertEquals(2, unmodifiableArray.indexOfX(3.0));
        assertEquals(-1, unmodifiableArray.indexOfX(5.0));

        assertEquals(0, unmodifiableLinkedList.indexOfX(1.0));
        assertEquals(2, unmodifiableLinkedList.indexOfX(3.0));
        assertEquals(-1, unmodifiableLinkedList.indexOfX(5.0));
    }

    @Test
    public void testIndexOfY() {
        assertEquals(0, unmodifiableArray.indexOfY(10.0));
        assertEquals(2, unmodifiableArray.indexOfY(30.0));
        assertEquals(-1, unmodifiableArray.indexOfY(100.0));

        assertEquals(0, unmodifiableLinkedList.indexOfY(10.0));
        assertEquals(2, unmodifiableLinkedList.indexOfY(30.0));
        assertEquals(-1, unmodifiableLinkedList.indexOfY(100.0));
    }

    @Test
    public void testBounds() {
        assertEquals(1.0, unmodifiableArray.leftBound(), 1e-10);
        assertEquals(4.0, unmodifiableArray.rightBound(), 1e-10);
        assertEquals(1.0, unmodifiableLinkedList.leftBound(), 1e-10);
        assertEquals(4.0, unmodifiableLinkedList.rightBound(), 1e-10);
    }

    @Test
    public void testApply() {
        assertEquals(10.0, unmodifiableArray.apply(1.0), 1e-10);
        assertEquals(30.0, unmodifiableArray.apply(3.0), 1e-10);
        assertEquals(10.0, unmodifiableLinkedList.apply(1.0), 1e-10);
        assertEquals(30.0, unmodifiableLinkedList.apply(3.0), 1e-10);
    }

    @Test
    public void testIteratorWithWhileLoop() {
        // Тест array function
        Iterator<Point> arrayIterator = unmodifiableArray.iterator();
        int arrayCount = 0;
        double[] expectedX = {1.0, 2.0, 3.0, 4.0};
        double[] expectedY = {10.0, 20.0, 30.0, 40.0};

        while (arrayIterator.hasNext()) {
            Point point = arrayIterator.next();
            assertEquals(expectedX[arrayCount], point.x, 1e-10);
            assertEquals(expectedY[arrayCount], point.y, 1e-10);
            arrayCount++;
        }
        assertEquals(4, arrayCount);

        // Тест linked list function
        Iterator<Point> linkedListIterator = unmodifiableLinkedList.iterator();
        int linkedListCount = 0;

        while (linkedListIterator.hasNext()) {
            Point point = linkedListIterator.next();
            assertEquals(expectedX[linkedListCount], point.x, 1e-10);
            assertEquals(expectedY[linkedListCount], point.y, 1e-10);
            linkedListCount++;
        }
        assertEquals(4, linkedListCount);
    }

    @Test
    public void testIteratorWithForEachLoop() {
        double[] expectedX = {1.0, 2.0, 3.0, 4.0};
        double[] expectedY = {10.0, 20.0, 30.0, 40.0};

        // Тест array function
        int arrayIndex = 0;
        for (Point point : unmodifiableArray) {
            assertEquals(expectedX[arrayIndex], point.x, 1e-10);
            assertEquals(expectedY[arrayIndex], point.y, 1e-10);
            arrayIndex++;
        }
        assertEquals(4, arrayIndex);

        // Тест linked list function
        int linkedListIndex = 0;
        for (Point point : unmodifiableLinkedList) {
            assertEquals(expectedX[linkedListIndex], point.x, 1e-10);
            assertEquals(expectedY[linkedListIndex], point.y, 1e-10);
            linkedListIndex++;
        }
        assertEquals(4, linkedListIndex);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemoveThrowsExceptionForArray() {
        Iterator<Point> iterator = unmodifiableArray.iterator();
        iterator.next();
        iterator.remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemoveThrowsExceptionForLinkedList() {
        Iterator<Point> iterator = unmodifiableLinkedList.iterator();
        iterator.next();
        iterator.remove();
    }

}
