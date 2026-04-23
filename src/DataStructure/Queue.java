package DataStructure;

import model.Order;

public class Queue {
    private static final int MAX_SIZE = 3;
    private Order[] orders;
    private int front, rear, size;

    public Queue() {
        orders = new Order[MAX_SIZE];
        front = 0;
        rear = -1;
        size = 0;
    }

    public void enqueue(Order order) {
        if (order == null) return;

        rear = (rear + 1) % MAX_SIZE;
        orders[rear] = order;

        if (size < MAX_SIZE) {
            size++;
        } else {
            front = (front + 1) % MAX_SIZE;
        }
    }

    public Order[] getOrders() {
        if (isEmpty()) {
            return new Order[0];
        }

        Order[] result = new Order[size];
        for (int i = 0; i < size; i++) {
            int index = (front + i) % MAX_SIZE;
            result[i] = orders[index];
        }
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
