package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.entity.PointEntity;
import org.example.entity.Tabulated_function;
import org.example.entity.User;
import org.example.entity.Role;
import org.example.repository.TabulatedFunctionRepository;
import org.example.repository.UserRepository;
import org.example.repository.PointRepository;
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
    private PointRepository pointRepository;

    // ОДИНОЧНЫЙ ПОИСК
    public Optional<User> findSingleUserByUsername(String username) {
        log.info("Выполнение одиночного поиска пользователя по username: {}", username);
        Optional<User> result = userRepository.findByUsername(username);
        log.info("Результат одиночного поиска: {}", result.isPresent() ? "найден" : "не найден");
        return result;
    }

    // Поиск функций по ID пользователя
    public List<Tabulated_function> findFunctionsByUserId(Long userId) {
        log.info("Поиск табулированных функций по ID пользователя: {}", userId);
        // Используем метод репозитория, который мы добавили ранее
        List<Tabulated_function> results = tabulatedFunctionRepository.findByUserId(userId);
        log.info("Найдено функций для пользователя {}: {}", userId, results.size());
        return results;
    }



    // МНОЖЕСТВЕННЫЙ ПОИСК
    public List<User> findMultipleUsersByRole(Role role) {
        log.info("Выполнение множественного поиска пользователей по роли: {}", role);
        List<User> results = userRepository.findByRole(role);
        log.info("Найдено пользователей: {}", results.size());
        return results;
    }


    // ПОИСК С СОРТИРОВКОЙ ПО ПОЛЯМ
    public List<User> findAllUsersWithSorting(String sortBy, String direction) {
        log.info("Поиск всех пользователей с сортировкой по полю: {}, направление: {}", sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(Sort.Direction.DESC, sortBy)
                : Sort.by(Sort.Direction.ASC, sortBy);

        // Используем метод репозитория с сортировкой
        List<User> users = userRepository.findAll(sort);

        log.info("Найдено и отсортировано пользователей: {}", users.size());
        return users;
    }

    // ПОИСК В ГЛУБИНУ (DFS) - для иерархических данных (User -> Tabulated_function -> Point)
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
        Set<Long> visitedUsers = new HashSet<>();
        Set<Long> visitedFunctions = new HashSet<>();
        Set<Long> visitedPoints = new HashSet<>();

        performDFS(user, result, visitedUsers, visitedFunctions, visitedPoints);

        log.info("Поиск в глубину завершен. Найдено элементов: {}", result.size());
        return result;
    }

    // Обновлённый метод DFS для новой иерархии
    private void performDFS(Object current, List<Object> result, Set<Long> visitedUsers, Set<Long> visitedFunctions, Set<Long> visitedPoints) {
        if (current instanceof User) {
            User user = (User) current;
            if (visitedUsers.contains(user.getId())) return;

            visitedUsers.add(user.getId());
            result.add(user);
            log.debug("DFS: посещен пользователь: {}", user.getUsername());

            // Рекурсивно обходим табулированные функции пользователя
            for (Tabulated_function tf : user.getTabulated_functions()) {
                performDFS(tf, result, visitedUsers, visitedFunctions, visitedPoints);
            }

        } else if (current instanceof Tabulated_function) {
            Tabulated_function tf = (Tabulated_function) current;
            if (visitedFunctions.contains(tf.getId())) return;

            visitedFunctions.add(tf.getId());
            result.add(tf);
            log.debug("DFS: посещена табулированная функция: {}", tf.getId());

            // Рекурсивно обходим точки функции
            for (PointEntity point : tf.getPoints()) {
                performDFS(point, result, visitedUsers, visitedFunctions, visitedPoints);
            }

        } else if (current instanceof PointEntity) {
            PointEntity point = (PointEntity) current;
            if (visitedPoints.contains(point.getId())) return;

            visitedPoints.add(point.getId());
            result.add(point);
            log.debug("DFS: посещена точка: ({}, {})", point.getX(), point.getY());
        }
    }

    // ПОИСК В ШИРИНУ (BFS) - для иерархических данных (User -> Tabulated_function -> Point)
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
        Set<Long> visitedUsers = new HashSet<>();
        Set<Long> visitedFunctions = new HashSet<>();
        Set<Long> visitedPoints = new HashSet<>();

        User user = userOpt.get();
        queue.add(user);
        visitedUsers.add(user.getId());

        while (!queue.isEmpty()) {
            Object current = queue.poll();
            result.add(current);

            if (current instanceof User) {
                User currentUser = (User) current;
                log.debug("BFS: посещен пользователь: {}", currentUser.getUsername());

                // Добавляем все табулированные функции пользователя
                for (Tabulated_function tf : currentUser.getTabulated_functions()) {
                    if (!visitedFunctions.contains(tf.getId())) {
                        queue.add(tf);
                        visitedFunctions.add(tf.getId());
                    }
                }

            } else if (current instanceof Tabulated_function) {
                Tabulated_function tf = (Tabulated_function) current;
                log.debug("BFS: посещена табулированная функция: {}", tf.getId());

                // Добавляем все точки функции
                for (PointEntity point : tf.getPoints()) {
                    if (!visitedPoints.contains(point.getId())) {
                        queue.add(point);
                        visitedPoints.add(point.getId());
                    }
                }

            } else if (current instanceof PointEntity) {
                PointEntity point = (PointEntity) current;
                log.debug("BFS: посещена точка: ({}, {})", point.getX(), point.getY());
                // У точек нет дочерних элементов в нашей модели
            }
        }

        log.info("Поиск в ширину завершен. Найдено элементов: {}", result.size());
        return result;
    }

    // ПОИСК ПО ИЕРАРХИИ (User -> Tabulated_function -> Point)
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

        List<Map<String, Object>> functionsWithPoints = new ArrayList<>();
        for (Tabulated_function tf : user.getTabulated_functions()) {
            Map<String, Object> functionData = new HashMap<>();
            functionData.put("function", tf);
            functionData.put("points", tf.getPoints());
            functionsWithPoints.add(functionData);
        }

        hierarchy.put("functionsWithPoints", functionsWithPoints);

        log.info("Поиск по иерархии завершен. Уровней иерархии функций: {}", functionsWithPoints.size());
        return hierarchy;
    }

    // РАСШИРЕННЫЙ ПОИСК С ФИЛЬТРАЦИЕЙ
    // Поиск точек по ID функции
    public List<PointEntity> findPointsByFunctionId(Long functionId) {
        log.info("Поиск точек по ID функции: {}", functionId);
        List<PointEntity> results = pointRepository.findByTabulatedFunctionId(functionId);
        log.info("Найдено точек: {}", results.size());
        return results;
    }

    // Поиск точек в диапазоне X и Y
    public List<PointEntity> findPointsInXYRange(Double minX, Double maxX, Double minY, Double maxY) {
        log.info("Поиск точек в диапазоне X: [{} - {}], Y: [{} - {}]", minX, maxX, minY, maxY);
        List<PointEntity> results = pointRepository.findByXBetweenAndYBetween(minX, maxX, minY, maxY);
        log.info("Найдено точек в диапазоне: {}", results.size());
        return results;
    }

    // Поиск функций, содержащих точки с определённым X
    public List<Tabulated_function> findFunctionsByPointX(Double x) {
        log.info("Поиск функций, содержащих точку с X = {}", x);
        List<PointEntity> points = pointRepository.findByX(x);
        Set<Long> functionIds = points.stream()
                .map(point -> point.getTabulatedFunction().getId()) // Получаем ID функций
                .collect(Collectors.toSet());
        List<Tabulated_function> results = tabulatedFunctionRepository.findAllById(functionIds);
        log.info("Найдено функций с точкой X = {}: {}", x, results.size());
        return results;
    }

    // Поиск функций, содержащих точки с определённым Y
    public List<Tabulated_function> findFunctionsByPointY(Double y) {
        log.info("Поиск функций, содержащих точку с Y = {}", y);
        List<PointEntity> points = pointRepository.findByY(y);
        Set<Long> functionIds = points.stream()
                .map(point -> point.getTabulatedFunction().getId()) // Получаем ID функций
                .collect(Collectors.toSet());
        List<Tabulated_function> results = tabulatedFunctionRepository.findAllById(functionIds);
        log.info("Найдено функций с точкой Y = {}: {}", y, results.size());
        return results;
    }

    // Расширенный поиск по точкам
    public List<PointEntity> advancedPointSearch(
            Long tabulatedFunctionId,
            Double minX, Double maxX,
            Double minY, Double maxY,
            String sortField,
            String sortDirection) {

        log.info("Расширенный поиск точек. Параметры: functionId={}, minX={}, maxX={}, minY={}, maxY={}, sort={} {}",
                tabulatedFunctionId, minX, maxX, minY, maxY, sortField, sortDirection);

        List<PointEntity> results = pointRepository.findAll();

        // Применяем фильтры
        if (tabulatedFunctionId != null) {
            results = results.stream()
                    .filter(p -> p.getTabulatedFunction().getId().equals(tabulatedFunctionId))
                    .collect(Collectors.toList());
            log.debug("Применен фильтр по ID функции. Осталось элементов: {}", results.size());
        }

        if (minX != null) {
            results = results.stream()
                    .filter(p -> p.getX() >= minX)
                    .collect(Collectors.toList());
            log.debug("Применен фильтр по minX. Осталось элементов: {}", results.size());
        }

        if (maxX != null) {
            results = results.stream()
                    .filter(p -> p.getX() <= maxX)
                    .collect(Collectors.toList());
            log.debug("Применен фильтр по maxX. Осталось элементов: {}", results.size());
        }

        if (minY != null) {
            results = results.stream()
                    .filter(p -> p.getY() >= minY)
                    .collect(Collectors.toList());
            log.debug("Применен фильтр по minY. Осталось элементов: {}", results.size());
        }

        if (maxY != null) {
            results = results.stream()
                    .filter(p -> p.getY() <= maxY)
                    .collect(Collectors.toList());
            log.debug("Применен фильтр по maxY. Осталось элементов: {}", results.size());
        }

        // Применяем сортировку
        if (sortField != null && !sortField.isEmpty()) {
            results.sort((p1, p2) -> {
                int comparison = 0;
                switch (sortField) {
                    case "x":
                        comparison = Double.compare(p1.getX(), p2.getX());
                        break;
                    case "y":
                        comparison = Double.compare(p1.getY(), p2.getY());
                        break;
                    case "id": // Сортировка по ID точки
                        comparison = p1.getId().compareTo(p2.getId());
                        break;
                    default:
                        comparison = 0;
                }
                return "desc".equalsIgnoreCase(sortDirection) ? -comparison : comparison;
            });
            log.debug("Применена сортировка по полю: {}", sortField);
        }

        log.info("Расширенный поиск точек завершен. Найдено элементов: {}", results.size());
        return results;
    }



    // Поиск функций по количеству точек (заменяет findWithMinFunctionTypes)
    public List<Tabulated_function> findFunctionsWithMinPoints(int minPoints) {
        log.info("Поиск табулированных функций с минимальным количеством точек: {}", minPoints);
        List<Tabulated_function> allFunctions = tabulatedFunctionRepository.findAll();
        List<Tabulated_function> results = allFunctions.stream()
                .filter(f -> f.getPoints().size() >= minPoints) // Фильтрация в памяти
                .collect(Collectors.toList());
        log.info("Найдено функций с {}+ точками: {}", minPoints, results.size());
        return results;
    }

    // Поиск функций по имени (если поле name есть)
    public List<Tabulated_function> findFunctionsByNameContaining(String name) {
        log.info("Поиск табулированных функций по имени (содержит): {}", name);
        List<Tabulated_function> results = tabulatedFunctionRepository.findByNameContaining(name);
        log.info("Найдено функций: {}", results.size());
        return results;
    }
}