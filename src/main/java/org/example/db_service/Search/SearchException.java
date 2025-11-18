// SearchException.java
package org.example.db_service.Search;

// Исключение для операций поиска

public class SearchException extends RuntimeException {
    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }
}