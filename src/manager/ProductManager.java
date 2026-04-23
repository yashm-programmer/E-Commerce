package manager;

import DataStructure.BST;
import databaseConnection.DBConnection;
import model.Product;

import java.sql.*;
import java.util.*;

public class ProductManager {
    private final Map<Integer, Product> productMap = new HashMap<>();
    private BST productBST;

    // Load products from DB on object creation
    public ProductManager() {
        loadProductsFromDB();
    }

    private void loadProductsFromDB() {
        productMap.clear();
        productBST = new BST(); // Initialize BST
        String query = "SELECT id, name, category, price, stock, description FROM products";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("description")
                );
                productMap.put(product.getId(), product);
                productBST.insert(product); // Add product to BST
            }
        } catch (SQLException e) {
            System.out.println("❌ Error loading products from DB: " + e.getMessage());
        }
    }

    public void addProduct(Product product) {
        // Check if a product with the same name, category, description, and price exists
        String checkSQL = "SELECT id, stock FROM products WHERE LOWER(name) = LOWER(?) AND " +
                "LOWER(category) = LOWER(?) AND price = ? AND LOWER(description) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {

            checkStmt.setString(1, product.getName());
            checkStmt.setString(2, product.getCategory());
            checkStmt.setDouble(3, product.getPrice());
            checkStmt.setString(4, product.getDescription());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Product exists, update its stock
                    int existingId = rs.getInt("id");
                    int currentStock = rs.getInt("stock");
                    int newStock = currentStock + product.getStock();

                    // Update the existing product's stock
                    String updateSQL = "UPDATE products SET stock = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        updateStmt.setInt(1, newStock);
                        updateStmt.setInt(2, existingId);
                        updateStmt.executeUpdate();

                        System.out.println("✅ Stock updated for existing product ID: " + existingId);
                        loadProductsFromDB(); // Reload to reflect changes
                        return; // Exit after updating stock
                    }
                }
            }

            // If no matching product exists, insert a new one
            String insertSQL = "INSERT INTO products (name, category, price, stock, description) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, product.getName());
                ps.setString(2, product.getCategory());
                ps.setDouble(3, product.getPrice());
                ps.setInt(4, product.getStock());
                ps.setString(5, product.getDescription());

                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0) {
                    System.out.println("❌ Adding product failed, no rows affected.");
                    return;
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        product.setId(newId);
                        System.out.println("✅ New product added with ID: " + newId);
                        loadProductsFromDB(); // Reload to update map and BST
                    } else {
                        System.out.println("❌ Adding product failed, no ID obtained.");
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ SQL error in addProduct: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    public List<Product> searchProductsByName(String name) {
        List<Product> result = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(product);
            }
        }
        return result;
    }

    public List<Product> searchProductsByNameBST(String name) {
        return productBST.searchByName(name);
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> result = new ArrayList<>();
        for (Product product : productMap.values()) {
            if (product.getCategory() != null &&
                    product.getCategory().equalsIgnoreCase(category)) {
                result.add(product);
            }
        }
        return result;
    }

    public Product getProductById(int id) {
        return productMap.get(id);
    }

    public Set<String> getAllCategories() {
        Set<String> categories = new HashSet<>();
        for (Product product : productMap.values()) {
            if (product.getCategory() != null) {
                categories.add(product.getCategory().toLowerCase());
            }
        }
        return categories;
    }

    public void printAllProducts() {
        loadProductsFromDB(); // reload from DB
        if (productMap.isEmpty()) {
            System.out.println("🛒 No products available.");
        } else {
            System.out.println("📦 Available Products:");
            for (Product product : productMap.values()) {
                System.out.println(product);
            }
        }
    }

    public void reduceStock(int productId, int quantity) {
        Product product = productMap.get(productId);
        if (product != null) {
            int newStock = Math.max(product.getStock() - quantity, 0);
            product.setStock(newStock);

            String updateSQL = "UPDATE products SET stock = ? WHERE id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateSQL)) {

                ps.setInt(1, newStock);
                ps.setInt(2, productId);
                ps.executeUpdate();

            } catch (SQLException e) {
                System.out.println("❌ Error updating stock in DB: " + e.getMessage());
            }
        }
    }

    public void updateStock(int productId, int newStock) {
        String query = "UPDATE products SET stock = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                loadProductsFromDB(); // Reload to update map and BST
                System.out.println("✅ Stock updated successfully for Product ID: " + productId);
            } else {
                System.out.println("❌ Product not found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error updating stock: " + e.getMessage());
        }
    }

    public void updateStockFromOrder(Map<Integer, Integer> cartItems) {
        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = productMap.get(productId);
            if (product != null) {
                product.setStock(product.getStock() - quantity);
            }
        }
    }
}
