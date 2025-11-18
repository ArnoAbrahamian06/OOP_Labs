// DepthFirstSearch.java
package org.example.db_service.Search.Algorithms;

import org.example.db_service.Search.SearchAlgorithm;
import org.example.db_service.Search.SearchCriteria;
import org.example.db_service.Search.HierarchicalData;
import org.example.db_service.Search.util.SearchSortUtils;

import org.example.db_service.Search.SortField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


// Поиск в глубину (DFS)
public class DepthFirstSearch<T extends HierarchicalData<T>> implements SearchAlgorithm<T> {
    private static final Logger logger = LoggerFactory.getLogger(DepthFirstSearch.class);

    @Override
    public List<T> search(SearchCriteria<T> criteria) {
        logger.debug("Запуск поиска в глубину. Корень: {}, макс. глубина: {}",
                criteria.getRoot(), criteria.getMaxDepth());

        if (criteria.getRoot() == null) {
            logger.warn("Корневой элемент не задан для поиска в глубину");
            return Collections.emptyList();
        }

        List<T> results = new ArrayList<>();
        Set<T> visited = new HashSet<>();

        dfs(criteria.getRoot(), 0, criteria, results, visited);

        logger.info("Поиск в глубину завершен. Найдено результатов: {}", results.size());
        return SearchSortUtils.applySorting(results, criteria.getSortFields());
    }

    private void dfs(T current, int depth, SearchCriteria<T> criteria,
                     List<T> results, Set<T> visited) {
        if (depth > criteria.getMaxDepth() || visited.contains(current)) {
            return;
        }

        visited.add(current);
        logger.trace("Обработка элемента: {} на глубине: {}", current, depth);

        // Проверяем условие фильтра
        if (criteria.getFilter().test(current)) {
            results.add(current);
            logger.debug("Найден подходящий элемент: {}", current);

            // Если нужен только один результат
            if (!criteria.isMultipleResults()) {
                logger.debug("Найден одиночный результат, завершаем обход");
                return;
            }
        }

        // Рекурсивно обходим детей
        if (current.getChildren() != null) {
            for (T child : current.getChildren()) {
                if (!visited.contains(child)) {
                    dfs(child, depth + 1, criteria, results, visited);

                    // Прерываем если нашли одиночный результат
                    if (!criteria.isMultipleResults() && !results.isEmpty()) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "Depth-First Search (DFS)";
    }
}