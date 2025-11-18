// SearchService.java
package org.example.db_service.Search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


// Основной сервис поиска

public class SearchService<T extends HierarchicalData<T>> {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    public List<T> search(SearchType algorithmType, SearchCriteria<T> criteria) {
        logger.info("Запуск поиска. Алгоритм: {}, стратегия: {}, множественный: {}",
                algorithmType, criteria.getStrategy(), criteria.isMultipleResults());

        long startTime = System.currentTimeMillis();

        try {
            SearchAlgorithm<T> algorithm = SearchAlgorithmFactory.createAlgorithm(algorithmType);
            List<T> results = algorithm.search(criteria);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Поиск завершен за {} мс. Найдено результатов: {}", duration, results.size());

            return results;
        } catch (Exception e) {
            logger.error("Ошибка при выполнении поиска: {}", e.getMessage(), e);
            throw new SearchException("Ошибка поиска: " + e.getMessage(), e);
        }
    }

    // Вспомогательные методы для быстрого создания критериев
    public SearchCriteria<T> createCriteria(T root, SearchFilter<T> filter, SearchStrategy strategy) {
        return new SearchCriteria<>(root, filter, strategy);
    }

    public SearchCriteria<T> createSingleResultCriteria(T root, SearchFilter<T> filter) {
        SearchCriteria<T> criteria = new SearchCriteria<>(root, filter, SearchStrategy.SINGLE);
        criteria.setMultipleResults(false);
        return criteria;
    }

    public SearchCriteria<T> createMultipleResultsCriteria(T root, SearchFilter<T> filter) {
        return new SearchCriteria<>(root, filter, SearchStrategy.MULTIPLE);
    }
}