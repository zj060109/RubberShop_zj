-- ============================================
-- V13 迁移脚本：在 Navicat 直接复制粘贴运行
-- 安全添加字段（已存在则跳过）
-- 不影响 user_zj
-- ============================================

-- === 1. product_zj 加字段 ===

SET @cnt = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_zj' AND COLUMN_NAME = 'spec_zj');
SET @s = IF(@cnt = 0, 'ALTER TABLE product_zj ADD COLUMN spec_zj VARCHAR(100) DEFAULT NULL COMMENT ''尺寸规格'' AFTER name_zj', 'SELECT 1');
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @cnt = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_zj' AND COLUMN_NAME = 'brand_zj');
SET @s = IF(@cnt = 0, 'ALTER TABLE product_zj ADD COLUMN brand_zj VARCHAR(50) DEFAULT NULL COMMENT ''品牌'' AFTER spec_zj', 'SELECT 1');
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @cnt = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_zj' AND COLUMN_NAME = 'model_zj');
SET @s = IF(@cnt = 0, 'ALTER TABLE product_zj ADD COLUMN model_zj VARCHAR(100) DEFAULT NULL COMMENT ''型号'' AFTER brand_zj', 'SELECT 1');
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @cnt = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product_zj' AND COLUMN_NAME = 'material_zj');
SET @s = IF(@cnt = 0, 'ALTER TABLE product_zj ADD COLUMN material_zj VARCHAR(50) DEFAULT NULL COMMENT ''材质'' AFTER model_zj', 'SELECT 1');
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE product_zj MODIFY COLUMN name_zj VARCHAR(200) NOT NULL COMMENT '商品名称（自动生成：分类+品牌+型号+材质）';

-- === 2. category_zj 重建分类层级 ===

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM category_zj;
ALTER TABLE category_zj AUTO_INCREMENT = 1;

INSERT INTO category_zj (id_zj, name_zj, parent_id_zj, sort_zj, created_at_zj) VALUES
(1,  '橡胶产品', 0, 1, NOW()),
(10, '油封',   1, 1, NOW()),
(11, '骨架油封', 10, 1, NOW()),
(12, '液压油封', 10, 2, NOW()),
(13, '气门油封', 10, 3, NOW()),
(14, '旋转油封', 10, 4, NOW()),
(20, '密封',   1, 2, NOW()),
(21, '活塞杆密封', 20, 1, NOW()),
(22, '活塞密封',   20, 2, NOW()),
(23, '旋转密封',   20, 3, NOW()),
(24, '防尘密封',   20, 4, NOW()),
(25, '导向环',     20, 5, NOW()),
(26, '支撑环',     20, 6, NOW()),
(30, '管', 1, 3, NOW()),
(31, '条', 1, 4, NOW()),
(32, '棒', 1, 5, NOW()),
(33, '板', 1, 6, NOW());
SET FOREIGN_KEY_CHECKS = 1;

-- === 3. product_zj 替换示例商品 ===

-- 只删除之前的 seed 商品（id <= 100）
DELETE FROM product_zj WHERE id_zj <= 100;

INSERT INTO product_zj (category_id_zj, name_zj, spec_zj, brand_zj, model_zj, material_zj, description_zj, images_zj, price_zj, stock_zj, warning_stock_zj, status_zj, created_at_zj) VALUES
(11, '骨架油封 NAK TC-25-40-7 NBR 25x40x7',     '25x40x7',  'NAK','TC-25-40-7','NBR','高品质骨架油封，适用于旋转轴密封','["/uploads/seal1.jpg","/uploads/seal2.jpg"]',12.50,200,20,'on',NOW()),
(11, '骨架油封 CFW BA-30-47-7 FKM 30x47x7',     '30x47x7',  'CFW','BA-30-47-7','FKM','氟橡胶骨架油封，耐高温耐油','["/uploads/seal3.jpg"]',35.00,150,15,'on',NOW()),
(12, '液压油封 NOK USI-40-55-9 PU 40x55x9',     '40x55x9',  'NOK','USI-40-55-9','PU','聚氨酯液压油封，高压工况适用','["/uploads/seal4.jpg"]',28.00,100,10,'on',NOW()),
(21, '活塞杆密封 Parker B3-80-95-12 PU 80x95x12','80x95x12','Parker','B3-80-95-12','PU','活塞杆用密封件，低摩擦高性能','["/uploads/seal5.jpg"]',45.00,80,10,'on',NOW()),
(21, '活塞杆密封 鼎基 GS-50-60-7 NBR 50x60x7', '50x60x7',  '鼎基','GS-50-60-7','NBR','经济型活塞杆密封，性价比之选','["/uploads/seal6.jpg"]',8.80,500,50,'on',NOW()),
(22, '活塞密封 NOK ODU-100-84-18 PU 100x84x18', '100x84x18','NOK','ODU-100-84-18','PU','双作用活塞密封，长寿命设计','["/uploads/seal7.jpg"]',55.00,60,10,'on',NOW()),
(24, '防尘密封 SKF DA-25-33-7 PU 25x33x7',      '25x33x7',  'SKF','DA-25-33-7','PU','优质防尘圈，防护等级IP65','["/uploads/seal8.jpg"]',6.00,300,30,'on',NOW());

ALTER TABLE product_zj AUTO_INCREMENT = 101;
