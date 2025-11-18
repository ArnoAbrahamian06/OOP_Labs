package org.example.db_service.Search.Algorithms;

import org.example.db_service.Search.SearchAlgorithm;
import org.example.db_service.Search.SearchCriteria;
import org.example.db_service.Search.SortField;
import org.example.db_service.Search.HierarchicalData;
import org.example.db_service.Search.util.SearchSortUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


 // Поиск в ширину (BFS)

public class BreadthFirstSearch<T extends HierarchicalData<T>> implements SearchAlgorithm<T> {
    private static final Logger logger = LoggerFactory.getLogger(BreadthFirstSearch.class);

    @Override
    public List<T> search(SearchCriteria<T> criteria) {
        logger.debug("Запуск поиска в ширину. Корень: {}, макс. глубина: {}",
                criteria.getRoot(), criteria.getMaxDepth());

        if (criteria.getRoot() == null) {
            logger.warn("Корневой элемент не задан для поиска в ширину");
            return Collections.emptyList();
        }

        List<T> results = new ArrayList<>();
        Queue<QueueItem<T>> queue = new LinkedList<>();
        Set<T> visited = new HashSet<>();

        queue.offer(new QueueItem<>(criteria.getRoot(), 0));
        visited.add(criteria.getRoot());

        while (!queue.isEmpty()) {
            QueueItem<T> current = queue.poll();
            T item = current.item;
            int depth = current.depth;

            logger.trace("Обработка элемента: {} на глубине: {}", item, depth);

            // Проверяем условие фильтра
            if (criteria.getFilter().test(item)) {
                results.add(item);
                logger.debug("Найден подходящий элемент: {}", item);

                // Если нужен только один результат
                if (!criteria.isMultipleResults()) {
                    logger.debug("Найден одиночный результат, завершаем поиск");
                    break;
                }
            }

            // Если не достигли максимальной глубины, добавляем детей
            if (depth < criteria.getMaxDepth() && item.getChildren() != null) {
                for (T child : item.getChildren()) {
                    if (!visited.contains(child)) {
                        visited.add(child);
                        queue.offer(new QueueItem<>(child, depth + 1));
                        logger.trace("Добавлен дочерний элемент в очередь: {}", child);
                    }
                }
            }
        }

        logger.info("Поиск в ширину завершен. Найдено результатов: {}", results.size());
        return SearchSortUtils.applySorting(results, criteria.getSortFields());
    }

    @Override
    public String getAlgorithmName() {
        return "Breadth-First Search (BFS)";
    }

    private static class QueueItem<T> {
        T item;
        int depth;

        QueueItem(T item, int depth) {
            this.item = item;
            this.depth = depth;
        }
    }
}