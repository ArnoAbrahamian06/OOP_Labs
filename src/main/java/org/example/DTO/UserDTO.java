package org.example.DTO;

import java.time.LocalDateTime;

public class UserDTO {
    private Integer id;
    private String username;
    private String passwordHash;
    private String role;
    private LocalDateTime created_at;

    // Конструкторы
    public UserDTO() {}

    public UserDTO(Integer id, String username, String role,  LocalDateTime created_at) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.created_at = created_at;
    }

    // Геттеры и сеттеры
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLogin() { return username; }
    public void setLogin(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    @Override
    public String toString() {
        return String.format("UserDTO{id=%d, username='%s', role='%s', created_at=%b}",
                id, username, role, created_at);
    }
}