package org.example.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "Functions_types")
public class Function_type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)

    @Column(name = "name")
    private String name;

    @Column(name = "loc_name")
    private String locName;

    @Column(name = "priority")
    private String priority;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Связь Many-to-One с Tabulated_function
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Tabulated_function_id", nullable = false)
    private Tabulated_function tabulated_function;

    // Обязательные конструкторы
    public Function_type() {}

    public Function_type(String name, String locName, String priority, Tabulated_function tabulated_function) {
        this.name = name;
        this.locName = locName;
        this.priority = priority;
        this.tabulated_function = tabulated_function;
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocName() { return locName; }
    public void setLocName(String locName) { this.locName = locName; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

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
