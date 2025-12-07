package org.example.controller;

import org.example.DTO.FunctionType.*;
import org.example.DTO.TabulatedFunction.TabulatedFunctionDTO;
import org.example.entity.Function_type;
import org.example.entity.Tabulated_function;
import org.example.Mapper.FunctionTypeMapper;
import org.example.service.FunctionTypeService;
import org.example.service.TabulatedFunctionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/function-types")
public class FunctionTypeController {

    @Autowired
    private FunctionTypeService functionTypeService;

    @Autowired
    private TabulatedFunctionService tabulatedFunctionService; // Для получения Tabulated_function по ID

    @Autowired
    private FunctionTypeMapper functionTypeMapper;

    // GET /api/function-types - получить все типы функций (список)
    @GetMapping
    public ResponseEntity<List<FunctionTypeListDTO>> getAllFunctionTypes() {
        List<Function_type> types = functionTypeService.findAll();
        List<FunctionTypeListDTO> listDTOs = types.stream()
                .map(functionTypeMapper::toListDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(listDTOs);
    }

    // GET /api/function-types/{id} - получить тип функции по ID
    @GetMapping("/{id}")
    public ResponseEntity<FunctionTypeResponseDTO> getFunctionTypeById(@PathVariable Long id) {
        Optional<Function_type> typeOpt = functionTypeService.findById(id);
        if (typeOpt.isPresent()) {
            FunctionTypeResponseDTO responseDTO = functionTypeMapper.toResponseDTO(typeOpt.get());
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/function-types - создать новый тип функции
    @PostMapping
    public ResponseEntity<FunctionTypeResponseDTO> createFunctionType(@Valid @RequestBody FunctionTypeCreateDTO createDTO) {
        Optional<Tabulated_function> tabFuncOpt = tabulatedFunctionService.findById(createDTO.getTabulatedFunctionId());
        if (!tabFuncOpt.isPresent()) {
            return ResponseEntity.badRequest().build(); // Табулированная функция не найдена
        }

        Function_type typeToSave = functionTypeMapper.toEntity(createDTO, tabFuncOpt.get());
        Function_type savedType = functionTypeService.save(typeToSave);
        FunctionTypeResponseDTO responseDTO = functionTypeMapper.toResponseDTO(savedType);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // PUT /api/function-types/{id} - обновить тип функции
    @PutMapping("/{id}")
    public ResponseEntity<FunctionTypeResponseDTO> updateFunctionType(@PathVariable Long id, @Valid @RequestBody FunctionTypeUpdateDTO updateDTO) {
        Optional<Function_type> existingTypeOpt = functionTypeService.findById(id);
        if (!existingTypeOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Function_type existingType = existingTypeOpt.get();
        functionTypeMapper.partialUpdateFromDTO(updateDTO, existingType);
        Function_type updatedType = functionTypeService.save(existingType);
        FunctionTypeResponseDTO responseDTO = functionTypeMapper.toResponseDTO(updatedType);
        return ResponseEntity.ok(responseDTO);
    }

    // DELETE /api/function-types/{id} - удалить тип функции
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunctionType(@PathVariable Long id) {
        if (functionTypeService.findById(id).isPresent()) {
            functionTypeService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}