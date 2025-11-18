// HierarchicalData.java
package org.example.db_service.Search;

import java.util.List;

//Интерфейс для иерархических данных

public interface HierarchicalData<T> {
    T getParent();
    List<T> getChildren();
    void setParent(T parent);
    void addChild(T child);
    void removeChild(T child);
    boolean isRoot();
    boolean isLeaf();
    int getDepth();
}