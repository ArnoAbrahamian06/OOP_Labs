// DTOTransformerTest.java
package org.example.db_service.DTO;

import org.example.DTO.UserDTO;
import org.example.db_service.User;
import org.example.models.Point;
import org.example.models.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserIdsDTOTest {

    private User testUser;
    private Point testFunctionType;
    private Function testTabulatedFunction;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashedpassword");
        testUser.setCreatedTime(LocalDateTime.now());
        testUser.setRole("USER");

        testFunctionType = new Point();
        testFunctionType.setId(1);
        testFunctionType.setName("linear");
        testFunctionType.setLocalizedName("Линейная");
        testFunctionType.setPriority(10);
        testFunctionType.setCreatedTime(LocalDateTime.now());
        testFunctionType.setUpdatedTime(LocalDateTime.now());

        testTabulatedFunction = new Function();
        testTabulatedFunction.setId(1L);
        testTabulatedFunction.setUserId(1L);
        testTabulatedFunction.setFunctionTypeId(1);
        testTabulatedFunction.setSerializedData(new byte[]{1, 2, 3, 4, 5});
        testTabulatedFunction.setCreatedTime(LocalDateTime.now());
        testTabulatedFunction.setUpdatedTime(LocalDateTime.now());
        testTabulatedFunction.setUserUsername("testuser");
        testTabulatedFunction.setUserEmail("test@example.com");
        testTabulatedFunction.setFunctionTypeName("linear");
        testTabulatedFunction.setFunctionTypeLocalized("Линейная");
    }

    @Test
    void testUserToUserDTO() {
        // When
        UserDTO result = UserIdsDTO.toUserDTO(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getPasswordHash(), result.getPasswordHash());
        assertEquals(testUser.getCreatedTime(), result.getCreatedTime());
        assertEquals(testUser.getRole(), result.getRole());
    }

    @Test
    void testUserDTOToUser() {
        // Given
        UserDTO userDTO = UserIdsDTO.toUserDTO(testUser);

        // When
        User result = UserIdsDTO.toUser(userDTO);

        // Then
        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getUsername(), result.getUsername());
        assertEquals(userDTO.getPasswordHash(), result.getPasswordHash());
        assertEquals(userDTO.getCreatedTime(), result.getCreatedTime());
        assertEquals(userDTO.getRole(), result.getRole());
    }

    @Test
    void testFunctionTypeToFunctionTypeDTO() {
        // When
        FunctionTypeDTO result = UserIdsDTO.toFunctionTypeDTO(testFunctionType);

        // Then
        assertNotNull(result);
        assertEquals(testFunctionType.getId(), result.getId());
        assertEquals(testFunctionType.getName(), result.getName());
        assertEquals(testFunctionType.getLocalizedName(), result.getLocalizedName());
        assertEquals(testFunctionType.getPriority(), result.getPriority());
        assertEquals(testFunctionType.getCreatedTime(), result.getCreatedTime());
        assertEquals(testFunctionType.getUpdatedTime(), result.getUpdatedTime());
    }

    @Test
    void testFunctionTypeDTOToFunctionType() {
        // Given
        FunctionTypeDTO functionTypeDTO = UserIdsDTO.toFunctionTypeDTO(testFunctionType);

        // When
        Point result = UserIdsDTO.toFunctionType(functionTypeDTO);

        // Then
        assertNotNull(result);
        assertEquals(functionTypeDTO.getId(), result.getId());
        assertEquals(functionTypeDTO.getName(), result.getName());
        assertEquals(functionTypeDTO.getLocalizedName(), result.getLocalizedName());
        assertEquals(functionTypeDTO.getPriority(), result.getPriority());
        assertEquals(functionTypeDTO.getCreatedTime(), result.getCreatedTime());
        assertEquals(functionTypeDTO.getUpdatedTime(), result.getUpdatedTime());
    }

    @Test
    void testTabulatedFunctionToTabulatedFunctionDTO() {
        // When
        TabulatedFunctionDTO result = UserIdsDTO.toTabulatedFunctionDTO(testTabulatedFunction);

        // Then
        assertNotNull(result);
        assertEquals(testTabulatedFunction.getId(), result.getId());
        assertEquals(testTabulatedFunction.getUserId(), result.getUserId());
        assertEquals(testTabulatedFunction.getFunctionTypeId(), result.getFunctionTypeId());
        assertArrayEquals(testTabulatedFunction.getSerializedData(), result.getSerializedData());
        assertEquals(testTabulatedFunction.getCreatedTime(), result.getCreatedTime());
        assertEquals(testTabulatedFunction.getUpdatedTime(), result.getUpdatedTime());
        assertEquals(testTabulatedFunction.getUserUsername(), result.getUserUsername());
        assertEquals(testTabulatedFunction.getUserEmail(), result.getUserEmail());
        assertEquals(testTabulatedFunction.getFunctionTypeName(), result.getFunctionTypeName());
        assertEquals(testTabulatedFunction.getFunctionTypeLocalized(), result.getFunctionTypeLocalized());
    }

    @Test
    void testTabulatedFunctionDTOToTabulatedFunction() {
        // Given
        TabulatedFunctionDTO functionDTO = UserIdsDTO.toTabulatedFunctionDTO(testTabulatedFunction);

        // When
        Function result = UserIdsDTO.toTabulatedFunction(functionDTO);

        // Then
        assertNotNull(result);
        assertEquals(functionDTO.getId(), result.getId());
        assertEquals(functionDTO.getUserId(), result.getUserId());
        assertEquals(functionDTO.getFunctionTypeId(), result.getFunctionTypeId());
        assertArrayEquals(functionDTO.getSerializedData(), result.getSerializedData());
        assertEquals(functionDTO.getCreatedTime(), result.getCreatedTime());
        assertEquals(functionDTO.getUpdatedTime(), result.getUpdatedTime());
        assertEquals(functionDTO.getUserUsername(), result.getUserUsername());
        assertEquals(functionDTO.getUserEmail(), result.getUserEmail());
        assertEquals(functionDTO.getFunctionTypeName(), result.getFunctionTypeName());
        assertEquals(functionDTO.getFunctionTypeLocalized(), result.getFunctionTypeLocalized());
    }

    @Test
    void testUserListTransformation() {
        // Given
        List<User> users = Arrays.asList(testUser, testUser);

        // When
        List<UserDTO> result = UserIdsDTO.toUserDTOList(users);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(testUser.getId(), result.get(1).getId());
    }

    @Test
    void testFunctionTypeListTransformation() {
        // Given
        List<Point> functionTypes = Arrays.asList(testFunctionType, testFunctionType);

        // When
        List<FunctionTypeDTO> result = UserIdsDTO.toFunctionTypeDTOList(functionTypes);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testFunctionType.getId(), result.get(0).getId());
        assertEquals(testFunctionType.getId(), result.get(1).getId());
    }

    @Test
    void testTabulatedFunctionListTransformation() {
        // Given
        List<Function> functions = Arrays.asList(testTabulatedFunction, testTabulatedFunction);

        // When
        List<TabulatedFunctionDTO> result = UserIdsDTO.toTabulatedFunctionDTOList(functions);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testTabulatedFunction.getId(), result.get(0).getId());
        assertEquals(testTabulatedFunction.getId(), result.get(1).getId());
    }

    @Test
    void testNullUserTransformation() {
        // When
        UserDTO result = UserIdsDTO.toUserDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void testNullFunctionTypeTransformation() {
        // When
        FunctionTypeDTO result = UserIdsDTO.toFunctionTypeDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void testNullTabulatedFunctionTransformation() {
        // When
        TabulatedFunctionDTO result = UserIdsDTO.toTabulatedFunctionDTO(null);

        // Then
        assertNull(result);
    }
}