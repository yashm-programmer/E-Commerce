-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 29, 2025 at 11:56 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ecommerce`
--

-- --------------------------------------------------------

--
-- Table structure for table `addresses`
--

CREATE TABLE `addresses` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `house_no` varchar(255) DEFAULT NULL,
  `building_name` varchar(255) DEFAULT NULL,
  `area` varchar(255) DEFAULT NULL,
  `landmark` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `pincode` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`id`, `user_id`, `product_id`, `quantity`) VALUES
(11, 3, 1, 4);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `shipping_address` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `user_id`, `transaction_id`, `order_date`, `status`, `shipping_address`) VALUES
(1, 2, '74a5fdb2-8ab4-47de-8efc-3488e9a31256', '2025-08-19', 'processing', 'ac, aecf, wef, wsf 123'),
(2, 2, '8d0b8c33-e914-4e25-9eee-b8a7fa0a3bcb', '2025-08-19', 'processing', 'ad, ad, safe, sfe 123412'),
(3, 4, 'cd017007-987b-4d93-ac44-11c07d909ca7', '2025-08-21', 'successful', 'b-21, jivraj park, shyamal, , ahmedabad, gujarat, 234567'),
(4, 5, 'd77e695c-9a78-46c9-a53c-cac1ce9738d3', '2025-08-21', 'successful', '33 sh, bun, sc, li, ahm, guj, 360080'),
(5, 5, '4d13ae73-3ddf-4bfe-a316-5cc10294fcd7', '2025-08-21', 'successful', '234, sddfh, dxfgj, dcfgf, dcfgf, dcfgfh, 123456'),
(6, 3, 'bf1d362f-af6f-4ad6-a6c6-740420e5c1b2', '2025-08-23', 'successful', '262, khh4, lhv, mjv, jbg, y, 123456'),
(7, 3, '8a46644f-e286-4df1-9b8d-27c8065508fa', '2025-08-23', 'successful', '2, j, j\\, gt, f, jb, 789456'),
(8, 2, 'd3d69301-6538-43b0-b3bf-d853e23de661', '2025-08-23', 'successful', ',gb, djsdbf, dbhere, dnjdbg, djbhg, dberh, 123456'),
(9, 6, 'df34fdd5-312e-4c8a-b955-c4ae2b2b5077', '2025-08-27', 'successful', '302, vraj vihar, rampura, nr vt choksi school, surat, gujarat, 395003');

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `id` int(11) NOT NULL,
  `order_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `quantity`, `price`) VALUES
(1, 1, 1, 8, 20.00),
(2, 2, 1, 1, 20.00),
(3, 2, 2, 2, 11000.00),
(4, 3, 2, 2, 11000.00),
(5, 4, 4, 1, 20.00),
(6, 5, 3, 5, 1000000.00),
(7, 6, 3, 5, 1000000.00),
(8, 7, 5, 2, 11.00),
(9, 8, 1, 5, 20.00),
(10, 9, 3, 2, 1000000.00),
(11, 9, 6, 3, 1000.00);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `stock` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `category`, `price`, `stock`, `description`) VALUES
(1, 'pillow', 'home and living', 20.00, 5, 'white plain pillow'),
(2, 'mobile ', 'electronics', 11000.00, 16, ''),
(3, 'iphone', 'electronics', 1000000.00, 4988, 'phone'),
(4, 'pillow', 'home and living', 20.00, 19, 'white plain pillow'),
(5, 'iphone', 'electronics', 11.00, 0, 'phone'),
(6, 'shoes', 'clothing', 1000.00, 6, 'boys');

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `transaction_id` varchar(255) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `payment_mode` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`transaction_id`, `user_id`, `total_amount`, `payment_mode`, `status`) VALUES
('06281f7c-50d9-4c64-893c-bfa06b6faa1a', 3, 80.00, 'cash', 'success'),
('3377300f-8132-49c4-8f2e-f6f578cdbcf1', 2, 160.00, 'cash', 'success'),
('4d13ae73-3ddf-4bfe-a316-5cc10294fcd7', 5, 5000000.00, 'card', 'success'),
('74a5fdb2-8ab4-47de-8efc-3488e9a31256', 2, 160.00, 'cash', 'success'),
('7515fb30-c890-45be-b275-850e1e72a28a', 5, 5000000.00, 'card', 'failed'),
('8a46644f-e286-4df1-9b8d-27c8065508fa', 3, 22.00, 'cash', 'success'),
('8d0b8c33-e914-4e25-9eee-b8a7fa0a3bcb', 2, 22020.00, 'card', 'success'),
('b1316d6d-5abb-498f-bc47-23d980e5277c', 2, 160.00, 'cash', 'failed'),
('bf1d362f-af6f-4ad6-a6c6-740420e5c1b2', 3, 5000000.00, 'upi', 'success'),
('cd017007-987b-4d93-ac44-11c07d909ca7', 4, 22000.00, 'upi', 'success'),
('d3d69301-6538-43b0-b3bf-d853e23de661', 2, 100.00, 'cash', 'success'),
('d77e695c-9a78-46c9-a53c-cac1ce9738d3', 5, 20.00, 'cash', 'success'),
('df34fdd5-312e-4c8a-b955-c4ae2b2b5077', 6, 2003000.00, 'upi', 'success');

--
-- Triggers `transactions`
--
DELIMITER $$
CREATE TRIGGER `before_transaction_insert` BEFORE INSERT ON `transactions` FOR EACH ROW BEGIN
    IF NEW.total_amount >= 10000 AND NEW.payment_mode = 'cash' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cash payment is not allowed for transactions of 10000 or more.';
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `before_transaction_update` BEFORE UPDATE ON `transactions` FOR EACH ROW BEGIN
    IF NEW.total_amount >= 10000 AND NEW.payment_mode = 'cash' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cash payment is not allowed for transactions of 10000 or more.';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `phone`, `password`, `role`) VALUES
(1, 'Admin', 'admin@shop.com', '9999999999', 'admin123', 'admin'),
(2, 'manav', 'man@gmail.com', '9876543210', 'man', 'customer'),
(3, 'honey', 'honey@gmail.com', '9033516149', '1234', 'customer'),
(4, 'ayushi shukla', 'thakarkavy522@gmail.com', '9898345678', 'ayu@123', 'customer'),
(5, 'mahi', 'mahi@gmail.com', '9510046052', 'mahi876', 'customer'),
(6, 'manav surti', 'manav@gmail.com', '9033516149', '1234', 'customer');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `addresses`
--
ALTER TABLE `addresses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `transaction_id` (`transaction_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`transaction_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `addresses`
--
ALTER TABLE `addresses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `cart`
--
ALTER TABLE `cart`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`);

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  ADD CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
