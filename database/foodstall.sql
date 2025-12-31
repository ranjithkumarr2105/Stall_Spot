-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 27, 2025 at 02:28 PM
-- Server version: 8.0.42
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `foodstall`
--

-- --------------------------------------------------------

--
-- Table structure for table `app_policies`
--

CREATE TABLE `app_policies` (
  `policy_id` int NOT NULL,
  `policy_version` varchar(10) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1.0',
  `policy_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'e.g., owner_agreement',
  `content` text COLLATE utf8mb4_general_ci NOT NULL,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `app_policies`
--

INSERT INTO `app_policies` (`policy_id`, `policy_version`, `policy_type`, `content`, `last_updated`) VALUES
(1, '1.0', 'owner_agreement', '<h1>Stall Spot Terms of Service for Owners</h1><p>Welcome to Stall Spot! By accepting this agreement, you agree to the following terms...</p><p><b>1. Rent Policy:</b> Rent is calculated based on monthly revenue. For revenue up to ₹50,000, the rent is 10%. For revenue above ₹50,000, the rent is 15%.</p><p><b>2. Payment Deadline:</b> Rent must be paid by the 8th of each month. A late fee of ₹50 per day will be applied thereafter.</p><p><b>3. Service Obligations:</b> You are required to maintain a high standard of quality and hygiene...</p><p><i>(Admin: Please replace this with your full legal agreement text).</i></p>', '2025-10-21 12:01:30'),
(2, '1.0', 'user_privacy', '<h2>User Privacy Policy</h2><p><b>Last Updated: October 23, 2025</b></p><p>Welcome to Stall Spot! We value your privacy. This policy explains how we collect, use, and protect your information when you use our app.</p><p><b>Information We Collect:</b></p><ul>    <li><b>Account Information:</b> Name, Student ID, Email, Phone Number, Password (hashed).</li>    <li><b>Order Information:</b> Stalls visited, items ordered, order times, payment details (processed via Razorpay or Wallet).</li>    <li><b>Usage Data:</b> Favorite stalls, reviews submitted, app interactions.</li>    <li><b>Location Data (Optional):</b> If you grant permission, we collect your location to show nearby stalls. You can disable this anytime in your device settings.</li></ul><p><b>How We Use Information:</b></p><ul>    <li>To process your orders and payments.</li>    <li>To facilitate communication between you and stall owners regarding orders.</li>    <li>To personalize your experience (e.g., showing relevant stalls or specials).</li>    <li>To improve our app and services.</li>    <li>To provide customer support.</li>    <li>To send important updates or promotional offers (you can opt-out).</li></ul><p><b>Data Sharing:</b> We share necessary order details (items, pickup time) with the stall owner you order from. Payment information is handled securely by Razorpay or our internal wallet system. We do not sell your personal data to third parties.</p><p><b>Security:</b> We use standard security measures like encryption (SSL) to protect your data.</p><p><b>Contact Us:</b> If you have questions, please contact us via the Help & Support section or email privacy@stallspot.com.</p>', '2025-10-23 14:19:57'),
(3, '1.0', 'owner_terms', '<h2>Stall Owner Terms & Conditions</h2><p><b>Last Updated: October 23, 2025</b></p><p>These terms govern your use of the Stall Spot platform as a registered stall owner.</p><p><b>1. Account & Stall Information:</b> You agree to provide accurate and complete information during registration and keep your stall details (name, menu, pricing, operating hours, FSSAI number) up-to-date.</p><p><b>2. Order Fulfillment:</b> You are responsible for promptly reviewing incoming orders, preparing food safely and according to the order details, and having orders ready by the specified pickup time (for pre-orders). You must handle order acceptance/rejection professionally.</p><p><b>3. Menu & Pricing:</b> You control your menu items, descriptions, and prices displayed in the app. Ensure all information is accurate.</p><p><b>4. Payments & Fees:</b> Orders paid via Razorpay or Wallet will be credited to your account balance within the app, viewable in your dashboard. Stall Spot charges a monthly rent based on a percentage of your revenue generated through the app, calculated automatically.</p><p><b>5. Rent Payment:</b> Monthly rent invoices are generated automatically. You are responsible for paying the rent on time via the owner dashboard (using Razorpay). Failure to pay rent by the due date may result in late fees and automatic temporary closure of your stall within the app until payment is received.</p><p><b>6. Food Safety & Compliance:</b> You are solely responsible for adhering to all food safety regulations, maintaining necessary licenses (like FSSAI), and ensuring the quality of food served.</p><p><b>7. Data Privacy:</b> You will have access to customer order details necessary for fulfillment. You agree to handle this data responsibly and only for order-related purposes, respecting user privacy.</p><p><b>8. Termination:</b> Stall Spot reserves the right to suspend or terminate your account for violations of these terms, failure to pay rent, or other breaches of conduct.</p><p><b>Contact Us:</b> For owner-specific support, use the Help & Support section or email ownersupport@stallspot.com.</p>', '2025-10-23 14:20:06'),
(4, '1.0', 'admin_policy', '<h2>Administrator Policy & Guidelines</h2><p><b>Last Updated: October 23, 2025</b></p><p>This document outlines the responsibilities and conduct expected of Stall Spot administrators.</p><p><b>1. Role & Responsibilities:</b> Administrators manage the overall platform health. Key duties include:</p><ul>    <li>Reviewing and approving/rejecting new stall applications based on established criteria (completeness, FSSAI verification, etc.).</li>    <li>Providing clear reasons for rejection.</li>    <li>Managing stall locations and ensuring accuracy.</li>    <li>Monitoring platform activity and addressing issues reported by users or owners.</li>    <li>Overseeing the automated rent collection system and handling exceptions.</li>    <li>Maintaining the integrity and confidentiality of platform data.</li></ul><p><b>2. Data Access:</b> Admin accounts have access to user, owner, order, and financial data. This access is strictly for performing administrative duties. Confidentiality must be maintained at all times. Sharing or misusing this data is grounds for immediate access revocation.</p><p><b>3. Stall Approval Process:</b> Review applications diligently. Verify FSSAI numbers where possible. Ensure stall details are appropriate. Approve or reject based on defined guidelines, not personal bias. Provide constructive feedback for rejections.</p><p><b>4. Rent System Oversight:</b> Monitor the automated rent calculation and notification process. Address owner queries regarding rent invoices fairly. Follow procedures for handling overdue payments and stall closures.</p><p><b>5. Account Security:</b> Protect your admin login credentials. Do not share your password. Report any suspicious activity immediately.</p><p><b>6. Professional Conduct:</b> Interact with users and owners professionally and respectfully when handling support inquiries or disputes.</p><p><b>Contact:</b> For internal admin support or reporting issues, contact admin.internal@stallspot.com.</p>', '2025-10-23 14:20:16');

-- --------------------------------------------------------

--
-- Table structure for table `favorite_stalls`
--

CREATE TABLE `favorite_stalls` (
  `favorite_id` int NOT NULL,
  `student_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `stall_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `favorite_stalls`
--

INSERT INTO `favorite_stalls` (`favorite_id`, `student_id`, `stall_id`) VALUES
(14, '192210687', 'S81208'),
(11, '192210688', 'S94231');

-- --------------------------------------------------------

--
-- Table structure for table `menudetails`
--

CREATE TABLE `menudetails` (
  `item_id` int NOT NULL,
  `stall_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `item_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `item_price` decimal(10,2) NOT NULL,
  `item_category` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `item_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `is_available` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `menudetails`
--

INSERT INTO `menudetails` (`item_id`, `stall_id`, `item_name`, `item_price`, `item_category`, `item_image`, `is_available`) VALUES
(2, 'S44791', 'Briyani', 100.00, 'Regular', 'S44791_Briyani_1758010915.jpg', 1),
(3, 'S44791', 'Fried rice', 98.00, 'Regular', 'S44791_Friedrice_1758010944.jpg', 1),
(4, 'S44791', 'idly + dosa', 68.00, 'Combo', 'S44791_idlydosa_1758011131.jpg', 1),
(5, 'S44791', 'Poori', 50.00, 'Regular', 'S44791_Poori_1758012793.jpg', 1),
(6, 'S44791', 'Masala Dosa', 40.00, 'Regular', 'S44791_MasalaDosa_1758012889.jpg', 1),
(7, 'S94231', 'Chapathi', 40.00, 'Regular', 'S94231_Chapathi_1758032004.jpg', 1),
(8, 'S94231', 'Idly', 30.00, 'Regular', 'S94231_Idly_1758032089.jpg', 1),
(9, 'S94231', 'Burger+Coc+ Fries', 99.00, 'Combo', 'S94231_BurgerCocFries_1758032213.jpg', 1),
(10, 'S94231', 'Podi dosa', 40.00, 'Regular', 'S94231_Podidosa_1758032310.jpg', 1),
(11, 'S94231', 'Variety Meals+Side dish', 89.00, 'Today\'s Special', 'S94231_VarietyMealsSidedish_1758032448.jpg', 1),
(12, 'S81208', 'Ada Dosa', 39.00, 'Regular', 'S81208_AdaDosa_1758032859.jpg', 1),
(13, 'S81208', 'Chinese Meals', 69.00, 'Combo', 'S81208_ChineseMeals_1758032947.jpg', 1),
(14, 'S81208', 'Rava kichidi', 39.00, 'Regular', 'S81208_Ravakichidi_1758033058.jpg', 1),
(15, 'S81208', 'Idiyapam', 49.00, 'Regular', 'S81208_Puttu_1758033088.jpg', 1),
(16, 'S81208', 'Puttu', 59.00, 'Today\'s Special', 'S81208_Puttu_1758033233.jpg', 1),
(17, 'S04525', 'Amritsari Kulcha', 99.00, 'Today\'s Special', 'S04525_AmritsariKulcha_1758034255.jpg', 1),
(18, 'S04525', 'Punjabi Thali', 89.00, 'Combo', 'S04525_PunjabiThali_1758034295.jpg', 1),
(19, 'S04525', 'Makki Di Roti', 69.00, 'Regular', 'S04525_MakkiDiRoti_1758034427.jpg', 1),
(20, 'S04525', 'Amritsari Fish', 59.00, 'Regular', 'S04525_AmritsariFish_1758034487.jpg', 1),
(21, 'S04525', 'Chole Bhature', 59.00, 'Regular', 'S04525_CholeBhature_1758034575.jpg', 1),
(22, 'S04525', 'Dal Makhani', 59.00, 'Regular', 'S04525_DalMakhani_1758034667.jpg', 1),
(23, 'S44791', 'Panner butter Masala', 99.00, 'Today\'s Special', 'S44791_PannerbutterMasala_1758034881.jpg', 1),
(24, 'S44791', 'Rava Upma', 39.00, 'Regular', 'S44791_RavaUpma_1758035008.jpg', 1);

-- --------------------------------------------------------

--
-- Table structure for table `menu_items`
--

CREATE TABLE `menu_items` (
  `item_id` int NOT NULL,
  `stall_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `description` text COLLATE utf8mb4_general_ci,
  `category` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `is_available` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int NOT NULL,
  `stall_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `student_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00',
  `parcel_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `order_status` varchar(50) COLLATE utf8mb4_general_ci DEFAULT 'Pending',
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `payment_method` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `parcel_type` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `payment_id` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `pickup_time` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `display_order_id` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `refund_timestamp` datetime DEFAULT NULL COMMENT 'Timestamp when a refund was processed'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `stall_id`, `student_id`, `total_amount`, `subtotal`, `parcel_fee`, `order_status`, `order_date`, `payment_method`, `parcel_type`, `payment_id`, `pickup_time`, `display_order_id`, `updated_at`, `refund_timestamp`) VALUES
(1, 'S44791', '192210687', 100.00, 0.00, 0.00, 'Pending', '2025-09-13 04:55:37', 'Razorpay', NULL, '987654578', '10AM', NULL, '2025-10-21 12:58:38', NULL),
(2, 'S04525', '192210687', 78.00, 0.00, 0.00, 'Pending', '2025-09-13 04:58:41', 'Razorpay', NULL, 'pay_RGxisz8X5rRA9o', '10:30 AM', NULL, '2025-10-21 12:58:38', NULL),
(3, 'S81208', '192210687', 50.00, 0.00, 0.00, 'Pending', '2025-09-13 05:01:10', 'Razorpay', NULL, 'pay_RGxlZNmITOMvGC', '10:45 AM', NULL, '2025-10-21 12:58:38', NULL),
(4, 'S04525', '192210687', 68.00, 0.00, 0.00, 'Pending', '2025-09-13 05:28:51', 'Razorpay', NULL, 'pay_RGyEjaG9JA0EF1', '11:15 AM', NULL, '2025-10-21 12:58:38', NULL),
(5, 'S04525', '192210687', 108.00, 0.00, 0.00, 'Pending', '2025-09-13 05:30:46', 'Razorpay', NULL, 'pay_RGyGm5hxCoR9lC', '11:30 AM', NULL, '2025-10-21 12:58:38', NULL),
(6, 'S94231', '192210687', 120.00, 0.00, 0.00, 'Pending', '2025-09-13 05:56:12', 'Razorpay', NULL, 'pay_RGyhf8vbrWOlkX', '11:45 AM', 'S94-1', '2025-10-21 12:58:38', NULL),
(7, 'S94231', '192210687', 50.00, 0.00, 0.00, 'Pending', '2025-09-13 06:55:55', 'Wallet', NULL, 'wallet_txn_68c5157b88b10', NULL, 'S94-2', '2025-10-21 12:58:38', NULL),
(8, 'S04525', '192210687', 376.00, 0.00, 0.00, 'Rejected', '2025-09-18 15:13:38', 'Razorpay', NULL, 'pay_RJ6ryYX4y2u8z0', NULL, 'S04-4', '2025-10-21 12:58:38', NULL),
(9, 'S44791', '192210687', 150.00, 0.00, 0.00, 'Pending', '2025-09-18 15:20:49', 'Razorpay', NULL, 'pay_RJ6zdNryl4fYTV', NULL, 'S44-2', '2025-10-21 12:58:38', NULL),
(10, 'S81208', '192210687', 147.00, 0.00, 0.00, 'Pending', '2025-09-18 15:25:05', 'Razorpay', NULL, 'pay_RJ74A55O6rs3Qt', NULL, 'S81-2', '2025-10-21 12:58:38', NULL),
(11, 'S94231', '192210687', 436.00, 0.00, 0.00, 'Pending', '2025-09-18 15:26:56', 'Razorpay', NULL, 'pay_RJ7650x9KMgHs5', NULL, 'S94-3', '2025-10-21 12:58:38', NULL),
(12, 'S94231', '192210687', 208.00, 0.00, 0.00, 'Pending', '2025-09-19 05:41:20', 'Razorpay', NULL, 'pay_RJLeT33aAI8dKU', '11:30 AM', 'S94-4', '2025-10-21 12:58:38', NULL),
(13, 'S04525', '192210687', 96.00, 0.00, 0.00, 'Delivered', '2025-09-20 13:23:49', 'Razorpay', NULL, 'pay_RJs4GSmbNbbHtj', NULL, 'S04-5', '2025-10-21 12:58:38', NULL),
(14, 'S04525', '192210687', 198.00, 0.00, 0.00, 'Rejected', '2025-09-20 13:25:24', 'Razorpay', NULL, 'pay_RJs62PBsLCxu9e', NULL, 'S04-6', '2025-10-21 12:58:38', NULL),
(15, 'S04525', '192210687', 89.00, 0.00, 0.00, 'Delivered', '2025-09-20 13:26:04', 'Razorpay', NULL, 'pay_RJs6irgHQmvXai', NULL, 'S04-7', '2025-10-21 12:58:38', NULL),
(16, 'S04525', '192210687', 218.00, 0.00, 0.00, 'Rejected', '2025-09-21 05:00:58', 'Razorpay', NULL, 'pay_RK82Eisb5CxieC', NULL, 'S04-8', '2025-10-21 12:58:38', NULL),
(17, 'S04525', '192210687', 306.00, 0.00, 0.00, 'Pending', '2025-09-21 05:08:33', 'Razorpay', NULL, 'pay_RK8AGBd5HejSfv', '11:00 AM', 'S04-9', '2025-10-21 12:58:38', NULL),
(18, 'S04525', '192210687', 138.00, 0.00, 0.00, 'Pending', '2025-09-21 05:10:12', 'Razorpay', NULL, 'pay_RK8C31okuYPSQI', '11:00 AM', 'S04-10', '2025-10-21 12:58:38', NULL),
(19, 'S04525', '192210687', 158.00, 0.00, 0.00, 'Pending', '2025-09-21 05:18:08', 'Razorpay', NULL, 'pay_RK8KNxWbnU2Qxw', '11:00 AM', 'S04-11', '2025-10-21 12:58:38', NULL),
(20, 'S04525', '192210687', 307.00, 287.00, 20.00, 'Pending', '2025-09-21 06:28:17', 'Razorpay', NULL, 'pay_RK9WSmX5JHhW7f', '12:30 PM', 'S04-12', '2025-10-21 12:58:38', NULL),
(21, 'S04525', '192210687', 218.00, 198.00, 20.00, 'Pending', '2025-09-21 06:33:08', 'Razorpay', NULL, 'pay_RK9bUfaFGlQbsg', '12:30 PM', 'S04-13', '2025-10-21 12:58:38', NULL),
(22, 'S04525', '192210687', 375.00, 325.00, 50.00, 'Pending', '2025-09-21 08:46:24', 'Razorpay', 'Pre-parcel', 'pay_RKBsLpe5qnDAU7', '2:25 PM', 'S04-14', '2025-10-21 12:58:38', NULL),
(23, 'S04525', '192210687', 414.00, 374.00, 40.00, 'Pending', '2025-09-21 09:26:11', 'Razorpay', 'Pre-parcel', 'pay_RKCYOq5Yj76EhA', '3:20 PM', 'S04-15', '2025-10-21 12:58:38', NULL),
(24, 'S04525', '192210687', 485.00, 465.00, 20.00, 'Rejected', '2025-09-22 17:58:30', 'Razorpay', NULL, 'pay_RKjokLyvp4GLB5', '11:52 PM', 'S04-16', '2025-10-21 12:58:38', NULL),
(25, 'S04525', '192210687', 227.00, 207.00, 20.00, 'Delivered', '2025-09-22 18:09:15', 'Razorpay', NULL, 'pay_RKk03rrlNL5l74', '11:50 PM', 'S04-17', '2025-10-21 12:58:38', NULL),
(26, 'S04525', '192210687', 227.00, 207.00, 20.00, 'Pending', '2025-09-22 18:13:57', 'Razorpay', NULL, 'pay_RKk50OPw1oNiDV', '12:00 AM', 'S04-18', '2025-10-21 12:58:38', NULL),
(27, 'S04525', '192210687', 227.00, 207.00, 20.00, 'Pending', '2025-09-23 05:35:52', 'Razorpay', NULL, 'pay_RKvhIVXdWXRzr8', '11:19 PM', 'S04-19', '2025-10-21 12:58:38', NULL),
(28, 'S04525', '192210687', 326.00, 296.00, 30.00, 'Delivered', '2025-09-28 05:31:38', 'Razorpay', NULL, 'pay_RMuIWmVDqJNMhI', '11:20 AM', 'S04-20', '2025-10-21 12:58:38', NULL),
(29, 'S04525', '192210687', 316.00, 286.00, 30.00, 'Pending', '2025-09-28 05:46:13', 'Razorpay', NULL, 'pay_RMuXwcCPuIw0OH', '11:35 AM', 'S04-21', '2025-10-21 12:58:38', NULL),
(30, 'S04525', '192210687', 356.00, 326.00, 30.00, 'Rejected', '2025-09-28 06:01:17', 'Razorpay', NULL, 'pay_RMunqFuZDGP3IW', '11:50 AM', 'S04-22', '2025-10-21 12:58:38', NULL),
(31, 'S04525', '192210687', 464.00, 424.00, 40.00, 'Delivered', '2025-09-28 08:04:02', 'Razorpay', NULL, 'pay_RMwtRugQX0JG8Q', '1:56 PM', 'S04-23', '2025-10-21 12:58:38', NULL),
(32, 'S04525', '192210687', 336.00, 316.00, 20.00, 'Delivered', '2025-09-29 14:05:20', 'Razorpay', NULL, 'pay_RNRaHXPw9wvrIt', '7:50 PM', 'S04-24', '2025-10-21 12:58:38', NULL),
(33, 'S04525', '192210687', 375.00, 345.00, 30.00, 'Rejected', '2025-09-29 14:07:06', 'Razorpay', NULL, 'pay_RNRcAhu62iijmH', NULL, 'S04-25', '2025-10-21 12:58:38', NULL),
(34, 'S04525', '192210687', 336.00, 306.00, 30.00, 'Delivered', '2025-09-29 14:08:20', 'Razorpay', NULL, 'pay_RNRdScE187TR0D', '7:55 PM', 'S04-26', '2025-10-21 12:58:38', NULL),
(35, 'S04525', '192210687', 79.00, 69.00, 10.00, 'Pending', '2025-09-29 18:02:00', 'Razorpay', NULL, 'pay_RNVcIeySazkPUC', NULL, 'S04-27', '2025-10-21 12:58:38', NULL),
(36, 'S04525', '192210687', 237.00, 217.00, 20.00, 'Pending', '2025-09-30 08:14:47', 'Razorpay', NULL, 'pay_RNk98LWq8hMsq7', '2:00 PM', 'S04-28', '2025-10-21 12:58:38', NULL),
(37, 'S81208', '192210687', 167.00, 147.00, 20.00, 'Pending', '2025-09-30 08:16:35', 'Razorpay', NULL, 'pay_RNkB1C1Q7yfFiz', '2:00 PM', 'S81-3', '2025-10-21 12:58:38', NULL),
(38, 'S94231', '192210687', 348.00, 308.00, 40.00, 'Pending', '2025-09-30 08:17:33', 'Razorpay', NULL, 'pay_RNkC3N0Vl1vrPE', '2:05 PM', 'S94-5', '2025-10-21 12:58:38', NULL),
(39, 'S44791', '192210687', 444.00, 414.00, 30.00, 'Pending', '2025-09-30 08:21:01', 'Razorpay', NULL, 'pay_RNkFfj7pfo3Znd', '2:15 PM', 'S44-3', '2025-10-21 12:58:38', NULL),
(40, 'S04525', '192210687', 237.00, 217.00, 20.00, 'Delivered', '2025-10-01 03:35:53', 'Razorpay', NULL, 'pay_RO3vcf39gNuATf', '9:20 AM', 'S04-29', '2025-10-21 12:58:38', NULL),
(42, 'S81208', '192221064', 373.00, 323.00, 50.00, 'Delivered', '2025-10-01 03:41:22', 'Razorpay', NULL, 'pay_RO41PsrTq7iiVT', '9:20 AM', 'S81-4', '2025-10-21 12:58:38', NULL),
(44, 'S44791', '192221064', 286.00, 266.00, 20.00, 'Rejected', '2025-10-01 03:44:18', 'Razorpay', NULL, 'pay_RO44WbspqdVNwf', NULL, 'S44-5', '2025-10-21 12:58:38', NULL),
(46, 'S94231', '192210688', 279.00, 249.00, 30.00, 'Rejected', '2025-10-01 03:56:19', 'Razorpay', NULL, 'pay_RO4HCpIVsks3pF', '9:40 AM', 'S94-7', '2025-10-21 12:58:38', NULL),
(47, 'S81208', '192210688', 167.00, 147.00, 20.00, 'Delivered', '2025-10-01 03:59:05', 'Razorpay', NULL, 'pay_RO4K97uKmU0Aqn', '9:40 AM', 'S81-5', '2025-10-21 12:58:38', NULL),
(48, 'S94231', '192210688', 140.00, 120.00, 20.00, 'Delivered', '2025-10-01 04:10:03', 'Razorpay', NULL, 'pay_RO4UwCBVhD39Ga', '9:49 AM', 'S94-8', '2025-10-21 12:58:38', NULL),
(49, 'S04525', '192210687', 79.00, 69.00, 10.00, 'Pending', '2025-10-02 08:24:05', 'Wallet', 'Dine-in', 'wallet_txn_68de36a531de9', NULL, 'S04-30', '2025-10-21 12:58:38', NULL),
(50, 'S04525', '192210687', 99.00, 89.00, 10.00, 'Pending', '2025-10-02 08:33:56', 'Wallet', 'Dine-in', 'wallet_txn_68de38f42d954', NULL, 'S04-31', '2025-10-21 12:58:38', NULL),
(51, 'S81208', '192210687', 49.00, 39.00, 10.00, 'Pending', '2025-10-02 08:39:06', 'Wallet', 'Dine-in', 'wallet_txn_68de3a2a2c4bc', '2:35 PM', 'S81-6', '2025-10-21 12:58:38', NULL),
(52, 'S04525', '192210687', 99.00, 89.00, 10.00, 'Pending', '2025-10-02 09:28:25', 'Wallet', 'Dine-in', 'wallet_txn_68de45b9a858d', NULL, 'S04-32', '2025-10-21 12:58:38', NULL),
(62, 'S44791', '192210687', 110.00, 100.00, 10.00, 'Pending', '2025-10-02 09:50:22', 'Wallet', 'Dine-in', 'wallet_txn_68de4ade187c1', NULL, 'S44-6', '2025-10-21 12:58:38', NULL),
(63, 'S81208', '192210687', 59.00, 49.00, 10.00, 'Pending', '2025-10-02 09:52:39', 'Razorpay', 'Dine-in', 'pay_ROYsjM2hifWxPO', '3:35 PM', 'S81-7', '2025-10-21 12:58:38', NULL),
(64, 'S04525', '192210687', 168.00, 158.00, 10.00, 'Pending', '2025-10-02 09:56:26', 'Wallet', 'Dine-in', 'wallet_txn_68de4c4a2f132', NULL, 'S04-33', '2025-10-21 12:58:38', NULL),
(65, 'S04525', '192210687', 79.00, 69.00, 10.00, 'Pending', '2025-10-02 14:04:13', 'Razorpay', 'Dine-in', 'pay_ROdAS5XYA8G8uB', '7:50 PM', 'S04-34', '2025-10-21 12:58:38', NULL),
(66, 'S04525', '192210687', 79.00, 69.00, 10.00, 'Pending', '2025-10-02 14:18:54', 'Razorpay', NULL, 'pay_ROdQ0siBllKmOu', '8:52 PM', 'S04-35', '2025-10-21 12:58:38', NULL),
(67, 'S04525', '192210687', 79.00, 69.00, 10.00, 'Pending', '2025-10-02 14:25:12', 'Razorpay', NULL, 'pay_ROdWdTkj4it831', NULL, 'S04-36', '2025-10-21 12:58:38', NULL),
(73, 'S94231', '192210687', 189.00, 169.00, 20.00, 'Pending', '2025-10-02 14:42:57', 'Razorpay', 'Dine-in', 'pay_ROdpPIjVCFJBhd', '8:20 PM', 'S94-9', '2025-10-21 12:58:38', NULL),
(74, 'S04525', '192210687', 197.00, 177.00, 20.00, 'Pending', '2025-10-02 15:05:29', 'Razorpay', 'Dine-in', 'pay_ROeDCh8M2SHBVZ', '8:59 PM', 'S04-37', '2025-10-21 12:58:38', NULL),
(75, 'S44791', '192210687', 269.00, 249.00, 20.00, 'Pending', '2025-10-02 15:18:14', 'Razorpay', 'Dine-in', 'pay_ROeQdWzQHFKRdW', '9:00 PM', 'S44-7', '2025-10-21 12:58:38', NULL),
(76, 'S94231', '192210687', 317.00, 297.00, 20.00, 'Pending', '2025-10-02 15:38:28', 'Razorpay', 'Dine-in', 'pay_ROelu1Nv77QsYs', '9:20 PM', 'S94-10', '2025-10-21 12:58:38', NULL),
(77, 'S94231', '192210687', 140.00, 120.00, 20.00, 'Pending', '2025-10-02 15:52:16', 'Razorpay', 'Dine-in', 'pay_ROf0bfR4sR0pOy', '9:35 PM', 'S94-11', '2025-10-21 12:58:38', NULL),
(78, 'S44791', '192210687', 110.00, 100.00, 10.00, 'Pending', '2025-10-02 16:10:13', 'Razorpay', 'Parcel', 'pay_ROfJZ6pdkoGKfT', NULL, 'S44-8', '2025-10-21 12:58:38', NULL),
(79, 'S44791', '192210687', 286.00, 266.00, 20.00, 'Pending', '2025-10-02 16:11:18', 'Razorpay', 'Pre-parcel', 'pay_ROfKipTrFfHILg', '9:59 PM', 'S44-9', '2025-10-21 12:58:38', NULL),
(80, 'S04525', '192210687', 237.00, 217.00, 20.00, 'Pending', '2025-10-20 08:29:39', 'Wallet', 'Pre-parcel', 'wallet_txn_68f5f2f3825a9', '2:15 PM', 'S04-38', '2025-10-21 12:58:38', NULL),
(81, 'S04525', '192210687', 287.00, 267.00, 20.00, 'Delivered', '2025-10-21 10:15:34', 'Wallet', 'Pre-parcel', 'wallet_txn_68f75d46c6c19', '4:00 PM', 'S04-39', '2025-10-21 12:58:38', NULL),
(82, 'S04525', '192210687', 237.00, 217.00, 20.00, 'Delivered', '2025-10-21 14:00:35', 'Razorpay', 'Dine-in', 'pay_RW9Fv5H5LBv9Zz', '7:50 PM', 'S04-40', '2025-10-21 14:04:41', NULL),
(83, 'S04525', '192210687', 247.00, 227.00, 20.00, 'Delivered', '2025-10-21 14:06:59', 'Razorpay', 'Dine-in', 'pay_RW9MfoJx2LXbXD', '7:54 PM', 'S04-41', '2025-10-21 16:33:55', NULL),
(84, 'S04525', '192210687', 287.00, 267.00, 20.00, 'Rejected', '2025-10-21 15:56:06', 'Razorpay', 'Dine-in', 'pay_RWBDvyrXh97ZYI', '9:47 PM', 'S04-42', '2025-10-21 15:58:52', '2025-10-21 21:28:52'),
(85, 'S04525', '192210687', 237.00, 217.00, 20.00, 'Rejected', '2025-10-21 16:17:27', 'Wallet', 'Dine-in', 'wallet_txn_68f7b217b1b8a', '9:56 PM', 'S04-43', '2025-10-21 16:33:22', '2025-10-21 22:03:22'),
(86, 'S04525', '192210687', 227.00, 207.00, 20.00, 'Delivered', '2025-10-21 16:31:57', 'Wallet', 'Pre-parcel', 'wallet_txn_68f7b57da1cbc', '10:10 PM', 'S04-44', '2025-10-21 16:33:14', NULL),
(87, 'S04525', '192210687', 197.00, 177.00, 20.00, 'Rejected', '2025-10-21 17:15:18', 'Wallet', 'Pre-parcel', 'wallet_txn_68f7bfa669d46', '10:54 PM', 'S04-45', '2025-10-21 17:16:22', '2025-10-21 22:46:22'),
(88, 'S04525', '192210687', 237.00, 217.00, 20.00, 'Delivered', '2025-10-24 09:18:32', 'Razorpay', 'Pre-parcel', 'pay_RXG3KOSMvOxetl', '3:06 PM', 'S04-46', '2025-10-24 09:19:27', NULL),
(89, 'S04525', '192210687', 69.00, 69.00, 0.00, 'Pending', '2025-10-24 09:23:59', 'Wallet', 'Dine-in', 'wallet_txn_68fb45afd409f', NULL, 'S04-47', '2025-10-24 09:23:59', NULL),
(90, 'S04525', '192210687', 237.00, 217.00, 20.00, 'Rejected', '2025-10-24 10:00:23', 'Razorpay', 'Pre-parcel', 'pay_RXGlYhu82saDoY', '3:45 PM', 'S04-48', '2025-10-24 10:10:38', '2025-10-24 15:40:38'),
(91, 'S04525', '192210687', 247.00, 227.00, 20.00, 'Delivered', '2025-10-24 10:04:09', 'Razorpay', 'Pre-parcel', 'pay_RXGpWAg0vWWr8K', '3:51 PM', 'S04-49', '2025-10-24 10:09:26', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `order_items`
--

CREATE TABLE `order_items` (
  `item_id` int NOT NULL,
  `order_id` int NOT NULL,
  `item_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `parcel_status` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Dine-in'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_items`
--

INSERT INTO `order_items` (`item_id`, `order_id`, `item_name`, `quantity`, `price`, `parcel_status`) VALUES
(1, 2, 'Idly', 1, 38.00, 'Dine-in'),
(2, 2, 'Dosa', 1, 20.00, 'Dine-in'),
(3, 3, 'Poori', 1, 40.00, 'Dine-in'),
(4, 4, 'Idly', 1, 38.00, 'Dine-in'),
(5, 4, 'Dosa', 1, 20.00, 'Dine-in'),
(6, 5, 'Idly', 1, 38.00, 'Dine-in'),
(7, 5, 'Cobri', 1, 60.00, 'Dine-in'),
(8, 6, 'Masala dosa', 2, 50.00, 'Dine-in'),
(9, 7, 'Masala dosa', 1, 50.00, 'Dine-in'),
(10, 8, 'Amritsari Kulcha', 2, 99.00, 'Dine-in'),
(11, 8, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(12, 8, 'Chole Bhature', 1, 59.00, 'Dine-in'),
(13, 9, 'Poori', 3, 50.00, 'Dine-in'),
(14, 10, 'Idiyapam', 3, 49.00, 'Dine-in'),
(15, 11, 'Burger+Coc+ Fries', 4, 99.00, 'Dine-in'),
(16, 12, 'Burger+Coc+ Fries', 1, 99.00, 'Dine-in'),
(17, 12, 'Variety Meals+Side dish', 1, 89.00, 'Dine-in'),
(18, 13, 'Idly', 2, 38.00, 'Dine-in'),
(19, 14, 'Amritsari Kulcha', 2, 99.00, 'Dine-in'),
(20, 15, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(21, 16, 'Amritsari Kulcha', 2, 99.00, 'Dine-in'),
(22, 17, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(23, 17, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(24, 17, 'Amritsari Fish', 2, 59.00, 'Dine-in'),
(25, 18, 'Chole Bhature', 2, 59.00, 'Dine-in'),
(26, 19, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(27, 19, 'Chole Bhature', 1, 59.00, 'Dine-in'),
(28, 20, 'Amritsari Kulcha', 2, 99.00, 'Dine-in'),
(29, 20, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(30, 21, 'Amritsari Kulcha', 2, 99.00, 'Dine-in'),
(31, 22, 'Amritsari Fish', 4, 59.00, 'Dine-in'),
(32, 22, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(33, 23, 'Makki Di Roti', 2, 69.00, 'Dine-in'),
(34, 23, 'Amritsari Fish', 3, 59.00, 'Dine-in'),
(35, 23, 'Chole Bhature', 1, 59.00, 'Dine-in'),
(36, 24, 'Amritsari Kulcha', 2, 99.00, 'Dine-in'),
(37, 24, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(38, 24, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(39, 24, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(40, 25, 'Makki Di Roti', 1, 69.00, 'Pre-parcel'),
(41, 25, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(42, 25, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(43, 26, 'Makki Di Roti', 1, 69.00, 'Pre-parcel'),
(44, 26, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(45, 26, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(46, 27, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(47, 27, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(48, 27, 'Makki Di Roti', 1, 69.00, 'Pre-parcel'),
(49, 28, 'Makki Di Roti', 2, 69.00, 'Parcel'),
(50, 28, 'Amritsari Fish', 1, 59.00, 'Pre-parcel'),
(51, 28, 'Amritsari Kulcha', 1, 99.00, 'Dine-in'),
(52, 29, 'Amritsari Kulcha', 1, 99.00, 'Dine-in'),
(53, 29, 'Amritsari Fish', 1, 59.00, 'Pre-parcel'),
(54, 29, 'Amritsari Fish', 1, 59.00, 'Parcel'),
(55, 29, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(56, 30, 'Makki Di Roti', 2, 69.00, 'Parcel'),
(57, 30, 'Amritsari Kulcha', 1, 99.00, 'Dine-in'),
(58, 30, 'Punjabi Thali', 1, 89.00, 'Pre-parcel'),
(59, 31, 'Makki Di Roti', 2, 69.00, 'Pre-parcel'),
(60, 31, 'Punjabi Thali', 1, 89.00, 'Parcel'),
(61, 31, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(62, 31, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(63, 31, 'Amritsari Fish', 1, 59.00, 'Dine-in'),
(64, 32, 'Amritsari Kulcha', 1, 99.00, 'Dine-in'),
(65, 32, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(66, 32, 'Punjabi Thali', 1, 89.00, 'Pre-parcel'),
(67, 32, 'Chole Bhature', 1, 59.00, 'Dine-in'),
(68, 33, 'Punjabi Thali', 1, 89.00, 'Pre-parcel'),
(69, 33, 'Amritsari Fish', 1, 59.00, 'Parcel'),
(70, 33, 'Chole Bhature', 1, 59.00, 'Parcel'),
(71, 33, 'Makki Di Roti', 2, 69.00, 'Dine-in'),
(72, 34, 'Makki Di Roti', 1, 69.00, 'Pre-parcel'),
(73, 34, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(74, 34, 'Chole Bhature', 1, 59.00, 'Pre-parcel'),
(75, 34, 'Punjabi Thali', 1, 89.00, 'Parcel'),
(76, 35, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(77, 36, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(78, 36, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(79, 36, 'Amritsari Fish', 1, 59.00, 'Pre-parcel'),
(80, 37, 'Puttu', 1, 59.00, 'Parcel'),
(81, 37, 'Idiyapam', 1, 49.00, 'Dine-in'),
(82, 37, 'Ada Dosa', 1, 39.00, 'Pre-parcel'),
(83, 38, 'Burger+Coc+ Fries', 1, 99.00, 'Parcel'),
(84, 38, 'Chapathi', 1, 40.00, 'Pre-parcel'),
(85, 38, 'Idly', 1, 30.00, 'Dine-in'),
(86, 38, 'Podi dosa', 1, 40.00, 'Parcel'),
(87, 38, 'Burger+Coc+ Fries', 1, 99.00, 'Pre-parcel'),
(88, 39, 'Briyani', 1, 100.00, 'Pre-parcel'),
(89, 39, 'idly + dosa', 1, 68.00, 'Dine-in'),
(90, 39, 'Poori', 1, 50.00, 'Pre-parcel'),
(91, 39, 'Fried rice', 1, 98.00, 'Parcel'),
(92, 39, 'Fried rice', 1, 98.00, 'Dine-in'),
(93, 40, 'Makki Di Roti', 1, 69.00, 'Pre-parcel'),
(94, 40, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(95, 40, 'Amritsari Fish', 1, 59.00, 'Parcel'),
(101, 42, 'Idiyapam', 1, 49.00, 'Parcel'),
(102, 42, 'Ada Dosa', 1, 39.00, 'Dine-in'),
(103, 42, 'Chinese Meals', 1, 69.00, 'Pre-parcel'),
(104, 42, 'Rava kichidi', 1, 39.00, 'Parcel'),
(105, 42, 'Idiyapam', 1, 49.00, 'Dine-in'),
(106, 42, 'Ada Dosa', 2, 39.00, 'Parcel'),
(111, 44, 'idly + dosa', 1, 68.00, 'Parcel'),
(112, 44, 'Fried rice', 1, 98.00, 'Dine-in'),
(113, 44, 'Briyani', 1, 100.00, 'Parcel'),
(119, 46, 'Idly', 1, 30.00, 'Pre-parcel'),
(120, 46, 'Podi dosa', 2, 40.00, 'Dine-in'),
(121, 46, 'Burger+Coc+ Fries', 1, 99.00, 'Parcel'),
(122, 46, 'Chapathi', 1, 40.00, 'Pre-parcel'),
(123, 47, 'Ada Dosa', 1, 39.00, 'Pre-parcel'),
(124, 47, 'Rava kichidi', 1, 39.00, 'Dine-in'),
(125, 47, 'Chinese Meals', 1, 69.00, 'Pre-parcel'),
(126, 48, 'Podi dosa', 1, 40.00, 'Parcel'),
(127, 48, 'Podi dosa', 1, 40.00, 'Pre-parcel'),
(128, 48, 'Podi dosa', 1, 40.00, 'Dine-in'),
(129, 49, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(130, 50, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(131, 51, 'Ada Dosa', 1, 39.00, 'Dine-in'),
(132, 52, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(133, 62, 'Briyani', 1, 100.00, 'Dine-in'),
(134, 63, 'Idiyapam', 1, 49.00, 'Dine-in'),
(135, 64, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(136, 64, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(137, 65, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(138, 66, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(139, 67, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(140, 73, 'Idly', 1, 30.00, 'Dine-in'),
(141, 73, 'Chapathi', 1, 40.00, 'Dine-in'),
(142, 73, 'Burger+Coc+ Fries', 1, 99.00, 'Dine-in'),
(143, 74, 'Chole Bhature', 1, 59.00, 'Dine-in'),
(144, 74, 'Chole Bhature', 1, 59.00, 'Dine-in'),
(145, 74, 'Chole Bhature', 1, 59.00, 'Dine-in'),
(146, 75, 'Poori', 1, 50.00, 'Dine-in'),
(147, 75, 'Panner butter Masala', 1, 99.00, 'Dine-in'),
(148, 75, 'Briyani', 1, 100.00, 'Dine-in'),
(149, 76, 'Burger+Coc+ Fries', 1, 99.00, 'Dine-in'),
(150, 76, 'Burger+Coc+ Fries', 1, 99.00, 'Dine-in'),
(151, 76, 'Burger+Coc+ Fries', 1, 99.00, 'Dine-in'),
(152, 77, 'Podi dosa', 1, 40.00, 'Dine-in'),
(153, 77, 'Podi dosa', 1, 40.00, 'Dine-in'),
(154, 77, 'Podi dosa', 1, 40.00, 'Dine-in'),
(155, 78, 'Briyani', 1, 100.00, 'Parcel'),
(156, 79, 'Briyani', 1, 100.00, 'Pre-parcel'),
(157, 79, 'Fried rice', 1, 98.00, 'Parcel'),
(158, 79, 'idly + dosa', 1, 68.00, 'Dine-in'),
(159, 80, 'Punjabi Thali', 1, 89.00, 'Pre-parcel'),
(160, 80, 'Chole Bhature', 1, 59.00, 'Parcel'),
(161, 80, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(162, 81, 'Amritsari Kulcha', 1, 99.00, 'Dine-in'),
(163, 81, 'Amritsari Kulcha', 1, 99.00, 'Parcel'),
(164, 81, 'Makki Di Roti', 1, 69.00, 'Pre-parcel'),
(165, 82, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(166, 82, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(167, 82, 'Amritsari Fish', 1, 59.00, 'Dine-in'),
(168, 83, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(169, 83, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(170, 83, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(171, 84, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(172, 84, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(173, 84, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(174, 85, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(175, 85, 'Punjabi Thali', 1, 89.00, 'Dine-in'),
(176, 85, 'Amritsari Fish', 1, 59.00, 'Dine-in'),
(177, 86, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(178, 86, 'Makki Di Roti', 1, 69.00, 'Pre-parcel'),
(179, 86, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(180, 87, 'Chole Bhature', 1, 59.00, 'Dine-in'),
(181, 87, 'Chole Bhature', 1, 59.00, 'Parcel'),
(182, 87, 'Chole Bhature', 1, 59.00, 'Pre-parcel'),
(183, 88, 'Punjabi Thali', 1, 89.00, 'Parcel'),
(184, 88, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(185, 88, 'Amritsari Fish', 1, 59.00, 'Pre-parcel'),
(186, 89, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(187, 90, 'Makki Di Roti', 1, 69.00, 'Dine-in'),
(188, 90, 'Punjabi Thali', 1, 89.00, 'Parcel'),
(189, 90, 'Chole Bhature', 1, 59.00, 'Pre-parcel'),
(190, 91, 'Amritsari Kulcha', 1, 99.00, 'Dine-in'),
(191, 91, 'Makki Di Roti', 1, 69.00, 'Parcel'),
(192, 91, 'Chole Bhature', 1, 59.00, 'Pre-parcel');

-- --------------------------------------------------------

--
-- Table structure for table `osignup`
--

CREATE TABLE `osignup` (
  `id` int NOT NULL,
  `fullname` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phonenumber` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `osignup`
--

INSERT INTO `osignup` (`id`, `fullname`, `email`, `phonenumber`, `password`) VALUES
(1, 'Sri', NULL, '9638527410', '$2y$10$ApUIXMkXU3EMoSqAMQ9VFeLubt0NiEP5kSrbFRFBtFyBJlIqmOsPu'),
(2, 'Nithish', NULL, '9638527411', '$2y$10$/o2nWBbG/lcxLZR/iDRl0.iy9GepTwvIhsoaGn7Js73Gg038w.gMu'),
(3, 'Suriya', NULL, '9638527412', '$2y$10$IV.eqG5SRlhFzjRzsuELYe1aLlXldXAJ5OvOKZjE1rt/CTZHRspdy'),
(4, 'Suriya', NULL, '9638527419', '$2y$10$UpoLAHszZo0vSe1VubGvXe6pObeTFW9mur0OfIZebsqyiZblbb1bq'),
(6, 'Chella', NULL, '9638527414', '$2y$10$bShUZttN3fU2qeBIYdnQte1CXf4hofha8h6PiSupBlRpjMzKtLj6q'),
(7, 'Sabari', NULL, '9638527416', '$2y$10$DeUsF/m1sVohmNaNV0T1heW26rHR4gL8bw/v.2LpIaRweDqeV3Fzi'),
(8, 'Nithi', NULL, '9638527418', '$2y$10$64GxDUuw1G1CbDEbNJb4ROF5Xgxhdry.oXoWdCK.Uaq/wiTyqZes2'),
(12, 'Rajasekar', NULL, '9940809241', '$2y$10$O4buZA3zrb4mntFdte5PzO6Wa1q31kPe4dWWFFFgP2aDpPPlxuswq'),
(15, 'Lala', NULL, '9638527413', '$2y$10$JFhai3yqzFljWblOzeTffuGP2Bgd4hvPKTjrzgqy6Eu.6PaVmwsNu'),
(17, 'Kaviya', NULL, '6383979999', '$2y$10$Am/E3uRNnZs9VSjKhRlIn.o0RwLP4tBgjzry7LX5GvlKEH0QB8XH6'),
(25, 'Maha', NULL, '9764318520', '$2y$10$GpgBdpaVTt67tPtzQb8az.uPd/dLkIwXwawgNFWpcui70Iy7UMt5u'),
(35, 'Ranjith Kumar', 'mrbadlydevilrk@gmail.com', '9129120852', '$2y$10$kSAgvKLxyPT9EuZb5X7C0eOxIFK.eX7t4cKCVc7.RuT3Ha.XODm5m'),
(41, 'Srimathi', 'srimathisivan09@gmail.com', '9879876540', '$2y$10$Luxsxit00gHoP.7.e39Z1OC1ix8s1h8Pon1C5THbLNpUT9BKX2L0C'),
(42, 'Ranjith Kumar', 'rkrajasekar005@gmail.com', '', '$2y$10$Q5e1f8/.tjveHOY4k39dAOemk6vk.1wgJLPDedx5MRMaNpS5PBCm6');

-- --------------------------------------------------------

--
-- Table structure for table `owner_reviews`
--

CREATE TABLE `owner_reviews` (
  `review_id` int NOT NULL,
  `stall_id` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `review_type` enum('environment','location','student') COLLATE utf8mb4_general_ci NOT NULL,
  `review_text` text COLLATE utf8mb4_general_ci,
  `review_date` date NOT NULL DEFAULT (curdate())
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `owner_reviews`
--

INSERT INTO `owner_reviews` (`review_id`, `stall_id`, `review_type`, `review_text`, `review_date`) VALUES
(1, 'S07108', 'environment', 'cleaned thanks for your response', '2025-07-25'),
(2, 'S20842', 'location', 'i want to change the location ', '2025-07-24'),
(3, 'S07108', 'student', 'student behaviour is so rude', '2025-07-24'),
(4, 'S01724', 'location', 'nice location thanks for alloted location', '2025-07-24');

-- --------------------------------------------------------

--
-- Table structure for table `password_resets`
--

CREATE TABLE `password_resets` (
  `email` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `otp` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `expires_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `password_resets`
--

INSERT INTO `password_resets` (`email`, `otp`, `expires_at`) VALUES
('ranjithkumarsse@gmail.com', '955745', '2025-09-09 19:54:33'),
('srimathisivan09@gmail.com', '856266', '2025-10-22 20:05:40'),
('tsrimathi1609@gmail.com', '202647', '2025-09-29 08:41:07');

-- --------------------------------------------------------

--
-- Table structure for table `rent_invoices`
--

CREATE TABLE `rent_invoices` (
  `invoice_id` int NOT NULL,
  `stall_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `invoice_month` int NOT NULL COMMENT 'e.g., 1 for January, 2 for February',
  `invoice_year` int NOT NULL,
  `total_revenue` decimal(12,2) NOT NULL,
  `rent_amount` decimal(10,2) NOT NULL,
  `late_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` enum('unpaid','paid','overdue') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'unpaid',
  `generated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `paid_at` datetime DEFAULT NULL,
  `is_acknowledged` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0=Not Acknowledged by Admin, 1=Acknowledged'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `rent_invoices`
--

INSERT INTO `rent_invoices` (`invoice_id`, `stall_id`, `invoice_month`, `invoice_year`, `total_revenue`, `rent_amount`, `late_fee`, `status`, `generated_at`, `paid_at`, `is_acknowledged`) VALUES
(3, 'S04525', 10, 2025, 60000.00, 9000.00, 0.00, 'paid', '2025-10-21 17:13:24', '2025-10-21 22:52:18', 1),
(4, 'S04525', 10, 2025, 60000.00, 9000.00, 0.00, 'paid', '2025-10-22 06:31:40', '2025-10-22 12:02:30', 0),
(6, 'S04525', 10, 2025, 55000.00, 8250.00, 0.00, 'paid', '2025-10-22 09:56:02', '2025-10-22 18:41:47', 0),
(7, 'S04525', 10, 2025, 5000.00, 500.00, 0.00, 'paid', '2025-10-24 08:38:31', '2025-10-24 15:46:04', 0);

-- --------------------------------------------------------

--
-- Table structure for table `stalldetails`
--

CREATE TABLE `stalldetails` (
  `id` int NOT NULL,
  `stall_id` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `stallname` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `ownername` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `phonenumber` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `fulladdress` text COLLATE utf8mb4_general_ci NOT NULL,
  `fssainumber` varchar(14) COLLATE utf8mb4_general_ci NOT NULL,
  `latitude` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `longitude` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Rating` decimal(3,2) NOT NULL DEFAULT '0.00',
  `approval` tinyint(1) NOT NULL DEFAULT '0',
  `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `rejection_reason` text COLLATE utf8mb4_general_ci,
  `request_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_open_today` tinyint(1) DEFAULT '0',
  `opening_hours` time DEFAULT NULL,
  `closing_hours` time DEFAULT NULL,
  `profile_photo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `agreement_accepted` datetime DEFAULT NULL COMMENT 'Timestamp when the owner accepted the agreement'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stalldetails`
--

INSERT INTO `stalldetails` (`id`, `stall_id`, `stallname`, `ownername`, `phonenumber`, `email`, `fulladdress`, `fssainumber`, `latitude`, `longitude`, `Rating`, `approval`, `password`, `rejection_reason`, `request_date`, `is_open_today`, `opening_hours`, `closing_hours`, `profile_photo`, `agreement_accepted`) VALUES
(1, 'S04525', 'Sivan Unavagam', 'Sri', '9638527410', 'sri@gmail.com', 'tirupathur', '96385274101234', '12.831048', '79.709312', 0.00, 1, NULL, NULL, '2025-09-09 13:29:24', 1, '08:00:00', '21:00:00', 'owner_S04525_68f9f1ac0ec765.38850582.jpg', '2025-10-21 19:25:09'),
(2, 'S81208', 'Ulthapam Ganesh', 'Nithish', '9638527411', 'nithish@gmail.com', 'Goa', '96385274109874', '13.028246', '80.015546', 0.00, 1, NULL, NULL, '2025-09-09 13:32:25', 1, '08:00:00', '19:00:00', NULL, '2025-10-23 12:57:19'),
(3, 'S94231', 'Aliyas', 'Suriya', '9638527419', 'suriya@gmail.com', 'Light house', '96385274100852', '13.132336', '79.906151', 0.00, 1, NULL, NULL, '2025-09-09 13:35:40', 0, '07:00:00', '18:00:00', NULL, '2025-10-23 12:10:54'),
(5, 'S44791', 'Panda', 'Chella', '9638527414', 'chella@gmail.com', 'Velankani', '96385274112346', '13.1323362', '79.9061514', 0.00, 1, NULL, NULL, '2025-09-09 13:39:27', 1, '07:00:00', '18:00:00', NULL, NULL),
(6, NULL, 'Ranga Meals', 'Sabari', '9638527416', 'sabari@gmail.com', 'Tanjavur', '96385274100741', NULL, NULL, 0.00, -1, NULL, 'Invalid details', '2025-09-09 13:49:20', 0, NULL, NULL, NULL, NULL),
(7, NULL, 'Ramesh Sooru', 'Nithi', '9638527418', 'nithi@gmail.com', 'Kanchipuram', '96385274101470', NULL, NULL, 0.00, -1, NULL, 'inappropriate stall name', '2025-09-09 14:16:33', 0, NULL, NULL, NULL, NULL),
(11, 'S39041', 'Kalyani Unavagam', 'Rajasekar', '9940809241', 'rajasekarsuriya08@gmail.com', '209/1 rajarajan street\nmin nagar\nkanchipuram', '74185296301234', '13.132336', '79.906151', 0.00, 1, NULL, NULL, '2025-09-20 07:46:48', 0, NULL, NULL, NULL, NULL),
(12, NULL, 'Shortie', 'Lala', '9638527413', 'lala@gmail.com', 'velankani', '78945612301234', NULL, NULL, 0.00, 0, NULL, NULL, '2025-09-20 07:49:16', 0, NULL, NULL, NULL, NULL),
(13, 'S15497', 'Paalraj', 'Maha', '9764318520', 'maha@gmail.com', 'thiruvallur', '12345678996300', NULL, NULL, 0.00, 1, NULL, NULL, '2025-10-22 14:53:39', 0, NULL, NULL, NULL, '2025-10-22 23:35:45'),
(14, NULL, 'Bhh', 'Ranjith Kumar', '9129120852', 'mrbadlydevilrk@gmail.com', 'gvv', '12345678902580', NULL, NULL, 0.00, -1, NULL, 'Change the stall name', '2025-10-22 16:24:53', 0, NULL, NULL, NULL, NULL),
(15, NULL, 'Sri', 'Srimathi', '9879876540', 'srimathisivan09@gmail.com', 'kanchipuram', '98798745645620', NULL, NULL, 0.00, -1, NULL, 'rejected', '2025-10-22 17:43:40', 0, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `student_reviews`
--

CREATE TABLE `student_reviews` (
  `review_id` int NOT NULL,
  `stall_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `student_id` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `rating` decimal(2,1) NOT NULL,
  `review_text` text COLLATE utf8mb4_general_ci,
  `review_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student_reviews`
--

INSERT INTO `student_reviews` (`review_id`, `stall_id`, `student_id`, `rating`, `review_text`, `review_date`) VALUES
(1, 'S04525', '192210687', 2.5, 'nice stall', '2025-10-20 08:26:05'),
(4, 'S94231', '192210687', 4.0, 'nice stall', '2025-09-18 14:26:38'),
(5, 'S81208', '192210687', 1.0, 'okie', '2025-09-12 17:17:18'),
(11, 'S44791', '192210687', 2.5, 'nice stall, good food quality', '2025-09-18 14:57:58');

-- --------------------------------------------------------

--
-- Table structure for table `usignup`
--

CREATE TABLE `usignup` (
  `id` int NOT NULL,
  `student_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fullname` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `is_admin` tinyint(1) DEFAULT '0',
  `phonenumber` varchar(10) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `profile_photo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `wallet_balance` decimal(10,2) NOT NULL DEFAULT '0.00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `usignup`
--

INSERT INTO `usignup` (`id`, `student_id`, `fullname`, `email`, `password`, `is_admin`, `phonenumber`, `profile_photo`, `wallet_balance`) VALUES
(35, '192210687', 'Ranjith Kumar', 'ranjithkumarsse@gmail.com', '$2y$10$oPqFtFI3PhVSxvA8FbdJQOUh55cX4SNbEogqQiZV8i9Obm3tdK3FG', 0, '6383979998', 'user_192210687_68fb3ce6965109.31576871.jpg', 410.00),
(36, 'admin', 'Ganesh', 'admin123@gmail.com', '$2y$10$K5uVbiXxIJMIA0TkmwGB5u99QjVYf5997ltcLSRvD8kp.qecnksP2', 1, '9638527410', 'admin_admin_68fb537cc0ea74.31937008.jpeg', 0.00),
(37, '192210590', 'Sai', 'sai@gmail.com', '$2y$10$gVOsV.cWUcS6mgihDJBSpegWYXIHDJVu8yCrH/L.zkvLGI6yoUuCW', 0, '', NULL, 0.00),
(39, '192210688', 'Sidd', 'sidd@gmail.com', '$2y$10$PuSlZX974Y7qZqrVX7/InehODLiPZz5niRa/QF2V1vTFFpfDQL9JC', 0, '', NULL, 0.00),
(41, '192210580', 'Madhavan Samanydeaswaran', 'maddy13904@gmail.com', '$2y$10$eeRZNCFy8eu7XRDlWUSiQuRZKcTo4zhThSIkmaUyY4CQ0q.5wZFHG', 0, '', NULL, 0.00),
(42, '192221138', 'Nithish', 'nithishganesh1710@gmail.com', '$2y$10$G3b6SAO3tlR.h7GJ.nH7VeLMuMnEzYXzTQ3lq8mK5/GrpOJVDFIO.', 0, '', NULL, 0.00),
(43, '192210692', 'Nithyanandhan', 'nithyanandhan205@gmail.com', '$2y$10$6ZvDHZR7SpG6MytH7Ka/Eux4Ejmt6iI89OPeiJ75K8N9IrX4XKVD2', 0, '', NULL, 0.00),
(44, '192221064', 'Srimathi', 'tsrimathi1609@gmail.com', '$2y$10$AnO8rV4wUAYPC7duuZeaKuVONK2hdYnXd/OCBp7fkVWRCAGeMiNQa', 0, '', NULL, 0.00),
(47, '192210587', 'Kaviya S', 'kaviya@gmail.com', '$2y$10$ICGYl3MeMJE4cCjED0NjqeKFQa06MCKci5t06Ic1m/Iv3txSzjQtS', 0, '', NULL, 0.00),
(51, 'G1761040626', 'RANJITH KUMAR R', 'ranjithkumarr0687.sse@saveetha.com', '$2y$10$vpaWawFOmSfp5mIiwxTsA.gFrfOFh6jkUMYUkR4o2sENzc.pwD2KW', 0, '', NULL, 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `wallets`
--

CREATE TABLE `wallets` (
  `wallet_id` int NOT NULL,
  `student_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00',
  `last_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `wallets`
--

INSERT INTO `wallets` (`wallet_id`, `student_id`, `balance`, `last_updated`) VALUES
(1, '192210687', 401.00, '2025-10-02 08:12:53');

-- --------------------------------------------------------

--
-- Table structure for table `wallet_transactions`
--

CREATE TABLE `wallet_transactions` (
  `transaction_id` int NOT NULL,
  `student_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `transaction_type` enum('Added Money','Order Payment','Refund') COLLATE utf8mb4_general_ci NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `transaction_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `wallet_transactions`
--

INSERT INTO `wallet_transactions` (`transaction_id`, `student_id`, `transaction_type`, `amount`, `description`, `transaction_date`) VALUES
(2, '192210687', 'Added Money', 100.00, 'Via Razorpay: pay_ROHSTWTBGN9b1f', '2025-10-01 16:50:00'),
(3, '192210687', 'Added Money', 1.00, 'Via Razorpay: pay_ROHUAXAslOSo5C', '2025-10-01 16:51:36'),
(4, '192210687', 'Added Money', 100.00, 'Via Razorpay: pay_ROI7NEj87GGB26', '2025-10-01 17:28:49'),
(5, '192210687', 'Added Money', 100.00, 'Via Razorpay: pay_ROX9O6HlFwyDJZ', '2025-10-02 08:11:04'),
(6, '192210687', 'Added Money', 100.00, 'Via Razorpay: pay_ROXBMbbL0OLBJk', '2025-10-02 08:12:53'),
(7, '192210687', 'Added Money', 100.00, 'Via Razorpay: pay_ROXN0TPjsAwSXu', '2025-10-02 08:23:59'),
(8, '192210687', 'Order Payment', -79.00, 'Paid for order S04-30', '2025-10-02 08:24:05'),
(9, '192210687', 'Added Money', 100.00, 'Via Razorpay: pay_ROXPCKrbZUlBg9', '2025-10-02 08:25:59'),
(10, '192210687', 'Order Payment', -99.00, 'Paid for order S04-31', '2025-10-02 08:33:56'),
(11, '192210687', 'Added Money', 100.00, 'Via Razorpay: pay_ROXazH34oEkuQK', '2025-10-02 08:37:09'),
(14, '192210687', 'Order Payment', -99.00, 'Paid for order S04-32', '2025-10-02 09:28:25'),
(20, '192210687', 'Added Money', 300.00, 'Via Razorpay: pay_RVf3ipeKHppEVv', '2025-10-20 08:28:14'),
(21, '192210687', 'Added Money', 300.00, 'Via Razorpay: pay_RVf4XJ0OUOwiOC', '2025-10-20 08:29:00'),
(22, '192210687', 'Order Payment', -237.00, 'Paid for order S04-38', '2025-10-20 08:29:39'),
(23, '192210687', 'Added Money', 100.00, 'Via Razorpay: pay_RVf7BfASjL5bz1', '2025-10-20 08:31:32'),
(24, '192210687', 'Order Payment', -287.00, 'Paid for order S04-39', '2025-10-21 10:15:34'),
(25, '192210687', 'Refund', 287.00, 'Refunded from S04-42', '2025-10-21 15:58:52'),
(26, '192210687', 'Order Payment', -237.00, 'Paid for order S04-43', '2025-10-21 16:17:27'),
(27, '192210687', 'Order Payment', -227.00, 'Paid for order S04-44', '2025-10-21 16:31:57'),
(28, '192210687', 'Refund', 237.00, 'Refunded from S04-43', '2025-10-21 16:33:22'),
(29, '192210687', 'Order Payment', -197.00, 'Paid for order S04-45', '2025-10-21 17:15:18'),
(30, '192210687', 'Refund', 197.00, 'Refunded from S04-45', '2025-10-21 17:16:22'),
(31, '192210687', 'Added Money', 5.00, 'Via Razorpay: pay_RXG7LsamHgtbLs', '2025-10-24 09:22:19'),
(32, '192210687', 'Order Payment', -69.00, 'Paid for order S04-47', '2025-10-24 09:23:59'),
(33, '192210687', 'Refund', 237.00, 'Refunded from S04-48', '2025-10-24 10:10:38');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `app_policies`
--
ALTER TABLE `app_policies`
  ADD PRIMARY KEY (`policy_id`),
  ADD UNIQUE KEY `policy_type` (`policy_type`);

--
-- Indexes for table `favorite_stalls`
--
ALTER TABLE `favorite_stalls`
  ADD PRIMARY KEY (`favorite_id`),
  ADD UNIQUE KEY `student_id` (`student_id`,`stall_id`),
  ADD KEY `idx_student_stall` (`student_id`,`stall_id`),
  ADD KEY `favorite_stalls_ibfk_2` (`stall_id`);

--
-- Indexes for table `menudetails`
--
ALTER TABLE `menudetails`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `stall_id` (`stall_id`),
  ADD KEY `idx_stall_id` (`stall_id`),
  ADD KEY `idx_category` (`item_category`);

--
-- Indexes for table `menu_items`
--
ALTER TABLE `menu_items`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `stall_id` (`stall_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD UNIQUE KEY `display_order_id` (`display_order_id`),
  ADD KEY `idx_stall_id` (`stall_id`),
  ADD KEY `idx_order_date` (`order_date`),
  ADD KEY `idx_stall_id_orders` (`stall_id`),
  ADD KEY `idx_stall_status` (`stall_id`,`order_status`),
  ADD KEY `idx_stall_status_date` (`stall_id`,`order_status`,`order_date`),
  ADD KEY `orders_ibfk_2` (`student_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `idx_order_id` (`order_id`),
  ADD KEY `idx_item_name` (`item_name`);

--
-- Indexes for table `osignup`
--
ALTER TABLE `osignup`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `phonenumber` (`phonenumber`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `owner_reviews`
--
ALTER TABLE `owner_reviews`
  ADD PRIMARY KEY (`review_id`);

--
-- Indexes for table `password_resets`
--
ALTER TABLE `password_resets`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `rent_invoices`
--
ALTER TABLE `rent_invoices`
  ADD PRIMARY KEY (`invoice_id`),
  ADD KEY `rent_invoices_ibfk_1` (`stall_id`);

--
-- Indexes for table `stalldetails`
--
ALTER TABLE `stalldetails`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `phonenumber` (`phonenumber`),
  ADD UNIQUE KEY `fssainumber` (`fssainumber`),
  ADD UNIQUE KEY `stall_id` (`stall_id`),
  ADD KEY `idx_approval` (`approval`);

--
-- Indexes for table `student_reviews`
--
ALTER TABLE `student_reviews`
  ADD PRIMARY KEY (`review_id`),
  ADD UNIQUE KEY `stall_id` (`stall_id`,`student_id`),
  ADD KEY `idx_stall_id` (`stall_id`),
  ADD KEY `idx_student_id` (`student_id`),
  ADD KEY `idx_stall_id_reviews` (`stall_id`);

--
-- Indexes for table `usignup`
--
ALTER TABLE `usignup`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `student_id` (`student_id`);

--
-- Indexes for table `wallets`
--
ALTER TABLE `wallets`
  ADD PRIMARY KEY (`wallet_id`),
  ADD UNIQUE KEY `student_id` (`student_id`);

--
-- Indexes for table `wallet_transactions`
--
ALTER TABLE `wallet_transactions`
  ADD PRIMARY KEY (`transaction_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `student_id_index` (`student_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `app_policies`
--
ALTER TABLE `app_policies`
  MODIFY `policy_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `favorite_stalls`
--
ALTER TABLE `favorite_stalls`
  MODIFY `favorite_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT for table `menudetails`
--
ALTER TABLE `menudetails`
  MODIFY `item_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `menu_items`
--
ALTER TABLE `menu_items`
  MODIFY `item_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=92;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `item_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=193;

--
-- AUTO_INCREMENT for table `osignup`
--
ALTER TABLE `osignup`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- AUTO_INCREMENT for table `owner_reviews`
--
ALTER TABLE `owner_reviews`
  MODIFY `review_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `rent_invoices`
--
ALTER TABLE `rent_invoices`
  MODIFY `invoice_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `stalldetails`
--
ALTER TABLE `stalldetails`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `student_reviews`
--
ALTER TABLE `student_reviews`
  MODIFY `review_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `usignup`
--
ALTER TABLE `usignup`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=55;

--
-- AUTO_INCREMENT for table `wallets`
--
ALTER TABLE `wallets`
  MODIFY `wallet_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `wallet_transactions`
--
ALTER TABLE `wallet_transactions`
  MODIFY `transaction_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `favorite_stalls`
--
ALTER TABLE `favorite_stalls`
  ADD CONSTRAINT `favorite_stalls_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `usignup` (`student_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `favorite_stalls_ibfk_2` FOREIGN KEY (`stall_id`) REFERENCES `stalldetails` (`stall_id`) ON DELETE CASCADE;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`stall_id`) REFERENCES `stalldetails` (`stall_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `usignup` (`student_id`) ON DELETE CASCADE;

--
-- Constraints for table `order_items`
--
ALTER TABLE `order_items`
  ADD CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`);

--
-- Constraints for table `rent_invoices`
--
ALTER TABLE `rent_invoices`
  ADD CONSTRAINT `rent_invoices_ibfk_1` FOREIGN KEY (`stall_id`) REFERENCES `stalldetails` (`stall_id`) ON DELETE CASCADE;

--
-- Constraints for table `student_reviews`
--
ALTER TABLE `student_reviews`
  ADD CONSTRAINT `student_reviews_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `usignup` (`student_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `student_reviews_ibfk_2` FOREIGN KEY (`stall_id`) REFERENCES `stalldetails` (`stall_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
