// DTOTransformerTest.java
package org.example.db_service.DTO;

import org.example.db_service.User;
import org.example.db_service.FunctionType;
import org.example.db_service.TabulatedFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DTOTransformerTest {

    private User testUser;
    private FunctionType testFunctionType;
    private TabulatedFunction testTabulatedFunction;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setLogin("testuser");
        testUser.setPasswordHash("hashedpassword");
        testUser.setCreatedTime(LocalDateTime.now());
        testUser.setRole("USER");

        testFunctionType = new FunctionType();
        testFunctionType.setId(1);
        testFunctionType.setName("linear");
        testFunctionType.setLocalizedName("Линейная");
        testFunctionType.setPriority(10);
        testFunctionType.setCreatedTime(LocalDateTime.now());
        testFunctionType.setUpdatedTime(LocalDateTime.now());

        testTabulatedFunction = new TabulatedFunction();
        testTabulatedFunction.setId(1L);
        testTabulatedFunction.setUserId(1L);
        testTabulatedFunction.setFunctionTypeId(1);
        testTabulatedFunction.setSerializedData(new byte[]{1, 2, 3, 4, 5});
        testTabulatedFunction.setCreatedTime(LocalDateTime.now());
        testTabulatedFunction.setUpdatedTime(LocalDateTime.now());
        testTabulatedFunction.setUserLogin("testuser");
        testTabulatedFunction.setUserEmail("test@example.com");
        testTabulatedFunction.setFunctionTypeName("linear");
        testTabulatedFunction.setFunctionTypeLocalized("Линейная");
    }

    @Test
    void testUserToUserDTO() {
        // When
        UserDTO result = DTOTransformer.toUserDTO(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getLogin(), result.getLogin());
        assertEquals(testUser.getPasswordHash(), result.getPasswordHash());
        assertEquals(testUser.getCreatedTime(), result.getCreatedTime());
        assertEquals(testUser.getRole(), result.getRole());
    }

    @Test
    void testUserDTOToUser() {
        // Given
        UserDTO userDTO = DTOTransformer.toUserDTO(testUser);

        // When
        User result = DTOTransformer.toUser(userDTO);

        // Then
        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getLogin(), result.getLogin());
        assertEquals(userDTO.getPasswordHash(), result.getPasswordHash());
        assertEquals(userDTO.getCreatedTime(), result.getCreatedTime());
        assertEquals(userDTO.getRole(), result.getRole());
    }

    @Test
    void testFunctionTypeToFunctionTypeDTO() {
        // When
        FunctionTypeDTO result = DTOTransformer.toFunctionTypeDTO(testFunctionType);

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
        FunctionTypeDTO functionTypeDTO = DTOTransformer.toFunctionTypeDTO(testFunctionType);

        // When
        FunctionType result = DTOTransformer.toFunctionType(functionTypeDTO);

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
        TabulatedFunctionDTO result = DTOTransformer.toTabulatedFunctionDTO(testTabulatedFunction);

        // Then
        assertNotNull(result);
        assertEquals(testTabulatedFunction.getId(), result.getId());
        assertEquals(testTabulatedFunction.getUserId(), result.getUserId());
        assertEquals(testTabulatedFunction.getFunctionTypeId(), result.getFunctionTypeId());
        assertArrayEquals(testTabulatedFunction.getSerializedData(), result.getSerializedData());
        assertEquals(testTabulatedFunction.getCreatedTime(), result.getCreatedTime());
        assertEquals(testTabulatedFunction.getUpdatedTime(), result.getUpdatedTime());
        assertEquals(testTabulatedFunction.getUserLogin(), result.getUserLogin());
        assertEquals(testTabulatedFunction.getUserEmail(), result.getUserEmail());
        assertEquals(testTabulatedFunction.getFunctionTypeName(), result.getFunctionTypeName());
        assertEquals(testTabulatedFunction.getFunctionTypeLocalized(), result.getFunctionTypeLocalized());
    }

    @Test
    void testTabulatedFunctionDTOToTabulatedFunction() {
        // Given
        TabulatedFunctionDTO functionDTO = DTOTransformer.toTabulatedFunctionDTO(testTabulatedFunction);

        // When
        TabulatedFunction result = DTOTransformer.toTabulatedFunction(functionDTO);

        // Then
        assertNotNull(result);
        assertEquals(functionDTO.getId(), result.getId());
        assertEquals(functionDTO.getUserId(), result.getUserId());
        assertEquals(functionDTO.getFunctionTypeId(), result.getFunctionTypeId());
        assertArrayEquals(functionDTO.getSerializedData(), result.getSerializedData());
        assertEquals(functionDTO.getCreatedTime(), result.getCreatedTime());
        assertEquals(functionDTO.getUpdatedTime(), result.getUpdatedTime());
        assertEquals(functionDTO.getUserLogin(), result.getUserLogin());
        assertEquals(functionDTO.getUserEmail(), result.getUserEmail());
        assertEquals(functionDTO.getFunctionTypeName(), result.getFunctionTypeName());
        assertEquals(functionDTO.getFunctionTypeLocalized(), result.getFunctionTypeLocalized());
    }

    @Test
    void testUserListTransformation() {
        // Given
        List<User> users = Arrays.asList(testUser, testUser);

        // When
        List<UserDTO> result = DTOTransformer.toUserDTOList(users);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
        assertEquals(testUser.getId(), result.get(1).getId());
    }

    @Test
    void testFunctionTypeListTransformation() {
        // Given
        List<FunctionType> functionTypes = Arrays.asList(testFunctionType, testFunctionType);

        // When
        List<FunctionTypeDTO> result = DTOTransformer.toFunctionTypeDTOList(functionTypes);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testFunctionType.getId(), result.get(0).getId());
        assertEquals(testFunctionType.getId(), result.get(1).getId());
    }

    @Test
    void testTabulatedFunctionListTransformation() {
        // Given
        List<TabulatedFunction> functions = Arrays.asList(testTabulatedFunction, testTabulatedFunction);

        // When
        List<TabulatedFunctionDTO> result = DTOTransformer.toTabulatedFunctionDTOList(functions);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testTabulatedFunction.getId(), result.get(0).getId());
        assertEquals(testTabulatedFunction.getId(), result.get(1).getId());
    }

    @Test
    void testNullUserTransformation() {
        // When
        UserDTO result = DTOTransformer.toUserDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void testNullFunctionTypeTransformation() {
        // When
        FunctionTypeDTO result = DTOTransformer.toFunctionTypeDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void testNullTabulatedFunctionTransformation() {
        // When
        TabulatedFunctionDTO result = DTOTransformer.toTabulatedFunctionDTO(null);

        // Then
        assertNull(result);
    }
}