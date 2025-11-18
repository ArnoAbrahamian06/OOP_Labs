// HierarchicalSearch.java
package org.example.db_service.Search.Algorithms;

import org.example.db_service.Search.SearchAlgorithm;
import org.example.db_service.Search.SearchCriteria;
import org.example.db_service.Search.HierarchicalData;
import org.example.db_service.Search.util.SearchSortUtils;
import org.example.db_service.Search.SortField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


// Поиск по иерархии

public class HierarchicalSearch<T extends HierarchicalData<T>> implements SearchAlgorithm<T> {
    private static final Logger logger = LoggerFactory.getLogger(HierarchicalSearch.class);

    @Override
    public List<T> search(SearchCriteria<T> criteria) {
        logger.debug("Запуск иерархического поиска. Корень: {}", criteria.getRoot());

        if (criteria.getRoot() == null) {
            logger.warn("Корневой элемент не задан для иерархического поиска");
            return Collections.emptyList();
        }

        List<T> results = new ArrayList<>();
        hierarchicalSearch(criteria.getRoot(), criteria, results, new HashSet<>());

        logger.info("Иерархический поиск завершен. Найдено результатов: {}", results.size());
        return SearchSortUtils.applySorting(results, criteria.getSortFields());
    }

    private void hierarchicalSearch(T current, SearchCriteria<T> criteria,
                                    List<T> results, Set<T> visited) {
        if (visited.contains(current)) {
            return;
        }

        visited.add(current);
        logger.trace("Обработка элемента в иерархии: {}", current);

        // Проверяем текущий элемент
        if (criteria.getFilter().test(current)) {
            results.add(current);
            logger.debug("Найден подходящий элемент в иерархии: {}", current);

            if (!criteria.isMultipleResults()) {
                return;
            }
        }

        // Обходим родителей (вверх по иерархии)
        if (current.getParent() != null && !visited.contains(current.getParent())) {
            hierarchicalSearch(current.getParent(), criteria, results, visited);
            if (!criteria.isMultipleResults() && !results.isEmpty()) {
                return;
            }
        }

        // Обходим детей (вниз по иерархии)
        if (current.getChildren() != null) {
            for (T child : current.getChildren()) {
                if (!visited.contains(child)) {
                    hierarchicalSearch(child, criteria, results, visited);
                    if (!criteria.isMultipleResults() && !results.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "Hierarchical Search";
    }
}