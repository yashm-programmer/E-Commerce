🛒 E-Commerce Management System

A Java-based E-Commerce Management System that simulates a real-world online shopping platform. It provides a complete solution for user management, product catalog, shopping cart, and order processing, along with an admin panel for managing products and users.
This project demonstrates the integration of Core Java, Data Structures, and MySQL Database to build a scalable and efficient application.

✨ Features 

👥 **User Management**
- User registration and authentication
- Role-based access: Admin / Customer
- Profile management

📦 **Product Management**
- Browse products by categories
- Search products efficiently (using Binary Search Tree)
- View product details and availability

🛍️ **Shopping Cart**
- Add / remove items
- Update quantities
- View cart summary

📑 **Order Processing**
- Place orders
- Order history tracking
- Order status management

🔐 **Admin Panel**
- Manage products (Add / Edit / Delete)
- View and manage all orders
- Manage users

🛠️ **Technologies Used**
- Programming Language: Java
- Database: MySQL
- Database Management Tool: phpMyAdmin
- Data Structures: Binary Search Tree (BST), Queue
- Database Connectivity: JDBC
- Version Control: Git & GitHub

  📂 **Project Structure**
  ```bash
  /src
  /app                -> Main application classes
  /cli                -> Console interface classes
  /model              -> Entity classes (User, Product, Order, etc.)
  /service            -> Business logic & services
  /databaseConnection -> DBConnection setup
  /database
  SQLSchema.sql       -> Database schema
  README.md


⚙️ **Prerequisites**
- Java Development Kit (JDK 8+)
- MySQL Server
- phpMyAdmin (optional, for DB management)
- Git (for version control)


🚀 **Installation & Setup**
1. **Clone the repository**
```bash
git clone https://github.com/yashm-programmer/E-Commerce
cd E-Commerce
```
2. **Database Setup**
- Import SQLSchema.sql into your MySQL database (via phpMyAdmin or MySQL CLI).
- Update database connection details in DBConnection.java.

3. **Configure Application**
- Update DB credentials in configuration files.
- Ensure all required dependencies (JDBC driver, etc.) are included.

4. **Run the Application**
- Compile and run Main.java.
- Access features via the console interface.


🔑 **Default Admin Credentials**
- Email: admin@shop.com
- Password: admin123

 
 🚧 **Future Enhancements**
 - Payment Gateway Integration
- Web-based Frontend (React / Angular)
- REST API for external integration
- Product Recommendation System
- Advanced Security (JWT authentication, hashing passwords)


🙌 **Contributors**
- Honey Surati
- Manav Mehta
- Yash Mohanani
