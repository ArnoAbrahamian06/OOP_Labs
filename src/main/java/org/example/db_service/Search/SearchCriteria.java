// SearchCriteria.java
package org.example.db_service.Search;

import java.util.List;
import java.util.Map;

// Критерии поиска

public class SearchCriteria<T> {
    private T root;
    private SearchFilter<T> filter;
    private SearchStrategy strategy;
    private int maxDepth = Integer.MAX_VALUE;
    private List<SortField> sortFields;
    private boolean multipleResults = true;
    private Map<String, Object> parameters;

    public SearchCriteria() {}

    public SearchCriteria(T root, SearchFilter<T> filter, SearchStrategy strategy) {
        this.root = root;
        this.filter = filter;
        this.strategy = strategy;
    }

    // Геттеры и сеттеры
    public T getRoot() { return root; }
    public void setRoot(T root) { this.root = root; }
    public SearchFilter<T> getFilter() { return filter; }
    public void setFilter(SearchFilter<T> filter) { this.filter = filter; }
    public SearchStrategy getStrategy() { return strategy; }
    public void setStrategy(SearchStrategy strategy) { this.strategy = strategy; }
    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }
    public List<SortField> getSortFields() { return sortFields; }
    public void setSortFields(List<SortField> sortFields) { this.sortFields = sortFields; }
    public boolean isMultipleResults() { return multipleResults; }
    public void setMultipleResults(boolean multipleResults) { this.multipleResults = multipleResults; }
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}