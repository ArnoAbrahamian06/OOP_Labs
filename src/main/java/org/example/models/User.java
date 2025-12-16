package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private String role;
    private LocalDateTime created_at;

    // Конструкторы
    public User() {}

    public User(String username, String role, String passwordHash, LocalDateTime created_at) {
        this.username = username;
        this.role = role;
        this.passwordHash = passwordHash;
        this.created_at = created_at;
    }

    // Геттеры и сеттеры
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    @Override
    public String toString() {
        return String.format("UserDTO{id=%d, username='%s', role='%s', created_at=%s}",
                id, username, role, created_at);
    }


}