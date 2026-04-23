package model;

import java.time.LocalDate;

public class Order {
    private int id;
    private int userId;
    private String transactionId; // UUID-based transaction ID
    private LocalDate orderDate;
    private String status;
    private String shippingAddress;  // new field

    public Order(int id, int userId, String transactionId, LocalDate orderDate, String status, String shippingAddress) {
        this.id = id;
        this.userId = userId;
        this.transactionId = transactionId;
        this.orderDate = orderDate;
        this.status = status;
        this.shippingAddress = shippingAddress;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", transactionId=" + transactionId +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                '}';
    }
}
