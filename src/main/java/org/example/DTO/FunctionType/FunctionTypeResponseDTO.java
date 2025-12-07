package org.example.DTO.FunctionType;

import java.time.LocalDateTime;

// Для полного вывода
public class FunctionTypeResponseDTO {

    private Long id;
    private String name;
    private String locName;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long tabulatedFunctionId;
    private String tabulatedFunctionSerializedData; // для удобства

    // Конструкторы
    public FunctionTypeResponseDTO() {}

    public FunctionTypeResponseDTO(Long id, String name, String locName, Integer priority,
                                   LocalDateTime createdAt, LocalDateTime updatedAt,
                                   Long tabulatedFunctionId, String tabulatedFunctionSerializedData) {
        this.id = id;
        this.name = name;
        this.locName = locName;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tabulatedFunctionId = tabulatedFunctionId;
        this.tabulatedFunctionSerializedData = tabulatedFunctionSerializedData;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getTabulatedFunctionId() {
        return tabulatedFunctionId;
    }

    public void setTabulatedFunctionId(Long tabulatedFunctionId) {
        this.tabulatedFunctionId = tabulatedFunctionId;
    }

    public String getTabulatedFunctionSerializedData() {
        return tabulatedFunctionSerializedData;
    }

    public void setTabulatedFunctionSerializedData(String tabulatedFunctionSerializedData) {
        this.tabulatedFunctionSerializedData = tabulatedFunctionSerializedData;
    }

    @Override
    public String toString() {
        return "FunctionTypeResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", locName='" + locName + '\'' +
                ", priority=" + priority +
                ", tabulatedFunctionId=" + tabulatedFunctionId +
                '}';
    }
}