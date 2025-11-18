package org.example.db_service;

import java.time.LocalDateTime;

public class FunctionType {
    private Integer id;
    private String name;
    private String localizedName;
    private Integer priority;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    // constructors
    public FunctionType() {}

    public FunctionType(String name, String localizedName, Integer priority) {
        this.name = name;
        this.localizedName = localizedName;
        this.priority = priority;
    }

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocalizedName() { return localizedName; }
    public void setLocalizedName(String localizedName) { this.localizedName = localizedName; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    @Override
    public String toString() {
        return "FunctionType{id=" + id + ", name='" + name + "', localizedName='" + localizedName + "', priority=" + priority + "}";
    }
}