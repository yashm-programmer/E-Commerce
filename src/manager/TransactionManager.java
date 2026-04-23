package manager;

import databaseConnection.DBConnection;
import model.Transaction;
import util.Utils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransactionManager {

    // Process transaction with simulated payment validation and database storage
    public Transaction processTransaction(int userId, double totalAmount, String paymentMode) {
        if (totalAmount <= 0) {
            System.out.println("❌ Transaction amount must be positive.");
            return null;
        }

        Transaction txn = null;
        try {
            String status = validatePayment(paymentMode, totalAmount) ? "success" : "failed";
            String transactionId = Utils.generateTransactionID(); // Generate UUID-based transaction ID

            // Insert transaction into database with UUID as primary key
            String insertSQL = "INSERT INTO transactions (transaction_id, user_id, total_amount, payment_mode, status) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertSQL)) {

                ps.setString(1, transactionId);
                ps.setInt(2, userId);
                ps.setDouble(3, totalAmount);
                ps.setString(4, paymentMode);
                ps.setString(5, status);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    txn = new Transaction(transactionId, userId, totalAmount, paymentMode, status);
                    System.out.println("✅ Transaction created with ID: " + transactionId);
                } else {
                    System.err.println("❌ Failed to create transaction");
                }
            } catch (SQLException e) {
                System.err.println("❌ Error saving transaction to database: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("❌ Error processing transaction: " + e.getMessage());
        }
        return txn;
    }

    // Simulate payment validation
    public boolean validatePayment(String paymentMode, double amount) {
        try {
            Random random = new Random();
            int chance = random.nextInt(100);
            if (chance < 90) {
                System.out.println("✅ Payment successful using " + paymentMode + " for amount ₹" + amount);
                return true;
            } else {
                System.out.println("❌ Payment failed! Please try again.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error validating payment: " + e.getMessage());
            return false;
        }
    }



    public void generateBill(Transaction txn) {
        try {
            if (txn == null) {
                System.out.println("⚠️ No transaction data to generate bill.");
                return;
            }

            // Prepare bill content
            StringBuilder bill = new StringBuilder();
            bill.append("----- Transaction Bill -----\n");
            bill.append("Transaction ID: ").append(txn.getTransactionId()).append("\n");
            bill.append("User ID: ").append(txn.getUserId()).append("\n");
            bill.append("Amount: ₹").append(util.Utils.formatCurrency(txn.getTotalAmount())).append("\n");
            bill.append("Payment Mode: ").append(txn.getPaymentMode().toUpperCase()).append("\n");
            bill.append("Status: ").append(txn.getStatus().toUpperCase()).append("\n");
            bill.append("-----------------------------\n");

            // Print bill to console
            System.out.println(bill);

            // Save bill to a text file
            String fileName = "C:\\Users\\Bhumi Patel\\OneDrive\\Desktop\\Sem-2 JAVA project\\FINAL SEM 2 PROJECT\\ECommerce\\src/" + txn.getTransactionId() + ".txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write(bill.toString());
                System.out.println("📝 Bill saved to file: " + fileName);
            } catch (IOException e) {
                System.err.println("❌ Error writing bill to file: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("❌ Error generating bill: " + e.getMessage());
        }
    }


    // Get all transactions from database
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try {
            String selectSQL = "SELECT * FROM transactions";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(selectSQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Transaction txn = new Transaction(
                            rs.getString("transaction_id"),
                            rs.getInt("user_id"),
                            rs.getDouble("total_amount"),
                            rs.getString("payment_mode"),
                            rs.getString("status")
                    );
                    transactions.add(txn);
                }
            } catch (SQLException e) {
                System.err.println("❌ Error fetching transactions from database: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("❌ Error fetching all transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Get all transactions of a specific user from database
    public List<Transaction> getUserTransactions(int userId) {
        List<Transaction> result = new ArrayList<>();
        try {
            String selectSQL = "SELECT * FROM transactions WHERE user_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(selectSQL)) {

                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Transaction txn = new Transaction(rs.getString("transaction_id"), rs.getInt("user_id"), rs.getDouble("total_amount"), rs.getString("payment_mode"), rs.getString("status"));
                        result.add(txn);
                    }
                }
            } catch (SQLException e) {
                System.err.println("❌ Error fetching user transactions from database: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("❌ Error fetching user transactions: " + e.getMessage());
        }
        return result;
    }

}
