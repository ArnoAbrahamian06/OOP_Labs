package org.example.functions;


public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Removable {
    private Node head;
    protected int count;

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("индекс находится за пределами допустимого диапазона: " + index);
        }

        if (count == 0) {
            throw new IllegalStateException("Удаление из пустого списка невозможно");
        }

        // Если удаляется единственный узел
        if (count == 1) {
            head = null;
            count = 0;
            return;
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

        // Для случая с одним узлом
        if (count == 1) {
            return head;
        }

        // Экстраполяция слева
        if (x < head.x) {
            return head;
        }

        // Экстраполяция справа
        if (x > head.prev.x) {
            return head.prev.prev;
        }

        // Определяем, с какой стороны начинать поиск
        double distanceFromStart = x - head.x;
        double distanceFromEnd = head.prev.x - x;

        if (distanceFromStart <= distanceFromEnd) {
            // x ближе к началу - ищем от головы
            Node current = head;
            while (current.next != head && x >= current.next.x) {
                current = current.next;
            }
            return current;
        } else {
            // x ближе к концу - ищем от хвоста
            Node current = head.prev;
            while (current != head && x < current.x) {
                current = current.prev;
            }
            return current;
        }
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

    // Конструктор из массивов x и y
    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
    }

    // Конструктор через дискретизацию функции
    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        if (count < 2) {
            throw new IllegalArgumentException("Count must be greater than 1");
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

    // метод для получения узла по индексу
    protected Node getNode(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
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
            return 0;
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
        return interpolate(x, head.x, head.next.x, head.y, head.next.y);
    }

    @Override
    protected double extrapolateRight(double x) {
        Node last = head.prev;
        Node prevLast = last.prev;
        return interpolate(x, prevLast.x, last.x, prevLast.y, last.y);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        Node left = getNode(floorIndex);
        Node right = left.next;
        return interpolate(x, left.x, right.x, left.y, right.y);
    }
}