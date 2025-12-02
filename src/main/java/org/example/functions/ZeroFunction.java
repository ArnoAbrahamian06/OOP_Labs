package org.example.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class    ZeroFunction extends ConstantFunction{
    private static final Logger log = LoggerFactory.getLogger(ZeroFunction.class);

    public ZeroFunction (){
        super(0);
        log.info("Создан объект класса ZeroFunction");
    }
}
