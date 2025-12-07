package org.example.DTO.FunctionType;

import org.example.DTO.TabulatedFunction.TabulatedFunctionResponseDTO;

// Для включения информации о функции
public class FunctionTypeWithFunctionDTO {

    private FunctionTypeResponseDTO functionType;
    private TabulatedFunctionResponseDTO tabulatedFunction;

    // Конструкторы
    public FunctionTypeWithFunctionDTO() {}

    public FunctionTypeWithFunctionDTO(FunctionTypeResponseDTO functionType,
                                       TabulatedFunctionResponseDTO tabulatedFunction) {
        this.functionType = functionType;
        this.tabulatedFunction = tabulatedFunction;
    }

    // Геттеры и сеттеры
    public FunctionTypeResponseDTO getFunctionType() {
        return functionType;
    }

    public void setFunctionType(FunctionTypeResponseDTO functionType) {
        this.functionType = functionType;
    }

    public TabulatedFunctionResponseDTO getTabulatedFunction() {
        return tabulatedFunction;
    }

    public void setTabulatedFunction(TabulatedFunctionResponseDTO tabulatedFunction) {
        this.tabulatedFunction = tabulatedFunction;
    }

    @Override
    public String toString() {
        return "FunctionTypeWithFunctionDTO{" +
                "functionType=" + functionType +
                ", tabulatedFunction=" + tabulatedFunction +
                '}';
    }
}