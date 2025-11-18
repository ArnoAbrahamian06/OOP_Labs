// SearchServiceTest.java
package org.example.db_service.Search;

import org.example.db_service.Search.model.TreeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(SearchServiceTest.class);

    private SearchService<TreeNode<String>> searchService;
    private TreeNode<String> root;

    @BeforeEach
    void setUp() {
        searchService = new SearchService<>();

        root = new TreeNode<>("A");
        TreeNode<String> nodeB = new TreeNode<>("B");
        TreeNode<String> nodeC = new TreeNode<>("C");
        TreeNode<String> nodeD = new TreeNode<>("D");
        TreeNode<String> nodeE = new TreeNode<>("E");
        TreeNode<String> nodeF = new TreeNode<>("F");
        TreeNode<String> nodeG = new TreeNode<>("G");

        root.addChild(nodeB);
        root.addChild(nodeC);
        nodeB.addChild(nodeD);
        nodeB.addChild(nodeE);
        nodeC.addChild(nodeF);
        nodeE.addChild(nodeG);
    }

    @Test
    void testBreadthFirstSearch() {
        logger.info("Тест поиска в ширину");

        SearchFilter<TreeNode<String>> filter = node ->
                node.getData().equals("E") || node.getData().equals("F");

        SearchCriteria<TreeNode<String>> criteria = searchService.createMultipleResultsCriteria(root, filter);

        List<TreeNode<String>> results = searchService.search(SearchType.BREADTH_FIRST, criteria);

        assertEquals(2, results.size());
        // BFS должен найти E и F на одном уровне
        assertTrue(results.stream().anyMatch(node -> node.getData().equals("E")));
        assertTrue(results.stream().anyMatch(node -> node.getData().equals("F")));
    }

    @Test
    void testDepthFirstSearch() {
        logger.info("Тест поиска в глубину");

        SearchFilter<TreeNode<String>> filter = node ->
                node.getData().equals("G") || node.getData().equals("F");

        SearchCriteria<TreeNode<String>> criteria = searchService.createMultipleResultsCriteria(root, filter);

        List<TreeNode<String>> results = searchService.search(SearchType.DEPTH_FIRST, criteria);

        assertEquals(2, results.size());
        // DFS найдет G раньше F (A->B->E->G, затем A->C->F)
        assertEquals("G", results.get(0).getData());
        assertEquals("F", results.get(1).getData());
    }

    @Test
    void testHierarchicalSearch() {
        logger.info("Тест иерархического поиска");

        // Начинаем с узла E, ищем соседние узлы
        TreeNode<String> nodeE = findNode(root, "E");

        SearchFilter<TreeNode<String>> filter = node ->
                node.getDepth() == 2; // Узлы на глубине 2

        SearchCriteria<TreeNode<String>> criteria = searchService.createMultipleResultsCriteria(nodeE, filter);

        List<TreeNode<String>> results = searchService.search(SearchType.HIERARCHICAL, criteria);

        // Должны найти D, E, F (все на глубине 2)
        assertEquals(3, results.size());
    }

    @Test
    void testSingleResultSearch() {
        logger.info("Тест одиночного поиска");

        SearchFilter<TreeNode<String>> filter = node ->
                node.getData().startsWith("G");

        SearchCriteria<TreeNode<String>> criteria = searchService.createSingleResultCriteria(root, filter);

        List<TreeNode<String>> results = searchService.search(SearchType.DEPTH_FIRST, criteria);

        assertEquals(1, results.size());
        assertEquals("G", results.get(0).getData());
    }

    @Test
    void testSearchWithSorting() {
        logger.info("Тест поиска с сортировкой");

        SearchFilter<TreeNode<String>> filter = node ->
                node.getDepth() >= 1;

        SearchCriteria<TreeNode<String>> criteria = searchService.createMultipleResultsCriteria(root, filter);
        criteria.setSortFields(Arrays.asList(
                new SortField("depth", SortDirection.ASC),
                new SortField("data", SortDirection.DESC)
        ));

        List<TreeNode<String>> results = searchService.search(SearchType.BREADTH_FIRST, criteria);

        assertFalse(results.isEmpty());
        // Проверяем что результаты отсортированы по глубине
        for (int i = 1; i < results.size(); i++) {
            assertTrue(results.get(i-1).getDepth() <= results.get(i).getDepth());
        }
    }

    @Test
    void testMaxDepth() {
        logger.info("Тест ограничения глубины");

        SearchFilter<TreeNode<String>> filter = node -> true; // Все узлы

        SearchCriteria<TreeNode<String>> criteria = searchService.createMultipleResultsCriteria(root, filter);
        criteria.setMaxDepth(2); // Только до глубины 2

        List<TreeNode<String>> results = searchService.search(SearchType.BREADTH_FIRST, criteria);

        // Должны найти A(0), B(1), C(1), D(2), E(2), F(2)
        assertEquals(6, results.size());
        assertTrue(results.stream().allMatch(node -> node.getDepth() <= 2));
    }

    private TreeNode<String> findNode(TreeNode<String> start, String data) {
        if (start.getData().equals(data)) {
            return start;
        }

        for (TreeNode<String> child : start.getChildren()) {
            TreeNode<String> found = findNode(child, data);
            if (found != null) {
                return found;
            }
        }

        return null;
    }
}