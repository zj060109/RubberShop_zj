-- ============================================
-- 橡胶实体店进销存管理系统 数据库初始化脚本
-- 数据库：db_rubber_shop_zj_2024
-- 版本：V12.0 最终定稿
-- ============================================

CREATE DATABASE IF NOT EXISTS db_rubber_shop_zj_2024 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_rubber_shop_zj_2024;

-- ----------------------------
-- 1. user_zj 用户表
-- ----------------------------
DROP TABLE IF EXISTS user_zj;
CREATE TABLE user_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    phone_zj VARCHAR(20) NOT NULL COMMENT '手机号（登录账号）',
    password_zj VARCHAR(128) NOT NULL COMMENT '登录密码',
    role_zj ENUM('customer','merchant','factory') NOT NULL DEFAULT 'customer' COMMENT '角色',
    real_name_zj VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    avatar_zj VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    balance_zj DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    points_zj INT(11) DEFAULT 0 COMMENT '积分（10分解锁赊账）',
    credit_limit_zj DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '赊账额度（系统自动授予）',
    company_name_zj VARCHAR(100) DEFAULT NULL COMMENT '厂家公司名',
    default_receiver_name_zj VARCHAR(50) DEFAULT NULL COMMENT '默认收货人',
    default_receiver_phone_zj VARCHAR(20) DEFAULT NULL COMMENT '默认收货电话',
    default_province_zj VARCHAR(20) DEFAULT NULL COMMENT '省',
    default_city_zj VARCHAR(20) DEFAULT NULL COMMENT '市',
    default_district_zj VARCHAR(20) DEFAULT NULL COMMENT '区',
    default_detail_address_zj VARCHAR(200) DEFAULT NULL COMMENT '详细地址',
    status_zj TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1-正常 0-禁用',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at_zj DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id_zj),
    UNIQUE KEY uk_phone_zj (phone_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. identity_verification_zj 实名认证表
-- ----------------------------
DROP TABLE IF EXISTS identity_verification_zj;
CREATE TABLE identity_verification_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id_zj BIGINT(20) NOT NULL COMMENT '用户ID',
    id_card_zj VARCHAR(18) NOT NULL COMMENT '身份证号',
    real_name_zj VARCHAR(50) NOT NULL COMMENT '真实姓名',
    face_image_zj VARCHAR(500) DEFAULT NULL COMMENT '人脸照片URL',
    status_zj TINYINT(1) NOT NULL DEFAULT 1 COMMENT '1-已认证 0-已失效',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '认证时间',
    updated_at_zj DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id_zj),
    UNIQUE KEY uk_verify_user (user_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实名认证表';

-- ----------------------------
-- 3. category_zj 商品分类表
-- ----------------------------
DROP TABLE IF EXISTS category_zj;
CREATE TABLE category_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    name_zj VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id_zj BIGINT(20) NOT NULL DEFAULT 0 COMMENT '父级ID，0为顶级',
    sort_zj INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
    icon_zj VARCHAR(255) DEFAULT NULL COMMENT '图标URL',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id_zj),
    KEY idx_category_parent (parent_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- 3. product_zj 商品表
-- ----------------------------
DROP TABLE IF EXISTS product_zj;
CREATE TABLE product_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    category_id_zj BIGINT(20) NOT NULL COMMENT '分类ID',
    name_zj VARCHAR(200) NOT NULL COMMENT '商品名称（自动生成：分类+品牌+型号+材质）',
    spec_zj VARCHAR(100) DEFAULT NULL COMMENT '尺寸规格',
    brand_zj VARCHAR(50) DEFAULT NULL COMMENT '品牌',
    model_zj VARCHAR(100) DEFAULT NULL COMMENT '型号',
    material_zj VARCHAR(50) DEFAULT NULL COMMENT '材质',
    description_zj TEXT COMMENT '描述',
    images_zj JSON COMMENT '图片数组',
    price_zj DECIMAL(10,2) NOT NULL COMMENT '销售价',
    stock_zj INT(11) NOT NULL DEFAULT 0 COMMENT '库存数量',
    warning_stock_zj INT(11) NOT NULL DEFAULT 10 COMMENT '库存预警阈值',
    status_zj ENUM('on','off') NOT NULL DEFAULT 'on' COMMENT '上架/下架',
    factory_id_zj BIGINT(20) DEFAULT NULL COMMENT '关联厂家ID',
    is_customized_zj TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否来自定制 1-是 0-否',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at_zj DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id_zj),
    KEY idx_product_category (category_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 4. order_zj 订单主表
-- ----------------------------
DROP TABLE IF EXISTS order_zj;
CREATE TABLE order_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no_zj VARCHAR(32) NOT NULL COMMENT '订单号',
    user_id_zj BIGINT(20) NOT NULL COMMENT '顾客ID',
    total_amount_zj DECIMAL(10,2) NOT NULL COMMENT '商品总额',
    actual_amount_zj DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    payment_method_zj ENUM('balance','credit') NOT NULL COMMENT '支付方式',
    status_zj ENUM('paid','accepted','shipped','completed','cancelled','refunding','refunded') NOT NULL DEFAULT 'paid' COMMENT '订单状态',
    receiver_name_zj VARCHAR(50) DEFAULT NULL COMMENT '收货人快照',
    receiver_phone_zj VARCHAR(20) DEFAULT NULL COMMENT '收货电话快照',
    province_zj VARCHAR(20) DEFAULT NULL COMMENT '省',
    city_zj VARCHAR(20) DEFAULT NULL COMMENT '市',
    district_zj VARCHAR(20) DEFAULT NULL COMMENT '区',
    detail_address_zj VARCHAR(200) DEFAULT NULL COMMENT '详细地址',
    express_company_zj VARCHAR(50) DEFAULT NULL COMMENT '快递公司',
    tracking_no_zj VARCHAR(50) DEFAULT NULL COMMENT '快递单号',
    mark_zj VARCHAR(200) DEFAULT NULL COMMENT '备注',
    paid_at_zj DATETIME DEFAULT NULL COMMENT '支付时间',
    shipped_at_zj DATETIME DEFAULT NULL COMMENT '发货时间',
    finished_at_zj DATETIME DEFAULT NULL COMMENT '完成时间',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    updated_at_zj DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id_zj),
    UNIQUE KEY uk_order_no_zj (order_no_zj),
    KEY idx_order_user (user_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- ----------------------------
-- 5. order_item_zj 订单明细表
-- ----------------------------
DROP TABLE IF EXISTS order_item_zj;
CREATE TABLE order_item_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id_zj BIGINT(20) NOT NULL COMMENT '订单ID',
    product_id_zj BIGINT(20) NOT NULL COMMENT '商品ID',
    product_name_zj VARCHAR(100) NOT NULL COMMENT '商品名称快照',
    product_image_zj VARCHAR(255) DEFAULT NULL COMMENT '商品主图快照',
    price_zj DECIMAL(10,2) NOT NULL COMMENT '下单时单价',
    quantity_zj INT(11) NOT NULL COMMENT '数量',
    subtotal_zj DECIMAL(10,2) NOT NULL COMMENT '小计',
    PRIMARY KEY (id_zj),
    KEY idx_item_order (order_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ----------------------------
-- 6. order_status_log_zj 订单状态变更日志
-- ----------------------------
DROP TABLE IF EXISTS order_status_log_zj;
CREATE TABLE order_status_log_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id_zj BIGINT(20) NOT NULL COMMENT '订单ID',
    from_status_zj VARCHAR(20) DEFAULT NULL COMMENT '原状态',
    to_status_zj VARCHAR(20) NOT NULL COMMENT '新状态',
    operator_id_zj BIGINT(20) DEFAULT NULL COMMENT '操作人ID',
    remark_zj TEXT COMMENT '备注',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id_zj),
    KEY idx_log_order (order_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态变更日志';

-- ----------------------------
-- 7. customization_zj 定制主表
-- ----------------------------
DROP TABLE IF EXISTS customization_zj;
CREATE TABLE customization_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id_zj BIGINT(20) NOT NULL COMMENT '顾客ID',
    status_zj ENUM('pending_quote','quoted','confirmed','converted','cancelled') NOT NULL DEFAULT 'pending_quote' COMMENT '定制状态',
    description_zj TEXT COMMENT '需求描述',
    reference_images_zj JSON COMMENT '参考图片数组',
    total_quoted_price_zj DECIMAL(10,2) DEFAULT NULL COMMENT '商家总报价',
    order_id_zj BIGINT(20) DEFAULT NULL COMMENT '确认后生成的订单ID',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at_zj DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id_zj),
    KEY idx_custom_user (user_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定制主表';

-- ----------------------------
-- 8. customization_item_zj 定制报价明细
-- ----------------------------
DROP TABLE IF EXISTS customization_item_zj;
CREATE TABLE customization_item_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    customization_id_zj BIGINT(20) NOT NULL COMMENT '定制ID',
    product_spec_zj VARCHAR(200) NOT NULL COMMENT '规格描述',
    quantity_zj INT(11) NOT NULL COMMENT '数量',
    unit_price_zj DECIMAL(10,2) NOT NULL COMMENT '商家报价单价',
    PRIMARY KEY (id_zj),
    KEY idx_citem_custom (customization_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定制报价明细';

-- ----------------------------
-- 9. purchase_zj 采购单
-- ----------------------------
DROP TABLE IF EXISTS purchase_zj;
CREATE TABLE purchase_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no_zj VARCHAR(32) NOT NULL COMMENT '采购单号',
    factory_id_zj BIGINT(20) NOT NULL COMMENT '厂家ID',
    total_amount_zj DECIMAL(10,2) NOT NULL COMMENT '采购总额',
    status_zj VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '采购状态(pending/quoted/paid/shipped/received/cancelled)',
    expected_delivery_date_zj DATE DEFAULT NULL COMMENT '预计交货日期',
    express_company_zj VARCHAR(50) DEFAULT NULL COMMENT '物流公司',
    tracking_no_zj VARCHAR(50) DEFAULT NULL COMMENT '物流单号',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at_zj DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id_zj),
    UNIQUE KEY uk_purchase_no (order_no_zj),
    KEY idx_purchase_factory (factory_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单';

-- ----------------------------
-- 10. purchase_item_zj 采购明细
-- ----------------------------
DROP TABLE IF EXISTS purchase_item_zj;
CREATE TABLE purchase_item_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    purchase_id_zj BIGINT(20) NOT NULL COMMENT '采购单ID',
    product_id_zj BIGINT(20) DEFAULT NULL COMMENT '关联商品（可为空）',
    product_name_zj VARCHAR(100) NOT NULL COMMENT '商品名称',
    spec_zj VARCHAR(200) DEFAULT NULL COMMENT '规格',
    quantity_zj INT(11) NOT NULL COMMENT '数量',
    unit_price_zj DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal_zj DECIMAL(10,2) NOT NULL COMMENT '小计',
    PRIMARY KEY (id_zj),
    KEY idx_pitem_purchase (purchase_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购明细';

-- ----------------------------
-- 11. receivable_zj 应收账款（赊账）
-- ----------------------------
DROP TABLE IF EXISTS receivable_zj;
CREATE TABLE receivable_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id_zj BIGINT(20) NOT NULL COMMENT '订单ID',
    user_id_zj BIGINT(20) NOT NULL COMMENT '欠款顾客ID',
    amount_owed_zj DECIMAL(10,2) NOT NULL COMMENT '应收总额',
    amount_paid_zj DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '已还金额',
    status_zj ENUM('unpaid','partially_paid','paid','void') NOT NULL DEFAULT 'unpaid' COMMENT '状态',
    due_date_zj DATE DEFAULT NULL COMMENT '到期日',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at_zj DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id_zj),
    KEY idx_receivable_user (user_id_zj),
    KEY idx_receivable_order (order_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应收账款';

-- ----------------------------
-- 12. receipt_zj 收款记录
-- ----------------------------
DROP TABLE IF EXISTS receipt_zj;
CREATE TABLE receipt_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    receivable_id_zj BIGINT(20) NOT NULL COMMENT '应收款ID',
    amount_zj DECIMAL(10,2) NOT NULL COMMENT '收款金额',
    payment_method_zj ENUM('balance','cash','bank_transfer') NOT NULL COMMENT '收款方式',
    operator_id_zj BIGINT(20) NOT NULL COMMENT '操作人ID',
    remark_zj VARCHAR(200) DEFAULT NULL COMMENT '备注',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收款时间',
    PRIMARY KEY (id_zj),
    KEY idx_receipt_receivable (receivable_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收款记录';

-- ----------------------------
-- 13. stock_log_zj 库存出入库流水
-- ----------------------------
DROP TABLE IF EXISTS stock_log_zj;
CREATE TABLE stock_log_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    product_id_zj BIGINT(20) NOT NULL COMMENT '商品ID',
    change_quantity_zj INT(11) NOT NULL COMMENT '变动数量（正为入，负为出）',
    current_stock_zj INT(11) NOT NULL COMMENT '变动后库存快照',
    type_zj ENUM('purchase_in','sale_out','manual_in','manual_out','refund_in') NOT NULL COMMENT '变动类型',
    reference_id_zj BIGINT(20) DEFAULT NULL COMMENT '关联采购单/订单ID',
    remark_zj VARCHAR(200) DEFAULT NULL COMMENT '备注',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id_zj),
    KEY idx_stock_product (product_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存出入库流水';

-- ----------------------------
-- 14. balance_log_zj 余额变动流水
-- ----------------------------
DROP TABLE IF EXISTS balance_log_zj;
CREATE TABLE balance_log_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id_zj BIGINT(20) NOT NULL COMMENT '用户ID',
    change_amount_zj DECIMAL(10,2) NOT NULL COMMENT '变动金额（正为增，负为减）',
    current_balance_zj DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    type_zj ENUM('recharge','consume','repay','refund','withdraw','admin_adjust') NOT NULL COMMENT '变动类型',
    reference_id_zj BIGINT(20) DEFAULT NULL COMMENT '关联订单/收款ID',
    remark_zj VARCHAR(200) DEFAULT NULL COMMENT '备注',
    created_at_zj DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id_zj),
    KEY idx_balance_user (user_id_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额变动流水';

-- ----------------------------
-- 15. sys_config_zj 系统配置
-- ----------------------------
DROP TABLE IF EXISTS sys_config_zj;
CREATE TABLE sys_config_zj (
    id_zj BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    config_key_zj VARCHAR(50) NOT NULL COMMENT '唯一键',
    config_value_zj VARCHAR(255) DEFAULT NULL COMMENT '配置值',
    remark_zj VARCHAR(200) DEFAULT NULL COMMENT '说明',
    updated_at_zj DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id_zj),
    UNIQUE KEY uk_config_key (config_key_zj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';

-- ============================================
-- 预置数据
-- ============================================

-- 预置商家账号（密码 admin123，明文存储）
INSERT INTO user_zj (phone_zj, password_zj, role_zj, real_name_zj, balance_zj, status_zj, created_at_zj)
VALUES ('13800000000', 'admin123', 'merchant', '系统管理员', 0.00, 1, NOW());

-- 预置厂家账号
INSERT INTO user_zj (phone_zj, password_zj, role_zj, company_name_zj, status_zj, created_at_zj)
VALUES ('13900000001', 'factory123', 'factory', '正佳橡胶厂', 1, NOW());

-- ============================================
-- 商品分类层级
-- 橡胶产品 > 油封 / 密封 / 管 / 条 / 棒 / 板
-- 油封 > 骨架油封·液压油封·气门油封·旋转油封
-- 密封 > 活塞杆密封·活塞密封·旋转密封·防尘密封·导向环·支撑环
-- ============================================
INSERT INTO category_zj (id_zj, name_zj, parent_id_zj, sort_zj, created_at_zj) VALUES
(1,  '橡胶产品', 0, 1, NOW()),
-- 油封
(10, '油封',   1, 1, NOW()),
(11, '骨架油封', 10, 1, NOW()),
(12, '液压油封', 10, 2, NOW()),
(13, '气门油封', 10, 3, NOW()),
(14, '旋转油封', 10, 4, NOW()),
-- 密封
(20, '密封',   1, 2, NOW()),
(21, '活塞杆密封', 20, 1, NOW()),
(22, '活塞密封',   20, 2, NOW()),
(23, '旋转密封',   20, 3, NOW()),
(24, '防尘密封',   20, 4, NOW()),
(25, '导向环',     20, 5, NOW()),
(26, '支撑环',     20, 6, NOW()),
-- 管/条/棒/板（占位）
(30, '管', 1, 3, NOW()),
(31, '条', 1, 4, NOW()),
(32, '棒', 1, 5, NOW()),
(33, '板', 1, 6, NOW());

-- 商品示例
INSERT INTO product_zj (category_id_zj, name_zj, spec_zj, brand_zj, model_zj, material_zj, description_zj, images_zj, price_zj, stock_zj, warning_stock_zj, status_zj, created_at_zj) VALUES
-- 骨架油封
(11, '骨架油封 NAK TC-25-40-7 NBR', '25x40x7', 'NAK', 'TC-25-40-7', 'NBR', '高品质骨架油封，适用于旋转轴密封', '["/uploads/seal1.jpg","/uploads/seal2.jpg"]', 12.50, 200, 20, 'on', NOW()),
(11, '骨架油封 CFW BA-30-47-7 FKM', '30x47x7', 'CFW', 'BA-30-47-7', 'FKM', '氟橡胶骨架油封，耐高温耐油', '["/uploads/seal3.jpg"]', 35.00, 150, 15, 'on', NOW()),
-- 液压油封
(12, '液压油封 NOK USI-40-55-9 PU', '40x55x9', 'NOK', 'USI-40-55-9', 'PU', '聚氨酯液压油封，高压工况适用', '["/uploads/seal4.jpg"]', 28.00, 100, 10, 'on', NOW()),
-- 活塞杆密封
(21, '活塞杆密封 Parker B3-80-95-12 PU', '80x95x12', 'Parker', 'B3-80-95-12', 'PU', '活塞杆用密封件，低摩擦高性能', '["/uploads/seal5.jpg"]', 45.00, 80, 10, 'on', NOW()),
(21, '活塞杆密封 国产 GS-50-60-7 NBR', '50x60x7', '鼎基', 'GS-50-60-7', 'NBR', '经济型活塞杆密封，性价比之选', '["/uploads/seal6.jpg"]', 8.80, 500, 50, 'on', NOW()),
-- 活塞密封
(22, '活塞密封 NOK ODU-100-84-18 PU', '100x84x18', 'NOK', 'ODU-100-84-18', 'PU', '双作用活塞密封，长寿命设计', '["/uploads/seal7.jpg"]', 55.00, 60, 10, 'on', NOW()),
-- 防尘密封
(24, '防尘密封 SKF DA-25-33-7 PU', '25x33x7', 'SKF', 'DA-25-33-7', 'PU', '优质防尘圈，防护等级IP65', '["/uploads/seal8.jpg"]', 6.00, 300, 30, 'on', NOW());

-- 系统配置
INSERT INTO sys_config_zj (config_key_zj, config_value_zj, remark_zj) VALUES
('site_name', '橡胶进销存', '系统名称'),
('auto_complete_days', '7', '自动完成天数');
