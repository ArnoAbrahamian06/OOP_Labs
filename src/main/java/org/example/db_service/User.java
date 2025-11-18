package org.example.db_service;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String email;
    private String login;
    private String passwordHash;
    private LocalDateTime createdTime;
    private String role;

    // Конструкторы
    public User() {}

    public User(String email, String login, String passwordHash, String role) {
        this.email = email;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // геттеры и сеттеры
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
        return "User{id=" + id + ", email='" + email + "', login='" + login + "', role='" + role + "'}";
    }
}