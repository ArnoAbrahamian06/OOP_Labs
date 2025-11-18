package org.example.db_service.DTO;

import java.time.LocalDateTime;

public class TabulatedFunctionDTO {
    private Long id;
    private Long userId;
    private Integer functionTypeId;
    private byte[] serializedData;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    // Дополнительные поля для joins
    private String userLogin;
    private String userEmail;
    private String functionTypeName;
    private String functionTypeLocalized;

    // Конструкторы
    public TabulatedFunctionDTO() {}

    public TabulatedFunctionDTO(Long id, Long userId, Integer functionTypeId,
                                byte[] serializedData, LocalDateTime createdTime,
                                LocalDateTime updatedTime) {
        this.id = id;
        this.userId = userId;
        this.functionTypeId = functionTypeId;
        this.serializedData = serializedData;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getFunctionTypeId() { return functionTypeId; }
    public void setFunctionTypeId(Integer functionTypeId) { this.functionTypeId = functionTypeId; }
    public byte[] getSerializedData() { return serializedData; }
    public void setSerializedData(byte[] serializedData) { this.serializedData = serializedData; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
    public String getUserLogin() { return userLogin; }
    public void setUserLogin(String userLogin) { this.userLogin = userLogin; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getFunctionTypeName() { return functionTypeName; }
    public void setFunctionTypeName(String functionTypeName) { this.functionTypeName = functionTypeName; }
    public String getFunctionTypeLocalized() { return functionTypeLocalized; }
    public void setFunctionTypeLocalized(String functionTypeLocalized) { this.functionTypeLocalized = functionTypeLocalized; }

    @Override
    public String toString() {
        return "TabulatedFunctionDTO{id=" + id + ", userId=" + userId +
                ", functionTypeId=" + functionTypeId + ", dataSize=" +
                (serializedData != null ? serializedData.length : 0) +
                " bytes, createdTime=" + createdTime + ", updatedTime=" + updatedTime + "}";
    }
}