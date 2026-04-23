package manager;

import databaseConnection.DBConnection;
import model.Order;
import model.OrderItem;
import model.Product;

import java.sql.*;
import java.util.*;

public class OrderManager {

    /**
     * Places an order for the user by persisting to DB and updating stock.
     * Returns the created Order object or null on failure.
     */
    public Order placeOrder(int userId, String transactionId, Map<Integer, Integer> cartItems,
                            ProductManager productManager, String address) {

        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("❌ Cart is empty. Cannot place order.");
            return null;
        }

        if (address == null || address.isBlank()) {
            System.out.println("❌ Cannot place order: Address is required.");
            return null;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Step 1: Lock and verify stock within the transaction
            String stockCheckSql = "SELECT name, stock FROM products WHERE id = ? FOR UPDATE";
            try (PreparedStatement ps = conn.prepareStatement(stockCheckSql)) {
                for (Map.Entry<Integer, Integer> item : cartItems.entrySet()) {
                    ps.setInt(1, item.getKey());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            if (rs.getInt("stock") < item.getValue()) {
                                conn.rollback();
                                System.out.println("❌ Insufficient stock for product: " + rs.getString("name"));
                                return null;
                            }
                        } else {
                            conn.rollback();
                            System.out.println("❌ Product with ID " + item.getKey() + " not found.");
                            return null;
                        }
                    }
                }
            }

            // Step 2: Insert the order
            String orderSql = "INSERT INTO orders (user_id, transaction_id, status, shipping_address, order_date) VALUES (?, ?, ?, ?, CURDATE())";
            int orderId;
            try (PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, userId);
                orderStmt.setString(2, transactionId);
                orderStmt.setString(3, "successful"); // Status is now 'successful' on creation
                orderStmt.setString(4, address);
                orderStmt.executeUpdate();

                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        return null; // Failed to get ID
                    }
                }
            }

            // Step 3: Insert order items
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                    Product product = productManager.getProductById(entry.getKey());
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, entry.getKey());
                    itemStmt.setInt(3, entry.getValue());
                    itemStmt.setDouble(4, product.getPrice());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }

            // Step 4: Update stock in DB
            String updateStockSql = "UPDATE products SET stock = stock - ? WHERE id = ?";
            try (PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql)) {
                for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                    updateStockStmt.setInt(1, entry.getValue());
                    updateStockStmt.setInt(2, entry.getKey());
                    updateStockStmt.addBatch();
                }
                updateStockStmt.executeBatch();
            }

            conn.commit(); // Commit transaction

            // Update in-memory product stock after successful DB commit
            productManager.updateStockFromOrder(cartItems);

            return getOrder(orderId);

        } catch (SQLException e) {
            System.err.println("❌ Error placing order: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    System.err.println("❌ Error during rollback: " + ex.getMessage());
                }
            }
            return null;
        }
    }

    // Get orders by user ID
    public List<Order> getUserOrders(int userId) {
        List<Order> result = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC"; // Sort by ID descending

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("transaction_id"),
                            rs.getDate("order_date").toLocalDate(),
                            rs.getString("status"),
                            rs.getString("shipping_address")
                    );
                    result.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching user orders: " + e.getMessage());
        }
        return result;
    }

    // Get order items for an order
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> result = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem(
                            rs.getInt("id"),
                            rs.getInt("order_id"),
                            rs.getInt("product_id"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    );
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching order items: " + e.getMessage());
        }
        return result;
    }

    // Cancel order
    public void cancelOrder(int orderId) {
        Order order = getOrder(orderId);

        if (order == null) {
            System.out.println("❌ Order not found.");
            return;
        }

        // Only successful orders that are not shipped/delivered can be canceled.
        if (!order.getStatus().equalsIgnoreCase("successful")) {
            System.out.println("❌ Order cannot be canceled as it is already " + order.getStatus());
            return;
        }

        List<OrderItem> items = getOrderItems(orderId);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Restore stock
            String updateStockSql = "UPDATE products SET stock = stock + ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateStockSql)) {
                for (OrderItem item : items) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getProductId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // Update order status
            String updateOrderSql = "UPDATE orders SET status = 'cancelled' WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
                ps.setInt(1, orderId);
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Cancelling order failed, no rows affected.");
                }
            }

            conn.commit(); // Commit transaction
            System.out.println("✅ Order " + orderId + " has been canceled and stock restored.");

        } catch (SQLException e) {
            System.err.println("❌ Error canceling order: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("❌ Error during rollback: " + ex.getMessage());
                }
            }
        }
    }

    // Get all orders from database
    public List<Order> getAllOrders() {
        List<Order> result = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY id DESC"; // Sort by ID descending

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("transaction_id"),
                            rs.getDate("order_date").toLocalDate(),
                            rs.getString("status"),
                            rs.getString("shipping_address")
                    );
                    result.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching all orders: " + e.getMessage());
        }
        return result;
    }

    // Print summary for an order
    public void printOrderSummary(Order order) {
        System.out.println("Order ID: " + order.getId());
        System.out.println("User ID: " + order.getUserId());
        System.out.println("Transaction ID: " + order.getTransactionId());
        System.out.println("Date: " + order.getOrderDate());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Shipping Address: " + order.getShippingAddress());
        System.out.println("Items:");
        for (OrderItem item : getOrderItems(order.getId())) {
            System.out.println("  Product ID: " + item.getProductId() +
                    ", Qty: " + item.getQuantity() +
                    ", total amount: " + (item.getPrice() * item.getQuantity()));
        }
    }

    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error updating order status: " + e.getMessage());
        }
    }

    // Print all orders
    public void printAllOrders() {
        List<Order> allOrders = getAllOrders();
        if (allOrders.isEmpty()) {
            System.out.println("📬 No orders found.");
        } else {
            System.out.println("📋 All Orders:");
            for (Order order : allOrders) {
                System.out.println(order);
            }
        }
    }

    // Print orders for a specific user
    public void printUserOrders(int userId) {
        List<Order> userOrders = getUserOrders(userId);
        if (userOrders.isEmpty()) {
            System.out.println("📬 No orders found for user ID: " + userId);
        } else {
            System.out.println("🛒 Orders for user ID: " + userId);
            for (Order order : userOrders) {
                printOrderSummary(order);
                System.out.println("----------------------------");
            }
        }
    }

    // Get order by ID
    public Order getOrder(int orderId) {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("transaction_id"),
                            rs.getDate("order_date").toLocalDate(),
                            rs.getString("status"),
                            rs.getString("shipping_address")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching order: " + e.getMessage());
        }
        return order;
    }

    public Order getLastOrder(int userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC, id DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("transaction_id"),
                            rs.getDate("order_date").toLocalDate(),
                            rs.getString("status"),
                            rs.getString("shipping_address")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching last order: " + e.getMessage());
        }
        return null;
    }

    public void updateShippingAddress(int orderId, String newAddress) {
        String sql = "UPDATE orders SET shipping_address = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newAddress);
            ps.setInt(2, orderId);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Shipping address updated successfully for Order ID: " + orderId);
            } else {
                System.out.println("❌ Failed to update shipping address. Order not found.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error updating shipping address: " + e.getMessage());
        }
    }
}