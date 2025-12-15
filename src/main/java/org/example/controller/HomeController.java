package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "OOP Labs API is running");
        response.put("status", "OK");
        response.put("version", "1.0");
        response.put("documentation", "Available at /swagger-ui.html (if Swagger is configured)");
        return response;
    }
}