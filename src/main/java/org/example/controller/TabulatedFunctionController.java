package org.example.controller;

import org.example.DTO.TabulatedFunction.*;
import org.example.DTO.FunctionType.FunctionTypeDTO;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.Mapper.TabulatedFunctionMapper;
import org.example.service.TabulatedFunctionService;
import org.example.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tabulated-functions")
public class TabulatedFunctionController {

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService;

    @Autowired
    private UserService userService; // Для получения User по userId

    @Autowired
    private TabulatedFunctionMapper tabulatedFunctionMapper;

    // GET /api/tabulated-functions - получить все табулированные функции (список)
    @GetMapping
    public ResponseEntity<List<TabulatedFunctionListDTO>> getAllTabulatedFunctions() {
        List<Tabulated_function> functions = tabulatedFunctionService.findAll();
        List<TabulatedFunctionListDTO> listDTOs = functions.stream()
                .map(tabulatedFunctionMapper::toListDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(listDTOs);
    }

    // GET /api/tabulated-functions/{id} - получить табулированную функцию по ID
    @GetMapping("/{id}")
    public ResponseEntity<TabulatedFunctionResponseDTO> getTabulatedFunctionById(@PathVariable Long id) {
        Optional<Tabulated_function> funcOpt = tabulatedFunctionService.findById(id);
        if (funcOpt.isPresent()) {
            TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(funcOpt.get());
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/tabulated-functions - создать новую табулированную функцию
    @PostMapping
    public ResponseEntity<TabulatedFunctionResponseDTO> createTabulatedFunction(@Valid @RequestBody TabulatedFunctionCreateDTO createDTO) {
        Optional<User> userOpt = userService.findById(createDTO.getUserId());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().build(); // Пользователь не найден
        }

        Tabulated_function functionToSave = tabulatedFunctionMapper.toEntity(createDTO, userOpt.get());
        Tabulated_function savedFunction = tabulatedFunctionService.save(functionToSave);
        TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(savedFunction);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // PUT /api/tabulated-functions/{id} - обновить табулированную функцию
    @PutMapping("/{id}")
    public ResponseEntity<TabulatedFunctionResponseDTO> updateTabulatedFunction(@PathVariable Long id, @Valid @RequestBody TabulatedFunctionUpdateDTO updateDTO) {
        Optional<Tabulated_function> existingFuncOpt = tabulatedFunctionService.findById(id);
        if (!existingFuncOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Tabulated_function existingFunc = existingFuncOpt.get();
        tabulatedFunctionMapper.partialUpdateFromDTO(updateDTO, existingFunc);
        Tabulated_function updatedFunc = tabulatedFunctionService.save(existingFunc);
        TabulatedFunctionResponseDTO responseDTO = tabulatedFunctionMapper.toResponseDTO(updatedFunc);
        return ResponseEntity.ok(responseDTO);
    }

    // DELETE /api/tabulated-functions/{id} - удалить табулированную функцию
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTabulatedFunction(@PathVariable Long id) {
        if (tabulatedFunctionService.findById(id).isPresent()) {
            tabulatedFunctionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}