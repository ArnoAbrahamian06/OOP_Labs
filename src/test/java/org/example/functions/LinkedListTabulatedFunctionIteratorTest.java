package org.example.functions;

import org.junit.Test;
import java.util.Iterator;
import static org.junit.Assert.*;

public class LinkedListTabulatedFunctionIteratorTest {

    @Test
    public void testIteratorWhileLoop() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction f = new LinkedListTabulatedFunction(x, y);

        Iterator<Point> it = f.iterator();
        int i = 0;
        while (it.hasNext()) {
            Point p = it.next();
            assertEquals(x[i], p.x, 1e-10);
            assertEquals(y[i], p.y, 1e-10);
            i++;
        }
        assertEquals(3, i);
    }

    @Test
    public void testIteratorForEachLoop() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction f = new LinkedListTabulatedFunction(x, y);

        int i = 0;
        for (Point p : f) {
            assertEquals(x[i], p.x, 1e-10);
            assertEquals(y[i], p.y, 1e-10);
            i++;
        }
        assertEquals(3, i);
    }
}


