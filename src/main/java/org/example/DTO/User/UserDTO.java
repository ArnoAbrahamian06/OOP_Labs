package org.example.DTO.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.DTO.TabulatedFunction.TabulatedFunctionDTO;
import org.example.entity.Role;

import java.time.LocalDateTime;
import java.util.List;

// Основной
public class UserDTO {

    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    private Role role;

    private LocalDateTime createdAt;

    private List<TabulatedFunctionDTO> tabulatedFunctions;

    // Конструкторы
    public UserDTO() {}

    public UserDTO(Long id, String username, String email, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TabulatedFunctionDTO> getTabulatedFunctions() {
        return tabulatedFunctions;
    }

    public void setTabulatedFunctions(List<TabulatedFunctionDTO> tabulatedFunctions) {
        this.tabulatedFunctions = tabulatedFunctions;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}