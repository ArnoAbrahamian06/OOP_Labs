package org.example.mapper;

import org.example.DTO.UserDTO;
import org.example.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserMapper {
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    public static UserDTO toDTO(User user) {
        if (user == null) {
            logger.warn("Попытка преобразования null-объекта User в DTO");
            return null;
        }

        logger.debug("Преобразование User(id={}) в UserDTO", user.getId());
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getCreated_at()
        );
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) {
            logger.warn("Попытка преобразования null-объекта UserDTO в сущность");
            return null;
        }

        logger.debug("Преобразование UserDTO в User");
        User user = new User();
        user.setId(dto.getId());
        user.setLogin(dto.getUsername());
        user.setRole(dto.getRole());
        user.setPasswordHash(dto.getPasswordHash());
        user.setCreated_at(dto.getCreated_at());
        return user;
    }
}