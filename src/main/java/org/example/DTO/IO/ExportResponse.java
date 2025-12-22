package org.example.DTO.IO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными экспорта")
public class ExportResponse {

    @Schema(description = "Имя файла для скачивания")
    private String filename;

    @Schema(description = "Тип контента файла")
    private String contentType;

    @Schema(description = "Размер данных в байтах")
    private long size;

    // Конструкторы
    public ExportResponse() {
    }

    public ExportResponse(String filename, String contentType, long size) {
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
    }

    // Геттеры и сеттеры
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ExportResponse{" +
                "filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                '}';
    }
}