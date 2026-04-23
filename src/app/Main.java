package app;

import manager.*;
import model.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Managers
        UserManager userManager = new UserManager();
        ProductManager productManager = new ProductManager();
        CartManager cartManager = new CartManager();
        TransactionManager transactionManager = new TransactionManager();
        OrderManager orderManager = new OrderManager();

        final String ADMIN_EMAIL = "admin@shop.com";
        final String ADMIN_PASSWORD = "admin123";

        while (true) {
            try {
                System.out.println("\n--- Welcome to E-Commerce ---");
                System.out.println("1. Admin");
                System.out.println("2. Customer");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                int roleChoice = sc.nextInt();
                sc.nextLine();

                switch (roleChoice) {
                    case 1 -> {
                        System.out.print("Admin Email: ");
                        String email = sc.nextLine();
                        System.out.print("Password: ");
                        String password = sc.nextLine();

                        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                            userManager.setCurrentUser(new User(0, "Admin", email, "", password, "admin"));
                            System.out.println("✅ Admin logged in.");
                            adminMenu(sc, productManager, orderManager, userManager);
                        } else {
                            System.out.println("❌ Invalid admin credentials.");
                        }
                    }
                    case 2 -> customerFlow(sc, userManager, productManager, cartManager, transactionManager, orderManager, ADMIN_EMAIL);
                    case 3 -> {
                        System.out.println("👋 Exiting program. Goodbye!");
                        System.exit(0);
                    }
                    default -> System.out.println("❌ Invalid role choice.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a valid number.");
                sc.nextLine();
            }
        }
    }

    private static void customerFlow(Scanner sc, UserManager userManager, ProductManager productManager,
                                     CartManager cartManager, TransactionManager transactionManager,
                                     OrderManager orderManager, String ADMIN_EMAIL) {
        boolean back = false;
        while (!back) {
            try {
                System.out.println("\n--- Customer Access ---");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Back");
                System.out.print("Choose option: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.print("Name: ");
                        String name = sc.nextLine();

                        String email;
                        while (true) {
                            System.out.print("Email: ");
                            email = sc.nextLine();
                            if (email.contains("@") && email.contains(".")) break;
                            System.out.println("❌ Invalid email format.");
                        }

                        String phone;
                        while (true) {
                            System.out.print("Phone: ");
                            phone = sc.nextLine();
                            if (phone.matches("[6-9]\\d{9}")) break; // Must be 10 digits, starting with 9, 8, 7, or 6
                            System.out.println("❌ Invalid phone number. It must be 10 digits and start with 9, 8, 7, or 6.");
                        }

                        System.out.print("Password: ");
                        String password = sc.nextLine();

                        if (email.equalsIgnoreCase(ADMIN_EMAIL)) {
                            System.out.println("❌ Cannot register as admin.");
                        } else {
                            userManager.registerUser(name, email, phone, password, "customer");
                        }
                    }

                    case 2 -> {
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        System.out.print("Password: ");
                        String password = sc.nextLine();

                        if (email.equalsIgnoreCase(ADMIN_EMAIL)) {
                            System.out.println("❌ Use Admin login option.");
                            break;
                        }

                        if (userManager.login(email, password)) {
                            User currentUser = userManager.getCurrentUser();
                            customerMenu(sc, currentUser, productManager, cartManager, transactionManager, orderManager, userManager);
                        } else {
                            System.out.println("❌ Invalid credentials.");
                        }
                    }

                    case 3 -> back = true;
                    default -> System.out.println("❌ Invalid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Try again.");
                sc.nextLine();
            }
        }
    }

    private static void adminMenu(Scanner sc, ProductManager productManager, OrderManager orderManager, UserManager userManager) {
        while (true) {
            try {
                System.out.println("\n--- Admin Menu ---");
                System.out.println("1. Add Product");
                System.out.println("2. View Products");
                System.out.println("3. Update Product Stock");
                System.out.println("4. View All Orders");
                System.out.println("5. View All Transactions");
                System.out.println("6. Search Product by Name");
                System.out.println("7. Logout");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> {
                        try {
                            System.out.print("Product Name: ");
                            String name = sc.nextLine();
                            System.out.print("Category: ");
                            String category = sc.nextLine();
                            System.out.print("Price: ");
                            double price = sc.nextDouble();
                            System.out.print("Stock: ");
                            int stock = sc.nextInt();
                            sc.nextLine(); // Consume newline
                            System.out.print("Description: ");
                            String desc = sc.nextLine();

                            if (price < 0 || stock < 0) {
                                System.out.println("❌ Price and stock cannot be negative.");
                                break;
                            }

                            productManager.addProduct(new Product(name, category, price, stock, desc));
                        } catch (InputMismatchException e) {
                            System.out.println("❌ Invalid input format.");
                            sc.nextLine(); // Clear buffer
                        }
                    }

                    case 2 -> productManager.printAllProducts();

                    case 3 -> { // UPDATE STOCK
                        productManager.printAllProducts();
                        System.out.print("Enter Product ID to update: ");
                        int pid = sc.nextInt();
                        System.out.print("Enter New Stock: ");
                        int newStock = sc.nextInt();
                        sc.nextLine();

                        if (newStock < 0) {
                            System.out.println("❌ Stock cannot be negative.");
                        } else {
                            productManager.updateStock(pid, newStock); // Must exist in ProductManager
                        }
                    }

                    case 4 -> orderManager.printAllOrders();

                    case 5 -> {
                        TransactionManager transactionManager = new TransactionManager();
                        var transactions = transactionManager.getAllTransactions();
                        if (transactions.isEmpty()) {
                            System.out.println("📭 No transactions found.");
                        } else {
                            System.out.println("💳 All Transactions:");
                            for (Transaction t : transactions) {
                                transactionManager.generateBill(t);
                            }
                        }
                    }
                    case 6 -> {
                        System.out.print("Enter product name to search: ");
                        String searchName = sc.nextLine();
                        var results = productManager.searchProductsByNameBST(searchName);
                        if (results.isEmpty()) {
                            System.out.println("❌ No matching products found.");
                        } else {
                            results.forEach(System.out::println);
                        }
                    }
                    case 7 -> {
                        userManager.logout();
                        return;
                    }

                    default -> System.out.println("❌ Invalid option!");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input.");
                sc.nextLine();
            }
        }
    }

    private static void customerMenu(Scanner sc, User user, ProductManager productManager, CartManager cartManager,
                                     TransactionManager transactionManager, OrderManager orderManager,
                                     UserManager userManager) {
        while (true) {
            try {
                System.out.println("1. View Products");
                System.out.println("2. Add to Cart");
                System.out.println("3. View Cart");
                System.out.println("4. Checkout");
                System.out.println("5. View Orders");
                System.out.println("6. Cancel Last Order");
                System.out.println("7. Update Quantity in Cart");
                System.out.println("8. Remove Item from Cart");
                System.out.println("9. Update Shipping Address");
                System.out.println("10. Search Product by Name");
                System.out.println("11. View My Transactions");
                System.out.println("12. Edit Profile");
                System.out.println("13. Logout");

                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> productManager.printAllProducts();

                    case 2 -> {
                        System.out.print("Enter Product ID: ");
                        int pid = sc.nextInt();
                        System.out.print("Enter Quantity: ");
                        int qty = sc.nextInt();
                        sc.nextLine();

                        cartManager.addToCart(user.getId(), pid, qty, productManager);
                    }

                    case 3 -> cartManager.viewCart(user.getId(), productManager);

                    case 4 -> {
                        if (cartManager.isCartEmpty(user.getId())) {
                            System.out.println("❌ Your cart is empty.");
                            break;
                        }

                        // Calculate total first
                        double total = cartManager.calculateTotal(user.getId(), productManager);
                        boolean isHighValue = total >= 10000;

                        System.out.println("Enter Shipping Address:");
                        System.out.print("House/Flat No: ");
                        String houseNo = sc.nextLine().trim();
                        System.out.print("Building Name: ");
                        String buildingName = sc.nextLine().trim();
                        System.out.print("Area: ");
                        String area = sc.nextLine().trim();
                        System.out.print("Landmark: ");
                        String landmark = sc.nextLine().trim();
                        System.out.print("City: ");
                        String city = sc.nextLine().trim();
                        System.out.print("State: ");
                        String state = sc.nextLine().trim();
                        String pincode;
                        while (true) {
                            System.out.print("Pincode (6-digit): ");
                            pincode = sc.nextLine().trim();
                            if (pincode.matches("\\d{6}")) break;
                            System.out.println("❌ Invalid pincode. It must be 6 digits.");
                        }

                        String address = String.join(", ", houseNo, buildingName, area, landmark, city, state, pincode);

                        String paymentMethod;
                        while (true) {
                            System.out.print("Enter payment method (" +
                                    (isHighValue ? "card/upi" : "cash/card/upi") + "): ");
                            paymentMethod = sc.nextLine().trim().toLowerCase();

                            if (isHighValue && paymentMethod.equals("cash")) {
                                System.out.println("❌ Cash payment not allowed for orders above ₹10,000.");
                                System.out.println("Please use card or UPI payment.");
                                continue;
                            }

                            if (paymentMethod.matches("^(cash|card|upi)$")) {
                                break;
                            }
                            System.out.println("❌ Invalid payment method. Please enter 'cash', 'card', or 'upi'.");
                        }

                        // Card and UPI validation
                        if (paymentMethod.equals("card")) {
                            while (true) {
                                System.out.print("Enter 16-digit card number: ");
                                String cardNumber = sc.nextLine().trim();
                                if (cardNumber.matches("\\d{16}")) {
                                    System.out.println("✅ Card number accepted.");
                                    break;
                                } else {
                                    System.out.println("❌ Invalid card number. Must be exactly 16 digits.");
                                }
                            }
                        } else if (paymentMethod.equals("upi")) {
                            while (true) {
                                System.out.print("Enter UPI ID (e.g., name@upi): ");
                                String upiId = sc.nextLine().trim();
                                if (upiId.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+$")) {
                                    System.out.println("✅ UPI ID accepted.");
                                    break;
                                } else {
                                    System.out.println("❌ Invalid UPI ID. Format should be name@upi");
                                }
                            }
                        }

                        // Process transaction
                        Transaction txn = transactionManager.processTransaction(
                                user.getId(), total, paymentMethod);

                        if (txn != null && txn.getStatus().equals("success")) {
                            // Place order
                            Order order = orderManager.placeOrder(
                                    user.getId(),
                                    txn.getTransactionId(),
                                    cartManager.getCartItems(user.getId()),
                                    productManager,
                                    address
                            );

                            if (order != null) {
                                cartManager.clearCart(user.getId());
                                transactionManager.generateBill(txn);
                                System.out.println("✅ Order placed successfully!");
                            } else {
                                System.out.println("❌ Failed to place order.");
                            }
                        } else {
                            System.out.println("❌ Transaction failed. Order not placed.");
                        }
                    }

                    case 5 -> orderManager.printUserOrders(user.getId());

                    case 6 -> {
                        Order lastOrder = orderManager.getLastOrder(user.getId());
                        if (lastOrder != null) {
                            orderManager.cancelOrder(lastOrder.getId());
                        } else {
                            System.out.println("❌ You have no orders to cancel.");
                        }
                    }

                    case 7 -> {
                        System.out.print("Enter Product ID: ");
                        int pid = sc.nextInt();
                        System.out.print("Enter New Quantity (0 to remove): ");
                        int qty = sc.nextInt();
                        sc.nextLine();
                        cartManager.updateQuantity(user.getId(), pid, qty, productManager);
                    }

                    case 8 -> {
                        System.out.print("Enter Product ID to remove: ");
                        int pid = sc.nextInt();
                        sc.nextLine();
                        cartManager.removeFromCart(user.getId(), pid);
                    }
                    case 9 -> {
                        // Safely update shipping address
                        Order lastOrder = orderManager.getLastOrder(user.getId());

                        if (lastOrder == null) {
                            System.out.println("❌ You have no orders to update.");
                            break;
                        }

                        if (!lastOrder.getStatus().equalsIgnoreCase("processing")) {
                            System.out.println("❌ Cannot update address for an order that is already " + lastOrder.getStatus());
                            break;
                        }

                        System.out.println("\nUpdating Shipping Address for Order ID: " + lastOrder.getId());
                        System.out.print("House/Flat No: ");
                        String houseNo = sc.nextLine().trim();
                        System.out.print("Building Name: ");
                        String buildingName = sc.nextLine().trim();
                        System.out.print("Area: ");
                        String area = sc.nextLine().trim();
                        System.out.print("Landmark: ");
                        String landmark = sc.nextLine().trim();
                        System.out.print("City: ");
                        String city = sc.nextLine().trim();
                        System.out.print("State: ");
                        String state = sc.nextLine().trim();
                        String pincode;
                        while (true) {
                            System.out.print("Pincode (6-digit): ");
                            pincode = sc.nextLine().trim();
                            if (pincode.matches("\\d{6}")) break;
                            System.out.println("❌ Invalid pincode. It must be 6 digits.");
                        }

                        String newAddress = String.join(", ", houseNo, buildingName, area, landmark, city, state, pincode);
                        orderManager.updateShippingAddress(lastOrder.getId(), newAddress);
                    }
                    case 10 -> {
                        System.out.print("Enter product name to search: ");
                        String searchName = sc.nextLine();
                        var results = productManager.searchProductsByNameBST(searchName);
                        if (results.isEmpty()) {
                            System.out.println("❌ No matching products found.");
                        } else {
                            results.forEach(System.out::println);
                        }
                    }
                    case 11 -> {
                        var myTransactions = transactionManager.getUserTransactions(user.getId());
                        if (myTransactions.isEmpty()) {
                            System.out.println("📭 No transactions found.");
                        } else {
                            System.out.println("💳 Your Transactions:");
                            for (Transaction t : myTransactions) {
                                transactionManager.generateBill(t);
                            }
                        }
                    }
                    case 12 -> userManager.editCurrentUserDetails(sc);
                    case 13 -> {
                        userManager.logout();
                        return;
                    }

                    default -> System.out.println("❌ Invalid option!");
                }

            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input.");
                sc.nextLine();
            }
        }
    }
}
