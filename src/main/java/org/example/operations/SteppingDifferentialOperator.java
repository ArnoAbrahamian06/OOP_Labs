package org.example.operations;

import org.example.functions.MathFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunction>  {
    protected double step;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SteppingDifferentialOperator(double step) {
        logger.debug("Создание SteppingDifferentialOperator с шагом: {}", step);
        validStep(step);
        this.step = step;
        logger.debug("SteppingDifferentialOperator успешно создан");
    }

    protected void validStep(double step) {
        logger.trace("Валидация шага: {}", step);

        if (step <= 0) {
            String errorMsg = "Шаг должен быть положительным: " + step;
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        if (Double.isInfinite(step)) {
            String errorMsg = "Шаг не может равняться бесконечности: " + step;
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        if (Double.isNaN(step)) {
            String errorMsg = "Шаг не может быть NaN";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        logger.trace("Шаг {} прошел валидацию", step);
    }

    public double getStep() {
        logger.trace("Получение шага: {}", step);
        return step;
    }

    public void setStep(double step) {
        logger.debug("Установка нового шага: {} (предыдущий: {})", step, this.step);
        validStep(step);
        this.step = step;
        logger.info("Шаг успешно изменен на: {}", step);
    }

    //@Override
    //public abstract MathFunction derive(MathFunction function);
}
