package manager;

import databaseConnection.DBConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class UserManager {
    private User currentUser = null;

    // Constructor to ensure admin exists
    public UserManager() {
        try (Connection conn = DBConnection.getConnection()) {
            String checkAdminSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkAdminSql);
            checkStmt.setString(1, "admin@shop.com");
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                String insertAdminSql = "INSERT INTO users (name, email, phone, password, role) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertAdminSql);
                insertStmt.setString(1, "Admin");
                insertStmt.setString(2, "admin@shop.com");
                insertStmt.setString(3, "9999999999");
                insertStmt.setString(4, "admin123");
                insertStmt.setString(5, "admin");
                insertStmt.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("❌ Error ensuring admin exists: " + e.getMessage());
        }
    }

    // Register a new user (only customer allowed)
    public boolean registerUser(String name, String email, String phone, String password, String role) {
        try (Connection conn = DBConnection.getConnection()) {
            if (!role.equalsIgnoreCase("customer")) {
                System.out.println("❌ Only 'customer' role can be registered.");
                return false;
            }

            String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("⚠️ Email already registered.");
                return false;
            }

            String insertSql = "INSERT INTO users (name, email, phone, password, role) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, password);
            stmt.setString(5, "customer");
            stmt.executeUpdate();

            System.out.println("✅ Registration successful for " + name);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error registering user: " + e.getMessage());
            return false;
        }
    }

    // Login user directly from DB
    public boolean login(String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUser = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                System.out.println("🔐 Login successful! Welcome, " + currentUser.getName());
                return true;
            }
        } catch (Exception e) {
            System.err.println("❌ Error during login: " + e.getMessage());
        }
        System.out.println("❌ Invalid email or password.");
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("👋 Logged out: " + currentUser.getName());
            currentUser = null;
        } else {
            System.out.println("⚠️ No user is currently logged in.");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public User getUserById(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            System.err.println("❌ Error fetching user by ID: " + e.getMessage());
        }
        return null;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void editCurrentUserDetails(Scanner sc) {
        if (currentUser == null) {
            System.out.println("⚠️ No user is currently logged in.");
            return;
        }

        System.out.println("👤 Editing details for: " + currentUser.getName());

        System.out.print("Enter new name (leave blank to keep same): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) {
            currentUser.setName(name);
        }

        System.out.print("Enter new phone number (leave blank to keep same): ");
        String phone = sc.nextLine().trim();
        if (!phone.isEmpty()) {
            currentUser.setPhone(phone);
        }

        System.out.print("Enter new password (leave blank to keep same): ");
        String password = sc.nextLine().trim();
        if (!password.isEmpty()) {
            currentUser.setPassword(password);
        }

        updateUserInDatabase(currentUser);
        System.out.println("✅ Details updated successfully!");
    }

    private void updateUserInDatabase(User user) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE users SET name = ?, phone = ?, password = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("❌ Error updating user in database: " + e.getMessage());
        }
    }
}
