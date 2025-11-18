package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tabulated_functions")
public class Tabulated_function {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)

    // Связь One-to-Many с Function_Types
    @OneToMany(mappedBy = "tabulatedFunction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Function_type> Functions_types = new ArrayList<>();

    @Column(name = "serialized_data")
    private String serializedData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Связь Many-to-One с User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Обязательные конструкторы
    public Tabulated_function() {}

    public Tabulated_function(String serializedData, User user) {
        this.serializedData = serializedData;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Жизненный цикл Entity - автоматическое обновление дат
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


    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSerializedData() { return serializedData; }
    public void setSerializedData(String serializedData) { this.serializedData = serializedData; }

    public User getUser() { return user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public List<Function_type> getFunction_types() { return Functions_types; }
    public void setFunction_types(List<Function_type> Function_types) { this.Functions_types = Function_types; }

    // Вспомогательные методы
    public void addFunction_types(Function_type Function_type) {
        Functions_types.add(Function_type);
    }

    public void removeFunction_types(Function_type Function_type) {
        Functions_types.remove(Function_type);
    }

    @Override
    public String toString() {
        return "Tabulated_function{" +
                "id=" + id +
                ", serializedData='" + serializedData +
                '}';
    }
}
