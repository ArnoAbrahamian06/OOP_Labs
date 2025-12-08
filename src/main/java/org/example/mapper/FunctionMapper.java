package org.example.mapper;

import org.example.DTO.FunctionDTO;
import org.example.models.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FunctionMapper {
    private static final Logger logger = LoggerFactory.getLogger(FunctionMapper.class);

    public static FunctionDTO toDTO(Function function) {
        if (function == null) {
            logger.warn("Попытка преобразования null-объекта Function в DTO");
            return null;
        }

        logger.debug("Преобразование Function(id={}) в FunctionDTO", function.getId());
        return new FunctionDTO(
                function.getId(),
                function.getUserId(),
                function.getName(),
                function.getCreated_at(),
                function.getUpdated_at()
        );
    }

    public static Function toEntity(FunctionDTO dto) {
        if (dto == null) {
            logger.warn("Попытка преобразования null-объекта FunctionDTO в сущность");
            return null;
        }

        logger.debug("Преобразование FunctionDTO в Function");
        Function function = new Function();
        function.setId(dto.getId());
        function.setUserId(dto.getUserId());
        function.setName(dto.getName());
        function.setCreated_at(dto.getCreated_at());
        function.setUpdated_at(dto.getUpdated_at());
        return function;
    }
}