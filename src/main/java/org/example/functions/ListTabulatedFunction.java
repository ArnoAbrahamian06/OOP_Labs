package org.example.functions;


public class ListTabulatedFunction {
    Node head;

    // Добавление элемента в конец списка
    public void append(int x, int y) {
        Node newNode = new Node(x, y);
        if (head == null) {
            head = newNode;
            return;
        }
        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = newNode;
    }

    // Вывод всех элементов списка
    public void printList() {
        Node current = head;
        while (current != null) {
            System.out.println("x: " + current.x + ", y: " + current.y);
            current = current.next;
        }
    }
}