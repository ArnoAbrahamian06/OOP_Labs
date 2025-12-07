package org.example.DTO.FunctionType;

import java.time.LocalDateTime;

// Для частичного вывода
public class FunctionTypeListDTO {

    private Long id;
    private String name;
    private String locName;
    private Integer priority;
    private LocalDateTime createdAt;
    private Long tabulatedFunctionId;
    private String tabulatedFunctionPreview; // короткий превью данных функции

    // Конструкторы
    public FunctionTypeListDTO() {}

    public FunctionTypeListDTO(Long id, String name, String locName, Integer priority,
                               LocalDateTime createdAt, Long tabulatedFunctionId,
                               String tabulatedFunctionPreview) {
        this.id = id;
        this.name = name;
        this.locName = locName;
        this.priority = priority;
        this.createdAt = createdAt;
        this.tabulatedFunctionId = tabulatedFunctionId;
        this.tabulatedFunctionPreview = tabulatedFunctionPreview;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getTabulatedFunctionId() {
        return tabulatedFunctionId;
    }

    public void setTabulatedFunctionId(Long tabulatedFunctionId) {
        this.tabulatedFunctionId = tabulatedFunctionId;
    }

    public String getTabulatedFunctionPreview() {
        return tabulatedFunctionPreview;
    }

    public void setTabulatedFunctionPreview(String tabulatedFunctionPreview) {
        this.tabulatedFunctionPreview = tabulatedFunctionPreview;
    }

    @Override
    public String toString() {
        return "FunctionTypeListDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", locName='" + locName + '\'' +
                ", priority=" + priority +
                ", tabulatedFunctionId=" + tabulatedFunctionId +
                ", tabulatedFunctionPreview='" + tabulatedFunctionPreview + '\'' +
                '}';
    }
}