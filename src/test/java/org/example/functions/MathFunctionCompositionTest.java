package org.example.functions;

import org.junit.Test;
import static org.junit.Assert.*;

public class MathFunctionCompositionTest {

    // Простые реализации функций для тестирования
    static class IncrementFunction implements MathFunction {
        @Override
        public double apply(double x) {
            return x + 1;
        }
    }

    static class DoubleFunction implements MathFunction {
        @Override
        public double apply(double x) {
            return x * 2;
        }
    }

    static class SquareFunction implements MathFunction {
        @Override
        public double apply(double x) {
            return x * x;
        }
    }

    @Test
    public void testSimpleComposition() {
        MathFunction square = new SquareFunction();
        MathFunction increment = new IncrementFunction();

        MathFunction composed = square.andThen(increment);

        assertEquals(5.0, composed.apply(2.0), 1e-10); // (2²) + 1 = 5
        assertEquals(2.0, composed.apply(1.0), 1e-10); // (1²) + 1 = 2
    }

    @Test
    public void testMultipleComposition() {
        MathFunction square = new SquareFunction();
        MathFunction increment = new IncrementFunction();
        MathFunction doubleFunc = new DoubleFunction();

        MathFunction composed = square.andThen(increment).andThen(doubleFunc);

        assertEquals(10.0, composed.apply(2.0), 1e-10); // ((2²) + 1) * 2 = 10
        assertEquals(4.0, composed.apply(1.0), 1e-10);  // ((1²) + 1) * 2 = 4
    }

    @Test
    public void testChainedComposition() {
        MathFunction f = new DoubleFunction();      // f(x) = 2x
        MathFunction g = new IncrementFunction();   // g(x) = x + 3
        MathFunction h = new SquareFunction();      // h(x) = x²

        // Композиция: f(g(h(x))) = (x² + 3) * 2
        MathFunction composed = f.andThen(g).andThen(h);

        assertEquals(8.0, composed.apply(1.0), 1e-10);  // ((1²)+3)*2 = 8
        assertEquals(14.0, composed.apply(2.0), 1e-10); // ((2²)+3)*2 = 14
    }

    @Test
    public void testCompositionWithBuiltinFunctions() {

        MathFunction sin = Math::sin;
        MathFunction exp = Math::exp;

        MathFunction composed = sin.andThen(exp);

        double result = composed.apply(Math.PI / 2);
        assertEquals(Math.exp(1), result, 1e-10); // e^(sin(π/2)) = e^1
    }

    @Test
    public void testCompositionOrder() {
        MathFunction increment = new IncrementFunction();
        MathFunction square = new SquareFunction();

        // Разный порядок композиции дает разные результаты
        MathFunction incrementThenSquare = increment.andThen(square); // (x+1)²
        MathFunction squareThenIncrement = square.andThen(increment); // x²+1

        assertEquals(4.0, incrementThenSquare.apply(1.0), 1e-10); // (1+1)² = 4
        assertEquals(2.0, squareThenIncrement.apply(1.0), 1e-10); // 1²+1 = 2
    }

    @Test
    public void testLongChainComposition() {
        MathFunction f1 = new IncrementFunction();   // x + 1
        MathFunction f2 = new DoubleFunction();      // x * 2
        MathFunction f3 = new IncrementFunction();   // x + 1 (но уже другой экземпляр)
        MathFunction f4 = new SquareFunction();      // x²

        // Длинная цепочка композиций: f4(f3(f2(f1(x))))
        MathFunction longChain = f1.andThen(f2).andThen(f3).andThen(f4);

        // Для x=2: (((2+1)*2)+1)² = ((3*2)+1)² = (6+1)² = 49
        assertEquals(49.0, longChain.apply(2.0), 1e-10);
    }

    @Test
    public void testCompositionWithSqrFunction() {
        MathFunction sqr = new SqrFunction();
        MathFunction increment = new IncrementFunction();

        MathFunction composed = sqr.andThen(increment);

        assertEquals(10.0, composed.apply(3.0), 1e-10); // 3² + 1 = 10
        assertEquals(2.0, composed.apply(1.0), 1e-10);  // 1² + 1 = 2
    }
}