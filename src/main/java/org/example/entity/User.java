package org.example.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)

    @Column(name = "role")
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Связь One-to-Many с Tabulated_function
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Tabulated_function> tabulatedFunctions = new ArrayList<>();

    // Обязательные конструкторы
    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.passwordHash = password;
        this.createdAt = LocalDateTime.now();
        this.role = Role.USER; // значение по умолчанию
    }

    // Жизненный цикл Entity - автоматическое обновление дат
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (role == null) {
            role = Role.USER;
        }
    }


    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<Tabulated_function> getTabulated_functions() { return tabulatedFunctions; }
    public void setTabulated_functions(List<Tabulated_function> Tabulated_functions) { this.tabulatedFunctions = Tabulated_functions; }

    // Вспомогательные методы
    public void addTabulated_function(Tabulated_function Tabulated_function) {
        tabulatedFunctions.add(Tabulated_function);
    }

    public void removeTabulated_function(Tabulated_function Tabulated_function) {
        tabulatedFunctions.remove(Tabulated_function);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}