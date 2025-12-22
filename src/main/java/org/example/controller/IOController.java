package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.DTO.IO.TextImportRequest;
import org.example.DTO.OpenAPI.ErrorResponse;
import org.example.DTO.TabulatedFunction.TabulatedFunctionResponseDTO;
import org.example.entity.PointEntity;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.functions.ArrayTabulatedFunction;
import org.example.functions.TabulatedFunction;
import org.example.io.FunctionsIO;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.service.IOService;
import org.example.service.TabulatedFunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
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

    // --- ИМПОРТ ИЗ ТЕКСТА ---

    @Operation(summary = "Импорт из текстового формата", description = "Импортирует функцию из текстового представления с возможностью указать имя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Функция успешно импортирована", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TabulatedFunctionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/import/text", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importFromText(
            @Valid @RequestBody TextImportRequest importRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Импорт функции из текстового формата. Имя: {}", importRequest.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Пользователь не аутентифицирован", null));
        }
        try {
            TabulatedFunction coreFunction = ioService.importFromText(importRequest.getData());
            Tabulated_function importedFunctionEntity = tabulatedFunctionMapper.toSpringTabulatedFunction(coreFunction);
            importedFunctionEntity.setUser(currentUser);
            if (importRequest.getName() != null && !importRequest.getName().trim().isEmpty()) {
                importedFunctionEntity.setName(importRequest.getName());
            }
            Tabulated_function savedFunction = tabulatedFunctionService.save(importedFunctionEntity);
            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(savedFunction);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("ImportFromText: Ошибка при импорте из текста: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при импорте из текста", e.getMessage()));
        }
    }

    // --- ИМПОРТ ИЗ ТЕКСТОВОГО ФАЙЛА ---

    @Operation(summary = "Импорт из текстового файла", description = "Импортирует функцию из текстового файла (.txt)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Функция успешно импортирована", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TabulatedFunctionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных или файл пуст", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/import/text-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importFromTextFile(
            @Parameter(description = "Текстовый файл с функцией", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Имя функции (необязательно)")
            @RequestParam(value = "name", required = false) String name,
            @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Импорт функции из текстового файла. Имя файла: {}, Имя функции: {}", file.getOriginalFilename(), name);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Пользователь не аутентифицирован", null));
        }

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("InvalidData", "Файл пуст", null));
        }

        try {
            // Читаем содержимое файла в строку
            String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);

            // Импортируем функцию из текста
            TabulatedFunction coreFunction = ioService.importFromText(fileContent);

            // Сохраняем как обычно
            Tabulated_function importedFunctionEntity = tabulatedFunctionMapper.toSpringTabulatedFunction(coreFunction);
            importedFunctionEntity.setUser(currentUser);
            if (name != null && !name.trim().isEmpty()) {
                importedFunctionEntity.setName(name);
            }
            Tabulated_function savedFunction = tabulatedFunctionService.save(importedFunctionEntity);
            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(savedFunction);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("ImportFromTextFile: Ошибка при импорте из файла: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при импорте из текстового файла", e.getMessage()));
        }
    }

    // --- ИМПОРТ ИЗ БИНАРНОГО ФАЙЛА ---

    @Operation(summary = "Импорт из бинарного файла", description = "Импортирует функцию из бинарного файла (.bin)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Функция успешно импортирована", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = TabulatedFunctionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/import/binary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importFromBinary(
            @Parameter(description = "Файл с бинарными данными функции", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Имя функции (необязательно)")
            @RequestParam(value = "name", required = false) String name,
            @AuthenticationPrincipal User currentUser
    ) {
        log.info("API: Импорт функции из бинарного файла. Имя файла: {}, Имя функции: {}", file.getOriginalFilename(), name);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", "Пользователь не аутентифицирован", null));
        }

        try {
            byte[] binaryData = file.getBytes();
            TabulatedFunction coreFunction = ioService.importFromBinary(binaryData);
            Tabulated_function importedFunctionEntity = tabulatedFunctionMapper.toSpringTabulatedFunction(coreFunction);
            importedFunctionEntity.setUser(currentUser);
            if (name != null && !name.trim().isEmpty()) {
                importedFunctionEntity.setName(name);
            }
            Tabulated_function savedFunction = tabulatedFunctionService.save(importedFunctionEntity);
            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(savedFunction);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            log.error("ImportFromBinary: Ошибка при импорте из бинарного файла: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("InternalError", "Ошибка при импорте из бинарного файла", e.getMessage()));
        }
    }

    // --- ЭКСПОРТ В ТЕКСТОВОМ ФОРМАТЕ ---

    @Operation(summary = "Экспорт в текстовом формате", description = "Скачать функцию в текстовом представлении (размер массива и точки).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный экспорт", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "404", description = "Функция не найдена", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/{id}/export/text", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> exportAsText(
            @Parameter(description = "ID функции для экспорта", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("API: Экспорт функции ID {} в текстовом формате", id);

        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            log.warn("Функция с ID {} не найдена для экспорта.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("NotFound", "Функция не найдена", null));
        }

        Tabulated_function func = funcOpt.get();

        try {
            List<PointEntity> points = func.getPoints();
            if (points == null || points.isEmpty()) {
                log.warn("Функция с ID {} не содержит точек.", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse("InvalidData", "Функция не содержит точек", null));
            }

            points.sort(Comparator.comparingDouble(PointEntity::getX));

            double[] xValues = new double[points.size()];
            double[] yValues = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                xValues[i] = points.get(i).getX();
                yValues[i] = points.get(i).getY();
            }

            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);

            StringWriter stringWriter = new StringWriter();
            try (BufferedWriter bufferedWriter = new BufferedWriter(stringWriter)) {
                FunctionsIO.writeTabulatedFunction(bufferedWriter, arrayFunc);
                bufferedWriter.flush();
            }

            String textContent = stringWriter.toString();
            byte[] responseBytes = textContent.getBytes(StandardCharsets.UTF_8);

            log.info("Функция ID {} успешно экспортирована в текстовом формате.", id);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"function_" + id + ".txt\"")
                    .body(responseBytes);

        } catch (Exception e) {
            log.error("ExportAsText: Ошибка при экспорте в текст: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("InternalError", "Ошибка при экспорте в текст", e.getMessage()));
        }
    }

    // --- ЭКСПОРТ В ТЕКСТОВОМ ФОРМАТЕ (как строка) ---

    @Operation(summary = "Экспорт в текстовом формате (не файл)", description = "Возвращает содержимое функции в текстовом формате (как строку).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный экспорт", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)),
            @ApiResponse(responseCode = "404", description = "Функция не найдена", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/{id}/export/text-content", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> exportTextContent(
            @Parameter(description = "ID функции для экспорта", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("API: Экспорт содержимого функции ID {} в текстовом формате (как строка)", id);

        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            log.warn("Функция с ID {} не найдена для экспорта.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("NotFound", "Функция не найдена", null));
        }

        Tabulated_function func = funcOpt.get();

        try {
            List<PointEntity> points = func.getPoints();
            if (points == null || points.isEmpty()) {
                log.warn("Функция с ID {} не содержит точек.", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse("InvalidData", "Функция не содержит точек", null));
            }

            points.sort(Comparator.comparingDouble(PointEntity::getX));

            double[] xValues = new double[points.size()];
            double[] yValues = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                xValues[i] = points.get(i).getX();
                yValues[i] = points.get(i).getY();
            }

            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);

            // Используем ioService для получения строки
            String textContent = ioService.exportToText(arrayFunc);

            log.info("Функция ID {} успешно экспортирована в текстовом формате (как строка).", id);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(textContent);

        } catch (Exception e) {
            log.error("ExportTextContent: Ошибка при экспорте в текст (как строка): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("InternalError", "Ошибка при экспорте в текст", e.getMessage()));
        }
    }


    // --- ЭКСПОРТ В БИНАРНОМ ФОРМАТЕ ---

    @Operation(summary = "Экспорт в бинарном формате", description = "Скачать функцию в бинарном представлении.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный экспорт", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
            @ApiResponse(responseCode = "404", description = "Функция не найдена", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/{id}/export/binary", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> exportAsBinary(
            @Parameter(description = "ID функции для экспорта", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("API: Экспорт функции ID {} в бинарном формате", id);

        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isEmpty()) {
            log.warn("Функция с ID {} не найдена для экспорта.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("NotFound", "Функция не найдена", null));
        }

        Tabulated_function func = funcOpt.get();

        try {
            List<PointEntity> points = func.getPoints();
            if (points == null || points.isEmpty()) {
                log.warn("Функция с ID {} не содержит точек.", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse("InvalidData", "Функция не содержит точек", null));
            }

            points.sort(Comparator.comparingDouble(PointEntity::getX));

            double[] xValues = new double[points.size()];
            double[] yValues = new double[points.size()];
            for (int i = 0; i < points.size(); i++) {
                xValues[i] = points.get(i).getX();
                yValues[i] = points.get(i).getY();
            }

            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);

            // Используем метод из FunctionsIO для бинарной записи
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(baos)) {
                FunctionsIO.writeTabulatedFunction(bufferedOutputStream, arrayFunc);
                bufferedOutputStream.flush();
            }

            byte[] binaryData = baos.toByteArray();

            log.info("Функция ID {} успешно экспортирована в бинарном формате.", id);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"function_" + id + ".bin\"")
                    .body(binaryData);

        } catch (Exception e) {
            log.error("ExportAsBinary: Ошибка при экспорте в бинарный формат: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("InternalError", "Ошибка при экспорте в бинарный формат", e.getMessage()));
        }
    }
}