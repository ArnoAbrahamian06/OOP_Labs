package org.example.DTO.Auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на аутентификацию пользователя")
public class AuthRequest {

    @Schema(
            description = "Имя пользователя",
            example = "admin",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @Schema(
            description = "Пароль пользователя",
            example = "admin123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return password; }
    public void setPasswordHash(String password) { this.password = password; }
}