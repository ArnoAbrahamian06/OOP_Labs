package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class LinkedListTabulatedFunctionInsertTest {

    @Test
    public void testInsertIntoEmptyList() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(new double[]{}, new double[]{});
        function.insert(1.0, 2.0);
        assertEquals(1, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-9);
        assertEquals(2.0, function.getY(0), 1e-9);
    }

    @Test
    public void testInsertReplaceExistingY() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(new double[]{1.0, 2.0}, new double[]{3.0, 4.0});
        function.insert(1.0, 5.0);
        assertEquals(2, function.getCount());
        assertEquals(5.0, function.getY(0), 1e-9);
    }

    @Test
    public void testInsertAtBeginning() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(new double[]{2.0, 3.0}, new double[]{4.0, 5.0});
        function.insert(1.0, 6.0);
        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-9);
        assertEquals(6.0, function.getY(0), 1e-9);
        assertEquals(2.0, function.getX(1), 1e-9);
        assertEquals(4.0, function.getY(1), 1e-9);
    }

    @Test
    public void testInsertAtEnd() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(new double[]{1.0, 2.0}, new double[]{3.0, 4.0});
        function.insert(3.0, 5.0);
        assertEquals(3, function.getCount());
        assertEquals(3.0, function.getX(2), 1e-9);
        assertEquals(5.0, function.getY(2), 1e-9);
    }

    @Test
    public void testInsertInMiddle() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(new double[]{1.0, 3.0}, new double[]{4.0, 5.0});
        function.insert(2.0, 6.0);
        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(1), 1e-9);
        assertEquals(6.0, function.getY(1), 1e-9);
    }
}