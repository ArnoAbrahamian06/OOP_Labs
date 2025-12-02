package org.example.web;

import org.example.db_service.DTO.DTOTransformer;
import org.example.db_service.DTO.TabulatedFunctionDTO;
import org.example.db_service.TabulatedFunction;
import org.example.db_service.TabulatedFunctionRepository;
import org.example.io.FunctionsIO;
import org.example.operations.TabulatedDifferentialOperator;
import org.example.operations.TabulatedFunctionOperationService;
import org.example.functions.factory.ArrayTabulatedFunctionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet для реализации Function Storage API согласно API-contract.yaml.
 *
 * Основная точка входа: /api/functions (base path из OpenAPI: /api).
 *
 * Поддерживаемые операции:
 *  - POST   /api/functions                             (создание функции)
 *  - GET    /api/functions/{id}                        (получение по id)
 *  - PUT    /api/functions/{id}                        (обновление данных)
 *  - DELETE /api/functions/{id}                        (удаление)
 *  - GET    /api/functions/owner/{ownerId}             (по всем функциям пользователя)
 *  - GET    /api/functions/owner/{ownerId}/type/{type} (по функциям пользователя и типу)
 *  - POST   /api/functions/{id}/operations/binary      (арифметические операции)
 *  - POST   /api/functions/{id}/derive                 (производная)
 */
@WebServlet(name = "TabulatedFunctionServlet", urlPatterns = {"/api/functions/*"})
public class TabulatedFunctionServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionServlet.class);
    private final TabulatedFunctionRepository repository = new TabulatedFunctionRepository();
    private final TabulatedFunctionOperationService operationService =
            new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());
    private final TabulatedDifferentialOperator differentialOperator =
            new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // может быть null, "/", "/{id}/operations/binary", "/{id}/derive", ...
        logger.info("Обработка POST в TabulatedFunctionServlet, pathInfo={}", pathInfo);

        if (pathInfo == null || "/".equals(pathInfo)) {
            handleCreateFunction(req, resp);
        } else if (pathInfo.matches("^/\\d+/operations/binary/?$")) {
            handleBinaryOperation(req, resp, extractIdFromPath(pathInfo));
        } else if (pathInfo.matches("^/\\d+/derive/?$")) {
            handleDerive(req, resp, extractIdFromPath(pathInfo));
        } else {
            logger.warn("Неизвестный POST маршрут: {}", pathInfo);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // null, "/", "/{id}", "/owner/{ownerId}", "/owner/{ownerId}/type/{typeId}", ...
        logger.info("Обработка GET в TabulatedFunctionServlet, pathInfo={}", pathInfo);
        resp.setContentType("application/json; charset=UTF-8");

        if (pathInfo == null || "/".equals(pathInfo)) {
            // Для совместимости оставим старый режим поиска через query-параметры
            handleSearchByQueryParams(req, resp);
            return;
        }

        if (pathInfo.matches("^/\\d+/?$")) {
            handleGetById(resp, extractIdFromPath(pathInfo));
        } else if (pathInfo.startsWith("/owner/")) {
            handleOwnerPaths(resp, pathInfo);
        } else {
            logger.warn("Неизвестный GET маршрут: {}", pathInfo);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"Unknown GET path\"}");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // ожидаем "/{id}"
        logger.info("Обработка PUT в TabulatedFunctionServlet, pathInfo={}", pathInfo);

        if (pathInfo == null || !pathInfo.matches("^/\\d+/?$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"PUT allowed only on /functions/{id}\"}");
            }
            return;
        }

        Long id = extractIdFromPath(pathInfo);
        handleUpdateFunction(req, resp, id);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // ожидаем "/{id}"
        logger.info("Обработка DELETE в TabulatedFunctionServlet, pathInfo={}", pathInfo);

        if (pathInfo == null || !pathInfo.matches("^/\\d+/?$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"DELETE allowed only on /functions/{id}\"}");
            }
            return;
        }

        Long id = extractIdFromPath(pathInfo);
        try (PrintWriter out = resp.getWriter()) {
            boolean deleted = repository.deleteById(id);
            if (deleted) {
                logger.info("Функция с ID {} успешно удалена", id);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                logger.warn("Функция с ID {} не найдена для удаления", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Function not found\"}");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при удалении функции ID {}", id, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------- Handlers -------------------

    private void handleCreateFunction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String body = readBody(req);
        logger.debug("Создание функции, тело запроса: {}", body);

        try (PrintWriter out = resp.getWriter()) {
            Long ownerId = extractLongField(body, "ownerId");
            Integer typeId = extractIntField(body, "typeId");
            String serializedBase64 = extractStringField(body, "serializedData");

            if (ownerId == null || typeId == null || serializedBase64 == null) {
                logger.warn("Некорректный FunctionCreateRequest");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"ownerId, typeId и serializedData обязательны\"}");
                return;
            }

            byte[] data = Base64.getDecoder().decode(serializedBase64);
            TabulatedFunction function = new TabulatedFunction(ownerId, typeId, data);
            TabulatedFunction saved = repository.insert(function);

            TabulatedFunctionDTO dto = DTOTransformer.toTabulatedFunctionDTO(saved);
            String location = req.getContextPath() + "/api/functions/" + saved.getId();
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setHeader("Location", location);
            out.write(toFunctionResponseJson(dto));
            logger.info("Функция создана с ID {}, Location={}", saved.getId(), location);
        } catch (SQLException e) {
            logger.error("Ошибка при создании функции", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleGetById(HttpServletResponse resp, Long id) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            TabulatedFunction function = repository.findById(id);
            if (function == null) {
                logger.warn("Функция с ID {} не найдена", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Function not found\"}");
                return;
            }
            TabulatedFunctionDTO dto = DTOTransformer.toTabulatedFunctionDTO(function);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.write(toFunctionResponseJson(dto));
            logger.info("Функция с ID {} успешно возвращена", id);
        } catch (SQLException e) {
            logger.error("Ошибка при получении функции ID {}", id, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleOwnerPaths(HttpServletResponse resp, String pathInfo) throws IOException {
        // /owner/{ownerId} или /owner/{ownerId}/type/{typeId}
        logger.debug("Обработка owner-путей: {}", pathInfo);
        String[] parts = pathInfo.split("/");
        // ["", "owner", "{ownerId}"] или ["", "owner", "{ownerId}", "type", "{typeId}"]
        if (parts.length < 3) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"ownerId is required\"}");
            }
            return;
        }

        Long ownerId;
        try {
            ownerId = Long.valueOf(parts[2]);
        } catch (NumberFormatException e) {
            logger.warn("Некорректный ownerId в пути: {}", parts[2]);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"ownerId must be integer\"}");
            }
            return;
        }

        try (PrintWriter out = resp.getWriter()) {
            if (parts.length == 3) {
                // /owner/{ownerId}
                List<TabulatedFunction> functions = repository.findByUserId(ownerId);
                List<TabulatedFunctionDTO> dtos = DTOTransformer.toTabulatedFunctionDTOList(functions);
                String json = dtos.stream()
                        .map(this::toFunctionResponseJson)
                        .collect(Collectors.joining(",", "[", "]"));
                resp.setStatus(HttpServletResponse.SC_OK);
                out.write(json);
                logger.info("Возвращено {} функций для ownerId {}", dtos.size(), ownerId);
            } else if (parts.length == 5 && "type".equals(parts[3])) {
                Integer typeId;
                try {
                    typeId = Integer.valueOf(parts[4]);
                } catch (NumberFormatException e) {
                    logger.warn("Некорректный typeId в пути: {}", parts[4]);
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write("{\"error\":\"typeId must be integer\"}");
                    return;
                }
                List<TabulatedFunction> functions = repository.findByUserIdAndFunctionTypeId(ownerId, typeId);
                List<TabulatedFunctionDTO> dtos = DTOTransformer.toTabulatedFunctionDTOList(functions);
                String json = dtos.stream()
                        .map(this::toFunctionResponseJson)
                        .collect(Collectors.joining(",", "[", "]"));
                resp.setStatus(HttpServletResponse.SC_OK);
                out.write(json);
                logger.info("Возвращено {} функций для ownerId {} и typeId {}", dtos.size(), ownerId, typeId);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Unsupported owner path\"}");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при получении функций по ownerId/path {}", pathInfo, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleUpdateFunction(HttpServletRequest req, HttpServletResponse resp, Long id) throws IOException {
        String body = readBody(req);
        logger.debug("Обновление функции ID {}, тело: {}", id, body);

        try (PrintWriter out = resp.getWriter()) {
            String serializedBase64 = extractStringField(body, "serializedData");
            if (serializedBase64 == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"serializedData is required for update\"}");
                return;
            }

            byte[] data = Base64.getDecoder().decode(serializedBase64);
            boolean updated = repository.updateSerializedData(id, data);
            if (!updated) {
                logger.warn("Функция ID {} не найдена для обновления", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Function not found\"}");
                return;
            }

            TabulatedFunction function = repository.findById(id);
            TabulatedFunctionDTO dto = DTOTransformer.toTabulatedFunctionDTO(function);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.write(toFunctionResponseJson(dto));
            logger.info("Функция ID {} успешно обновлена", id);
        } catch (SQLException e) {
            logger.error("Ошибка при обновлении функции ID {}", id, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleSearchByQueryParams(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userIdParam = req.getParameter("userId");
        String typeIdParam = req.getParameter("functionTypeId");
        String createdAfterParam = req.getParameter("createdAfter");
        String withDetailsParam = req.getParameter("withDetails");

        logger.debug("Поиск функций по query-параметрам: userId={}, functionTypeId={}, createdAfter={}, withDetails={}",
                userIdParam, typeIdParam, createdAfterParam, withDetailsParam);

        try (PrintWriter out = resp.getWriter()) {
            List<TabulatedFunction> functions;
            if ("true".equalsIgnoreCase(withDetailsParam)) {
                functions = repository.findWithUserAndTypeInfo();
            } else if (userIdParam != null) {
                functions = repository.findByUserId(Long.valueOf(userIdParam));
            } else if (typeIdParam != null) {
                functions = repository.findByFunctionTypeId(Integer.valueOf(typeIdParam));
            } else if (createdAfterParam != null) {
                LocalDateTime time = LocalDateTime.parse(createdAfterParam);
                Timestamp ts = Timestamp.valueOf(time);
                functions = repository.findByCreatedTimeAfter(ts);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Specify one of query parameters: userId, functionTypeId, createdAfter, withDetails\"}");
                return;
            }

            List<TabulatedFunctionDTO> dtos = DTOTransformer.toTabulatedFunctionDTOList(functions);
            String json = dtos.stream()
                    .map(this::toFunctionResponseJson)
                    .collect(Collectors.joining(",", "[", "]"));
            resp.setStatus(HttpServletResponse.SC_OK);
            out.write(json);
        } catch (SQLException e) {
            logger.error("Ошибка при поиске функций по query-параметрам", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleBinaryOperation(HttpServletRequest req, HttpServletResponse resp, Long baseId) throws IOException {
        String body = readBody(req);
        logger.debug("Бинарная операция для функции ID {}, тело: {}", baseId, body);

        try (PrintWriter out = resp.getWriter()) {
            Long secondId = extractLongField(body, "secondFunctionId");
            String operation = extractStringField(body, "operation");

            if (secondId == null || operation == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"secondFunctionId и operation обязательны\"}");
                return;
            }

            TabulatedFunction base = repository.findById(baseId);
            TabulatedFunction second = repository.findById(secondId);
            if (base == null || second == null) {
                logger.warn("Одна из функций не найдена: baseId={}, secondId={}", baseId, secondId);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"One of functions not found\"}");
                return;
            }

            // десериализуем математические функции
            org.example.functions.TabulatedFunction baseMath = deserializeMathFunction(base.getSerializedData());
            org.example.functions.TabulatedFunction secondMath = deserializeMathFunction(second.getSerializedData());

            org.example.functions.TabulatedFunction resultMath;
            switch (operation) {
                case "ADD":
                    resultMath = operationService.add(baseMath, secondMath);
                    break;
                case "SUB":
                    resultMath = operationService.sub(baseMath, secondMath);
                    break;
                case "MULT":
                    resultMath = operationService.mult(baseMath, secondMath);
                    break;
                case "DIV":
                    resultMath = operationService.div(baseMath, secondMath);
                    break;
                default:
                    logger.warn("Неизвестная операция: {}", operation);
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write("{\"error\":\"Unsupported operation\"}");
                    return;
            }

            byte[] resultBytes = serializeMathFunction(resultMath);
            TabulatedFunction resultDb = new TabulatedFunction(base.getUserId(), base.getFunctionTypeId(), resultBytes);
            TabulatedFunction saved = repository.insert(resultDb);
            TabulatedFunctionDTO dto = DTOTransformer.toTabulatedFunctionDTO(saved);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.write(toFunctionResponseJson(dto));
            logger.info("Бинарная операция {} для функций {}, {} выполнена, результат ID {}", operation, baseId, secondId, saved.getId());
        } catch (SQLException e) {
            logger.error("Ошибка при выполнении бинарной операции для функции ID {}", baseId, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (ClassNotFoundException e) {
            logger.error("Ошибка десериализации табулированной функции при бинарной операции", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleDerive(HttpServletRequest req, HttpServletResponse resp, Long id) throws IOException {
        String body = readBody(req);
        logger.debug("Вычисление производной для функции ID {}, тело: {}", id, body);

        Integer order = extractIntField(body, "order");
        if (order == null || order < 1) {
            order = 1;
        }

        try (PrintWriter out = resp.getWriter()) {
            TabulatedFunction base = repository.findById(id);
            if (base == null) {
                logger.warn("Функция ID {} не найдена для вычисления производной", id);
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Function not found\"}");
                return;
            }

            org.example.functions.TabulatedFunction mathFunction = deserializeMathFunction(base.getSerializedData());
            org.example.functions.TabulatedFunction result = mathFunction;
            for (int i = 0; i < order; i++) {
                result = differentialOperator.derive(result);
            }

            byte[] resultBytes = serializeMathFunction(result);
            TabulatedFunction resultDb = new TabulatedFunction(base.getUserId(), base.getFunctionTypeId(), resultBytes);
            TabulatedFunction saved = repository.insert(resultDb);
            TabulatedFunctionDTO dto = DTOTransformer.toTabulatedFunctionDTO(saved);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.write(toFunctionResponseJson(dto));
            logger.info("Производная порядка {} для функции ID {} вычислена, результат ID {}", order, id, saved.getId());
        } catch (SQLException e) {
            logger.error("Ошибка при вычислении производной для функции ID {}", id, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (ClassNotFoundException e) {
            logger.error("Ошибка десериализации табулированной функции при вычислении производной", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // ------------------- Helpers -------------------

    private Long extractIdFromPath(String pathInfo) {
        String[] parts = pathInfo.split("/");
        // "/123" -> ["", "123"]; "/123/derive" -> ["", "123", "derive"]
        return Long.valueOf(parts[1]);
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private Long extractLongField(String json, String field) {
        String pattern = "\""+field+"\"\\s*:\\s*(\\d+)";
        Matcher matcher = Pattern.compile(pattern).matcher(json);
        if (matcher.find()) {
            return Long.valueOf(matcher.group(1));
        }
        return null;
    }

    private Integer extractIntField(String json, String field) {
        String pattern = "\""+field+"\"\\s*:\\s*(\\d+)";
        Matcher matcher = Pattern.compile(pattern).matcher(json);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        }
        return null;
    }

    private String extractStringField(String json, String field) {
        String pattern = "\""+field+"\"\\s*:\\s*\"([^\"]*)\"";
        Matcher matcher = Pattern.compile(pattern).matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String toFunctionResponseJson(TabulatedFunctionDTO dto) {
        if (dto == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(dto.getId()).append(",");
        sb.append("\"ownerId\":").append(dto.getUserId()).append(",");
        sb.append("\"typeId\":").append(dto.getFunctionTypeId()).append(",");

        String base64 = dto.getSerializedData() != null
                ? Base64.getEncoder().encodeToString(dto.getSerializedData())
                : "";
        sb.append("\"serializedData\":\"").append(base64).append("\",");
        sb.append("\"createdAt\":\"").append(dto.getCreatedTime()).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private org.example.functions.TabulatedFunction deserializeMathFunction(byte[] data)
            throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             BufferedInputStream bis = new BufferedInputStream(bais)) {
            return (org.example.functions.TabulatedFunction) FunctionsIO.deserialize(bis);
        }
    }

    private byte[] serializeMathFunction(org.example.functions.TabulatedFunction function) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(baos)) {
            FunctionsIO.serialize(bos, function);
            bos.flush();
            return baos.toByteArray();
        }
    }
}


