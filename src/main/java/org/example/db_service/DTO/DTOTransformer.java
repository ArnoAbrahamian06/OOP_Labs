// DTOTransformer.java
package org.example.db_service.DTO;

import org.example.db_service.User;
import org.example.db_service.FunctionType;
import org.example.db_service.TabulatedFunction;
import org.example.db_service.DTO.UserDTO;
import org.example.db_service.DTO.FunctionTypeDTO;
import org.example.db_service.DTO.TabulatedFunctionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class DTOTransformer {
    private static final Logger logger = LoggerFactory.getLogger(DTOTransformer.class);

    // User трансформации
    public static UserDTO toUserDTO(User user) {
        logger.debug("Преобразование User в UserDTO: {}", user);
        if (user == null) {
            logger.warn("Попытка преобразования null User в UserDTO");
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setPasswordHash(user.getPasswordHash());
        dto.setCreatedTime(user.getCreatedTime());
        dto.setRole(user.getRole());

        logger.debug("Результат преобразования UserDTO: {}", dto);
        return dto;
    }

    public static User toUser(UserDTO userDTO) {
        logger.debug("Преобразование UserDTO в User: {}", userDTO);
        if (userDTO == null) {
            logger.warn("Попытка преобразования null UserDTO в User");
            return null;
        }

        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setLogin(userDTO.getLogin());
        user.setPasswordHash(userDTO.getPasswordHash());
        user.setCreatedTime(userDTO.getCreatedTime());
        user.setRole(userDTO.getRole());

        logger.debug("Результат преобразования User: {}", user);
        return user;
    }

    public static List<UserDTO> toUserDTOList(List<User> users) {
        logger.debug("Преобразование списка User в UserDTO, размер: {}", users.size());
        return users.stream()
                .map(DTOTransformer::toUserDTO)
                .collect(Collectors.toList());
    }

    // FunctionType трансформации
    public static FunctionTypeDTO toFunctionTypeDTO(FunctionType functionType) {
        logger.debug("Преобразование FunctionType в FunctionTypeDTO: {}", functionType);
        if (functionType == null) {
            logger.warn("Попытка преобразования null FunctionType в FunctionTypeDTO");
            return null;
        }

        FunctionTypeDTO dto = new FunctionTypeDTO();
        dto.setId(functionType.getId());
        dto.setName(functionType.getName());
        dto.setLocalizedName(functionType.getLocalizedName());
        dto.setPriority(functionType.getPriority());
        dto.setCreatedTime(functionType.getCreatedTime());
        dto.setUpdatedTime(functionType.getUpdatedTime());

        logger.debug("Результат преобразования FunctionTypeDTO: {}", dto);
        return dto;
    }

    public static FunctionType toFunctionType(FunctionTypeDTO functionTypeDTO) {
        logger.debug("Преобразование FunctionTypeDTO в FunctionType: {}", functionTypeDTO);
        if (functionTypeDTO == null) {
            logger.warn("Попытка преобразования null FunctionTypeDTO в FunctionType");
            return null;
        }

        FunctionType functionType = new FunctionType();
        functionType.setId(functionTypeDTO.getId());
        functionType.setName(functionTypeDTO.getName());
        functionType.setLocalizedName(functionTypeDTO.getLocalizedName());
        functionType.setPriority(functionTypeDTO.getPriority());
        functionType.setCreatedTime(functionTypeDTO.getCreatedTime());
        functionType.setUpdatedTime(functionTypeDTO.getUpdatedTime());

        logger.debug("Результат преобразования FunctionType: {}", functionType);
        return functionType;
    }

    public static List<FunctionTypeDTO> toFunctionTypeDTOList(List<FunctionType> functionTypes) {
        logger.debug("Преобразование списка FunctionType в FunctionTypeDTO, размер: {}", functionTypes.size());
        return functionTypes.stream()
                .map(DTOTransformer::toFunctionTypeDTO)
                .collect(Collectors.toList());
    }

    // TabulatedFunction трансформации
    public static TabulatedFunctionDTO toTabulatedFunctionDTO(TabulatedFunction function) {
        logger.debug("Преобразование TabulatedFunction в TabulatedFunctionDTO: {}", function);
        if (function == null) {
            logger.warn("Попытка преобразования null TabulatedFunction в TabulatedFunctionDTO");
            return null;
        }

        TabulatedFunctionDTO dto = new TabulatedFunctionDTO();
        dto.setId(function.getId());
        dto.setUserId(function.getUserId());
        dto.setFunctionTypeId(function.getFunctionTypeId());
        dto.setSerializedData(function.getSerializedData());
        dto.setCreatedTime(function.getCreatedTime());
        dto.setUpdatedTime(function.getUpdatedTime());
        dto.setUserLogin(function.getUserLogin());
        dto.setUserEmail(function.getUserEmail());
        dto.setFunctionTypeName(function.getFunctionTypeName());
        dto.setFunctionTypeLocalized(function.getFunctionTypeLocalized());

        logger.debug("Результат преобразования TabulatedFunctionDTO: {}", dto);
        return dto;
    }

    public static TabulatedFunction toTabulatedFunction(TabulatedFunctionDTO functionDTO) {
        logger.debug("Преобразование TabulatedFunctionDTO в TabulatedFunction: {}", functionDTO);
        if (functionDTO == null) {
            logger.warn("Попытка преобразования null TabulatedFunctionDTO в TabulatedFunction");
            return null;
        }

        TabulatedFunction function = new TabulatedFunction();
        function.setId(functionDTO.getId());
        function.setUserId(functionDTO.getUserId());
        function.setFunctionTypeId(functionDTO.getFunctionTypeId());
        function.setSerializedData(functionDTO.getSerializedData());
        function.setCreatedTime(functionDTO.getCreatedTime());
        function.setUpdatedTime(functionDTO.getUpdatedTime());
        function.setUserLogin(functionDTO.getUserLogin());
        function.setUserEmail(functionDTO.getUserEmail());
        function.setFunctionTypeName(functionDTO.getFunctionTypeName());
        function.setFunctionTypeLocalized(functionDTO.getFunctionTypeLocalized());

        logger.debug("Результат преобразования TabulatedFunction: {}", function);
        return function;
    }

    public static List<TabulatedFunctionDTO> toTabulatedFunctionDTOList(List<TabulatedFunction> functions) {
        logger.debug("Преобразование списка TabulatedFunction в TabulatedFunctionDTO, размер: {}", functions.size());
        return functions.stream()
                .map(DTOTransformer::toTabulatedFunctionDTO)
                .collect(Collectors.toList());
    }
}