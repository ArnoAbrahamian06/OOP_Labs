package org.example.service.Implementation;

import org.example.entity.Function_type;
import org.example.repository.FunctionTypeRepository;
import org.example.service.FunctionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FunctionTypeServiceImpl implements FunctionTypeService {

    @Autowired
    private FunctionTypeRepository functionTypeRepository;

    @Override
    public List<Function_type> findAll() {
        return functionTypeRepository.findAll();
    }

    @Override
    public Optional<Function_type> findById(Long id) {
        return functionTypeRepository.findById(id);
    }

    @Override
    public Function_type save(Function_type functionType) {
        return functionTypeRepository.save(functionType);
    }

    @Override
    public void deleteById(Long id) {
        functionTypeRepository.deleteById(id);
    }

    @Override
    public List<Function_type> findByTabulatedFunctionId(Long tabulatedFunctionId) {
        return functionTypeRepository.findByTabulatedFunctionId(tabulatedFunctionId);
    }
}