package org.example.DTO.OpenAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.example.enums.BinaryOperationType;

public class BinaryOperationRequest {

    @NotNull(message = "ID второй функции не может быть пустым")
    @JsonProperty("secondFunctionId")
    private Long secondFunctionId;

    @NotNull(message = "Тип операции не может быть пустым")
    @JsonProperty("operation")
    private BinaryOperationType operation;
    // Геттеры и сеттеры
    public Long getSecondFunctionId() {
        return secondFunctionId;
    }

    public void setSecondFunctionId(Long secondFunctionId) {
        this.secondFunctionId = secondFunctionId;
    }

    public BinaryOperationType getOperation() {
        return operation;
    }

    public void setOperation(BinaryOperationType operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "BinaryOperationRequest{" +
                "secondFunctionId=" + secondFunctionId +
                ", operation='" + operation + '\'' +
                '}';
    }
}