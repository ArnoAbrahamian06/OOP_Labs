package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.DTO.OpenAPI.ErrorResponse;
import org.example.DTO.TabulatedFunction.TabulatedFunctionResponseDTO;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.service.TabulatedFunctionService;
import org.example.service.IOService;
import org.example.functions.TabulatedFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/functions")
@Tag(name = "Import/Export", description = "Импорт и экспорт функций в разные форматы")
public class IOController {

    private static final Logger log = LoggerFactory.getLogger(IOController.class);

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;

    @Autowired
    private TabulatedFunctionMapper tabulatedFunctionMapper;

    @Autowired
    private IOService ioService;

    // POST /api/functions/import/text - Импорт из текстового формата
    @Operation(summary = "Скачать сериализованную функцию", description = "Возвращает файл .ser")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файл успешно сгенерирован",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Функция не найдена")
    })
    @PostMapping(value = "/import/text", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importFromText(
            @RequestBody String textData,
            @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Импорт функции из текстового формата");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Пользователь не аутентифицирован", null));
        }

        try {
            // 1. Импорт функции из текста в доменный объект
            TabulatedFunction coreFunction = ioService.importFromText(textData);

            // 2. Преобразование доменного объекта в сущность
            Tabulated_function importedFunctionEntity = tabulatedFunctionMapper.toSpringTabulatedFunction(coreFunction);

            importedFunctionEntity.setUser(currentUser);

            Tabulated_function savedFunction = tabulatedFunctionService.save(importedFunctionEntity);

            // 3. Возврат успешного ответа
            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(savedFunction);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (IOException e) {
            log.error("ImportFromText: Ошибка формата данных или ввода-вывода: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("InvalidFormat", "Ошибка формата данных или ввода-вывода при импорте из текста", e.getMessage()));
        } catch (Exception e) {
            log.error("ImportFromText: Непредвиденная ошибка при импорте из текста: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Непредвиденная ошибка при импорте из текста", e.getMessage()));
        }
    }

    // POST /api/functions/import/binary - Импорт из бинарного формата
    @Operation(summary = "Импорт из бинарного формата", description = "Загружает функцию из пользовательского бинарного формата.")
    @PostMapping(value = "/import/binary", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> importFromBinary(
            @RequestBody byte[] binaryData,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Импорт функции из бинарного формата");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Пользователь не аутентифицирован", null));
        }

        try {
            // 1. Импорт функции из бинарных данных
            TabulatedFunction coreFunction = ioService.importFromBinary(binaryData);

            // 2. Преобразование
            Tabulated_function importedFunctionEntity = tabulatedFunctionMapper.toSpringTabulatedFunction(coreFunction);

            importedFunctionEntity.setUser(currentUser);

            Tabulated_function savedFunction = tabulatedFunctionService.save(importedFunctionEntity);

            // 3. Возврат успешного ответа
            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(savedFunction);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (IOException e) {
            log.error("ImportFromText: Ошибка формата данных или ввода-вывода: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("ImportError", "Ошибка формата или ввода-вывода при импорте из бинарного формата", e.getMessage()));
        } catch (Exception e) {
            log.error("ImportFromText: Непредвиденная ошибка при импорте из текста: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Непредвиденная ошибка при импорте из бинарного формата", e.getMessage()));
        }
    }

    // POST /api/functions/import/serialized - Десериализация (Java Object Serialization)
    @Operation(summary = "Импорт из Java Serialized", description = "Загружает функцию из стандартной Java сериализации.")
    @PostMapping(value = "/import/serialized", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> importSerialized(
            @RequestBody byte[] serializedData,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Десериализация функции");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Пользователь не аутентифицирован", null));
        }

        try {
            // 1. Десериализация функции
            TabulatedFunction coreFunction = ioService.deserialize(serializedData);

            // 2. Преобразование
            Tabulated_function importedFunctionEntity = tabulatedFunctionMapper.toSpringTabulatedFunction(coreFunction);

            importedFunctionEntity.setUser(currentUser);

            Tabulated_function savedFunction = tabulatedFunctionService.save(importedFunctionEntity);

            // 3. Возврат успешного ответа
            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(savedFunction);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (ClassNotFoundException e) {
            log.error("Deserialization: Класс функции не найден (ClassNotFoundException): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("ClassNotFound", "Класс функции не найден. Проверьте совместимость версий.", e.getMessage()));
        } catch (IOException e) {
            log.error("Deserialization: Ошибка ввода-вывода при десериализации (неверный формат): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("DeserializationError", "Ошибка ввода-вывода при десериализации (возможно, неверный формат)", e.getMessage()));
        } catch (Exception e) {
            log.error("Deserialization: Непредвиденная ошибка при десериализации: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Непредвиденная ошибка при десериализации", e.getMessage()));
        }
    }

    // Методы экспорта

    // GET /api/functions/{id}/export/text - Экспорт в текстовом формате
    @Operation(summary = "Экспорт в текст", description = "Скачать функцию в текстовом представлении (размер массива и точки).")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(mediaType = "text/plain")))
    @GetMapping(value = "/{id}/export/text", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> exportToText(@PathVariable Long id) {
        log.info("API: Экспорт функции ID: {} в текстовый формат", id);
        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tabulated_function func = funcOpt.get();

        try {
            // 1. Преобразование сущности в доменный объект
            TabulatedFunction coreFunc = tabulatedFunctionMapper.toExternalTabulatedFunction(func);

            // 2. Экспорт в текст
            String textData = ioService.exportToText(coreFunc);

            // 3. Возврат результата с заголовком для скачивания
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"function_" + id + ".txt\"")
                    .body(textData);
        } catch (IOException e) {
            log.error("ExportToText: Ошибка ввода-вывода при экспорте функции ID={} в текст: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("IOException", "Ошибка ввода-вывода при экспорте в текст", e.getMessage()));
        } catch (Exception e) {
            log.error("ExportToText: Непредвиденная ошибка при экспорте функции ID={} в текст: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Непредвиденная ошибка при экспорте в текст", e.getMessage()));
        }
    }

    // GET /api/functions/{id}/export/binary - Экспорт в бинарном формате
    @Operation(summary = "Экспорт в бинарный формат", description = "Скачать функцию в собственном бинарном формате.")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/octet-stream")))
    @GetMapping(value = "/{id}/export/binary", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> exportToBinary(@PathVariable Long id) {
        log.info("API: Экспорт функции ID: {} в бинарный формат", id);
        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tabulated_function func = funcOpt.get();

        try {
            // 1. Преобразование сущности в доменный объект
            TabulatedFunction coreFunc = tabulatedFunctionMapper.toExternalTabulatedFunction(func);

            // 2. Экспорт в бинарные данные
            byte[] binaryData = ioService.exportToBinary(coreFunc);

            // 3. Возврат результата с заголовком для скачивания
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"function_" + id + ".bin\"")
                    .body(binaryData);
        } catch (IOException e) {
            log.error("ImportFromText: Ошибка формата данных или ввода-вывода: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("IOException", "Ошибка ввода-вывода при экспорте в бинарный формат", e.getMessage()));
        } catch (Exception e) {
            log.error("ImportFromText: Непредвиденная ошибка при импорте из текста: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Непредвиденная ошибка при экспорте в бинарный формат", e.getMessage()));
        }
    }

    // GET /api/functions/{id}/export/serialized - Сериализованное представление (Java Object Serialization)
    @Operation(summary = "Экспорт в Java Serialized", description = "Скачать сериализованный объект функции.")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/octet-stream")))
    @GetMapping(value = "/{id}/export/serialized", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> exportSerialized(@PathVariable Long id) {
        log.info("API: Сериализация функции ID: {}", id);
        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tabulated_function func = funcOpt.get();

        try {
            // 1. Преобразование сущности в доменный объект
            TabulatedFunction coreFunc = tabulatedFunctionMapper.toExternalTabulatedFunction(func);

            // 2. Сериализация
            byte[] serializedData = ioService.serialize(coreFunc);

            // 3. Возврат результата с заголовком для скачивания
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"function_" + id + ".ser\"")
                    .body(serializedData);
        } catch (IOException e) {
            log.error("ImportFromText: Ошибка формата данных или ввода-вывода: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("IOException", "Ошибка ввода-вывода при сериализации", e.getMessage()));
        } catch (Exception e) {
            log.error("ImportFromText: Непредвиденная ошибка при импорте из текста: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Непредвиденная ошибка при сериализации", e.getMessage()));
        }
    }
}