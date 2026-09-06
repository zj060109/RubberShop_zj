-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: localhost    Database: db_rubber_shop_zj_2024
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `db_rubber_shop_zj_2024`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `db_rubber_shop_zj_2024` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `db_rubber_shop_zj_2024`;

--
-- Table structure for table `balance_log_zj`
--

DROP TABLE IF EXISTS `balance_log_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `balance_log_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '??',
  `user_id_zj` bigint NOT NULL COMMENT '??ID',
  `change_amount_zj` decimal(10,2) NOT NULL COMMENT '????',
  `current_balance_zj` decimal(10,2) NOT NULL COMMENT '?????',
  `type_zj` enum('recharge','consume','repay','refund','withdraw','admin_adjust') NOT NULL COMMENT '????',
  `reference_id_zj` bigint DEFAULT NULL COMMENT '????/??ID',
  `remark_zj` varchar(200) DEFAULT NULL COMMENT '??',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????',
  PRIMARY KEY (`id_zj`),
  KEY `idx_balance_user` (`user_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='??????';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `balance_log_zj`
--

LOCK TABLES `balance_log_zj` WRITE;
/*!40000 ALTER TABLE `balance_log_zj` DISABLE KEYS */;
INSERT INTO `balance_log_zj` VALUES (16,1,1000.00,1000.00,'recharge',NULL,'余额充值','2026-06-25 00:15:13'),(17,1,-125.00,875.00,'consume',14,'订单支付','2026-06-25 00:15:28'),(18,1,125.00,1000.00,'refund',14,'订单退款','2026-06-25 00:15:40'),(19,13,1000.00,1000.00,'recharge',NULL,'余额充值','2026-06-25 09:02:58'),(20,13,1.00,1001.00,'recharge',NULL,'余额充值','2026-06-25 09:04:51'),(21,13,288888.00,289889.00,'recharge',NULL,'余额充值','2026-06-25 09:07:21'),(22,13,-289889.00,0.00,'admin_adjust',NULL,'管理员调整','2026-06-25 09:13:50'),(23,1,-1000.00,0.00,'admin_adjust',NULL,'管理员调整','2026-06-25 09:14:14'),(24,2,-50.00,0.00,'admin_adjust',NULL,'管理员调整','2026-06-25 09:14:22'),(25,17,1000.00,1000.00,'recharge',NULL,'余额充值','2026-06-25 17:29:08'),(26,17,-199.98,800.02,'consume',16,'订单支付','2026-06-25 17:29:09'),(27,18,500.00,500.00,'recharge',NULL,'余额充值','2026-06-25 17:30:01'),(28,18,-10.00,490.00,'consume',18,'订单支付','2026-06-25 17:30:01'),(29,18,10.00,500.00,'refund',18,'订单退款','2026-06-25 17:30:01'),(30,1,200.00,200.00,'admin_adjust',NULL,'管理员调整','2026-06-25 17:37:25'),(31,13,2000.00,2000.00,'admin_adjust',NULL,'管理员调整','2026-06-26 08:56:07'),(32,13,-132.00,1868.00,'consume',22,'订单支付','2026-06-26 08:56:16'),(33,13,-25.00,1843.00,'consume',23,'订单支付','2026-06-26 08:56:30'),(34,13,-25.00,1818.00,'consume',24,'订单支付','2026-06-26 08:57:50'),(35,13,-25.00,1793.00,'consume',25,'订单支付','2026-06-26 08:58:17'),(36,13,-25.00,1768.00,'consume',26,'订单支付','2026-06-26 08:58:44'),(37,13,-175.00,1593.00,'consume',27,'订单支付','2026-06-26 10:25:30'),(38,13,-895.95,697.05,'consume',28,'订单支付','2026-06-26 10:38:06'),(39,13,895.95,1593.00,'refund',28,'订单退款','2026-06-26 10:39:07');
/*!40000 ALTER TABLE `balance_log_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category_zj`
--

DROP TABLE IF EXISTS `category_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name_zj` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id_zj` bigint NOT NULL DEFAULT '0' COMMENT '父级ID，0为顶级',
  `sort_zj` int NOT NULL DEFAULT '0' COMMENT '排序',
  `icon_zj` varchar(255) DEFAULT NULL COMMENT '图标URL',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id_zj`),
  KEY `idx_category_parent` (`parent_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_zj`
--

LOCK TABLES `category_zj` WRITE;
/*!40000 ALTER TABLE `category_zj` DISABLE KEYS */;
INSERT INTO `category_zj` VALUES (1,'橡胶产品',0,1,NULL,'2026-07-08 14:37:02'),(10,'油封',1,1,NULL,'2026-07-08 14:37:02'),(11,'骨架油封',10,1,NULL,'2026-07-08 14:37:02'),(12,'液压油封',10,2,NULL,'2026-07-08 14:37:02'),(13,'气门油封',10,3,NULL,'2026-07-08 14:37:02'),(14,'旋转油封',10,4,NULL,'2026-07-08 14:37:02'),(20,'密封',1,2,NULL,'2026-07-08 14:37:02'),(21,'活塞杆密封',20,1,NULL,'2026-07-08 14:37:02'),(22,'活塞密封',20,2,NULL,'2026-07-08 14:37:02'),(23,'旋转密封',20,3,NULL,'2026-07-08 14:37:02'),(24,'防尘密封',20,4,NULL,'2026-07-08 14:37:02'),(25,'导向环',20,5,NULL,'2026-07-08 14:37:02'),(26,'支撑环',20,6,NULL,'2026-07-08 14:37:02'),(30,'管',1,3,NULL,'2026-07-08 14:37:02'),(31,'条',1,4,NULL,'2026-07-08 14:37:02'),(32,'棒',1,5,NULL,'2026-07-08 14:37:02'),(33,'板',1,6,NULL,'2026-07-08 14:37:02');
/*!40000 ALTER TABLE `category_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_conversation_zj`
--

DROP TABLE IF EXISTS `chat_conversation_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_conversation_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT,
  `customer_id_zj` bigint NOT NULL,
  `merchant_id_zj` bigint NOT NULL DEFAULT '1',
  `customer_name_zj` varchar(100) DEFAULT NULL,
  `customer_phone_zj` varchar(20) DEFAULT NULL,
  `customer_avatar_zj` varchar(500) DEFAULT NULL,
  `last_message_zj` varchar(500) DEFAULT NULL,
  `last_message_time_zj` datetime DEFAULT NULL,
  `unread_merchant_zj` int DEFAULT '0',
  `unread_customer_zj` int DEFAULT '0',
  `status_zj` varchar(20) DEFAULT 'active',
  `created_at_zj` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at_zj` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_conversation_zj`
--

LOCK TABLES `chat_conversation_zj` WRITE;
/*!40000 ALTER TABLE `chat_conversation_zj` DISABLE KEYS */;
INSERT INTO `chat_conversation_zj` VALUES (1,13,1,'zj','18942072228','/uploads/d766b57d-e606-438c-bc3a-66cf27f82d03.jpg','你好','2026-06-26 10:26:29',0,0,'active','2026-06-26 08:38:51','2026-06-26 08:38:51');
/*!40000 ALTER TABLE `chat_conversation_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message_zj`
--

DROP TABLE IF EXISTS `chat_message_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id_zj` bigint NOT NULL,
  `sender_id_zj` bigint NOT NULL,
  `sender_name_zj` varchar(100) DEFAULT NULL,
  `sender_avatar_zj` varchar(500) DEFAULT NULL,
  `sender_role_zj` varchar(20) NOT NULL,
  `content_zj` text NOT NULL,
  `message_type_zj` varchar(20) DEFAULT 'text',
  `is_read_zj` tinyint DEFAULT '0',
  `created_at_zj` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message_zj`
--

LOCK TABLES `chat_message_zj` WRITE;
/*!40000 ALTER TABLE `chat_message_zj` DISABLE KEYS */;
INSERT INTO `chat_message_zj` VALUES (1,1,13,'zj','/uploads/d766b57d-e606-438c-bc3a-66cf27f82d03.jpg','customer','Hi boss, need rubber pads','text',1,'2026-06-26 08:40:14'),(2,1,1,'中汇鑫',NULL,'merchant','OK, what quantity?','text',1,'2026-06-26 08:40:14'),(3,1,13,'zj','/uploads/d766b57d-e606-438c-bc3a-66cf27f82d03.jpg','customer','你好','text',1,'2026-06-26 10:26:29');
/*!40000 ALTER TABLE `chat_message_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customization_item_zj`
--

DROP TABLE IF EXISTS `customization_item_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customization_item_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customization_id_zj` bigint NOT NULL COMMENT '定制ID',
  `product_spec_zj` varchar(200) NOT NULL COMMENT '规格描述',
  `quantity_zj` int NOT NULL COMMENT '数量',
  `unit_price_zj` decimal(10,2) NOT NULL COMMENT '商家报价单价',
  PRIMARY KEY (`id_zj`),
  KEY `idx_citem_custom` (`customization_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定制报价明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customization_item_zj`
--

LOCK TABLES `customization_item_zj` WRITE;
/*!40000 ALTER TABLE `customization_item_zj` DISABLE KEYS */;
INSERT INTO `customization_item_zj` VALUES (7,9,'O型圈5*5',100,5.00),(8,10,'test',1,10.00);
/*!40000 ALTER TABLE `customization_item_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customization_zj`
--

DROP TABLE IF EXISTS `customization_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customization_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id_zj` bigint NOT NULL COMMENT '顾客ID',
  `status_zj` enum('pending_quote','quoted','confirmed','converted','cancelled') NOT NULL DEFAULT 'pending_quote' COMMENT '定制状态',
  `description_zj` text COMMENT '需求描述',
  `reference_images_zj` json DEFAULT NULL COMMENT '参考图片数组',
  `total_quoted_price_zj` decimal(10,2) DEFAULT NULL COMMENT '商家总报价',
  `order_id_zj` bigint DEFAULT NULL COMMENT '确认后生成的订单ID',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at_zj` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id_zj`),
  KEY `idx_custom_user` (`user_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定制主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customization_zj`
--

LOCK TABLES `customization_zj` WRITE;
/*!40000 ALTER TABLE `customization_zj` DISABLE KEYS */;
INSERT INTO `customization_zj` VALUES (7,13,'cancelled','密封圈\n规格要求：内径30外径45\n预估数量：1000\n样品图：/uploads/2fbed54a-ae55-474b-9dd8-7fbcdf1426cc.jpg',NULL,NULL,NULL,'2026-06-25 09:35:41','2026-06-25 14:37:46'),(8,13,'cancelled','111\n规格要求：111\n预估数量：100\n样品图：/uploads/3fcb103e-4f68-4446-82c6-3276d9f5e5c4.jpg ',NULL,NULL,NULL,'2026-06-25 11:25:08','2026-06-25 14:36:56'),(9,13,'cancelled','密封圈\n规格要求：外径5内径5\n预估数量：100\n样品图：/uploads/9f32b748-0acb-4d95-9fc7-49207e355a79.jpg /uploads/4daa2098-30f5-46ac-817c-4d73dca334d7.jpg ',NULL,500.00,NULL,'2026-06-25 14:38:37','2026-06-25 15:25:43'),(10,17,'quoted','test credit check',NULL,10.00,NULL,'2026-06-25 17:30:01','2026-06-25 17:30:01');
/*!40000 ALTER TABLE `customization_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `identity_verification_zj`
--

DROP TABLE IF EXISTS `identity_verification_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `identity_verification_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id_zj` bigint NOT NULL COMMENT '用户ID',
  `id_card_zj` varchar(18) NOT NULL COMMENT '身份证号',
  `real_name_zj` varchar(50) NOT NULL COMMENT '真实姓名',
  `face_image_zj` varchar(500) DEFAULT NULL COMMENT '人脸照片URL',
  `status_zj` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1-已认证 0-已失效',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '认证时间',
  `updated_at_zj` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id_zj`),
  UNIQUE KEY `uk_verify_user` (`user_id_zj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='实名认证表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `identity_verification_zj`
--

LOCK TABLES `identity_verification_zj` WRITE;
/*!40000 ALTER TABLE `identity_verification_zj` DISABLE KEYS */;
/*!40000 ALTER TABLE `identity_verification_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item_zj`
--

DROP TABLE IF EXISTS `order_item_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id_zj` bigint NOT NULL COMMENT '订单ID',
  `product_id_zj` bigint NOT NULL COMMENT '商品ID',
  `product_name_zj` varchar(100) NOT NULL COMMENT '商品名称快照',
  `product_image_zj` varchar(255) DEFAULT NULL COMMENT '商品主图快照',
  `price_zj` decimal(10,2) NOT NULL COMMENT '下单时单价',
  `quantity_zj` int NOT NULL COMMENT '数量',
  `subtotal_zj` decimal(10,2) NOT NULL COMMENT '小计',
  PRIMARY KEY (`id_zj`),
  KEY `idx_item_order` (`order_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item_zj`
--

LOCK TABLES `order_item_zj` WRITE;
/*!40000 ALTER TABLE `order_item_zj` DISABLE KEYS */;
INSERT INTO `order_item_zj` VALUES (19,14,1,'天然橡胶片',NULL,25.00,5,125.00),(21,16,21,'????',NULL,99.99,2,199.98),(23,18,23,'StockTest',NULL,10.00,1,10.00),(27,22,24,'??????',NULL,66.00,2,132.00),(28,23,1,'天然橡胶片',NULL,25.00,1,25.00),(29,24,1,'天然橡胶片',NULL,25.00,1,25.00),(30,25,1,'天然橡胶片',NULL,25.00,1,25.00),(31,26,1,'天然橡胶片',NULL,25.00,1,25.00),(32,27,1,'天然橡胶片',NULL,25.00,5,125.00),(33,27,23,'StockTest',NULL,10.00,5,50.00),(34,28,24,'??????',NULL,66.00,6,396.00),(35,28,21,'????',NULL,99.99,5,499.95);
/*!40000 ALTER TABLE `order_item_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status_log_zj`
--

DROP TABLE IF EXISTS `order_status_log_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_status_log_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id_zj` bigint NOT NULL COMMENT '订单ID',
  `from_status_zj` varchar(20) DEFAULT NULL COMMENT '原状态',
  `to_status_zj` varchar(20) NOT NULL COMMENT '新状态',
  `operator_id_zj` bigint DEFAULT NULL COMMENT '操作人ID',
  `remark_zj` text COMMENT '备注',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id_zj`),
  KEY `idx_log_order` (`order_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单状态变更日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status_log_zj`
--

LOCK TABLES `order_status_log_zj` WRITE;
/*!40000 ALTER TABLE `order_status_log_zj` DISABLE KEYS */;
INSERT INTO `order_status_log_zj` VALUES (22,14,NULL,'paid',1,'顾客下单','2026-06-25 00:15:28'),(23,14,'paid','refunded',1,NULL,'2026-06-25 00:15:40'),(24,16,NULL,'paid',17,'顾客下单','2026-06-25 17:29:09'),(25,16,'paid','accepted',15,NULL,'2026-06-25 17:29:30'),(26,16,'accepted','shipped',15,'物流：SF SF123','2026-06-25 17:29:30'),(27,16,'shipped','completed',17,NULL,'2026-06-25 17:29:30'),(28,18,NULL,'paid',18,'顾客下单','2026-06-25 17:30:01'),(29,18,'paid','refunded',18,NULL,'2026-06-25 17:30:01'),(30,22,NULL,'paid',13,'顾客下单','2026-06-26 08:56:16'),(31,22,'paid','shipped',1,'物流：SF SF999','2026-06-26 08:56:16'),(32,22,'shipped','completed',13,NULL,'2026-06-26 08:56:16'),(33,23,NULL,'paid',13,'顾客下单','2026-06-26 08:56:30'),(34,24,NULL,'paid',13,'顾客下单','2026-06-26 08:57:50'),(35,24,'paid','shipped',1,'物流：SF SF888','2026-06-26 08:57:51'),(36,24,'shipped','completed',13,NULL,'2026-06-26 08:57:51'),(37,25,NULL,'paid',13,'顾客下单','2026-06-26 08:58:17'),(38,25,'paid','shipped',1,'物流：SF SF999','2026-06-26 08:58:17'),(39,25,'shipped','completed',13,NULL,'2026-06-26 08:58:17'),(40,26,NULL,'paid',13,'顾客下单','2026-06-26 08:58:44'),(41,26,'paid','shipped_to_merchant',13,'顾客寄送商品至商户：SF SF001','2026-06-26 08:58:44'),(42,26,'shipped_to_merchant','installing',1,'商户已收到顾客寄送的商品，开始安装','2026-06-26 08:58:44'),(43,26,'installing','installed',1,'安装完成','2026-06-26 08:58:44'),(44,26,'installed','shipped',1,'物流：SF SF999','2026-06-26 08:58:44'),(45,26,'shipped','completed',13,NULL,'2026-06-26 08:58:44'),(46,27,NULL,'paid',13,'顾客下单','2026-06-26 10:25:30'),(47,28,NULL,'paid',13,'顾客下单','2026-06-26 10:38:06'),(48,28,'paid','accepted',1,NULL,'2026-06-26 10:38:43'),(49,28,'accepted','refunded',1,NULL,'2026-06-26 10:39:07');
/*!40000 ALTER TABLE `order_status_log_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_zj`
--

DROP TABLE IF EXISTS `order_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no_zj` varchar(32) NOT NULL COMMENT '订单号',
  `user_id_zj` bigint NOT NULL COMMENT '顾客ID',
  `total_amount_zj` decimal(10,2) NOT NULL COMMENT '商品总额',
  `actual_amount_zj` decimal(10,2) NOT NULL COMMENT '实付金额',
  `payment_method_zj` enum('balance','credit') NOT NULL COMMENT '支付方式',
  `status_zj` enum('paid','accepted','shipped','completed','cancelled','refunding','refunded','shipped_to_merchant','installing','installed') NOT NULL DEFAULT 'paid' COMMENT '订单状态',
  `receiver_name_zj` varchar(50) DEFAULT NULL COMMENT '收货人快照',
  `receiver_phone_zj` varchar(20) DEFAULT NULL COMMENT '收货电话快照',
  `province_zj` varchar(20) DEFAULT NULL COMMENT '省',
  `city_zj` varchar(20) DEFAULT NULL COMMENT '市',
  `district_zj` varchar(20) DEFAULT NULL COMMENT '区',
  `detail_address_zj` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `express_company_zj` varchar(50) DEFAULT NULL COMMENT '快递公司',
  `tracking_no_zj` varchar(50) DEFAULT NULL COMMENT '快递单号',
  `mark_zj` varchar(200) DEFAULT NULL COMMENT '备注',
  `paid_at_zj` datetime DEFAULT NULL COMMENT '支付时间',
  `shipped_at_zj` datetime DEFAULT NULL COMMENT '发货时间',
  `finished_at_zj` datetime DEFAULT NULL COMMENT '完成时间',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `updated_at_zj` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `need_installation_zj` tinyint DEFAULT '0',
  `customer_express_company_zj` varchar(50) DEFAULT NULL,
  `customer_tracking_no_zj` varchar(100) DEFAULT NULL,
  `installation_video_zj` varchar(500) DEFAULT NULL,
  `installation_images_zj` text,
  `installation_remark_zj` varchar(500) DEFAULT NULL,
  `installation_completed_at_zj` datetime DEFAULT NULL,
  PRIMARY KEY (`id_zj`),
  UNIQUE KEY `uk_order_no_zj` (`order_no_zj`),
  KEY `idx_order_user` (`user_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_zj`
--

LOCK TABLES `order_zj` WRITE;
/*!40000 ALTER TABLE `order_zj` DISABLE KEYS */;
INSERT INTO `order_zj` VALUES (14,'ORD20260625001527776179',1,125.00,125.00,'balance','refunded','张三','13800000001','广东省','广州市','天河区','体育西路100号',NULL,NULL,NULL,'2026-06-25 00:15:28',NULL,NULL,'2026-06-25 00:15:28','2026-06-25 00:15:40',0,NULL,NULL,NULL,NULL,NULL,NULL),(16,'ORD20260625172908489913',17,199.98,199.98,'balance','completed','??','15000000002','???','???','???','???100?','SF','SF123',NULL,'2026-06-25 17:29:08','2026-06-25 17:29:30','2026-06-25 17:29:30','2026-06-25 17:29:08','2026-06-25 17:29:30',0,NULL,NULL,NULL,NULL,NULL,NULL),(18,'ORD20260625173001185319',18,10.00,10.00,'balance','refunded','Full','15900000001','GD','GZ','TH','Addr1',NULL,NULL,NULL,'2026-06-25 17:30:01',NULL,NULL,'2026-06-25 17:30:01','2026-06-25 17:30:01',0,NULL,NULL,NULL,NULL,NULL,NULL),(22,'ORD20260626085615840744',13,132.00,132.00,'balance','completed','test','13800000000','GD','GZ','TH','addr 101','SF','SF999',NULL,'2026-06-26 08:56:16','2026-06-26 08:56:16','2026-06-26 08:56:16','2026-06-26 08:56:16','2026-06-26 08:56:16',1,NULL,NULL,NULL,NULL,NULL,NULL),(23,'ORD20260626085630178637',13,25.00,25.00,'balance','paid','t','13800000000','GD','GZ','TH','addr',NULL,NULL,NULL,'2026-06-26 08:56:30',NULL,NULL,'2026-06-26 08:56:30',NULL,1,NULL,NULL,NULL,NULL,NULL,NULL),(24,'ORD20260626085749847393',13,25.00,25.00,'balance','completed','t','13800000000','GD','GZ','TH','addr','SF','SF888',NULL,'2026-06-26 08:57:50','2026-06-26 08:57:51','2026-06-26 08:57:51','2026-06-26 08:57:50','2026-06-26 08:57:51',1,NULL,NULL,NULL,NULL,NULL,NULL),(25,'ORD20260626085817222827',13,25.00,25.00,'balance','completed','t','13000000000','GD','GZ','TH','addr','SF','SF999',NULL,'2026-06-26 08:58:17','2026-06-26 08:58:17','2026-06-26 08:58:17','2026-06-26 08:58:17','2026-06-26 08:58:17',1,NULL,NULL,NULL,NULL,NULL,NULL),(26,'ORD20260626085844235672',13,25.00,25.00,'balance','completed','t','13000000000','GD','GZ','TH','addr','SF','SF999',NULL,'2026-06-26 08:58:44','2026-06-26 08:58:44','2026-06-26 08:58:44','2026-06-26 08:58:44','2026-06-26 08:58:44',1,'SF','SF001','video.mp4','img1.jpg,img2.jpg',NULL,'2026-06-26 08:58:44'),(27,'ORD20260626102529586746',13,175.00,175.00,'balance','paid','111','111','11','1','1','111',NULL,NULL,NULL,'2026-06-26 10:25:30',NULL,NULL,'2026-06-26 10:25:30',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL),(28,'ORD20260626103805701240',13,895.95,895.95,'balance','refunded','zj111','189','湖南','长沙','岳麓','111',NULL,NULL,NULL,'2026-06-26 10:38:06',NULL,NULL,'2026-06-26 10:38:06','2026-06-26 10:39:07',1,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `order_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_zj`
--

DROP TABLE IF EXISTS `product_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id_zj` bigint NOT NULL COMMENT '分类ID',
  `name_zj` varchar(200) NOT NULL COMMENT '商品名称（自动生成：分类+品牌+型号+材质）',
  `spec_zj` varchar(255) DEFAULT NULL,
  `brand_zj` varchar(50) DEFAULT NULL COMMENT '品牌',
  `model_zj` varchar(100) DEFAULT NULL COMMENT '型号',
  `material_zj` varchar(50) DEFAULT NULL COMMENT '材质',
  `description_zj` text COMMENT '描述',
  `images_zj` json DEFAULT NULL COMMENT '图片数组',
  `price_zj` decimal(10,2) NOT NULL COMMENT '销售价',
  `stock_zj` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `warning_stock_zj` int NOT NULL DEFAULT '10' COMMENT '库存预警阈值',
  `status_zj` enum('on','off') NOT NULL DEFAULT 'on' COMMENT '上架/下架',
  `factory_id_zj` bigint DEFAULT NULL COMMENT '关联厂家ID',
  `is_customized_zj` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否来自定制 1-是 0-否',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at_zj` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id_zj`),
  KEY `idx_product_category` (`category_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=339 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_zj`
--

LOCK TABLES `product_zj` WRITE;
/*!40000 ALTER TABLE `product_zj` DISABLE KEYS */;
INSERT INTO `product_zj` VALUES (101,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:51',NULL),(102,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:51',NULL),(103,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:51',NULL),(104,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:51',NULL),(105,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:51',NULL),(106,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:51',NULL),(107,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:51',NULL),(108,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:53',NULL),(109,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:53',NULL),(110,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:53',NULL),(111,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:53',NULL),(112,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:53',NULL),(113,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:53',NULL),(114,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:53',NULL),(115,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:54',NULL),(116,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:54',NULL),(117,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:54',NULL),(118,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:54',NULL),(119,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:54',NULL),(120,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:54',NULL),(121,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:54',NULL),(122,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:55',NULL),(123,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:55',NULL),(124,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:55',NULL),(125,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:55',NULL),(126,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:55',NULL),(127,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:55',NULL),(128,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:55',NULL),(129,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:55',NULL),(130,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:55',NULL),(131,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:55',NULL),(132,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:55',NULL),(133,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:55',NULL),(134,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:55',NULL),(135,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:55',NULL),(136,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:56',NULL),(137,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:56',NULL),(138,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:56',NULL),(139,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:56',NULL),(140,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:56',NULL),(141,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:56',NULL),(142,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:56',NULL),(143,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:56',NULL),(144,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:56',NULL),(145,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:56',NULL),(146,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:56',NULL),(147,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:56',NULL),(148,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:56',NULL),(149,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:56',NULL),(150,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:57',NULL),(151,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:57',NULL),(152,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:57',NULL),(153,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:57',NULL),(154,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:57',NULL),(155,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:57',NULL),(156,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:57',NULL),(157,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:57',NULL),(158,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:57',NULL),(159,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:57',NULL),(160,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:57',NULL),(161,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:57',NULL),(162,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:57',NULL),(163,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:57',NULL),(164,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:58',NULL),(165,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:58',NULL),(166,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(167,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(168,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:58',NULL),(169,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(170,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:58',NULL),(171,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:58',NULL),(172,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:58',NULL),(173,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(174,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(175,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:58',NULL),(176,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(177,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:58',NULL),(178,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:58',NULL),(179,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:58',NULL),(180,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(181,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(182,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:58',NULL),(183,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(184,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:58',NULL),(185,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:58',NULL),(186,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:58',NULL),(187,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(188,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(189,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:58',NULL),(190,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(191,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:58',NULL),(192,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:58',NULL),(193,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:58',NULL),(194,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(195,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(196,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:58',NULL),(197,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:58',NULL),(198,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:58',NULL),(199,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:59',NULL),(200,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:59',NULL),(201,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(202,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(203,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:59',NULL),(204,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(205,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:59',NULL),(206,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:59',NULL),(207,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:59',NULL),(208,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(209,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(210,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:59',NULL),(211,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(212,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:59',NULL),(213,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:59',NULL),(214,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:59',NULL),(215,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(216,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(217,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:59',NULL),(218,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(219,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:59',NULL),(220,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:59',NULL),(221,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:59',NULL),(222,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(223,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(224,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:59',NULL),(225,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(226,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:59',NULL),(227,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:59',NULL),(228,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:59',NULL),(229,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(230,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(231,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:59',NULL),(232,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(233,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:59',NULL),(234,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:36:59',NULL),(235,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:36:59',NULL),(236,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(237,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(238,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:36:59',NULL),(239,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:36:59',NULL),(240,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:36:59',NULL),(241,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:00',NULL),(242,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:00',NULL),(243,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(244,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(245,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:00',NULL),(246,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(247,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:00',NULL),(248,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:00',NULL),(249,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:00',NULL),(250,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(251,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(252,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:00',NULL),(253,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(254,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:00',NULL),(255,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:00',NULL),(256,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:00',NULL),(257,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(258,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(259,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:00',NULL),(260,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(261,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:00',NULL),(262,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:00',NULL),(263,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:00',NULL),(264,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(265,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(266,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:00',NULL),(267,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(268,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:00',NULL),(269,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:00',NULL),(270,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:00',NULL),(271,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(272,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(273,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:00',NULL),(274,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(275,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:00',NULL),(276,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:00',NULL),(277,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:00',NULL),(278,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(279,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(280,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:00',NULL),(281,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:00',NULL),(282,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:00',NULL),(283,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:01',NULL),(284,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:01',NULL),(285,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(286,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(287,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:01',NULL),(288,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(289,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:01',NULL),(290,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:01',NULL),(291,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:01',NULL),(292,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(293,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(294,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:01',NULL),(295,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(296,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:01',NULL),(297,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:01',NULL),(298,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:01',NULL),(299,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(300,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(301,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:01',NULL),(302,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(303,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:01',NULL),(304,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:01',NULL),(305,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:01',NULL),(306,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(307,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(308,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:01',NULL),(309,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(310,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:01',NULL),(311,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:01',NULL),(312,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:01',NULL),(313,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(314,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(315,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:01',NULL),(316,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:01',NULL),(317,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:01',NULL),(318,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:02',NULL),(319,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:02',NULL),(320,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:02',NULL),(321,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:02',NULL),(322,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:02',NULL),(323,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:02',NULL),(324,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:02',NULL),(325,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:02',NULL),(326,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:02',NULL),(327,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:02',NULL),(328,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:02',NULL),(329,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:02',NULL),(330,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:02',NULL),(331,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:02',NULL),(332,11,'骨架油封 NAK TC-25-40-7 NBR 25x40x7','25x40x7','NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','[\"/uploads/seal1.jpg\", \"/uploads/seal2.jpg\"]',12.50,200,20,'on',NULL,0,'2026-07-08 14:37:02',NULL),(333,11,'骨架油封 CFW BA-30-47-7 FKM 30x47x7','30x47x7','CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','[\"/uploads/seal3.jpg\"]',35.00,150,15,'on',NULL,0,'2026-07-08 14:37:02',NULL),(334,12,'液压油封 NOK USI-40-55-9 PU 40x55x9','40x55x9','NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','[\"/uploads/seal4.jpg\"]',28.00,100,10,'on',NULL,0,'2026-07-08 14:37:02',NULL),(335,21,'活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','[\"/uploads/seal5.jpg\"]',45.00,80,10,'on',NULL,0,'2026-07-08 14:37:02',NULL),(336,21,'活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7','50x60x7','鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','[\"/uploads/seal6.jpg\"]',8.80,500,50,'on',NULL,0,'2026-07-08 14:37:02',NULL),(337,22,'活塞密封 NOK ODU-100-84-18 PU 100x84x18','100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','[\"/uploads/seal7.jpg\"]',55.00,60,10,'on',NULL,0,'2026-07-08 14:37:02',NULL),(338,24,'防尘密封 SKF DA-25-33-7 PU 25x33x7','25x33x7','SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','[\"/uploads/seal8.jpg\"]',6.00,300,30,'on',NULL,0,'2026-07-08 14:37:02',NULL);
/*!40000 ALTER TABLE `product_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_item_zj`
--

DROP TABLE IF EXISTS `purchase_item_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_item_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `purchase_id_zj` bigint NOT NULL COMMENT '采购单ID',
  `product_id_zj` bigint DEFAULT NULL COMMENT '关联商品（可为空）',
  `product_name_zj` varchar(100) NOT NULL COMMENT '商品名称',
  `spec_zj` varchar(200) DEFAULT NULL COMMENT '规格',
  `quantity_zj` int NOT NULL COMMENT '数量',
  `unit_price_zj` decimal(10,2) NOT NULL COMMENT '单价',
  `subtotal_zj` decimal(10,2) NOT NULL COMMENT '小计',
  PRIMARY KEY (`id_zj`),
  KEY `idx_pitem_purchase` (`purchase_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采购明细';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_item_zj`
--

LOCK TABLES `purchase_item_zj` WRITE;
/*!40000 ALTER TABLE `purchase_item_zj` DISABLE KEYS */;
INSERT INTO `purchase_item_zj` VALUES (13,7,NULL,'o型圈','75*90',100,10.00,1000.00),(14,7,NULL,'油封','9*9',50,20.00,1000.00),(15,8,NULL,'O??','10mm',100,0.00,0.00),(16,9,NULL,'O??','10mm',100,0.00,0.00),(17,10,NULL,'O??','10mm',100,2.50,250.00);
/*!40000 ALTER TABLE `purchase_item_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_zj`
--

DROP TABLE IF EXISTS `purchase_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no_zj` varchar(32) NOT NULL COMMENT '采购单号',
  `factory_id_zj` bigint NOT NULL COMMENT '厂家ID',
  `total_amount_zj` decimal(10,2) NOT NULL COMMENT '采购总额',
  `status_zj` varchar(20) NOT NULL DEFAULT 'pending' COMMENT 'pending/quoted/paid/shipped/received/cancelled',
  `expected_delivery_date_zj` date DEFAULT NULL COMMENT '预计交货日期',
  `express_company_zj` varchar(50) DEFAULT NULL COMMENT '物流公司',
  `tracking_no_zj` varchar(50) DEFAULT NULL COMMENT '物流单号',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at_zj` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id_zj`),
  UNIQUE KEY `uk_purchase_no` (`order_no_zj`),
  KEY `idx_purchase_factory` (`factory_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='采购单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_zj`
--

LOCK TABLES `purchase_zj` WRITE;
/*!40000 ALTER TABLE `purchase_zj` DISABLE KEYS */;
INSERT INTO `purchase_zj` VALUES (7,'PUR20260626093844999198',2,2000.00,'pending',NULL,NULL,NULL,'2026-06-26 09:38:45',NULL),(8,'PUR20260626100059449986',2,0.00,'pending',NULL,NULL,NULL,'2026-06-26 10:00:59',NULL),(9,'PUR20260626100349594229',2,0.00,'pending',NULL,NULL,NULL,'2026-06-26 10:03:50',NULL),(10,'PUR20260626100400195947',2,250.00,'received',NULL,'顺丰','SF123456','2026-06-26 10:04:00','2026-06-26 10:09:30');
/*!40000 ALTER TABLE `purchase_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receipt_zj`
--

DROP TABLE IF EXISTS `receipt_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receipt_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `receivable_id_zj` bigint NOT NULL COMMENT '应收款ID',
  `amount_zj` decimal(10,2) NOT NULL COMMENT '收款金额',
  `payment_method_zj` enum('balance','cash','bank_transfer') NOT NULL COMMENT '收款方式',
  `operator_id_zj` bigint NOT NULL COMMENT '操作人ID',
  `remark_zj` varchar(200) DEFAULT NULL COMMENT '备注',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收款时间',
  PRIMARY KEY (`id_zj`),
  KEY `idx_receipt_receivable` (`receivable_id_zj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收款记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receipt_zj`
--

LOCK TABLES `receipt_zj` WRITE;
/*!40000 ALTER TABLE `receipt_zj` DISABLE KEYS */;
/*!40000 ALTER TABLE `receipt_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receivable_zj`
--

DROP TABLE IF EXISTS `receivable_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receivable_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id_zj` bigint NOT NULL COMMENT '订单ID',
  `user_id_zj` bigint NOT NULL COMMENT '欠款顾客ID',
  `amount_owed_zj` decimal(10,2) NOT NULL COMMENT '应收总额',
  `amount_paid_zj` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已还金额',
  `status_zj` enum('unpaid','partially_paid','paid','void') NOT NULL DEFAULT 'unpaid' COMMENT '状态',
  `due_date_zj` date DEFAULT NULL COMMENT '到期日',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at_zj` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id_zj`),
  KEY `idx_receivable_user` (`user_id_zj`),
  KEY `idx_receivable_order` (`order_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='应收账款';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receivable_zj`
--

LOCK TABLES `receivable_zj` WRITE;
/*!40000 ALTER TABLE `receivable_zj` DISABLE KEYS */;
/*!40000 ALTER TABLE `receivable_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_log_zj`
--

DROP TABLE IF EXISTS `stock_log_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_log_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id_zj` bigint NOT NULL COMMENT '商品ID',
  `change_quantity_zj` int NOT NULL COMMENT '变动数量（正为入，负为出）',
  `current_stock_zj` int NOT NULL COMMENT '变动后库存快照',
  `type_zj` enum('purchase_in','sale_out','manual_in','manual_out','refund_in') NOT NULL COMMENT '变动类型',
  `reference_id_zj` bigint DEFAULT NULL COMMENT '关联采购单/订单ID',
  `remark_zj` varchar(200) DEFAULT NULL COMMENT '备注',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id_zj`),
  KEY `idx_stock_product` (`product_id_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='库存出入库流水';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_log_zj`
--

LOCK TABLES `stock_log_zj` WRITE;
/*!40000 ALTER TABLE `stock_log_zj` DISABLE KEYS */;
INSERT INTO `stock_log_zj` VALUES (28,1,-5,495,'sale_out',14,'订单销售出库','2026-06-25 00:15:28'),(29,1,5,500,'refund_in',14,'订单退款入库','2026-06-25 00:15:40'),(31,21,50,150,'manual_in',NULL,'测试入库','2026-06-25 17:29:08'),(32,21,-10,140,'manual_out',NULL,'测试出库','2026-06-25 17:29:08'),(33,21,-2,138,'sale_out',16,'订单销售出库','2026-06-25 17:29:09'),(34,23,-1,4,'sale_out',18,'订单销售出库','2026-06-25 17:30:01'),(35,23,1,5,'refund_in',18,'订单退款入库','2026-06-25 17:30:01'),(36,24,5,205,'manual_in',NULL,'前端测试','2026-06-25 17:36:37'),(40,24,-2,203,'sale_out',22,'订单销售出库','2026-06-26 08:56:16'),(41,1,-1,499,'sale_out',23,'订单销售出库','2026-06-26 08:56:30'),(42,1,-1,498,'sale_out',24,'订单销售出库','2026-06-26 08:57:50'),(43,1,-1,497,'sale_out',25,'订单销售出库','2026-06-26 08:58:17'),(44,1,-1,496,'sale_out',26,'订单销售出库','2026-06-26 08:58:44'),(45,1,-5,491,'sale_out',27,'订单销售出库','2026-06-26 10:25:30'),(46,23,-5,0,'sale_out',27,'订单销售出库','2026-06-26 10:25:30'),(47,24,-6,197,'sale_out',28,'订单销售出库','2026-06-26 10:38:06'),(48,21,-5,133,'sale_out',28,'订单销售出库','2026-06-26 10:38:06'),(49,24,6,203,'refund_in',28,'订单退款入库','2026-06-26 10:39:07'),(50,21,5,138,'refund_in',28,'订单退款入库','2026-06-26 10:39:07');
/*!40000 ALTER TABLE `stock_log_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config_zj`
--

DROP TABLE IF EXISTS `sys_config_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_key_zj` varchar(50) NOT NULL COMMENT '唯一键',
  `config_value_zj` varchar(255) DEFAULT NULL COMMENT '配置值',
  `remark_zj` varchar(200) DEFAULT NULL COMMENT '说明',
  `updated_at_zj` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id_zj`),
  UNIQUE KEY `uk_config_key` (`config_key_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config_zj`
--

LOCK TABLES `sys_config_zj` WRITE;
/*!40000 ALTER TABLE `sys_config_zj` DISABLE KEYS */;
INSERT INTO `sys_config_zj` VALUES (1,'site_name','?????V2','????','2026-06-25 17:36:21'),(2,'auto_complete_days','7','自动完成天数',NULL);
/*!40000 ALTER TABLE `sys_config_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_zj`
--

DROP TABLE IF EXISTS `user_zj`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_zj` (
  `id_zj` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone_zj` varchar(20) NOT NULL COMMENT '手机号（登录账号）',
  `password_zj` varchar(128) NOT NULL COMMENT 'BCrypt加密密码',
  `role_zj` enum('customer','merchant','factory') NOT NULL DEFAULT 'customer' COMMENT '角色',
  `real_name_zj` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `avatar_zj` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `balance_zj` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
  `points_zj` int DEFAULT '0' COMMENT '积分（预留）',
  `credit_limit_zj` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '赊账额度',
  `company_name_zj` varchar(100) DEFAULT NULL COMMENT '厂家公司名',
  `default_receiver_name_zj` varchar(50) DEFAULT NULL COMMENT '默认收货人',
  `default_receiver_phone_zj` varchar(20) DEFAULT NULL COMMENT '默认收货电话',
  `default_province_zj` varchar(20) DEFAULT NULL COMMENT '省',
  `default_city_zj` varchar(20) DEFAULT NULL COMMENT '市',
  `default_district_zj` varchar(20) DEFAULT NULL COMMENT '区',
  `default_detail_address_zj` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `status_zj` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1-正常 0-禁用',
  `created_at_zj` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `updated_at_zj` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `mch_province_zj` varchar(50) DEFAULT NULL,
  `mch_city_zj` varchar(50) DEFAULT NULL,
  `mch_district_zj` varchar(50) DEFAULT NULL,
  `mch_detail_address_zj` varchar(200) DEFAULT NULL,
  `mch_receiver_name_zj` varchar(50) DEFAULT NULL,
  `mch_receiver_phone_zj` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id_zj`),
  UNIQUE KEY `uk_phone_zj` (`phone_zj`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_zj`
--

LOCK TABLES `user_zj` WRITE;
/*!40000 ALTER TABLE `user_zj` DISABLE KEYS */;
INSERT INTO `user_zj` VALUES (1,'18973619103','123456','merchant','中汇鑫',NULL,200.00,0,0.00,NULL,'张三','13800000001','广东省','广州市','天河区','体育西路100号',1,'2026-06-08 23:42:57','2026-06-26 08:54:58','???','???','???','???101????','???','18973619103'),(2,'13786635062','123456','factory',NULL,NULL,0.00,0,500.00,'正佳檡胶厂',NULL,NULL,NULL,NULL,NULL,NULL,1,'2026-06-08 23:42:57','2026-06-25 09:14:22',NULL,NULL,NULL,NULL,NULL,NULL),(13,'18942072228','123456','customer','zj','/uploads/d766b57d-e606-438c-bc3a-66cf27f82d03.jpg',1593.00,7,0.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,'2026-06-25 01:08:09','2026-06-26 10:39:07',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `user_zj` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'db_rubber_shop_zj_2024'
--

--
-- Dumping routines for database 'db_rubber_shop_zj_2024'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-06 15:39:53
