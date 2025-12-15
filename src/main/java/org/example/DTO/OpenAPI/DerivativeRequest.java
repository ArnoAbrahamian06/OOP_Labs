package org.example.DTO.OpenAPI;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DerivativeRequest {

    @JsonProperty("order")
    private Integer order = 1; // По умолчанию первая производная

    // Геттеры и сеттеры
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "DerivativeRequest{" +
                "order=" + order +
                '}';
    }
}