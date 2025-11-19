package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "functions_types", indexes = {
        @Index(name = "idx_ft_name", columnList = "name"),
        @Index(name = "idx_ft_loc_name", columnList = "loc_name"),
        @Index(name = "idx_ft_priority", columnList = "priority"),
        @Index(name = "idx_ft_tabulated_function_id", columnList = "tabulated_function_id")
})
public class Function_type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "loc_name")
    private String locName;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabulated_function_id", nullable = false)
    private Tabulated_function tabulatedFunction;

    public Function_type() {}

    public Function_type(String name, String locName, Integer priority, Tabulated_function tabulated_function) {
        this.name = name;
        this.locName = locName;
        this.priority = priority;
        this.tabulatedFunction = tabulated_function;
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
    public String getName() { return name; }
    public String getLocName() { return locName; }
    public Integer getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Tabulated_function getTabulatedFunction() { return tabulatedFunction; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setLocName(String locName) { this.locName = locName; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public void setTabulatedFunction(Tabulated_function tabulatedFunction) {
        this.tabulatedFunction = tabulatedFunction;
    }

    @Override
    public String toString() {
        return "Function_type{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", loc_name='" + locName + '\'' +
                ", priority=" + priority +
                '}';
    }
}