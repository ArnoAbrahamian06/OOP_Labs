package org.example;

import org.example.DAO.UserDAO;
import org.example.auth.PasswordUtil;
import org.example.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Optional;

@WebListener
public class AppInitializer implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppInitializer.class);
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Инициализация приложения...");

        // Создание администратора по умолчанию, если еще не существует
        createDefaultAdmin();

        logger.info("Приложение успешно инициализировано");
    }

    private void createDefaultAdmin() {
        String adminLogin = "admin";
        Optional<User> admin = userDAO.findByUsername(adminLogin);

        if (admin.isEmpty()) {
            logger.info("Создание администратора по умолчанию");
            User newAdmin = new User();
            newAdmin.setUsername(adminLogin);
            newAdmin.setPasswordHash(PasswordUtil.hashPassword("admin"));
            newAdmin.setRole("admin");

            User createdAdmin = userDAO.insert(newAdmin);
            if (createdAdmin != null) {
                logger.info("Администратор успешно создан с логином 'admin' и паролем 'admin'");
            } else {
                logger.error("Не удалось создать администратора по умолчанию");
            }
        } else {
            logger.info("Администратор уже существует в базе данных");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Приложение останавливается...");
    }
}