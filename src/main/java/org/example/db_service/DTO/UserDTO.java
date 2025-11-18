package org.example.db_service.DTO;

import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String email;
    private String login;
    private String passwordHash;
    private LocalDateTime createdTime;
    private String role;

    // Конструкторы
    public UserDTO() {}

    public UserDTO(Long id, String email, String login, String passwordHash,
                   LocalDateTime createdTime, String role) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.passwordHash = passwordHash;
        this.createdTime = createdTime;
        this.role = role;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "UserDTO{id=" + id + ", email='" + email + "', login='" + login +
                "', role='" + role + "', createdTime=" + createdTime + "}";
    }
}