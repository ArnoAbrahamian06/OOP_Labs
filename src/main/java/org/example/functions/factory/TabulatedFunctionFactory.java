package org.example.functions.factory;

import org.example.functions.StrictTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.functions.UnmodifiableTabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);

    default TabulatedFunction createUnmodifiable (double[] xValues, double[] yValues){
        TabulatedFunction function = create(xValues, yValues);
        return new UnmodifiableTabulatedFunction(function);
    }

    default TabulatedFunction createStrict(double[] xValues, double[] yValues){
        TabulatedFunction function = create(xValues, yValues);
        return new StrictTabulatedFunction(function);
    }

    default TabulatedFunction createStrictUnmodifiable(double[] xValues, double[] yValues) {
        TabulatedFunction function = create(xValues, yValues);
        TabulatedFunction strictFunction = new StrictTabulatedFunction(function);
        return new UnmodifiableTabulatedFunction(strictFunction);
    }
}


