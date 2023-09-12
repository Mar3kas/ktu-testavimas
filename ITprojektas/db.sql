-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: telephoneportal
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admins`
--

DROP TABLE IF EXISTS `admins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admins` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `surname` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `username` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `roles_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_admins_roles_idx` (`roles_id`),
  CONSTRAINT `fk_admins_roles` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admins`
--

LOCK TABLES `admins` WRITE;
/*!40000 ALTER TABLE `admins` DISABLE KEYS */;
INSERT INTO `admins` VALUES (1,'Johny','Bravo','Johny','$2a$12$4cWUciGUUu3/XSvQH7BUE.3ygesUGV1jdTH.b1OCyXKtlCXe8UlsS',1);
/*!40000 ALTER TABLE `admins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultant_status`
--

DROP TABLE IF EXISTS `consultant_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultant_status` (
  `id` int NOT NULL,
  `status` enum('free','occupied') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultant_status`
--

LOCK TABLES `consultant_status` WRITE;
/*!40000 ALTER TABLE `consultant_status` DISABLE KEYS */;
INSERT INTO `consultant_status` VALUES (1,'free'),(2,'occupied');
/*!40000 ALTER TABLE `consultant_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultants`
--

DROP TABLE IF EXISTS `consultants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultants` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `surname` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `username` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `credits` double NOT NULL,
  `roles_id` int NOT NULL,
  `consultant_status_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_consultants_roles_idx` (`roles_id`),
  KEY `fk_consultants_consultant_status_idx` (`consultant_status_id`),
  CONSTRAINT `fk_consultants_consultant_status` FOREIGN KEY (`consultant_status_id`) REFERENCES `consultant_status` (`id`),
  CONSTRAINT `fk_consultants_roles` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultants`
--

LOCK TABLES `consultants` WRITE;
/*!40000 ALTER TABLE `consultants` DISABLE KEYS */;
INSERT INTO `consultants` VALUES (1,'Jonas','Jonaitis','jonukas','$2a$12$4z3f6dH.EnfAuwXzu63toe96Pf.ACxme6cRIJSxcXbbQqymG/pw92',175,3,1),(2,'Aniel','Sokoup','aniel','$2a$12$Fd4gn/HZWNuVKgqZxOT8qeYWVcBNl4egIKdPLqCd6o3CC1jSAcqmC',60.5,3,1);
/*!40000 ALTER TABLE `consultants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultations`
--

DROP TABLE IF EXISTS `consultations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `finished` tinyint NOT NULL DEFAULT '0',
  `users_id` int NOT NULL,
  `consultants_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_consultations_users_idx` (`users_id`),
  KEY `fk_consultations_consultants_idx` (`consultants_id`),
  CONSTRAINT `fk_consultations_consultants` FOREIGN KEY (`consultants_id`) REFERENCES `consultants` (`id`),
  CONSTRAINT `fk_consultations_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultations`
--

LOCK TABLES `consultations` WRITE;
/*!40000 ALTER TABLE `consultations` DISABLE KEYS */;
INSERT INTO `consultations` VALUES (3,1,1,1),(4,1,1,1),(5,1,1,1),(6,1,1,1),(7,1,1,2),(8,1,1,1);
/*!40000 ALTER TABLE `consultations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credit_requests`
--

DROP TABLE IF EXISTS `credit_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `credit_requests` (
  `id` int NOT NULL AUTO_INCREMENT,
  `credits` double NOT NULL,
  `users_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_credit_requests_users_idx` (`users_id`) /*!80000 INVISIBLE */,
  CONSTRAINT `fk_credit_requests_users` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credit_requests`
--

LOCK TABLES `credit_requests` WRITE;
/*!40000 ALTER TABLE `credit_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `credit_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role` enum('ROLE_ADMIN','ROLE_CONSULTANT','ROLE_USER') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER'),(3,'ROLE_CONSULTANT');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `surname` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `username` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `credits` double NOT NULL,
  `roles_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_users_roles_idx` (`roles_id`),
  CONSTRAINT `fk_users_roles` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'petka','petkauskas','petka','$2a$12$iJplqU90HkP4OdUZOoXpEOLUm4VrCXM54cxuNSMdEDeOgpP7o3BDy',13.200000000000003,2);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'telephoneportal'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-09-12 10:27:06
