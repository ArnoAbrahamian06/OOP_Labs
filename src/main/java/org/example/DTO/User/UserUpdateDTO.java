package org.example.DTO.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// Для обновления
public class UserUpdateDTO {

    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;

    @Email(message = "Некорректный формат email")
    private String email;

    @Size(min = 6, message = "Пароль должен быть не менее 6 символов")
    private String password;

    // Геттеры и сеттеры
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserUpdateDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}