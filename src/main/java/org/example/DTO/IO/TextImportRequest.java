package org.example.DTO.IO;

public class TextImportRequest {
    private String data;
    private String name;

    // Конструктор по умолчанию
    public TextImportRequest() {
    }

    // Конструктор с параметрами
    public TextImportRequest(String data, String name) {
        this.data = data;
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}