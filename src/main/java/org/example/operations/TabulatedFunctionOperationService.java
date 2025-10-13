package org.example.operations;

import org.example.functions.*;

public class TabulatedFunctionOperationService {

    public static Point[] asPoints(TabulatedFunction tabulatedFunction){
        int Count = tabulatedFunction.getCount();

        Point[] points = new Point[Count];

        Count = 0;

        for (Point point : tabulatedFunction){
            Point newPoint = new Point(point.x, point.y);
            points[Count] = newPoint;
            Count += 1;
        }

        return points;
    }
}
