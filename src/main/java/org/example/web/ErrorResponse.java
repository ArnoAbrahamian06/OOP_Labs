package org.example.web;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;

public class ErrorResponse {
    private final String error;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String timestamp;
    private final String path;

    public ErrorResponse(String error, String path) {
        this.error = error;
        this.timestamp = Instant.now().toString();
        this.path = path;
    }

    // Getters
    public String getError() { return error; }
    public String getTimestamp() { return timestamp; }
    public String getPath() { return path; }
}