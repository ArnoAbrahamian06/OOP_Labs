package org.example.service.Implementation;

import org.example.entity.Tabulated_function;
import org.example.repository.TabulatedFunctionRepository;
import org.example.service.TabulatedFunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TabulatedFunctionServiceImpl implements TabulatedFunctionService {

    @Autowired
    private TabulatedFunctionRepository tabulatedFunctionRepository;

    @Override
    public List<Tabulated_function> findAll() {
        return tabulatedFunctionRepository.findAll();
    }

    @Override
    public Optional<Tabulated_function> findById(Long id) {
        return tabulatedFunctionRepository.findById(id);
    }

    @Override
    public Tabulated_function save(Tabulated_function tabulatedFunction) {
        return tabulatedFunctionRepository.save(tabulatedFunction);
    }

    @Override
    public void deleteById(Long id) {
        tabulatedFunctionRepository.deleteById(id);
    }

    @Override
    public List<Tabulated_function> findByUserId(Long userId) {
        return tabulatedFunctionRepository.findByUserId(userId);
    }
}