 package org.example.functions;

import org.example.exceptions.*;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable, Serializable {
    private static final Logger log = LoggerFactory.getLogger(LinkedListTabulatedFunction.class);
    protected static class Node {
        public Node next;
        public Node prev;
        public double x;
        public double y;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private Node head;
    protected int count;
    private static final long serialVersionUID = 1L; // Поле для сериализации

    // Конструктор через дискретизацию функции
    public  LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) {
            throw new IllegalArgumentException("Длина таблицы должна быть не менее 2 точек");
        }
        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        if (xFrom == xTo) {
            double y = source.apply(xFrom);
            for (int i = 0; i < count; i++) {
                addNode(xFrom, y);
            }
        } else {
            double step = (xTo - xFrom) / (count - 1);
            for (int i = 0; i < count; i++) {
                double x = xFrom + i * step;
                addNode(x, source.apply(x));
            }
        }
        log.debug("Создан LinkedListTabulatedFunction: размер={}, диапазон=[{}, {}]", this.count, xFrom, xTo);
    }

    // Конструктор из массивов x и y
    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Длина таблицы должна быть не менее 2 точек");
        }

        AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues); // Проверка на одинаковую длину X и Y
        AbstractTabulatedFunction.checkSorted(xValues);// Проверка на отсортированность X

        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
        log.debug("Создан LinkedListTabulatedFunction из массивов: размер={}, левая граница={}, правая граница={}", this.count, head.x, head.prev.x);
    }



    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Индекс " + index + " выходит за границы");
        }

        if (count < 2) {
            throw new IllegalStateException("Нельзя удалить элемент из таблицы с менее чем 2 точками");
        }

        Node nodeToRemove = getNode(index);

        // Если удаляется головной узел
        if (nodeToRemove == head) {
            head = head.next;
        }

        // Перестраиваем связи
        nodeToRemove.prev.next = nodeToRemove.next;
        nodeToRemove.next.prev = nodeToRemove.prev;

        // Очищаем ссылки удаляемого узла
        nodeToRemove.next = null;
        nodeToRemove.prev = null;

        count--;
        log.debug("remove: удалён индекс={}, новый размер={}", index, count);
    }


    // Приватный метод для добавления узла в конец списка
    private void addNode(double x, double y) {
        Node newNode = new Node(x, y);
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            Node last = head.prev;
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
        }
        count++;
        log.debug("addNode: (x={}, y={}), новый размер={}", x, y, count);
    }

    protected Node floorNodeOfX(double x) {
        if (count == 0) {
            throw new IllegalStateException("Список пуст");
        }

        if (x < head.x) {
            throw new IllegalArgumentException("x = " + x + " меньше левой границы " + head.x);
        }

        if (x >= head.prev.x) {
            return head.prev; // Для интерполяции в правой части
        }

        // Начинаем поиск с головы и идем до тех пор, пока не найдем правильный интервал
        Node current = head;
        while (current.next != head && x >= current.next.x) {
            current = current.next;
        }
        return current;
    }

    private Node findNodeByX(double x) {
        if (count == 0) return null;

        Node current = head;
        do {
            if (current.x == x) {
                return current;
            }
            current = current.next;
        } while (current != head);

        return null;
    }

    @Override
    public double apply(double x) {
        if (count == 0) {
            log.warn("apply: функция пуста, возвращено 0");
            return 0;
        }

        // Проверка точного совпадения
        Node exactNode = findNodeByX(x);
        if (exactNode != null) {
            log.debug("apply: точное совпадение x={} -> y={}", x, exactNode.y);
            return exactNode.y;
        }

        // Определение режима (экстраполяция/интерполяция)
        if (x < head.x) {
            log.debug("apply: x={} < левая граница={}, экстраполяция влево", x, head.x);
            return extrapolateLeft(x);
        } else if (x > head.prev.x) {
            log.debug("apply: x={} > правая граница={}, экстраполяция вправо", x, head.prev.x);
            return extrapolateRight(x);
        } else {
            Node leftNode = floorNodeOfX(x);
            log.debug("apply: интерполяция x={} между [{}, {}]", x, leftNode.x, leftNode.next.x);
            return interpolate(x, leftNode.x, leftNode.next.x, leftNode.y, leftNode.next.y);
        }
    }

    // метод для получения узла по индексу
    protected Node getNode(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Индекс " + index + " выходит за границы");
        }

        Node current;
        if (index <= count / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = head.prev;
            for (int i = count - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    // Реализация методов интерфейса TabulatedFunction
    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Индекс " + index + " выходит за границы [0, " + (count - 1) + "]");
        }
        return getNode(index).x;
    }

    @Override
    public double getY(int index) {
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) {
        getNode(index).y = value;
    }

    @Override
    public int indexOfX(double x) {
        Node current = head;
        for (int i = 0; i < count; i++) {
            if (current.x == x) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        Node current = head;
        for (int i = 0; i < count; i++) {
            if (current.y == y) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public double leftBound() {
        return head.x;
    }

    @Override
    public double rightBound() {
        return head.prev.x;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < head.x) {
            throw new IllegalArgumentException("x = " + x + " меньше левой границы ");
        }
        if (x > head.prev.x) {
            return count;
        }

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (current.x == x) {
                return i;
            }
            if (current.x > x) {
                return i - 1;
            }
            current = current.next;
        }
        return count;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return extrapolate(x, 0);
    }

    @Override
    protected double extrapolateRight(double x) {
        return extrapolate(x, count - 2);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        Node left = getNode(floorIndex);
        Node right = left.next;

        if (x < left.x || x > right.x) {
            throw new InterpolationException("x = " + x + " is out of interpolation range [" + left.x + ", " + right.x + "]");
        }

        return interpolate(x, left.x, right.x, left.y, right.y);
    }

    protected double extrapolate(double x, int floorIndex) {
        Node left = getNode(floorIndex);
        Node right = left.next;
        return interpolate(x, left.x, right.x, left.y, right.y);
    }

    // Реализация интерфейса insertable
    @Override
    public void insert(double x, double y) {
        // Если список пуст, просто добавляем узел
        if (count == 0) {
            addNode(x, y);
            log.debug("insert: вставлен первый узел x={}, y={}", x, y);
            return;
        }

        // Проверяем, существует ли узел с заданным x
        Node existingNode = findNodeByX(x);
        if (existingNode != null) {
            existingNode.y = y; // Заменяем y и выходим
            log.debug("insert: заменён существующий узел x={} значением y={}", x, y);
            return;
        }

        // Если x меньше головного узла, вставляем в начало
        if (x < head.x) {
            Node newNode = new Node(x, y);
            newNode.next = head;
            newNode.prev = head.prev;
            head.prev.next = newNode;
            head.prev = newNode;
            head = newNode; // Обновляем голову
            count++;
            log.debug("insert: вставка в голову x={}, y={}", x, y);
        }
        // Если x больше последнего узла, добавляем в конец
        else if (x > head.prev.x) {
            addNode(x, y);
            log.debug("insert: вставка в хвост x={}, y={}", x, y);
        }
        // Вставляем в середину списка
        else {
            Node floorNode = floorNodeOfX(x);
            Node newNode = new Node(x, y);
            newNode.next = floorNode.next;
            newNode.prev = floorNode;
            floorNode.next.prev = newNode;
            floorNode.next = newNode;
            count++;
            log.debug("insert: вставка после x={}, новый узел x={}, y={}", floorNode.x, x, y);
        }
    }

    @Override
    public java.util.Iterator<Point> iterator() {
        return new java.util.Iterator<Point>() {
            private Node node = head;
            private int returned = 0;

            @Override
            public boolean hasNext() {
                return node != null && returned < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                Point p = new Point(node.x, node.y);
                if (returned == count - 1) {
                    node = null;
                } else {
                    node = node.next;
                }
                returned++;
                return p;
            }
        };
    }
}