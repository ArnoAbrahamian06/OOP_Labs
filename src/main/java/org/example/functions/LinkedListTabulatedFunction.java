package org.example.functions;

import org.example.exceptions.*;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable {
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

    // Конструктор через дискретизацию функции
    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
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
            System.out.println("Функция не содержит точек");
            return 0;
        }

        // Проверка точного совпадения
        Node exactNode = findNodeByX(x);
        if (exactNode != null) {
            return exactNode.y;
        }

        // Определение режима (экстраполяция/интерполяция)
        if (x < head.x) {
            return extrapolateLeft(x);
        } else if (x > head.prev.x) {
            return extrapolateRight(x);
        } else {
            Node leftNode = floorNodeOfX(x);
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
        Node last = head.prev;
        Node prevLast = last.prev;
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
            return;
        }

        // Проверяем, существует ли узел с заданным x
        Node existingNode = findNodeByX(x);
        if (existingNode != null) {
            existingNode.y = y; // Заменяем y и выходим
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
        }
        // Если x больше последнего узла, добавляем в конец
        else if (x > head.prev.x) {
            addNode(x, y);
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
        }
    }
}