package org.example.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitFunction extends ConstantFunction{
    private static final Logger log = LoggerFactory.getLogger(UnitFunction.class);

    public UnitFunction (){
        super(1);
        log.info("Создан объект класса StrictTabulatedFunction");
    }
}
