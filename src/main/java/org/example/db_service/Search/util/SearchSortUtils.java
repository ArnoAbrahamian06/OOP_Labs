// SearchSortUtils.java
package org.example.db_service.Search.util;

import org.example.db_service.Search.SortField;
import org.example.db_service.Search.SortDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;


 // Утилиты для сортировки результатов поиска

public class SearchSortUtils {
    private static final Logger logger = LoggerFactory.getLogger(SearchSortUtils.class);

    private SearchSortUtils() {
        // Утилитарный класс
    }

    /**
     * Применяет сортировку к списку элементов
     */
    public static <T> List<T> applySorting(List<T> items, List<SortField> sortFields) {
        if (items == null || items.isEmpty() || sortFields == null || sortFields.isEmpty()) {
            return items;
        }

        logger.debug("Применение сортировки к {} элементам по {} полям",
                items.size(), sortFields.size());

        return items.stream()
                .sorted(createComparator(sortFields))
                .toList();
    }

    /**
     * Создает компаратор на основе полей сортировки
     */
    @SuppressWarnings("unchecked")
    private static <T> Comparator<T> createComparator(List<SortField> sortFields) {
        return (a, b) -> {
            for (SortField sortField : sortFields) {
                try {
                    int comparison = compareField(a, b, sortField);
                    if (comparison != 0) {
                        return sortField.getDirection() == SortDirection.ASC ? comparison : -comparison;
                    }
                } catch (Exception e) {
                    logger.warn("Ошибка при сравнении поля '{}': {}",
                            sortField.getFieldName(), e.getMessage());
                }
            }
            return 0;
        };
    }

    /**
     * Сравнивает значение поля двух объектов
     */
    @SuppressWarnings("unchecked")
    private static <T> int compareField(T a, T b, SortField sortField)
            throws NoSuchFieldException, IllegalAccessException {

        Field field = getField(a.getClass(), sortField.getFieldName());
        field.setAccessible(true);

        Object valueA = field.get(a);
        Object valueB = field.get(b);

        // Обработка null значений
        if (valueA == null && valueB == null) return 0;
        if (valueA == null) return -1;
        if (valueB == null) return 1;

        // Сравнение Comparable объектов
        if (valueA instanceof Comparable && valueB instanceof Comparable) {
            return ((Comparable<Object>) valueA).compareTo(valueB);
        }

        // Сравнение по строковому представлению
        return valueA.toString().compareTo(valueB.toString());
    }

    /**
     * Рекурсивно ищет поле в классе и его родителях
     */
    private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getField(superClass, fieldName);
            }
            throw e;
        }
    }
}