package manager;

import databaseConnection.DBConnection;
import model.Address;
import model.User;

import java.sql.*;
import java.util.*;

public class AddressManager {

    public void addAddress(int userId, String houseNo, String buildingName, String area, String landmark, String city, String state, String pincode) {
        String insertSQL = "INSERT INTO addresses (user_id, house_no, building_name, area, landmark, city, state, pincode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setString(2, houseNo);
            ps.setString(3, buildingName);
            ps.setString(4, area);
            ps.setString(5, landmark);
            ps.setString(6, city);
            ps.setString(7, state);
            ps.setString(8, pincode);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Address added successfully.");
            } else {
                System.out.println("❌ Failed to add address.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding address to database: " + e.getMessage());
        }
    }

    public List<Address> getUserAddresses(int userId) {
        List<Address> result = new ArrayList<>();
        String selectSQL = "SELECT * FROM addresses WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSQL)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Address address = new Address(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("house_no"),
                            rs.getString("building_name"),
                            rs.getString("area"),
                            rs.getString("landmark"),
                            rs.getString("city"),
                            rs.getString("state"),
                            rs.getString("pincode")
                    );
                    result.add(address);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching user addresses from database: " + e.getMessage());
        }
        return result;
    }

    public void printUserAddresses(int userId) {
        List<Address> addrList = getUserAddresses(userId);
        if (addrList.isEmpty()) {
            System.out.println("📭 No addresses found.");
            return;
        }
        System.out.println("🏠 Your Addresses:");
        for (Address a : addrList) {
            System.out.println(a.getId() + ": " + a.getHouseNo() + ", " + a.getBuildingName() + ", " + a.getArea() + ", " + a.getLandmark() + ", " +
                    a.getCity() + ", " + a.getState() + " - " + a.getPincode());
        }
    }

    public void manageAddresses(Scanner sc, User user) {
        boolean back = false;
        while (!back) {
            try {
                System.out.println("\n\uD83C\uDFE0 Address Management Menu");
                System.out.println("1. View Addresses");
                System.out.println("2. Add Address");
                System.out.println("3. Back");
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();
                sc.nextLine();  // consume newline

                switch (choice) {
                    case 1 -> printUserAddresses(user.getId());
                    case 2 -> {
                        System.out.print("Enter House/Flat No: ");
                        String houseNo = sc.nextLine();
                        System.out.print("Enter Building Name: ");
                        String buildingName = sc.nextLine();
                        System.out.print("Enter Area: ");
                        String area = sc.nextLine();
                        System.out.print("Enter Landmark: ");
                        String landmark = sc.nextLine();
                        System.out.print("Enter city: ");
                        String city = sc.nextLine();
                        System.out.print("Enter state: ");
                        String state = sc.nextLine();

                        String pincode;
                        while (true) {
                            System.out.print("Enter pincode (6-digit): ");
                            pincode = sc.nextLine();
                            if (pincode.matches("\\d{6}")) break;
                            else System.out.println("❌ Invalid pincode. It must be 6 digits.");
                        }

                        addAddress(user.getId(), houseNo, buildingName, area, landmark, city, state, pincode);
                    }

                    case 3 -> back = true;
                    default -> System.out.println("❌ Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a valid number.");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("❌ An unexpected error occurred: " + e.getMessage());
                sc.nextLine();
            }
        }
    }
}