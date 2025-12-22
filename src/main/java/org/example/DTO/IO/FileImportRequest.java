package org.example.DTO.IO;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Запрос на импорт функции из файла")
public class FileImportRequest {

    @Schema(description = "Файл с данными функции", required = true)
    private MultipartFile file;

    @Schema(description = "Имя функции",
            example = "Моя функция из файла",
            nullable = true)
    private String name;

    // Конструктор по умолчанию
    public FileImportRequest() {
    }

    // Конструктор с параметрами
    public FileImportRequest(MultipartFile file, String name) {
        this.file = file;
        this.name = name;
    }

    // Геттеры и сеттеры
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FileImportRequest{" +
                "file=" + (file != null ? file.getOriginalFilename() : "null") +
                ", name='" + name + '\'' +
                '}';
    }
}