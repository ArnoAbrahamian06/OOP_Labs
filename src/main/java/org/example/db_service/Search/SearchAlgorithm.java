package org.example.db_service.Search;

import java.util.List;


// Интерфейс для алгоритмов поиска
public interface SearchAlgorithm<T> {
    List<T> search(SearchCriteria<T> criteria);
    String getAlgorithmName();
}