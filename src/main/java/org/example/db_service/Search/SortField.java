package org.example.db_service.Search;

//Поле для сортировки

public class SortField {
    private String fieldName;
    private SortDirection direction;

    public SortField(String fieldName, SortDirection direction) {
        this.fieldName = fieldName;
        this.direction = direction;
    }

    // Геттеры
    public String getFieldName() { return fieldName; }
    public SortDirection getDirection() { return direction; }
}