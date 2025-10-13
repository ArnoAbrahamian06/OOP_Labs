package org.example.operations;

import org.example.functions.*;
import org.example.exceptions.InconsistentFunctionsException;
import org.example.functions.*;
import org.example.functions.factory.*;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

public class TabulatedFunctionOperationServiceTest {

    private TabulatedFunction arrayFunction1;
    private TabulatedFunction arrayFunction2;
    private TabulatedFunction linkedListFunction1;
    private TabulatedFunction linkedListFunction2;
    private TabulatedFunctionOperationService service;

    @Before
    public void setUp() {
        double[] xValues1 = {1.0, 2.0, 3.0, 4.0};
        double[] yValues1 = {10.0, 20.0, 30.0, 40.0};
        double[] yValues2 = {5.0, 15.0, 25.0, 35.0};

        arrayFunction1 = new ArrayTabulatedFunction(xValues1, yValues1);
        arrayFunction2 = new ArrayTabulatedFunction(xValues1, yValues2);
        linkedListFunction1 = new LinkedListTabulatedFunction(xValues1, yValues1);
        linkedListFunction2 = new LinkedListTabulatedFunction(xValues1, yValues2);

        service = new TabulatedFunctionOperationService();
    }

    @Test
    public void testAdditionArrayArray() {
        service.setFactory(new ArrayTabulatedFunctionFactory());
        TabulatedFunction result = service.add(arrayFunction1, arrayFunction2);

        assertEquals(4, result.getCount());
        assertEquals(15.0, result.getY(0), 1e-10); // 10.0 + 5.0
        assertEquals(35.0, result.getY(1), 1e-10); // 20.0 + 15.0
        assertEquals(55.0, result.getY(2), 1e-10); // 30.0 + 25.0
        assertEquals(75.0, result.getY(3), 1e-10); // 40.0 + 35.0
    }

    @Test
    public void testAdditionLinkedListLinkedList() {
        service.setFactory(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction result = service.add(linkedListFunction1, linkedListFunction2);

        assertEquals(4, result.getCount());
        assertEquals(15.0, result.getY(0), 1e-10);
        assertEquals(35.0, result.getY(1), 1e-10);
        assertEquals(55.0, result.getY(2), 1e-10);
        assertEquals(75.0, result.getY(3), 1e-10);
    }

    @Test
    public void testMultiplicationArrayArray() {
        service.setFactory(new ArrayTabulatedFunctionFactory());
        TabulatedFunction result = service.mult(arrayFunction1, arrayFunction2);

        assertEquals(4, result.getCount());
        assertEquals(50.0, result.getY(0), 1e-10); // 10.0 * 5.0
        assertEquals(300.0, result.getY(1), 1e-10); // 20.0 * 15.0
        assertEquals(750.0, result.getY(2), 1e-10); // 30.0 * 25.0
        assertEquals(1400.0, result.getY(3), 1e-10); // 40.0 * 35.0
    }

    @Test
    public void testMultiplicationLinkedListLinkedList() {
        service.setFactory(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction result = service.mult(linkedListFunction1, linkedListFunction2);

        assertEquals(4, result.getCount());
        assertEquals(50.0, result.getY(0), 1e-10); // 10.0 * 5.0
        assertEquals(300.0, result.getY(1), 1e-10); // 20.0 * 15.0
        assertEquals(750.0, result.getY(2), 1e-10); // 30.0 * 25.0
        assertEquals(1400.0, result.getY(3), 1e-10); // 40.0 * 35.0
    }

    @Test
    public void testAdditionMixedTypes() {
        // Array + LinkedList
        TabulatedFunction result1 = service.add(arrayFunction1, linkedListFunction2);
        assertEquals(4, result1.getCount());
        assertEquals(15.0, result1.getY(0), 1e-10);

        // LinkedList + Array
        TabulatedFunction result2 = service.add(linkedListFunction1, arrayFunction2);
        assertEquals(4, result2.getCount());
        assertEquals(15.0, result2.getY(0), 1e-10);
    }

    @Test
    public void testMultiplicationMixedTypes() {
        // Array + LinkedList
        TabulatedFunction result1 = service.mult(arrayFunction1, linkedListFunction2);
        assertEquals(4, result1.getCount());
        assertEquals(50.0, result1.getY(0), 1e-10);

        // LinkedList + Array
        TabulatedFunction result2 = service.mult(linkedListFunction1, arrayFunction2);
        assertEquals(4, result2.getCount());
        assertEquals(50.0, result2.getY(0), 1e-10);
    }

    @Test
    public void testSubtractionArrayArray() {
        service.setFactory(new ArrayTabulatedFunctionFactory());
        TabulatedFunction result = service.sub(arrayFunction1, arrayFunction2);

        assertEquals(4, result.getCount());
        assertEquals(5.0, result.getY(0), 1e-10);  // 10.0 - 5.0
        assertEquals(5.0, result.getY(1), 1e-10);  // 20.0 - 15.0
        assertEquals(5.0, result.getY(2), 1e-10);  // 30.0 - 25.0
        assertEquals(5.0, result.getY(3), 1e-10);  // 40.0 - 35.0
    }

    @Test
    public void testDivisionArrayArray() {
        service.setFactory(new ArrayTabulatedFunctionFactory());
        TabulatedFunction result = service.div(arrayFunction1, arrayFunction2);

        assertEquals(4, result.getCount());
        assertEquals(2.0, result.getY(0), 1e-10);  // 10.0 / 5.0
        assertEquals(1.33333, result.getY(1), 1e-4);  // 20.0 / 15.0
        assertEquals(1.2, result.getY(2), 1e-10);  // 30.0 / 25.0
        assertEquals(1.14285, result.getY(3), 1e-4);  // 40.0 / 35.0
    }

    @Test
    public void testSubtractionLinkedListLinkedList() {
        service.setFactory(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction result = service.sub(linkedListFunction1, linkedListFunction2);

        assertEquals(4, result.getCount());
        assertEquals(5.0, result.getY(0), 1e-10);
        assertEquals(5.0, result.getY(1), 1e-10);
        assertEquals(5.0, result.getY(2), 1e-10);
        assertEquals(5.0, result.getY(3), 1e-10);
    }

    @Test
    public void testDivisionLinkedListLinkedList() {
        service.setFactory(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction result = service.div(linkedListFunction1, linkedListFunction2);

        assertEquals(4, result.getCount());
        assertEquals(2.0, result.getY(0), 1e-10);  // 10.0 / 5.0
        assertEquals(1.33333, result.getY(1), 1e-4);  // 20.0 / 15.0
        assertEquals(1.2, result.getY(2), 1e-10);  // 30.0 / 25.0
        assertEquals(1.14285, result.getY(3), 1e-4);  // 40.0 / 35.0
    }


    @Test
    public void testSubtractionMixedTypes() {
        // Array - LinkedList
        TabulatedFunction result1 = service.sub(arrayFunction1, linkedListFunction2);
        assertEquals(4, result1.getCount());
        assertEquals(5.0, result1.getY(0), 1e-10);

        // LinkedList - Array
        TabulatedFunction result2 = service.sub(linkedListFunction1, arrayFunction2);
        assertEquals(4, result2.getCount());
        assertEquals(5.0, result2.getY(0), 1e-10);
    }

    @Test
    public void testDivisionMixedTypes() {
        // Array - LinkedList
        TabulatedFunction result1 = service.div(arrayFunction1, linkedListFunction2);
        assertEquals(4, result1.getCount());
        assertEquals(2.0, result1.getY(0), 1e-10);

        // LinkedList - Array
        TabulatedFunction result2 = service.div(linkedListFunction1, arrayFunction2);
        assertEquals(4, result2.getCount());
        assertEquals(2.0, result2.getY(0), 1e-10);
    }

    @Test(expected = InconsistentFunctionsException.class)
    public void testAdditionWithDifferentPointCount() {
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {10.0, 20.0, 30.0};
        double[] xValues2 = {1.0, 2.0, 3.0, 4.0};
        double[] yValues2 = {5.0, 15.0, 25.0, 35.0};

        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);

        service.add(func1, func2);
    }


    @Test(expected = InconsistentFunctionsException.class)
    public void testAdditionWithDifferentXValues() {
        double[] xValues1 = {1.0, 2.0, 3.0, 4.0};
        double[] yValues1 = {10.0, 20.0, 30.0, 40.0};
        double[] xValues2 = {1.0, 2.0, 3.5, 4.0}; // 3.5 вместо 3.0
        double[] yValues2 = {5.0, 15.0, 25.0, 35.0};

        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);

        service.add(func1, func2);
    }

    @Test
    public void testFactoryInjection() {
        // Тестируем конструктор по умолчанию
        TabulatedFunctionOperationService defaultService = new TabulatedFunctionOperationService();
        assertTrue(defaultService.getFactory() instanceof ArrayTabulatedFunctionFactory);

        // Тестируем конструктор с фабрикой
        LinkedListTabulatedFunctionFactory linkedListFactory = new LinkedListTabulatedFunctionFactory();
        TabulatedFunctionOperationService customService = new TabulatedFunctionOperationService(linkedListFactory);
        assertEquals(linkedListFactory, customService.getFactory());

        // Тестируем сеттер
        customService.setFactory(new ArrayTabulatedFunctionFactory());
        assertTrue(customService.getFactory() instanceof ArrayTabulatedFunctionFactory);
    }

    @Test
    public void testResultUsesCorrectFactory() {
        // Проверяем, что результат использует указанную фабрику
        service.setFactory(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction result = service.add(arrayFunction1, arrayFunction2);
        assertTrue(result instanceof LinkedListTabulatedFunction);

        service.setFactory(new ArrayTabulatedFunctionFactory());
        result = service.add(linkedListFunction1, linkedListFunction2);
        assertTrue(result instanceof ArrayTabulatedFunction);
    }

    @Test
    public void testOperationsAreCommutative() {
        // a + b должно быть равно b + a
        TabulatedFunction result1 = service.add(arrayFunction1, arrayFunction2);
        TabulatedFunction result2 = service.add(arrayFunction2, arrayFunction1);

        assertEquals(result1.getCount(), result2.getCount());
        for (int i = 0; i < result1.getCount(); i++) {
            assertEquals(result1.getX(i), result2.getX(i), 1e-10);
            assertEquals(result1.getY(i), result2.getY(i), 1e-10);
        }
    }

    @Test
    public void testMultiplicationAreCommutative() {
        // a + b должно быть равно b + a
        TabulatedFunction result1 = service.mult(arrayFunction1, arrayFunction2);
        TabulatedFunction result2 = service.mult(arrayFunction2, arrayFunction1);

        assertEquals(result1.getCount(), result2.getCount());
        for (int i = 0; i < result1.getCount(); i++) {
            assertEquals(result1.getX(i), result2.getX(i), 1e-10);
            assertEquals(result1.getY(i), result2.getY(i), 1e-10);
        }
    }
}