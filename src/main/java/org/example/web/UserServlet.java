package org.example.web;

import org.example.DAO.UserDAO;
import org.example.DTO.UserDTO;
import org.example.auth.AuthorizationService;
import org.example.auth.PasswordUtil;
import org.example.mapper.UserMapper;
import org.example.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/users/*")
public class UserServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logRequest(req);
        long startTime = System.currentTimeMillis();
        try {
            // Проверка аутентификации для всех GET запросов
            if (!checkAccess(req, resp, null, null)) {
                return;
            }

            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllUsers(req, resp);
            } else if (pathInfo.matches("/\\d+")) {
                handleGetUserById(pathInfo.substring(1), req, resp);
            } else if (pathInfo.startsWith("/search/by-login/")) {
                handleSearchByLogin(pathInfo.substring("/search/by-login/".length()), req, resp);
            } else if (pathInfo.startsWith("/search/by-role/")) {
                handleSearchByRole(pathInfo.substring("/search/by-role/".length()), req, resp);
            } else if (pathInfo.equals("/search/by-ids")) {
                // Этот метод обрабатывается в POST запросе согласно OpenAPI
                handleError(resp, 405, "Метод не поддерживается для GET запроса", req.getRequestURI());
            } else {
                handleError(resp, 400, "Неверный путь", req.getRequestURI());
            }
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("GET запрос обработан за {} мс", executionTime);
        } catch (Exception e) {
            logger.error("Ошибка при обработке GET запроса: {}", e.getMessage(), e);
            handleError(resp, 500, "Внутренняя ошибка сервера", req.getRequestURI());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logRequest(req);
        long startTime = System.currentTimeMillis();
        try {
            // Проверка аутентификации для всех POST запросов
            if (!checkAccess(req, resp, null, null)) {
                return;
            }

            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("")) {
                handleCreateUser(req, resp);
            } else if (pathInfo.equals("/search/by-ids")) {
                handleBatchSearchByIds(req, resp);
            } else {
                handleError(resp, 400, "Неверный путь", req.getRequestURI());
            }
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("POST запрос обработан за {} мс", executionTime);
        } catch (Exception e) {
            logger.error("Ошибка при обработке POST запроса: {}", e.getMessage(), e);
            handleError(resp, 500, "Внутренняя ошибка сервера", req.getRequestURI());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logRequest(req);
        long startTime = System.currentTimeMillis();
        try {
            // Проверка аутентификации для всех PUT запросов
            if (!checkAccess(req, resp, null, null)) {
                return;
            }

            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                handleUpdateUser(pathInfo.substring(1), req, resp);
            } else {
                handleError(resp, 400, "Неверный путь", req.getRequestURI());
            }
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("PUT запрос обработан за {} мс", executionTime);
        } catch (Exception e) {
            logger.error("Ошибка при обработке PUT запроса: {}", e.getMessage(), e);
            handleError(resp, 500, "Внутренняя ошибка сервера", req.getRequestURI());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logRequest(req);
        long startTime = System.currentTimeMillis();
        try {
            // Проверка аутентификации для всех DELETE запросов
            if (!checkAccess(req, resp, null, null)) {
                return;
            }

            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                handleDeleteUserById(pathInfo.substring(1), req, resp);
            } else if (pathInfo != null && pathInfo.startsWith("/search/by-login/")) {
                handleDeleteByLogin(pathInfo.substring("/search/by-login/".length()), req, resp);
            } else {
                handleError(resp, 400, "Неверный путь", req.getRequestURI());
            }
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("DELETE запрос обработан за {} мс", executionTime);
        } catch (Exception e) {
            logger.error("Ошибка при обработке DELETE запроса: {}", e.getMessage(), e);
            handleError(resp, 500, "Внутренняя ошибка сервера", req.getRequestURI());
        }
    }

    private void handleGetAllUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Получение списка всех пользователей");

        // Только администраторы могут видеть всех пользователей
        if (!checkAdminAccess(req, resp)) {
            return;
        }

        String sortField = req.getParameter("sortField");
        boolean ascending = getBooleanParam(req, "ascending", true);
        List<User> users = userDAO.findAll();
        logger.debug("Получено {} пользователей из базы данных", users.size());
        List<UserDTO> userDTOs = users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        SortingUtils.sortUsers(userDTOs, sortField, ascending);
        writeJson(resp, 200, userDTOs);
        logger.info("Отправлен список всех пользователей. Количество: {}", userDTOs.size());
    }

    private void handleGetUserById(String idStr, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Получение пользователя по ID: {}", idStr);
        try {
            int id = Integer.parseInt(idStr);
            ValidationUtils.validateId(id, "пользователь");

            User currentUser = AuthorizationService.getCurrentUser(req);
            java.util.Optional<User> requestedUserOpt = userDAO.findById(id);

            if (!requestedUserOpt.isPresent()) {
                logger.warn("Пользователь не найден по ID: {}", id);
                handleError(resp, 404, "Пользователь не найден", "/users/" + id);
                return;
            }

            User requestedUser = requestedUserOpt.get();

            // Пользователь может видеть только свой профиль или админ может видеть всех
            if (!AuthorizationService.isAdmin(req) && !currentUser.getId().equals(id)) {
                logger.warn("Пользователь {} пытается получить данные другого пользователя с ID {}",
                        currentUser.getUsername(), id);
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Недостаточно прав для просмотра этого профиля");
                return;
            }

            UserDTO userDTO = UserMapper.toDTO(requestedUser);
            writeJson(resp, 200, userDTO);
            logger.info("Найден пользователь по ID: {}", id);
        } catch (NumberFormatException e) {
            logger.error("Неверный формат ID: {}", idStr, e);
            handleError(resp, 400, "Неверный формат ID", "/users/" + idStr);
        }
    }

    private void handleSearchByLogin(String login, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Поиск пользователя по логину: {}", login);
        if (login == null || login.trim().isEmpty()) {
            logger.error("Логин не может быть пустым");
            handleError(resp, 400, "Логин не может быть пустым", "/users/search/by-login/" + login);
            return;
        }

        java.util.Optional<User> requestedUserOpt = userDAO.findByUsername(login);
        if (!requestedUserOpt.isPresent()) {
            logger.warn("Пользователь не найден с логином: {}", login);
            handleError(resp, 404, "Пользователь не найден", "/users/search/by-login/" + login);
            return;
        }

        User requestedUser = requestedUserOpt.get();
        User currentUser = AuthorizationService.getCurrentUser(req);

        // Пользователь может искать только себя или админ может искать всех
        if (!AuthorizationService.isAdmin(req) && !currentUser.getUsername().equals(login)) {
            logger.warn("Пользователь {} пытается найти данные другого пользователя с логином {}",
                    currentUser.getUsername(), login);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Недостаточно прав для поиска этого пользователя");
            return;
        }

        UserDTO userDTO = UserMapper.toDTO(requestedUser);
        writeJson(resp, 200, userDTO);
        logger.info("Найден пользователь с логином: {}", login);
    }

    private void handleSearchByRole(String role, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Поиск пользователей по роли: {}", role);
        if (role == null || role.trim().isEmpty()) {
            logger.error("Роль не может быть пустой");
            handleError(resp, 400, "Роль не может быть пустой", "/users/search/by-role/" + role);
            return;
        }

        // Только администраторы могут искать по ролям
        if (!checkAdminAccess(req, resp)) {
            return;
        }

        String sortField = req.getParameter("sortField");
        boolean ascending = getBooleanParam(req, "ascending", true);
        List<User> users = userDAO.findByRole(role);
        logger.debug("Найдено {} пользователей с ролью: {}", users.size(), role);
        List<UserDTO> userDTOs = users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        SortingUtils.sortUsers(userDTOs, sortField, ascending);
        writeJson(resp, 200, userDTOs);
        logger.info("Отправлен список пользователей с ролью: {}. Количество: {}", role, userDTOs.size());
    }

    private void handleCreateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Создание нового пользователя");
        try {
            // Только администраторы могут создавать пользователей
            if (!checkAdminAccess(req, resp)) {
                return;
            }

            UserDTO userDTO = objectMapper.readValue(req.getInputStream(), UserDTO.class);
            logger.debug("Получены данные для создания пользователя: {}", userDTO);
            ValidationUtils.validateUserDTO(userDTO);

            User user = UserMapper.toEntity(userDTO);
            // Хеширование пароля
            if (userDTO.getPasswordHash() != null && !userDTO.getPasswordHash().isEmpty()) {
                user.setPasswordHash(PasswordUtil.hashPassword(userDTO.getPasswordHash()));
            }

            User savedUser = userDAO.insert(user);
            if (savedUser != null && savedUser.getId() != null) {
                UserDTO savedUserDTO = UserMapper.toDTO(savedUser);
                writeJson(resp, 201, savedUserDTO);
                logger.info("Создан новый пользователь с ID: {} администратором {}",
                        savedUser.getId(), AuthorizationService.getCurrentUser(req).getUsername());
            } else {
                logger.error("Не удалось создать пользователя");
                handleError(resp, 400, "Не удалось создать пользователя", "/users");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка валидации при создании пользователя: {}", e.getMessage());
            handleError(resp, 400, e.getMessage(), "/users");
        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            handleError(resp, 500, "Ошибка при создании пользователя", "/users");
        }
    }

    private void handleUpdateUser(String idStr, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Обновление пользователя с ID: {}", idStr);
        try {
            int id = Integer.parseInt(idStr);
            ValidationUtils.validateId(id, "пользователь");

            User currentUser = AuthorizationService.getCurrentUser(req);
            java.util.Optional<User> existingUserOpt = userDAO.findById(id);

            if (!existingUserOpt.isPresent()) {
                logger.warn("Пользователь не найден для обновления с ID: {}", id);
                handleError(resp, 404, "Пользователь не найден", "/users/" + id);
                return;
            }

            User existingUser = existingUserOpt.get();

            // Проверка прав на обновление
            if (!AuthorizationService.isAdmin(req) && !currentUser.getId().equals(id)) {
                logger.warn("Пользователь {} пытается обновить данные другого пользователя с ID {}",
                        currentUser.getUsername(), id);
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Недостаточно прав для обновления этого пользователя");
                return;
            }

            UserDTO userDTO = objectMapper.readValue(req.getInputStream(), UserDTO.class);
            logger.debug("Получены данные для обновления пользователя ID {}: {}", id, userDTO);
            userDTO.setId(id); // Устанавливаем ID из пути

            // Обычные пользователи не могут менять роль
            if (!AuthorizationService.isAdmin(req)) {
                userDTO.setRole(existingUser.getRole());
            }

            ValidationUtils.validateUserDTO(userDTO);
            User user = UserMapper.toEntity(userDTO);

            // Обработка пароля
            if (userDTO.getPasswordHash() != null && !userDTO.getPasswordHash().isEmpty()) {
                user.setPasswordHash(PasswordUtil.hashPassword(userDTO.getPasswordHash()));
            } else {
                user.setPasswordHash(existingUser.getPasswordHash()); // Сохраняем старый пароль
            }

            if (userDAO.update(user)) {
                writeJson(resp, 200, userDTO);
                logger.info("Пользователь с ID {} успешно обновлен пользователем {}",
                        id, currentUser.getUsername());
            } else {
                logger.warn("Пользователь не найден для обновления с ID: {}", id);
                handleError(resp, 404, "Пользователь не найден", "/users/" + id);
            }
        } catch (NumberFormatException e) {
            logger.error("Неверный формат ID: {}", idStr, e);
            handleError(resp, 400, "Неверный формат ID", "/users/" + idStr);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка валидации при обновлении пользователя: {}", e.getMessage());
            handleError(resp, 400, e.getMessage(), "/users/" + idStr);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя с ID {}: {}", idStr, e.getMessage(), e);
            handleError(resp, 500, "Ошибка при обновлении пользователя", "/users/" + idStr);
        }
    }

    private void handleDeleteUserById(String idStr, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Удаление пользователя по ID: {}", idStr);
        try {
            int id = Integer.parseInt(idStr);
            ValidationUtils.validateId(id, "пользователь");

            // Только администраторы могут удалять пользователей
            if (!checkAdminAccess(req, resp)) {
                return;
            }

            if (userDAO.delete(id)) {
                resp.setStatus(204); // No Content
                logger.info("Пользователь с ID {} успешно удален администратором {}",
                        id, AuthorizationService.getCurrentUser(req).getUsername());
            } else {
                logger.warn("Пользователь не найден для удаления с ID: {}", id);
                handleError(resp, 404, "Пользователь не найден", "/users/" + id);
            }
        } catch (NumberFormatException e) {
            logger.error("Неверный формат ID: {}", idStr, e);
            handleError(resp, 400, "Неверный формат ID", "/users/" + idStr);
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя с ID {}: {}", idStr, e.getMessage(), e);
            handleError(resp, 500, "Ошибка при удалении пользователя", "/users/" + idStr);
        }
    }

    private void handleDeleteByLogin(String login, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Удаление пользователя по логину: {}", login);
        if (login == null || login.trim().isEmpty()) {
            logger.error("Логин не может быть пустым");
            handleError(resp, 400, "Логин не может быть пустым", "/users/search/by-login/" + login);
            return;
        }

        // Только администраторы могут удалять пользователей
        if (!checkAdminAccess(req, resp)) {
            return;
        }

        java.util.Optional<User> userOpt = userDAO.findByUsername(login);
        if (!userOpt.isPresent()) {
            logger.warn("Пользователь не найден для удаления с логином: {}", login);
            handleError(resp, 404, "Пользователь не найден", "/users/search/by-login/" + login);
            return;
        }

        if (userDAO.deleteByUsername(login)) {
            resp.setStatus(204); // No Content
            logger.info("Пользователь с логином {} успешно удален администратором {}",
                    login, AuthorizationService.getCurrentUser(req).getUsername());
        } else {
            logger.warn("Пользователь не найден для удаления с логином: {}", login);
            handleError(resp, 404, "Пользователь не найден", "/api/users/search/by-login/" + login);
        }
    }

    private void handleBatchSearchByIds(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.debug("Множественный поиск пользователей по IDs");
        try {
            // Только администраторы могут выполнять множественный поиск
            if (!checkAdminAccess(req, resp)) {
                return;
            }

            // Читаем JSON с ID пользователями
            IdsRequest request = objectMapper.readValue(req.getInputStream(), IdsRequest.class);
            logger.debug("Получены ID для поиска: {}", request.getIds());
            ValidationUtils.validateIds(request.getIds(), "пользователь");

            List<UserDTO> result = request.getIds().stream()
                    .map(id -> {
                        java.util.Optional<User> userOpt = userDAO.findById(id);
                        return userOpt.map(UserMapper::toDTO).orElse(null);
                    })
                    .filter(userDTO -> userDTO != null)
                    .collect(Collectors.toList());

            writeJson(resp, 200, result);
            logger.info("Найдено {} пользователей из {} запрошенных", result.size(), request.getIds().size());
        } catch (Exception e) {
            logger.error("Ошибка при множественном поиске пользователей: {}", e.getMessage(), e);
            handleError(resp, 400, "Ошибка при поиске пользователей", "/api/users/search/by-ids");
        }
    }

    // Вспомогательный класс для обработки запросов с массивом ID
    private static class IdsRequest {
        private List<Integer> ids;
        public List<Integer> getIds() {
            return ids;
        }
        public void setIds(List<Integer> ids) {
            this.ids = ids;
        }
    }
}