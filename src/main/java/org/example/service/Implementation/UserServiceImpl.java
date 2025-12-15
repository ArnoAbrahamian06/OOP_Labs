package org.example.service.Implementation;

import org.example.entity.User;
import org.example.entity.Role;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class); // НОВОЕ ПОЛЕ

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        log.info("findAll: Запрос на получение всех пользователей");
        List<User> users = userRepository.findAll();
        log.debug("findAll: Найдено {} пользователей", users.size());
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        log.debug("findById: Поиск пользователя с ID: {}", id);
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            log.debug("findById: Пользователь с ID {} найден: {}", id,  userOpt.get().getUsername());
        } else {
            log.debug("findById: Пользователь с ID {} не найден", id);
        }
        return userOpt;
    }

    @Override
    public User save(User user) {
        log.info("save: Сохранение/обновление пользователя с username: {}", user.getUsername());
        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            log.debug("save: Выполняется хеширование пароля для пользователя {}", user.getUsername());
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        User savedUser = userRepository.save(user);
        log.info("save: Пользователь с ID {} успешно сохранен/обновлен", savedUser.getId());
        return savedUser;
    }

    @Override
    public void deleteById(Long id) {
        log.warn("deleteById: Удаление пользователя с ID: {}", id);
        userRepository.deleteById(id);
        log.info("deleteById: Пользователь с ID {} успешно удален", id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("existsById: Проверка существования пользователя с ID: {}", id);
        boolean exists = userRepository.existsById(id);
        log.debug("existsById: Пользователь с ID {} существует: {}", id, exists);
        return exists;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("findByUsername: Поиск пользователя с именем: {}", username);
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            log.debug("findByUsername: Пользователь {} найден", username);
        } else {
            log.debug("findByUsername: Пользователь {} не найден", username);
        }
        return userOpt;
    }

    @Override
    @Transactional
    public User createUser(String username, String rawPassword, Role role) {
        log.info("Создание нового пользователя: username='{}', role='{}'", username, role);

        // Проверка, что пользователь с таким именем не существует
        if (userRepository.findByUsername(username).isPresent()) {
            log.error("Попытка создания пользователя с уже существующим именем: {}", username);
            throw new IllegalArgumentException("Пользователь с именем '" + username + "' уже существует.");
        }

        // Хешируем пароль
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Создаём нового пользователя
        User newUser = new User(username, encodedPassword, role);

        // Сохраняем в базу данных
        User savedUser = userRepository.save(newUser);

        log.info("Успешно создан пользователь с ID: {} и ролью: {}", savedUser.getId(), savedUser.getRole());
        return savedUser;
    }

    @Override
    @Transactional
    public Optional<User> assignRole(Long userId, Role newRole) {
        log.info("Попытка назначения роли '{}' пользователю с ID: {}", newRole, userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("Попытка назначить роль пользователю, который не существует. ID: {}", userId);
            return Optional.empty();
        }

        User user = userOpt.get();
        Role oldRole = user.getRole();

        log.info("Назначение новой роли '{}' пользователю '{}' (ID: {}). Старая роль: '{}'", newRole, user.getUsername(), user.getId(), oldRole);

        // Устанавливаем новую роль
        user.setRole(newRole);

        // Сохраняем изменения
        User updatedUser = userRepository.save(user);

        log.info("Роль успешно изменена для пользователя '{}' (ID: {}). Новая роль: '{}'", updatedUser.getUsername(), updatedUser.getId(), updatedUser.getRole());
        return Optional.of(updatedUser);
    }

    @Override
    public List<User> findByRole(Role role) {
        log.debug("Поиск пользователей с ролью: {}", role);
        List<User> users = userRepository.findByRole(role);
        log.info("Найдено {} пользователей с ролью '{}'", users.size(), role);
        return users;
    }
}