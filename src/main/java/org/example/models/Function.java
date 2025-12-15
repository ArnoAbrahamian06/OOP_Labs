package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

public class Function {
    private Integer id;
    private Integer userId;
    private String name;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    // Конструкторы
    public Function() {
    }

    public Function(Integer id, Integer userId, String name, LocalDateTime created_at, LocalDateTime updated_at) {
        this.userId = userId;
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    // Геттеры и сеттеры
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }


    @Override
    public String toString() {
        return "FunctionDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}