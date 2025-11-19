package org.example.controller;

import org.example.entity.Function_type;
import org.example.entity.Role;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.repository.FunctionTypeRepository;
import org.example.repository.TabulatedFunctionRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class SearchControllerSortingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private FunctionTypeRepository functionTypeRepository;

    @BeforeEach
    void setUp() {
        // Очистка и создание тестовых данных для сортировки
        functionTypeRepository.deleteAll();
        tabulatedFunctionRepository.deleteAll();
        userRepository.deleteAll();

        // Создание пользователей с разными username для тестирования сортировки
        User user1 = new User();
        user1.setUsername("zeta");
        user1.setEmail("zeta@example.com");
        user1.setPasswordHash("pass1");
        user1.setRole(Role.USER);
        user1.setCreatedAt(LocalDateTime.now().minusDays(3));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("alpha");
        user2.setEmail("alpha@example.com");
        user2.setPasswordHash("pass2");
        user2.setRole(Role.ADMIN);
        user2.setCreatedAt(LocalDateTime.now().minusDays(1));
        userRepository.save(user2);

        User user3 = new User();
        user3.setUsername("beta");
        user3.setEmail("beta@example.com");
        user3.setPasswordHash("pass3");
        user3.setRole(Role.MODERATOR);
        user3.setCreatedAt(LocalDateTime.now().minusDays(2));
        userRepository.save(user3);

        // Создание типов функций для тестирования сортировки
        Tabulated_function func = new Tabulated_function();
        func.setSerializedData("test data");
        func.setUser(user1);
        tabulatedFunctionRepository.save(func);

        Function_type type1 = new Function_type();
        type1.setName("gamma_function");
        type1.setLocName("Гамма");
        type1.setPriority(10);
        type1.setTabulatedFunction(func);
        functionTypeRepository.save(type1);

        Function_type type2 = new Function_type();
        type2.setName("alpha_function");
        type2.setLocName("Альфа");
        type2.setPriority(5);
        type2.setTabulatedFunction(func);
        functionTypeRepository.save(type2);

        Function_type type3 = new Function_type();
        type3.setName("beta_type");
        type3.setLocName("Бета");
        type3.setPriority(15);
        type3.setTabulatedFunction(func);
        functionTypeRepository.save(type3);
    }

    @Test
    void testGetSortedUsers_ByName_Asc() throws Exception {
        mockMvc.perform(get("/api/search/users/sorted")
                        .param("sortBy", "username")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username").value("alpha"))
                .andExpect(jsonPath("$[1].username").value("beta"))
                .andExpect(jsonPath("$[2].username").value("zeta"));
    }

    @Test
    void testGetSortedUsers_ByName_Desc() throws Exception {
        mockMvc.perform(get("/api/search/users/sorted")
                        .param("sortBy", "username")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username").value("zeta"))
                .andExpect(jsonPath("$[1].username").value("beta"))
                .andExpect(jsonPath("$[2].username").value("alpha"));
    }

    @Test
    void testGetSortedUsers_ByCreatedAt_Default() throws Exception {
        mockMvc.perform(get("/api/search/users/sorted")
                        .param("sortBy", "createdAt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username").value("zeta")) // Самый старый
                .andExpect(jsonPath("$[2].username").value("alpha")); // Самый новый
    }

    @Test
    void testGetSortedUsers_WithDefaultParameters() throws Exception {
        mockMvc.perform(get("/api/search/users/sorted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].username").value("alpha")) // Сортировка по username asc по умолчанию
                .andExpect(jsonPath("$[1].username").value("beta"))
                .andExpect(jsonPath("$[2].username").value("zeta"));
    }

    @Test
    void testAdvancedSearch_WithSortingByName() throws Exception {
        mockMvc.perform(get("/api/search/function-types/advanced")
                        .param("sortBy", "name")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("alpha_function"))
                .andExpect(jsonPath("$[1].name").value("beta_type"))
                .andExpect(jsonPath("$[2].name").value("gamma_function"));
    }

    @Test
    void testAdvancedSearch_WithSortingByPriority_Desc() throws Exception {
        mockMvc.perform(get("/api/search/function-types/advanced")
                        .param("sortBy", "priority")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].priority").value(15)) // beta_type
                .andExpect(jsonPath("$[1].priority").value(10)) // gamma_function
                .andExpect(jsonPath("$[2].priority").value(5)); // alpha_function
    }

    @Test
    void testAdvancedSearch_WithFilteringAndSorting() throws Exception {
        mockMvc.perform(get("/api/search/function-types/advanced")
                        .param("minPriority", "6")
                        .param("maxPriority", "12")
                        .param("sortBy", "name")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("gamma_function"))
                .andExpect(jsonPath("$[0].priority").value(10));
    }

    @Test
    void testAdvancedSearch_WithNameFilterAndSorting() throws Exception {
        mockMvc.perform(get("/api/search/function-types/advanced")
                        .param("name", "function") // Ищем подстроку, которая есть в alpha_function и gamma_function
                        .param("sortBy", "priority")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // alpha_function и gamma_function
                .andExpect(jsonPath("$[0].name").value("gamma_function")) // Приоритет 10
                .andExpect(jsonPath("$[1].name").value("alpha_function")); // Приоритет 5
    }

    @Test
    void testAdvancedSearch_WithExactNameMatch() throws Exception {
        mockMvc.perform(get("/api/search/function-types/advanced")
                        .param("name", "alpha_function")
                        .param("sortBy", "priority")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("alpha_function"))
                .andExpect(jsonPath("$[0].priority").value(5));
    }

    @Test
    void testAdvancedSearch_WithNoMatchingName() throws Exception {
        mockMvc.perform(get("/api/search/function-types/advanced")
                        .param("name", "nonexistent")
                        .param("sortBy", "name")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testAdvancedSearch_WithPriorityRangeOnly() throws Exception {
        mockMvc.perform(get("/api/search/function-types/advanced")
                        .param("minPriority", "8")
                        .param("maxPriority", "12")
                        .param("sortBy", "name")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("gamma_function"))
                .andExpect(jsonPath("$[0].priority").value(10));
    }

    @Test
    void testAdvancedSearch_WithLetterAFilter() throws Exception {
        mockMvc.perform(get("/api/search/function-types/advanced")
                        .param("name", "a")
                        .param("sortBy", "name")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))) // alpha_function и gamma_function
                .andExpect(jsonPath("$[0].name").value("alpha_function"))
                .andExpect(jsonPath("$[2].name").value("gamma_function"));
    }
}