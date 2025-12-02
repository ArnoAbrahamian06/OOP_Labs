package org.example.web;

import org.example.db_service.DTO.DTOTransformer;
import org.example.db_service.DTO.UserDTO;
import org.example.db_service.User;
import org.example.db_service.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servlet, отвечающий за операции над пользователями и использованием UserDTO.
 * Логика:
 *  - GET /api/users              -> список всех пользователей (DTO)
 *  - GET /api/users?id={id}      -> один пользователь по id (DTO)
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/api/users"})
public class UserServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);
    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String idParam = req.getParameter("id");

        try (PrintWriter out = resp.getWriter()) {
            if (idParam != null) {
                Long id = Long.valueOf(idParam);
                logger.debug("Обработка GET /api/users?id={} ", id);
                User user = userRepository.findById(id);
                if (user == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.write("{\"error\":\"User not found\"}");
                    return;
                }
                UserDTO dto = DTOTransformer.toUserDTO(user);
                out.write(toJson(dto));
            } else {
                logger.debug("Обработка GET /api/users (все пользователи)");
                List<User> users = userRepository.findAll();
                List<UserDTO> dtoList = DTOTransformer.toUserDTOList(users);
                String jsonArray = dtoList.stream()
                        .map(this::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                out.write(jsonArray);
            }
        } catch (SQLException e) {
            logger.error("Ошибка при выполнении запроса в UserServlet", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // Простейшая ручная JSON-сериализация под учебные нужды
    private String toJson(UserDTO dto) {
        if (dto == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(dto.getId()).append(",");
        sb.append("\"email\":\"").append(escape(dto.getEmail())).append("\",");
        sb.append("\"login\":\"").append(escape(dto.getLogin())).append("\",");
        sb.append("\"passwordHash\":\"").append(escape(dto.getPasswordHash())).append("\",");
        sb.append("\"role\":\"").append(escape(dto.getRole())).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\\\"");
    }
}


