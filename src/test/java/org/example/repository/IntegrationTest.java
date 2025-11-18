package org.example.repository;

import org.example.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private FunctionTypeRepository functionTypeRepository;

    @Test
    void testCompleteWorkflow() {
        // 1. Создание пользователя
        User user = new User("integration_user", "integration@example.com", "password123");
        user.setRole(Role.ADMIN);
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals(Role.ADMIN, savedUser.getRole());

        // 2. Создание табулированной функции
        Tabulated_function function = new Tabulated_function("serialized_function_data", savedUser);
        Tabulated_function savedFunction = tabulatedFunctionRepository.save(function);

        assertNotNull(savedFunction.getId());
        assertEquals(savedUser.getId(), savedFunction.getUser().getId());

        // 3. Создание типов функций - используйте метод addFunctionType
        Function_type linearType = new Function_type("linear", "Линейная", 1, savedFunction);
        Function_type quadraticType = new Function_type("quadratic", "Квадратичная", 2, savedFunction);
        Function_type exponentialType = new Function_type("exponential", "Экспоненциальная", 3, savedFunction);

        // Добавляем типы через метод, который устанавливает обратную связь
        savedFunction.addFunctionType(linearType);
        savedFunction.addFunctionType(quadraticType);
        savedFunction.addFunctionType(exponentialType);

        // Сохраняем обновленную функцию с типами
        Tabulated_function updatedFunction = tabulatedFunctionRepository.save(savedFunction);

        // 4. Проверка связей
        List<Function_type> functionTypes = functionTypeRepository.findByTabulatedFunctionId(updatedFunction.getId());
        assertEquals(3, functionTypes.size());

        List<Tabulated_function> userFunctions = tabulatedFunctionRepository.findByUserId(savedUser.getId());
        assertEquals(1, userFunctions.size());
        assertEquals(3, functionTypeRepository.countByTabulatedFunctionId(userFunctions.get(0).getId()));

        // 5. Удаление и проверка каскадного удаления
        // Благодаря каскадным настройкам, удаление пользователя должно удалить все связанные сущности
        userRepository.deleteById(savedUser.getId());

        assertFalse(userRepository.findById(savedUser.getId()).isPresent());
        assertEquals(0, tabulatedFunctionRepository.findByUserId(savedUser.getId()).size());
    }
}