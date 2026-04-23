package model;

public class Transaction {
    private String transactionId; // UUID-based transaction ID from Utils (primary key)
    private int userId;
    private double totalAmount;
    private String paymentMode; // upi/cash/card
    private String status; // success/failed/pending

    public Transaction(String transactionId, int userId, double totalAmount, String paymentMode, String status) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.paymentMode = paymentMode;
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
