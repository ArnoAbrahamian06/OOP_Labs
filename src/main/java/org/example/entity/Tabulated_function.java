package org.example.entity;

import jakarta.persistence.*;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tabulated_functions")
public class Tabulated_function {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "tabulatedFunction", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Function_type> functionTypes = new ArrayList<>();

    @Column(name = "serialized_data")
    private String serializedData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Tabulated_function() {}

    public Tabulated_function(String serializedData, User user) {
        this.serializedData = serializedData;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Геттеры
    public Long getId() { return id; }
    public String getSerializedData() { return serializedData; }
    public User getUser() { return user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<Function_type> getFunctionTypes() { return functionTypes; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setSerializedData(String serializedData) { this.serializedData = serializedData; }
    public void setUser(User user) { this.user = user; } // ДОБАВЬТЕ ЭТОТ СЕТТЕР
    public void setFunctionTypes(List<Function_type> functionTypes) { this.functionTypes = functionTypes; }

    // Исправьте методы добавления/удаления
    public void addFunctionType(Function_type functionType) {
        functionTypes.add(functionType);
        functionType.setTabulatedFunction(this); // Устанавливаем обратную связь
    }

    public void removeFunctionType(Function_type functionType) {
        functionTypes.remove(functionType);
        functionType.setTabulatedFunction(null); // Убераем связь
    }

    @Override
    public String toString() {
        return "Tabulated_function{" +
                "id=" + id +
                ", serializedData='" + serializedData +
                '}';
    }
}