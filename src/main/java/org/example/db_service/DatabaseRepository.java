package org.example.db_service;

import java.util.List;
import java.util.Optional;

public interface DatabaseRepository {
    // User операции
    Long createUser(User user);
    Optional<User> findUserById(Long id);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByLogin(String login);
    List<User> findUsersByRole(String role);
    List<User> findAllUsers(int limit, int offset);
    boolean updateUser(User user);
    boolean deleteUser(Long id);

    // FunctionType операции
    Integer createFunctionType(FunctionType functionType);
    Optional<FunctionType> findFunctionTypeById(Integer id);
    Optional<FunctionType> findFunctionTypeByName(String name);
    Optional<FunctionType> findFunctionTypeByLocalizedName(String localizedName);
    List<FunctionType> findFunctionTypesByPriorityGreaterThan(Integer minPriority);
    List<FunctionType> findAllFunctionTypes();
    boolean updateFunctionType(FunctionType functionType);
    boolean deleteFunctionType(Integer id);

    // TabulatedFunction операции
    Long createTabulatedFunction(TabulatedFunction function);
    Optional<TabulatedFunction> findTabulatedFunctionById(Long id);
    List<TabulatedFunction> findTabulatedFunctionsByUserId(Long userId);
    List<TabulatedFunction> findTabulatedFunctionsByFunctionTypeId(Integer typeId);
    List<TabulatedFunction> findTabulatedFunctionsByUserIdAndTypeId(Long userId, Integer typeId);
    List<TabulatedFunction> findAllTabulatedFunctionsWithDetails();
    List<TabulatedFunction> findTabulatedFunctionsWithPagination(int limit, int offset);
    boolean updateTabulatedFunction(TabulatedFunction function);
    boolean deleteTabulatedFunction(Long id);
    boolean deleteTabulatedFunctionsByUserId(Long userId);
}
