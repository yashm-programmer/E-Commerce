package manager;

import databaseConnection.DBConnection;
import model.Product;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CartManager {

    // Add to cart
    public void addToCart(int userId, int productId, int quantity, ProductManager productManager) {
        // Input validation
        if (quantity <= 0) {
            System.out.println("❌ Quantity must be a positive number.");
            return;
        }

        // Check product availability and stock
        Product product = productManager.getProductById(productId);
        if (product == null) {
            System.out.println("❌ Product not found.");
            return;
        }

        int currentQtyInCart = getCartItems(userId).getOrDefault(productId, 0);
        if (quantity + currentQtyInCart > product.getStock()) {
            System.out.println("❌ Not enough stock! Available: " + product.getStock() + ", in cart: " + currentQtyInCart);
            return;
        }

        String selectQuery = "SELECT quantity FROM cart WHERE user_id = ? AND product_id = ?";
        String insertQuery = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            selectStmt.setInt(1, userId);
            selectStmt.setInt(2, productId);

            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                // Entry exists, update quantity
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();
            } else {
                // Insert new entry
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();
            }
            System.out.println("✅ Product added to cart.");
        } catch (SQLException e) {
            System.out.println("❌ Error adding to cart: " + e.getMessage());
        }
    }

    // Remove from cart
    public void removeFromCart(int userId, int productId) {
        String deleteQuery = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, productId);

            int affected = deleteStmt.executeUpdate();

            if (affected > 0) {
                System.out.println("🗑️ Product removed from cart.");
            } else {
                System.out.println("❌ Product not found in cart.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error removing from cart: " + e.getMessage());
        }
    }

    // Get cart items
    public Map<Integer, Integer> getCartItems(int userId) {
        Map<Integer, Integer> cart = new HashMap<>();
        String selectQuery = "SELECT product_id, quantity FROM cart WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setInt(1, userId);
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                cart.put(rs.getInt("product_id"), rs.getInt("quantity"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error retrieving cart items: " + e.getMessage());
        }
        return cart;
    }

    // Calculate total
    public double calculateTotal(int userId, ProductManager productManager) {
        double total = 0.0;
        try {
            Map<Integer, Integer> cart = getCartItems(userId);
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                int productId = entry.getKey();
                int qty = entry.getValue();
                Product product = productManager.getProductById(productId);
                if (product != null) {
                    total += product.getPrice() * qty;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error calculating total: " + e.getMessage());
        }
        return total;
    }

    // Clear cart
    public void clearCart(int userId) {
        String deleteQuery = "DELETE FROM cart WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            deleteStmt.setInt(1, userId);
            deleteStmt.executeUpdate();
            System.out.println("🧹 Cart cleared.");
        } catch (SQLException e) {
            System.out.println("❌ Error clearing cart: " + e.getMessage());
        }
    }

    // Check if cart is empty
    public boolean isCartEmpty(int userId) {
        String countQuery = "SELECT COUNT(*) AS cnt FROM cart WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countQuery)) {

            countStmt.setInt(1, userId);
            ResultSet rs = countStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cnt") == 0;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error checking cart: " + e.getMessage());
        }
        return true;
    }

    // Update quantity
    public void updateQuantity(int userId, int productId, int newQty, ProductManager productManager) {
        if (newQty < 0) {
            System.out.println("❌ Quantity cannot be negative.");
            return;
        }

        if (newQty > 0) {
            Product product = productManager.getProductById(productId);
            if (product == null) {
                System.out.println("❌ Product not found.");
                return;
            }
            if (newQty > product.getStock()) {
                System.out.println("❌ Not enough stock! Available: " + product.getStock());
                return;
            }
        }

        String updateQuery = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        String deleteQuery = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (newQty > 0) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, newQty);
                    updateStmt.setInt(2, userId);
                    updateStmt.setInt(3, productId);
                    int affected = updateStmt.executeUpdate();
                    if (affected > 0) {
                        System.out.println("✅ Quantity updated to " + newQty);
                    } else {
                        System.out.println("❌ Product not found in cart.");
                    }
                }
            } else {
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, userId);
                    deleteStmt.setInt(2, productId);
                    int affected = deleteStmt.executeUpdate();
                    if (affected > 0) {
                        System.out.println("🗑️ Product removed from cart.");
                    } else {
                        System.out.println("❌ Product not found in cart.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error updating quantity: " + e.getMessage());
        }
    }

    // View cart
    public void viewCart(int userId, ProductManager productManager) {
        try {
            Map<Integer, Integer> cart = getCartItems(userId);
            if (cart.isEmpty()) {
                System.out.println("🛒 Your cart is empty.");
                return;
            }

            System.out.println("🛍️ Cart for User ID: " + userId);
            double total = 0.0;
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                int productId = entry.getKey();
                int quantity = entry.getValue();
                Product product = productManager.getProductById(productId);

                if (product != null) {
                    double subtotal = product.getPrice() * quantity;
                    System.out.println("- " + product.getName() + " | ₹" + product.getPrice() +
                            " x " + quantity + " = ₹" + subtotal);
                    total += subtotal;
                } else {
                    System.out.println("- Product ID " + productId + " not found.");
                }
            }
            System.out.println("💰 Total: ₹" + total);
        } catch (Exception e) {
            System.out.println("❌ Error viewing cart: " + e.getMessage());
        }
    }
}
