package util;

import java.util.UUID;

public class Utils {
    public static String formatCurrency(double amount) {
        try {
            return String.format("₹%.2f", amount);
        } catch (Exception e) {
            System.err.println("❌ Error formatting currency: " + e.getMessage());
            return "₹0.00";
        }
    }

    public static String generateTransactionID() {
        try {
            return UUID.randomUUID().toString();
        } catch (Exception e) {
            System.err.println("❌ Error generating transaction ID: " + e.getMessage());
            return "UNKNOWN-ID";
        }
    }
}