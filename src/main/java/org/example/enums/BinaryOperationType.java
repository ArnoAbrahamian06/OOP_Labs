package org.example.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BinaryOperationType {
    ADD("ADD"),
    SUB("SUB"),
    MULT("MULT"),
    DIV("DIV");

    private final String value; // Приватное поле для хранения строки

    // Конструктор enum
    BinaryOperationType(String value) {
        this.value = value; // Присваиваем строку полю
    }

    // Геттер для получения строкового значения
    @JsonValue
    public String getValue() {
        return value; // Возвращаем строку (например, "ADD")
    }

    // Метод для получения enum по строковому значению (для десериализации из JSON)
    public static BinaryOperationType fromValue(String value) {
        for (BinaryOperationType op : BinaryOperationType.values()) {
            if (op.value.equals(value)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operation: " + value);
    }
}