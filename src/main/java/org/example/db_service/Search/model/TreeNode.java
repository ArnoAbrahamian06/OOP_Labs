// TreeNode.java
package org.example.db_service.Search.model;

import org.example.db_service.Search.HierarchicalData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

 //Пример реализации иерархических данных - узел дерева

public class TreeNode<T> implements HierarchicalData<TreeNode<T>> {
    private static final Logger logger = LoggerFactory.getLogger(TreeNode.class);

    private T data;
    private TreeNode<T> parent;
    private List<TreeNode<T>> children;
    private int depth;

    public TreeNode(T data) {
        this.data = data;
        this.children = new ArrayList<>();
        this.depth = 0;
    }

    @Override
    public TreeNode<T> getParent() {
        return parent;
    }

    @Override
    public List<TreeNode<T>> getChildren() {
        return children;
    }

    @Override
    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
        this.depth = parent != null ? parent.depth + 1 : 0;
        logger.debug("Установлен родитель для узла {}. Глубина: {}", data, depth);
    }

    @Override
    public void addChild(TreeNode<T> child) {
        if (!children.contains(child)) {
            children.add(child);
            child.setParent(this);
            logger.debug("Добавлен дочерний узел {} к родителю {}", child.data, data);
        }
    }

    @Override
    public void removeChild(TreeNode<T> child) {
        if (children.remove(child)) {
            child.setParent(null);
            logger.debug("Удален дочерний узел {} из родителя {}", child.data, data);
        }
    }

    @Override
    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public int getDepth() {
        return depth;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("TreeNode{data=%s, depth=%d, children=%d}",
                data, depth, children.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode<?> treeNode = (TreeNode<?>) o;
        return Objects.equals(data, treeNode.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}