package org.example.DTO.Auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ на успешную аутентификацию")
public class AuthResponse {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "admin")
    private String username;

    @Schema(description = "Роль пользователя", example = "ADMIN")
    private String role;

    @Schema(description = "Сообщение", example = "Успешная аутентификация")
    private String message;

    public AuthResponse(Long id, String username, String role, String message) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.message = message;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
}