package org.example.DTO.OpenAPI;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ErrorResponse {

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;

    @JsonProperty("field")
    private String field;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    // Конструкторы
    public ErrorResponse() {}

    public ErrorResponse(String error, String message, String field) {
        this.error = error;
        this.message = message;
        this.field = field;
        this.timestamp = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", field='" + field + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}