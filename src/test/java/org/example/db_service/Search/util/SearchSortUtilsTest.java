// SearchSortUtilsTest.java
package org.example.db_service.Search.util;

import org.example.db_service.Search.SortField;
import org.example.db_service.Search.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchSortUtilsTest {

    static class TestItem {
        private String name;
        private Integer value;
        private Double score;

        public TestItem(String name, Integer value, Double score) {
            this.name = name;
            this.value = value;
            this.score = score;
        }

        // Геттеры
        public String getName() { return name; }
        public Integer getValue() { return value; }
        public Double getScore() { return score; }
    }

    @Test
    void testSortBySingleFieldAsc() {
        List<TestItem> items = Arrays.asList(
                new TestItem("C", 30, 1.5),
                new TestItem("A", 10, 3.5),
                new TestItem("B", 20, 2.5)
        );

        List<SortField> sortFields = List.of(
                new SortField("name", SortDirection.ASC)
        );

        List<TestItem> sorted = SearchSortUtils.applySorting(items, sortFields);

        assertEquals("A", sorted.get(0).getName());
        assertEquals("B", sorted.get(1).getName());
        assertEquals("C", sorted.get(2).getName());
    }

    @Test
    void testSortBySingleFieldDesc() {
        List<TestItem> items = Arrays.asList(
                new TestItem("A", 10, 1.5),
                new TestItem("C", 30, 3.5),
                new TestItem("B", 20, 2.5)
        );

        List<SortField> sortFields = List.of(
                new SortField("name", SortDirection.DESC)
        );

        List<TestItem> sorted = SearchSortUtils.applySorting(items, sortFields);

        assertEquals("C", sorted.get(0).getName());
        assertEquals("B", sorted.get(1).getName());
        assertEquals("A", sorted.get(2).getName());
    }

    @Test
    void testSortByMultipleFields() {
        List<TestItem> items = Arrays.asList(
                new TestItem("A", 20, 1.0),
                new TestItem("B", 10, 2.0),
                new TestItem("A", 10, 3.0),
                new TestItem("B", 20, 4.0)
        );

        List<SortField> sortFields = Arrays.asList(
                new SortField("name", SortDirection.ASC),
                new SortField("value", SortDirection.ASC)
        );

        List<TestItem> sorted = SearchSortUtils.applySorting(items, sortFields);

        assertEquals("A", sorted.get(0).getName());
        assertEquals(10, sorted.get(0).getValue());
        assertEquals("A", sorted.get(1).getName());
        assertEquals(20, sorted.get(1).getValue());
        assertEquals("B", sorted.get(2).getName());
        assertEquals(10, sorted.get(2).getValue());
        assertEquals("B", sorted.get(3).getName());
        assertEquals(20, sorted.get(3).getValue());
    }

    @Test
    void testSortWithNullValues() {
        List<TestItem> items = Arrays.asList(
                new TestItem(null, 10, 1.0),
                new TestItem("A", 20, 2.0),
                new TestItem("B", null, 3.0)
        );

        List<SortField> sortFields = List.of(
                new SortField("name", SortDirection.ASC)
        );

        List<TestItem> sorted = SearchSortUtils.applySorting(items, sortFields);

        assertNull(sorted.get(0).getName()); // null first
        assertEquals("A", sorted.get(1).getName());
        assertEquals("B", sorted.get(2).getName());
    }

    @Test
    void testEmptySortFields() {
        List<TestItem> items = Arrays.asList(
                new TestItem("C", 30, 1.5),
                new TestItem("A", 10, 3.5)
        );

        List<TestItem> sorted = SearchSortUtils.applySorting(items, null);

        assertEquals(2, sorted.size()); // Порядок не изменился
    }

    @Test
    void testEmptyItems() {
        List<TestItem> items = List.of();
        List<SortField> sortFields = List.of(new SortField("name", SortDirection.ASC));

        List<TestItem> sorted = SearchSortUtils.applySorting(items, sortFields);

        assertTrue(sorted.isEmpty());
    }
}