// SearchAlgorithmFactory.java
package org.example.db_service.Search;

import org.example.db_service.Search.Algorithms.BreadthFirstSearch;
import org.example.db_service.Search.Algorithms.DepthFirstSearch;
import org.example.db_service.Search.Algorithms.HierarchicalSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


 // Фабрика алгоритмов поиска

public class SearchAlgorithmFactory {
    private static final Logger logger = LoggerFactory.getLogger(SearchAlgorithmFactory.class);

    public static <T extends HierarchicalData<T>> SearchAlgorithm<T> createAlgorithm(SearchType type) {
        logger.debug("Создание алгоритма поиска типа: {}", type);

        switch (type) {
            case BREADTH_FIRST:
                return new BreadthFirstSearch<>();
            case DEPTH_FIRST:
                return new DepthFirstSearch<>();
            case HIERARCHICAL:
                return new HierarchicalSearch<>();
            default:
                logger.warn("Неизвестный тип алгоритма: {}, используется BFS по умолчанию", type);
                return new BreadthFirstSearch<>();
        }
    }
}