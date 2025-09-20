package org.example.functions;
import org.junit.Test;
import static org.junit.Assert.*;

public class NewtonFunctionTest {
    @Test

    public void testApply() {
        MathFunction exp = x -> Math.exp(x) - 2;  //f(x) = e^x - 2
        MathFunction expDerivative = Math::exp;

        NewtonFunction Newton3 = new NewtonFunction(exp, expDerivative, 1e-6, 100);
        double root3 = Newton3.apply(1.0); // Начальное приближение 1.0

        //Корень: x = ln(2)
        assertEquals(0.693, root3, 0.001);

        MathFunction sin = Math::sin; //f(x) = sin(x)
        MathFunction sinDerivative = Math::cos;

        NewtonFunction Newton2 = new NewtonFunction(sin, sinDerivative, 1e-6, 100);
        double root2 = Newton2.apply(3.0); // Начальное приближение 3.0

        // Корни: x = kπ, где k ∈ Z
        assertEquals(3.14159, root2, 0.001);

        MathFunction quadratic = x -> x * x - 4; // x² - 4
        MathFunction quadraticDerivative = x -> 2 * x;

        NewtonFunction Newton1 = new NewtonFunction(quadratic, quadraticDerivative, 1e-6, 100);
        double root1 = Newton1.apply(3.0); // Начальное приближение 3.0

        //Корни: x = ±2
        assertEquals(2, root1, 0.0001);
    }
}