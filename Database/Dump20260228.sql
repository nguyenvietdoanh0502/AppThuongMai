CREATE DATABASE  IF NOT EXISTS `btl_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `btl_db`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: btl_db
-- ------------------------------------------------------
-- Server version	9.5.0

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
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '868f1fa6-d4f2-11f0-b0ea-088fc3e9b24f:1-1229';

--
-- Table structure for table `cartitems`
--

DROP TABLE IF EXISTS `cartitems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cartitems` (
  `cart_item_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cart_item_id`),
  UNIQUE KEY `unique_user_product` (`user_id`,`product_id`),
  KEY `fk_cartitems_products` (`product_id`),
  CONSTRAINT `fk_cartitems_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_cartitems_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cartitems`
--

LOCK TABLES `cartitems` WRITE;
/*!40000 ALTER TABLE `cartitems` DISABLE KEYS */;
/*!40000 ALTER TABLE `cartitems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=491 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (342,'đồ điện tử'),(1,'electronics'),(4,'jewelery'),(3,'men\'s clothing'),(17,'PC'),(2,'women\'s clothing');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderdetails`
--

DROP TABLE IF EXISTS `orderdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderdetails` (
  `order_details_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(15,2) NOT NULL,
  PRIMARY KEY (`order_details_id`),
  KEY `fk_orderdetails_orders` (`order_id`),
  KEY `fk_orderdetails_products` (`product_id`),
  CONSTRAINT `fk_orderdetails_orders` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `fk_orderdetails_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderdetails`
--

LOCK TABLES `orderdetails` WRITE;
/*!40000 ALTER TABLE `orderdetails` DISABLE KEYS */;
INSERT INTO `orderdetails` VALUES (1,11,5,1,15.99),(2,11,6,1,695.00),(3,11,7,3,168.00),(4,11,11,3,109.00),(5,12,5,3,15.99),(6,13,5,3,15.99),(7,13,6,4,695.00),(8,14,6,4,695.00),(9,15,6,4,695.00),(10,16,6,2,695.00),(12,18,11,6,109.00),(13,19,5,2,695.00),(14,19,7,2,9.99),(19,21,8,4,10.99),(20,22,7,2,9.99),(21,22,8,1,10.99),(22,23,3,2,55.99),(23,23,6,3,168.00),(24,23,12,5,114.00),(25,24,1702,14,200.00),(26,25,14,1,999.99),(27,25,15,2,56.99),(28,26,1,1,109.95),(29,26,2,1,22.30),(30,26,3,1,55.99);
/*!40000 ALTER TABLE `orderdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `total_amount` decimal(15,2) NOT NULL,
  `shipping_address` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `fk_orders_users` (`user_id`),
  CONSTRAINT `fk_orders_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,6,'2026-02-04 04:39:44',5565.00,'Hà Nội','0981927503'),(2,6,'2026-02-04 04:49:49',3480.00,'05Doanh02@','0981927503'),(3,6,'2026-02-04 06:56:56',205.00,'2','02'),(4,6,'2026-02-04 07:50:55',4232.96,'Hà Nội','0981927503'),(5,6,'2026-02-04 08:04:28',118.89,'Hà nội','0981927503'),(6,6,'2026-02-04 08:12:38',2785.00,'2','2'),(7,6,'2026-02-04 08:12:46',5.00,'2','2'),(8,6,'2026-02-04 08:14:31',604.00,'3','3'),(9,6,'2026-02-04 08:17:05',715.99,'2','2'),(10,6,'2026-02-04 09:12:47',1620.93,'Hà Nội','05022005'),(11,6,'2026-02-04 09:16:45',1546.99,'Hà Nội','0981927503'),(12,6,'2026-02-04 09:42:41',52.97,'d','d'),(13,6,'2026-02-04 09:46:54',2832.97,'2','0981927503'),(14,6,'2026-02-04 09:47:30',2785.00,'e','0981927503'),(15,6,'2026-02-04 09:55:04',2785.00,'2','0981927503'),(16,6,'2026-02-04 09:55:24',1395.00,'3','0981927503'),(17,6,'2026-02-04 09:56:37',1065.00,'Hà Nội','0981927503'),(18,6,'2026-02-04 15:29:56',659.00,'Hà Nội','0981927503'),(19,6,'2026-02-07 11:31:38',1414.98,'Hà Nội','0981927503'),(21,6,'2026-02-12 04:10:08',48.96,'Hà Nội','0981927503'),(22,6,'2026-02-16 12:42:11',35.97,'Hà Nội','0981927503'),(23,14,'2026-02-21 15:55:28',1190.98,'Ha Noi','0981927503'),(24,6,'2026-02-22 02:56:32',2805.00,'Hà Nội','0981927503'),(25,6,'2026-02-28 03:34:39',1118.97,'Hà Nội','0981927503'),(26,6,'2026-02-28 03:45:08',193.24,'Hà Nội','0981927503');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(15,2) NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `category_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rating_rate` float DEFAULT '0',
  `rating_count` int DEFAULT '0',
  `quantity` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `api_id` int DEFAULT NULL,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `api_id` (`api_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2443 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Fjallraven - Foldsack No. 1 Backpack, Fits 15 Laptops',109.95,'Your perfect pack for everyday use and walks in the forest. Stash your laptop (up to 15 inches) in the padded sleeve, your everyday','men\'s clothing','https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_t.png',3.9,120,15,'2026-02-04 15:21:35',1,0),(2,'Mens Casual Premium Slim Fit T-Shirts ',22.30,'Slim-fitting style, contrast raglan long sleeve, three-button henley placket, light weight & soft fabric for breathable and comfortable wearing. And Solid stitched shirts with round neck made for durability and a great fit for casual fashion wear and diehard baseball fans. The Henley style round neckline includes a three-button placket.','men\'s clothing','https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_t.png',4.1,259,9,'2026-02-04 15:21:35',2,0),(3,'Mens Cotton Jacket',55.99,'great outerwear jackets for Spring/Autumn/Winter, suitable for many occasions, such as working, hiking, camping, mountain/rock climbing, cycling, traveling or other outdoors. Good gift choice for you or your family member. A warm hearted love to Father, husband or son in this thanksgiving or Christmas Day.','men\'s clothing','https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_t.png',4.7,500,47,'2026-02-04 15:21:35',3,0),(4,'Mens Casual Slim Fit',15.99,'The color could be slightly different between on the screen and in practice. / Please note that body builds vary by person, therefore, detailed size information should be reviewed below on the product description.','men\'s clothing','https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_t.png',2.1,430,84,'2026-02-04 15:21:35',4,0),(5,'John Hardy Women\'s Legends Naga Gold & Silver Dragon Station Chain Bracelet',695.00,'From our Legends Collection, the Naga was inspired by the mythical water dragon that protects the ocean\'s pearl. Wear facing inward to be bestowed with love and abundance, or outward for protection.','jewelery','https://fakestoreapi.com/img/71pWzhdJNwL._AC_UL640_QL65_ML3_t.png',4.6,400,52,'2026-02-04 15:21:35',5,0),(6,'Solid Gold Petite Micropave ',168.00,'Satisfaction Guaranteed. Return or exchange any order within 30 days.Designed and sold by Hafeez Center in the United States. Satisfaction Guaranteed. Return or exchange any order within 30 days.','jewelery','https://fakestoreapi.com/img/61sbMiUnoGL._AC_UL640_QL65_ML3_t.png',3.9,70,53,'2026-02-04 15:21:35',6,0),(7,'White Gold Plated Princess',9.99,'Classic Created Wedding Engagement Solitaire Diamond Promise Ring for Her. Gifts to spoil your love more for Engagement, Wedding, Anniversary, Valentine\'s Day...','jewelery','https://fakestoreapi.com/img/71YAIFU48IL._AC_UL640_QL65_ML3_t.png',3,400,61,'2026-02-04 15:21:35',7,0),(8,'Pierced Owl Rose Gold Plated Stainless Steel Double',10.99,'Rose Gold Plated Double Flared Tunnel Plug Earrings. Made of 316L Stainless Steel','jewelery','https://fakestoreapi.com/img/51UDEzMJVpL._AC_UL640_QL65_ML3_t.png',1.9,100,66,'2026-02-04 15:21:35',8,0),(9,'WD 2TB Elements Portable External Hard Drive - USB 3.0 ',64.00,'USB 3.0 and USB 2.0 Compatibility Fast data transfers Improve PC Performance High Capacity; Compatibility Formatted NTFS for Windows 10, Windows 8.1, Windows 7; Reformatting may be required for other operating systems; Compatibility may vary depending on user’s hardware configuration and operating system','electronics','https://fakestoreapi.com/img/61IBBVJvSDL._AC_SY879_t.png',3.3,203,11,'2026-02-04 15:21:35',9,0),(10,'SanDisk SSD PLUS 1TB Internal SSD - SATA III 6 Gb/s',109.00,'Easy upgrade for faster boot up, shutdown, application load and response (As compared to 5400 RPM SATA 2.5” hard drive; Based on published specifications and internal benchmarking tests using PCMark vantage scores) Boosts burst write performance, making it ideal for typical PC workloads The perfect balance of performance and reliability Read/write speeds of up to 535MB/s/450MB/s (Based on internal testing; Performance may vary depending upon drive capacity, host device, OS and application.)','electronics','https://fakestoreapi.com/img/61U7T1koQqL._AC_SX679_t.png',2.9,470,63,'2026-02-04 15:21:35',10,0),(11,'Silicon Power 256GB SSD 3D NAND A55 SLC Cache Performance Boost SATA III 2.5',109.00,'3D NAND flash are applied to deliver high transfer speeds Remarkable transfer speeds that enable faster bootup and improved overall system performance. The advanced SLC Cache Technology allows performance boost and longer lifespan 7mm slim design suitable for Ultrabooks and Ultra-slim notebooks. Supports TRIM command, Garbage Collection technology, RAID, and ECC (Error Checking & Correction) to provide the optimized performance and enhanced reliability.','electronics','https://fakestoreapi.com/img/71kWymZ+c+L._AC_SX679_t.png',4.8,319,1,'2026-02-04 15:21:35',11,0),(12,'WD 4TB Gaming Drive Works with Playstation 4 Portable External Hard Drive',114.00,'Expand your PS4 gaming experience, Play anywhere Fast and easy, setup Sleek design with high capacity, 3-year manufacturer\'s limited warranty','electronics','https://fakestoreapi.com/img/61mtL65D4cL._AC_SX679_t.png',4.8,400,19,'2026-02-04 15:21:35',12,0),(13,'Acer SB220Q bi 21.5 inches Full HD (1920 x 1080) IPS Ultra-Thin',599.00,'21. 5 inches Full HD (1920 x 1080) widescreen IPS display And Radeon free Sync technology. No compatibility for VESA Mount Refresh Rate: 75Hz - Using HDMI port Zero-frame design | ultra-thin | 4ms response time | IPS panel Aspect ratio - 16: 9. Color Supported - 16. 7 million colors. Brightness - 250 nit Tilt angle -5 degree to 15 degree. Horizontal viewing angle-178 degree. Vertical viewing angle-178 degree 75 hertz','electronics','https://fakestoreapi.com/img/81QpkIctqPL._AC_SX679_t.png',2.9,250,66,'2026-02-04 15:21:35',13,0),(14,'Samsung 49-Inch CHG90 144Hz Curved Gaming Monitor (LC49HG90DMNXZA) – Super Ultrawide Screen QLED ',999.99,'49 INCH SUPER ULTRAWIDE 32:9 CURVED GAMING MONITOR with dual 27 inch screen side by side QUANTUM DOT (QLED) TECHNOLOGY, HDR support and factory calibration provides stunningly realistic and accurate color and contrast 144HZ HIGH REFRESH RATE and 1ms ultra fast response time work to eliminate motion blur, ghosting, and reduce input lag','electronics','https://fakestoreapi.com/img/81Zt42ioCgL._AC_SX679_t.png',2.2,140,84,'2026-02-04 15:21:35',14,0),(15,'BIYLACLESEN Women\'s 3-in-1 Snowboard Jacket Winter Coats',56.99,'Note:The Jackets is US standard size, Please choose size as your usual wear Material: 100% Polyester; Detachable Liner Fabric: Warm Fleece. Detachable Functional Liner: Skin Friendly, Lightweigt and Warm.Stand Collar Liner jacket, keep you warm in cold weather. Zippered Pockets: 2 Zippered Hand Pockets, 2 Zippered Pockets on Chest (enough to keep cards or keys)and 1 Hidden Pocket Inside.Zippered Hand Pockets and Hidden Pocket keep your things secure. Humanized Design: Adjustable and Detachable Hood and Adjustable cuff to prevent the wind and water,for a comfortable fit. 3 in 1 Detachable Design provide more convenience, you can separate the coat and inner as needed, or wear it together. It is suitable for different season and help you adapt to different climates','women\'s clothing','https://fakestoreapi.com/img/51Y5NI-I5jL._AC_UX679_t.png',2.6,235,30,'2026-02-04 15:21:35',15,0),(16,'Lock and Love Women\'s Removable Hooded Faux Leather Moto Biker Jacket',29.95,'100% POLYURETHANE(shell) 100% POLYESTER(lining) 75% POLYESTER 25% COTTON (SWEATER), Faux leather material for style and comfort / 2 pockets of front, 2-For-One Hooded denim style faux leather jacket, Button detail on waist / Detail stitching at sides, HAND WASH ONLY / DO NOT BLEACH / LINE DRY / DO NOT IRON','women\'s clothing','https://fakestoreapi.com/img/81XH0e8fefL._AC_UY879_t.png',2.9,340,74,'2026-02-04 15:21:35',16,0),(17,'Rain Jacket Women Windbreaker Striped Climbing Raincoats',39.99,'Lightweight perfet for trip or casual wear---Long sleeve with hooded, adjustable drawstring waist design. Button and zipper front closure raincoat, fully stripes Lined and The Raincoat has 2 side pockets are a good size to hold all kinds of things, it covers the hips, and the hood is generous but doesn\'t overdo it.Attached Cotton Lined Hood with Adjustable Drawstrings give it a real styled look.','women\'s clothing','https://fakestoreapi.com/img/71HblAHs5xL._AC_UY879_-2t.png',3.8,679,45,'2026-02-04 15:21:35',17,0),(18,'MBJ Women\'s Solid Short Sleeve Boat Neck V ',9.85,'95% RAYON 5% SPANDEX, Made in USA or Imported, Do Not Bleach, Lightweight fabric with great stretch for comfort, Ribbed on sleeves and neckline / Double stitching on bottom hem','women\'s clothing','https://fakestoreapi.com/img/71z3kpMAYsL._AC_UY879_t.png',4.7,130,44,'2026-02-04 15:21:35',18,0),(19,'Opna Women\'s Short Sleeve Moisture',7.95,'100% Polyester, Machine wash, 100% cationic polyester interlock, Machine Wash & Pre Shrunk for a Great Fit, Lightweight, roomy and highly breathable with moisture wicking fabric which helps to keep moisture away, Soft Lightweight Fabric with comfortable V-neck collar and a slimmer fit, delivers a sleek, more feminine silhouette and Added Comfort','women\'s clothing','https://fakestoreapi.com/img/51eg55uWmdL._AC_UX679_t.png',4.5,146,47,'2026-02-04 15:21:35',19,0),(20,'DANVOUY Womens T Shirt Casual Cotton Short',12.99,'95%Cotton,5%Spandex, Features: Casual, Short Sleeve, Letter Print,V-Neck,Fashion Tees, The fabric is soft and has some stretch., Occasion: Casual/Office/Beach/School/Home/Street. Season: Spring,Summer,Autumn,Winter.','women\'s clothing','https://fakestoreapi.com/img/61pHAEJ4NML._AC_UX679_t.png',3.6,145,43,'2026-02-04 15:21:35',20,0),(81,'Máy tính',1400.00,'pc','PC','https://th.bing.com/th/id/OIP.yw5UU9Rr8XYRaQ3mavgoKAHaEK?w=319&h=180&c=7&r=0&o=7&cb=defcachec2&pid=1.7&rm=3',0,0,3,'2026-02-04 15:31:40',NULL,1),(1702,'Bàn phím',200.00,NULL,'đồ điện tử','file:/C:/Users/ADMIN/Downloads/598974466_1348552240626909_3819675110324461544_n.jpg',0,0,13,'2026-02-22 02:52:54',NULL,1);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'user',
  `money` decimal(15,2) DEFAULT '0.00',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `provider` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'LOCAL',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (3,'admin_vip','admin@example.com','AdminPass123','ADMIN',0.00,'2026-01-25 09:41:24','LOCAL','ACTIVE'),(5,'vietdoanh222','vietdoanh0502@gmail.com','$2a$12$Wkoliqv3pJoWmbMvCN9bQOW7eP6D4UyYGT20lkNdD1FSTk2iClO9.','ADMIN',4000.00,'2026-01-28 03:33:59','LOCAL','ACTIVE'),(6,'vietdoanh223','vietdoanh0502@gmail.com','$2a$12$Wkoliqv3pJoWmbMvCN9bQOW7eP6D4UyYGT20lkNdD1FSTk2iClO9.','USER',5025826.18,'2026-01-28 03:37:28','LOCAL','ACTIVE'),(7,'admin_root','admin@system.com','$2a$12$lRO6Idnz3k/lnCOc1exe..ebtsMt11KIpbJQZqcKlru1qJZcjYtmu','ADMIN',0.00,'2026-02-01 15:05:38','LOCAL','ACTIVE'),(13,'checkinmeee_google','checkinmeee@gmail.com','GOOGLE_1771688962772','USER',0.00,'2026-02-21 15:49:22','GOOGLE','ACTIVE'),(14,'vietdoanh0502','doanh05022005@gmail.com','$2a$12$zew1fU5ZsJmMa6p1KkJz6.6UyOpJNc2I7QKCYjJjPJVLHR6Ba7oL2','USER',3809.02,'2026-02-21 15:54:22','LOCAL','ACTIVE');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_daily_revenue`
--

DROP TABLE IF EXISTS `v_daily_revenue`;
/*!50001 DROP VIEW IF EXISTS `v_daily_revenue`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_daily_revenue` AS SELECT 
 1 AS `report_date`,
 1 AS `total_orders`,
 1 AS `total_revenue`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist` (
  `user_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`product_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `wishlist_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `wishlist_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist`
--

LOCK TABLES `wishlist` WRITE;
/*!40000 ALTER TABLE `wishlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `wishlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `v_daily_revenue`
--

/*!50001 DROP VIEW IF EXISTS `v_daily_revenue`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_daily_revenue` AS select cast(`orders`.`order_date` as date) AS `report_date`,count(`orders`.`order_id`) AS `total_orders`,sum(`orders`.`total_amount`) AS `total_revenue` from `orders` group by cast(`orders`.`order_date` as date) order by `report_date` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-28 11:25:07
