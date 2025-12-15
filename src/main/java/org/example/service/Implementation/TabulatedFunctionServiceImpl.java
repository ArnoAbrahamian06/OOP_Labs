package org.example.service.Implementation;

import org.example.entity.Tabulated_function;
import org.example.repository.TabulatedFunctionRepository;
import org.example.service.TabulatedFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TabulatedFunctionServiceImpl implements TabulatedFunctionService {

    private static final Logger log = LoggerFactory.getLogger(TabulatedFunctionServiceImpl.class); // НОВОЕ ПОЛЕ

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Override
    public List<Tabulated_function> findAll() {
        log.info("findAll: Запрос на получение всех табулированных функций");
        List<Tabulated_function> functions = tabulatedFunctionRepository.findAll();
        log.debug("findAll: Найдено {} функций", functions.size());
        return functions;
    }

    @Override
    public Optional<Tabulated_function> findById(Long id) {
        log.debug("findById: Поиск функции с ID: {}", id);
        Optional<Tabulated_function> funcOpt = tabulatedFunctionRepository.findById(id);
        if (funcOpt.isPresent()) {
            log.debug("findById: Функция с ID {} найдена: {}", id, funcOpt.get().getName());
        } else {
            log.debug("findById: Функция с ID {} не найдена", id);
        }
        return funcOpt;
    }

    @Override
    public Tabulated_function save(Tabulated_function tabulatedFunction) {
        Long id = tabulatedFunction.getId();
        String action = (id == null) ? "Создание" : "Обновление";
        log.info("save: {} функции с именем: {}", action, tabulatedFunction.getName());
        Tabulated_function savedFunction = tabulatedFunctionRepository.save(tabulatedFunction);
        log.info("save: Функция с ID {} успешно сохранена/обновлена. Имя: {}", savedFunction.getId(), savedFunction.getName());
        return savedFunction;
    }

    @Override
    public void deleteById(Long id) {
        log.warn("deleteById: Удаление функции с ID: {}", id);
        tabulatedFunctionRepository.deleteById(id);
        log.info("deleteById: Функция с ID {} успешно удалена", id);
    }

    @Override
    public List<Tabulated_function> findByUserId(Long userId) {
        log.debug("findByUserId: Поиск функций для пользователя ID: {}", userId);
        List<Tabulated_function> functions = tabulatedFunctionRepository.findByUserId(userId);
        log.debug("findByUserId: Найдено {} функций для пользователя ID {}", functions.size(), userId);
        return functions;
    }

    @Override
    public List<Tabulated_function> findByNameContaining(String name) {
        log.debug("findByNameContaining: Поиск функций по части имени: {}", name);
        List<Tabulated_function> functions = tabulatedFunctionRepository.findByNameContaining(name);
        log.debug("findByNameContaining: Найдено {} функций, содержащих '{}'", functions.size(), name);
        return functions;
    }

    @Override
    public List<Tabulated_function> findByName(String name) {
        log.debug("findByName: Поиск функций по имени: {}", name);
        List<Tabulated_function> functions = tabulatedFunctionRepository.findByName(name);
        log.debug("findByName: Найдено {} функций с именем '{}'", functions.size(), name);
        return functions;
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("existsById: Проверка существования функции с ID: {}", id);
        boolean exists = tabulatedFunctionRepository.existsById(id);
        log.debug("existsById: Функция с ID {} существует: {}", id, exists);
        return exists;
    }

    @Override
    public Optional<Tabulated_function> findByNameAndUserId(String name, Long userId) {
        log.debug("findByNameAndUserId: Поиск функции по имени '{}' и пользователю ID: {}", name, userId);
        Optional<Tabulated_function> funcOpt = tabulatedFunctionRepository.findByNameAndUserId(name, userId);
        if (funcOpt.isPresent()) {
            log.debug("findByNameAndUserId: Функция найдена");
        } else {
            log.debug("findByNameAndUserId: Функция не найдена");
        }
        return funcOpt;
    }
}