-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 23, 2025 at 05:35 PM
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
-- Database: `mindbalance`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `admin_id` int(11) NOT NULL,
  `admin_name` varchar(50) NOT NULL,
  `admin_email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`admin_id`, `admin_name`, `admin_email`, `password`) VALUES
(1, 'admin', 'admin@mindbalance.com', 'admin');

-- --------------------------------------------------------

--
-- Table structure for table `appointments`
--

CREATE TABLE `appointments` (
  `appointment_id` int(11) NOT NULL,
  `patient_id` int(11) NOT NULL,
  `expert_id` int(11) NOT NULL,
  `appointment_date` datetime DEFAULT NULL,
  `status` enum('pending','approved','rejected','completed') DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `problem` varchar(500) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `slot_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointments`
--

INSERT INTO `appointments` (`appointment_id`, `patient_id`, `expert_id`, `appointment_date`, `status`, `created_at`, `problem`, `age`, `slot_id`) VALUES
(21, 19, 3, '2025-08-23 18:30:00', 'completed', '2025-08-23 12:09:49', 'sleep deprivation', 22, 18),
(22, 19, 4, '2025-08-23 19:00:00', 'pending', '2025-08-23 12:10:14', 'lack of concentration', 22, 23),
(23, 19, 3, '2025-08-23 20:00:00', 'pending', '2025-08-23 13:30:49', 'erytrdhy', 22, 26);

-- --------------------------------------------------------

--
-- Table structure for table `experts`
--

CREATE TABLE `experts` (
  `expert_id` int(11) NOT NULL,
  `expertsName` varchar(255) DEFAULT NULL,
  `expertAddress` varchar(255) DEFAULT NULL,
  `expertEmail` varchar(200) DEFAULT NULL,
  `password` varchar(200) DEFAULT NULL,
  `phone` int(11) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `role` enum('psychologist','psychiatrist') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `experts`
--

INSERT INTO `experts` (`expert_id`, `expertsName`, `expertAddress`, `expertEmail`, `password`, `phone`, `status`, `role`) VALUES
(3, 'Taslima', 'dhaka', 'taslima@gmail.com', 'taslima', 1711469513, 'active', 'psychologist'),
(4, 'Bushra', 'America', 'bushra@gmail.com', 'bushra', 1711469513, 'active', 'psychiatrist');

-- --------------------------------------------------------

--
-- Table structure for table `expert_availability`
--

CREATE TABLE `expert_availability` (
  `slot_id` int(11) NOT NULL,
  `expert_id` int(11) NOT NULL,
  `slot_datetime` datetime NOT NULL,
  `is_available` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `expert_availability`
--

INSERT INTO `expert_availability` (`slot_id`, `expert_id`, `slot_datetime`, `is_available`) VALUES
(18, 3, '2025-08-23 18:30:00', 0),
(19, 3, '2025-08-23 19:00:00', 1),
(21, 4, '2025-08-23 18:30:00', 1),
(23, 4, '2025-08-23 19:00:00', 0),
(25, 4, '2025-08-23 18:00:00', 1),
(26, 3, '2025-08-23 20:00:00', 0);

-- --------------------------------------------------------

--
-- Table structure for table `mood_logs`
--

CREATE TABLE `mood_logs` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `mood` varchar(10) NOT NULL,
  `intensity` int(11) NOT NULL,
  `note` text DEFAULT NULL,
  `log_date` date NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `mood_logs`
--

INSERT INTO `mood_logs` (`id`, `user_id`, `mood`, `intensity`, `note`, `log_date`, `created_at`) VALUES
(9, 19, '😐', 5, 'too much design project pressure', '2025-08-23', '2025-08-23 15:03:38'),
(10, 19, '😡', 9, 'team er ekta polapain o thik moto kaj kore nai', '2025-08-23', '2025-08-23 15:22:59');

-- --------------------------------------------------------

--
-- Table structure for table `mood_tests`
--

CREATE TABLE `mood_tests` (
  `test_id` int(11) NOT NULL,
  `disorder_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `mood_test_results`
--

CREATE TABLE `mood_test_results` (
  `result_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `test_id` int(11) NOT NULL,
  `score` int(11) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `suggestion` text DEFAULT NULL,
  `taken_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `mood_tracking`
--

CREATE TABLE `mood_tracking` (
  `log_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `mood_level` enum('Happy','Sad','Anxious','Depressed','Neutral') DEFAULT NULL,
  `note` text DEFAULT NULL,
  `tracked_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('patient','psychologist','psychiatrist','admin') NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `status` enum('active','pending','blocked') DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `patientDOB` date DEFAULT NULL,
  `gender` enum('Male','Female','Other') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `email`, `password`, `role`, `phone`, `address`, `status`, `created_at`, `patientDOB`, `gender`) VALUES
(19, 'Jeem', 'jeem@gmail.com', 'jeem', 'patient', '01711469513', 'Mohammadpur', 'active', '2025-08-23 08:20:10', '2003-08-15', 'Female');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`admin_id`),
  ADD UNIQUE KEY `admin_email` (`admin_email`);

--
-- Indexes for table `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`appointment_id`),
  ADD KEY `patient_id` (`patient_id`),
  ADD KEY `expert_id` (`expert_id`),
  ADD KEY `fk_slot` (`slot_id`);

--
-- Indexes for table `experts`
--
ALTER TABLE `experts`
  ADD PRIMARY KEY (`expert_id`);

--
-- Indexes for table `expert_availability`
--
ALTER TABLE `expert_availability`
  ADD PRIMARY KEY (`slot_id`),
  ADD KEY `expert_id` (`expert_id`);

--
-- Indexes for table `mood_logs`
--
ALTER TABLE `mood_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `mood_tests`
--
ALTER TABLE `mood_tests`
  ADD PRIMARY KEY (`test_id`);

--
-- Indexes for table `mood_test_results`
--
ALTER TABLE `mood_test_results`
  ADD PRIMARY KEY (`result_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `test_id` (`test_id`);

--
-- Indexes for table `mood_tracking`
--
ALTER TABLE `mood_tracking`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `appointments`
--
ALTER TABLE `appointments`
  MODIFY `appointment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `experts`
--
ALTER TABLE `experts`
  MODIFY `expert_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `expert_availability`
--
ALTER TABLE `expert_availability`
  MODIFY `slot_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `mood_logs`
--
ALTER TABLE `mood_logs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `mood_tests`
--
ALTER TABLE `mood_tests`
  MODIFY `test_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `mood_test_results`
--
ALTER TABLE `mood_test_results`
  MODIFY `result_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `mood_tracking`
--
ALTER TABLE `mood_tracking`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `appointments`
--
ALTER TABLE `appointments`
  ADD CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`expert_id`) REFERENCES `experts` (`expert_id`),
  ADD CONSTRAINT `fk_slot` FOREIGN KEY (`slot_id`) REFERENCES `expert_availability` (`slot_id`);

--
-- Constraints for table `expert_availability`
--
ALTER TABLE `expert_availability`
  ADD CONSTRAINT `expert_availability_ibfk_1` FOREIGN KEY (`expert_id`) REFERENCES `experts` (`expert_id`);

--
-- Constraints for table `mood_logs`
--
ALTER TABLE `mood_logs`
  ADD CONSTRAINT `mood_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `mood_test_results`
--
ALTER TABLE `mood_test_results`
  ADD CONSTRAINT `mood_test_results_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `mood_test_results_ibfk_2` FOREIGN KEY (`test_id`) REFERENCES `mood_tests` (`test_id`);

--
-- Constraints for table `mood_tracking`
--
ALTER TABLE `mood_tracking`
  ADD CONSTRAINT `mood_tracking_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
