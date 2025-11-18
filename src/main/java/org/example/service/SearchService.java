package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.entity.Function_type;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.repository.FunctionTypeRepository;
import org.example.repository.TabulatedFunctionRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service

public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Autowired
    private FunctionTypeRepository functionTypeRepository;

    // ОДИНОЧНЫЙ ПОИСК
    public Optional<User> findSingleUserByUsername(String username) {
        log.info("Выполнение одиночного поиска пользователя по username: {}", username);
        Optional<User> result = userRepository.findByUsername(username);
        log.info("Результат одиночного поиска: {}", result.isPresent() ? "найден" : "не найден");
        return result;
    }

    public Optional<User> findSingleUserByEmail(String email) {
        log.info("Выполнение одиночного поиска пользователя по email: {}", email);
        Optional<User> result = userRepository.findByEmail(email);
        log.info("Результат одиночного поиска: {}", result.isPresent() ? "найден" : "не найден");
        return result;
    }

    // МНОЖЕСТВЕННЫЙ ПОИСК
    public List<User> findMultipleUsersByRole(String role) {
        log.info("Выполнение множественного поиска пользователей по роли: {}", role);
        List<User> results = userRepository.findByRole(role);
        log.info("Найдено пользователей: {}", results.size());
        return results;
    }

    public List<Function_type> findMultipleFunctionTypesByName(String name) {
        log.info("Выполнение множественного поиска типов функций по имени: {}", name);
        List<Function_type> results = functionTypeRepository.findByName(name);
        log.info("Найдено типов функций: {}", results.size());
        return results;
    }

    // ПОИСК С СОРТИРОВКОЙ ПО ПОЛЯМ
    public List<User> findAllUsersWithSorting(String sortBy, String direction) {
        log.info("Поиск всех пользователей с сортировкой по полю: {}, направление: {}", sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(Sort.Direction.DESC, sortBy)
                : Sort.by(Sort.Direction.ASC, sortBy);

        // В реальном приложении нужно добавить метод в репозиторий
        // Для демонстрации используем существующие методы и сортируем в памяти
        List<User> users = userRepository.findAll();
        users.sort((u1, u2) -> {
            switch (sortBy) {
                case "username":
                    return direction.equalsIgnoreCase("desc")
                            ? u2.getUsername().compareTo(u1.getUsername())
                            : u1.getUsername().compareTo(u2.getUsername());
                case "createdAt":
                    return direction.equalsIgnoreCase("desc")
                            ? u2.getCreatedAt().compareTo(u1.getCreatedAt())
                            : u1.getCreatedAt().compareTo(u2.getCreatedAt());
                default:
                    return 0;
            }
        });

        log.info("Найдено и отсортировано пользователей: {}", users.size());
        return users;
    }

    public List<Function_type> findFunctionTypesWithSorting(String sortBy, String direction) {
        log.info("Поиск типов функций с сортировкой по полю: {}, направление: {}", sortBy, direction);

        List<Function_type> types = functionTypeRepository.findAll();
        types.sort((t1, t2) -> {
            switch (sortBy) {
                case "name":
                    return direction.equalsIgnoreCase("desc")
                            ? t2.getName().compareTo(t1.getName())
                            : t1.getName().compareTo(t2.getName());
                case "priority":
                    return direction.equalsIgnoreCase("desc")
                            ? t2.getPriority().compareTo(t1.getPriority())
                            : t1.getPriority().compareTo(t2.getPriority());
                case "createdAt":
                    return direction.equalsIgnoreCase("desc")
                            ? t2.getCreatedAt().compareTo(t1.getCreatedAt())
                            : t1.getCreatedAt().compareTo(t2.getCreatedAt());
                default:
                    return 0;
            }
        });

        log.info("Найдено и отсортировано типов функций: {}", types.size());
        return types;
    }

    // ПОИСК В ГЛУБИНУ (DFS) - для иерархических данных
    @Transactional(readOnly = true)
    public List<Object> depthFirstSearch(Long userId) {
        log.info("Запуск поиска в глубину для пользователя ID: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("Пользователь с ID {} не найден", userId);
            return Collections.emptyList();
        }

        User user = userOpt.get();
        List<Object> result = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        performDFS(user, result, visited);

        log.info("Поиск в глубину завершен. Найдено элементов: {}", result.size());
        return result;
    }

    private void performDFS(Object current, List<Object> result, Set<Long> visited) {
        if (current instanceof User) {
            User user = (User) current;
            if (visited.contains(user.getId())) return;

            visited.add(user.getId());
            result.add(user);
            log.debug("DFS: посещен пользователь: {}", user.getUsername());

            // Рекурсивно обходим табулированные функции пользователя
            for (Tabulated_function tf : user.getTabulated_functions()) {
                performDFS(tf, result, visited);
            }

        } else if (current instanceof Tabulated_function) {
            Tabulated_function tf = (Tabulated_function) current;
            if (visited.contains(tf.getId())) return;

            visited.add(tf.getId());
            result.add(tf);
            log.debug("DFS: посещена табулированная функция: {}", tf.getId());

            // Рекурсивно обходим типы функций
            for (Function_type ft : tf.getFunctionTypes()) {
                performDFS(ft, result, visited);
            }

        } else if (current instanceof Function_type) {
            Function_type ft = (Function_type) current;
            if (visited.contains(ft.getId())) return;

            visited.add(ft.getId());
            result.add(ft);
            log.debug("DFS: посещен тип функции: {}", ft.getName());
        }
    }

    // ПОИСК В ШИРИНУ (BFS) - для иерархических данных
    @Transactional(readOnly = true)
    public List<Object> breadthFirstSearch(Long userId) {
        log.info("Запуск поиска в ширину для пользователя ID: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("Пользователь с ID {} не найден", userId);
            return Collections.emptyList();
        }

        List<Object> result = new ArrayList<>();
        Queue<Object> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        User user = userOpt.get();
        queue.add(user);
        visited.add(user.getId());

        while (!queue.isEmpty()) {
            Object current = queue.poll();
            result.add(current);

            if (current instanceof User) {
                User currentUser = (User) current;
                log.debug("BFS: посещен пользователь: {}", currentUser.getUsername());

                // Добавляем все табулированные функции пользователя
                for (Tabulated_function tf : currentUser.getTabulated_functions()) {
                    if (!visited.contains(tf.getId())) {
                        queue.add(tf);
                        visited.add(tf.getId());
                    }
                }

            } else if (current instanceof Tabulated_function) {
                Tabulated_function tf = (Tabulated_function) current;
                log.debug("BFS: посещена табулированная функция: {}", tf.getId());

                // Добавляем все типы функций
                for (Function_type ft : tf.getFunctionTypes()) {
                    if (!visited.contains(ft.getId())) {
                        queue.add(ft);
                        visited.add(ft.getId());
                    }
                }

            } else if (current instanceof Function_type) {
                Function_type ft = (Function_type) current;
                log.debug("BFS: посещен тип функции: {}", ft.getName());
                // У типов функций нет дочерних элементов в нашей модели
            }
        }

        log.info("Поиск в ширину завершен. Найдено элементов: {}", result.size());
        return result;
    }

    // ПОИСК ПО ИЕРАРХИИ
    @Transactional(readOnly = true)
    public Map<String, Object> searchByHierarchy(Long userId) {
        log.info("Запуск поиска по иерархии для пользователя ID: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("Пользователь с ID {} не найден", userId);
            return Collections.emptyMap();
        }

        User user = userOpt.get();
        Map<String, Object> hierarchy = new LinkedHashMap<>();

        // Строим иерархию
        hierarchy.put("user", user);
        hierarchy.put("tabulatedFunctions", user.getTabulated_functions());

        List<Map<String, Object>> functionsWithTypes = new ArrayList<>();
        for (Tabulated_function tf : user.getTabulated_functions()) {
            Map<String, Object> functionData = new HashMap<>();
            functionData.put("function", tf);
            functionData.put("types", tf.getFunctionTypes());
            functionsWithTypes.add(functionData);
        }

        hierarchy.put("functionsWithTypes", functionsWithTypes);

        log.info("Поиск по иерархии завершен. Уровней иерархии: {}", functionsWithTypes.size());
        return hierarchy;
    }

    // РАСШИРЕННЫЙ ПОИСК С ФИЛЬТРАЦИЕЙ
    public List<Function_type> advancedFunctionTypeSearch(
            String name,
            Integer minPriority,
            Integer maxPriority,
            String sortField,
            String sortDirection) {

        log.info("Расширенный поиск типов функций. Параметры: name={}, minPriority={}, maxPriority={}, sort={} {}",
                name, minPriority, maxPriority, sortField, sortDirection);

        List<Function_type> results = functionTypeRepository.findAll();

        // Применяем фильтры
        if (name != null && !name.isEmpty()) {
            results = results.stream()
                    .filter(ft -> ft.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
            log.debug("Применен фильтр по имени. Осталось элементов: {}", results.size());
        }

        if (minPriority != null) {
            results = results.stream()
                    .filter(ft -> ft.getPriority() >= minPriority)
                    .collect(Collectors.toList());
            log.debug("Применен фильтр по минимальному приоритету. Осталось элементов: {}", results.size());
        }

        if (maxPriority != null) {
            results = results.stream()
                    .filter(ft -> ft.getPriority() <= maxPriority)
                    .collect(Collectors.toList());
            log.debug("Применен фильтр по максимальному приоритету. Осталось элементов: {}", results.size());
        }

        // Применяем сортировку
        if (sortField != null && !sortField.isEmpty()) {
            results.sort((ft1, ft2) -> {
                int comparison = 0;
                switch (sortField) {
                    case "name":
                        comparison = ft1.getName().compareTo(ft2.getName());
                        break;
                    case "priority":
                        comparison = ft1.getPriority().compareTo(ft2.getPriority());
                        break;
                    case "createdAt":
                        comparison = ft1.getCreatedAt().compareTo(ft2.getCreatedAt());
                        break;
                    default:
                        comparison = 0;
                }
                return "desc".equalsIgnoreCase(sortDirection) ? -comparison : comparison;
            });
            log.debug("Применена сортировка по полю: {}", sortField);
        }

        log.info("Расширенный поиск завершен. Найдено элементов: {}", results.size());
        return results;
    }

    // ПОИСК ПО СВЯЗАННЫМ СУЩНОСТЯМ
    @Transactional(readOnly = true)
    public List<Tabulated_function> findFunctionsByFunctionTypeName(String functionTypeName) {
        log.info("Поиск табулированных функций по имени типа функции: {}", functionTypeName);
        List<Tabulated_function> results = tabulatedFunctionRepository.findByFunctionTypeName(functionTypeName);
        log.info("Найдено табулированных функций: {}", results.size());
        return results;
    }

    public List<Tabulated_function> findFunctionsWithMinTypes(int minTypes) {
        log.info("Поиск табулированных функций с минимальным количеством типов: {}", minTypes);
        List<Tabulated_function> results = tabulatedFunctionRepository.findWithMinFunctionTypes(minTypes);
        log.info("Найдено функций с {}+ типами: {}", minTypes, results.size());
        return results;
    }
}