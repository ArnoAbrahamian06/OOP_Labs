package org.example.web;

import org.example.db_service.DTO.DTOTransformer;
import org.example.db_service.DTO.FunctionTypeDTO;
import org.example.db_service.FunctionType;
import org.example.db_service.FunctionTypeRepository;
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
 * Servlet для работы с типами функций и FunctionTypeDTO.
 * Логика:
 *  - GET /api/function-types?id={id}
 *  - GET /api/function-types?name={name}
 *  - GET /api/function-types?priorityGreaterThan={minPriority}
 */
@WebServlet(name = "FunctionTypeServlet", urlPatterns = {"/api/function-types"})
public class FunctionTypeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FunctionTypeServlet.class);
    private final FunctionTypeRepository repository = new FunctionTypeRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String idParam = req.getParameter("id");
        String nameParam = req.getParameter("name");
        String priorityParam = req.getParameter("priorityGreaterThan");

        try (PrintWriter out = resp.getWriter()) {
            if (idParam != null) {
                Integer id = Integer.valueOf(idParam);
                logger.debug("GET /api/function-types?id={}", id);
                FunctionType type = repository.findById(id);
                if (type == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.write("{\"error\":\"FunctionType not found\"}");
                    return;
                }
                FunctionTypeDTO dto = DTOTransformer.toFunctionTypeDTO(type);
                out.write(toJson(dto));
            } else if (nameParam != null) {
                logger.debug("GET /api/function-types?name={}", nameParam);
                FunctionType type = repository.findByName(nameParam);
                if (type == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.write("{\"error\":\"FunctionType not found\"}");
                    return;
                }
                FunctionTypeDTO dto = DTOTransformer.toFunctionTypeDTO(type);
                out.write(toJson(dto));
            } else if (priorityParam != null) {
                Integer minPriority = Integer.valueOf(priorityParam);
                logger.debug("GET /api/function-types?priorityGreaterThan={}", minPriority);
                List<FunctionType> types = repository.findByPriorityGreaterThan(minPriority);
                List<FunctionTypeDTO> dtos = DTOTransformer.toFunctionTypeDTOList(types);
                String json = dtos.stream()
                        .map(this::toJson)
                        .collect(Collectors.joining(",", "[", "]"));
                out.write(json);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Specify one of query parameters: id, name, priorityGreaterThan\"}");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при выполнении запроса в FunctionTypeServlet", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String toJson(FunctionTypeDTO dto) {
        if (dto == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(dto.getId()).append(",");
        sb.append("\"name\":\"").append(escape(dto.getName())).append("\",");
        sb.append("\"localizedName\":\"").append(escape(dto.getLocalizedName())).append("\",");
        sb.append("\"priority\":").append(dto.getPriority());
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


