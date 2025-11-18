package org.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.example.entity.Function_type;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/search")

public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    // Одиночный поиск
    @GetMapping("/users/single/{username}")
    public ResponseEntity<User> findSingleUser(@PathVariable String username) {
        log.info("API: Одиночный поиск пользователя по username: {}", username);
        Optional<User> user = searchService.findSingleUserByUsername(username);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Множественный поиск
    @GetMapping("/users/multiple/role/{role}")
    public ResponseEntity<List<User>> findUsersByRole(@PathVariable String role) {
        log.info("API: Множественный поиск пользователей по роли: {}", role);
        List<User> users = searchService.findMultipleUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    // Поиск с сортировкой
    @GetMapping("/users/sorted")
    public ResponseEntity<List<User>> getSortedUsers(
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        log.info("API: Поиск пользователей с сортировкой по {} {}", sortBy, direction);
        List<User> users = searchService.findAllUsersWithSorting(sortBy, direction);
        return ResponseEntity.ok(users);
    }

    // Поиск в глубину
    @GetMapping("/dfs/user/{userId}")
    public ResponseEntity<List<Object>> depthFirstSearch(@PathVariable Long userId) {
        log.info("API: Поиск в глубину для пользователя ID: {}", userId);
        List<Object> results = searchService.depthFirstSearch(userId);
        return ResponseEntity.ok(results);
    }

    // Поиск в ширину
    @GetMapping("/bfs/user/{userId}")
    public ResponseEntity<List<Object>> breadthFirstSearch(@PathVariable Long userId) {
        log.info("API: Поиск в ширину для пользователя ID: {}", userId);
        List<Object> results = searchService.breadthFirstSearch(userId);
        return ResponseEntity.ok(results);
    }

    // Поиск по иерархии
    @GetMapping("/hierarchy/user/{userId}")
    public ResponseEntity<Map<String, Object>> searchByHierarchy(@PathVariable Long userId) {
        log.info("API: Поиск по иерархии для пользователя ID: {}", userId);
        Map<String, Object> hierarchy = searchService.searchByHierarchy(userId);
        return ResponseEntity.ok(hierarchy);
    }

    // Расширенный поиск
    @GetMapping("/function-types/advanced")
    public ResponseEntity<List<Function_type>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minPriority,
            @RequestParam(required = false) Integer maxPriority,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        log.info("API: Расширенный поиск типов функций");
        List<Function_type> results = searchService.advancedFunctionTypeSearch(
                name, minPriority, maxPriority, sortBy, direction);
        return ResponseEntity.ok(results);
    }

    // Поиск по связанным сущностям
    @GetMapping("/functions/by-type/{typeName}")
    public ResponseEntity<List<Tabulated_function>> findFunctionsByType(@PathVariable String typeName) {
        log.info("API: Поиск функций по типу: {}", typeName);
        List<Tabulated_function> results = searchService.findFunctionsByFunctionTypeName(typeName);
        return ResponseEntity.ok(results);
    }
}