package org.example.operations;

import org.example.functions.MathFunction;

public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunction>  {
    protected double step;

    public SteppingDifferentialOperator(double step) {
        validStep(step);
        this.step = step;
    }

    protected void validStep(double step) {
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг должен быть положительным: " + step);
        }
        if (Double.isInfinite(step)) {
            throw new IllegalArgumentException("Шаг не может равняться бесконечности: " + step);
        }
        if (Double.isNaN(step)) {
            throw new IllegalArgumentException("Шаг не может быть NaN");
        }
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        validStep(step);
        this.step = step;
    }

    //@Override
    //public abstract MathFunction derive(MathFunction function);
}
