// SearchFilter.java
package org.example.db_service.Search;

// Фильтр для поиска

@FunctionalInterface
public interface SearchFilter<T> {
    boolean test(T item);

    default SearchFilter<T> and(SearchFilter<T> other) {
        return item -> this.test(item) && other.test(item);
    }

    default SearchFilter<T> or(SearchFilter<T> other) {
        return item -> this.test(item) || other.test(item);
    }

    default SearchFilter<T> not() {
        return item -> !this.test(item);
    }
}