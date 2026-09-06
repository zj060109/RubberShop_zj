# RubberShop_zj — 橡胶进销存管理系统

橡胶实体店进销存管理系统，三端架构：Spring Boot 后端 + Vue 3 管理后台 + Android 客户端。

## 项目结构

```
RubberShop_zj/
├── shop-server/          # 后端 (Spring Boot 3 + MyBatis-Plus + MySQL)
│   └── src/main/resources/db/
│       ├── init.sql                      # 全新部署数据库初始化脚本（含建表+种子数据）
│       ├── migration_v13_run.sql         # 已有数据库升级脚本（V13：品牌/型号/材质+分类层级）
│       └── backup_rubber_shop_full.sql   # 数据库完整备份（2026-09，含全部真实数据）
├── shop-admin/           # Web 管理后台 (Vue 3 + Element Plus + ECharts)
└── android/              # Android 客户端 (Java + Retrofit + Navigation)
```

## 技术栈

| 端 | 技术 | 关键版本 |
|----|------|---------|
| 后端 | Spring Boot 3 / MyBatis-Plus / JWT / WebSocket | JDK 17, Maven 3.9.6 |
| 前端 | Vue 3 / Element Plus / ECharts / Axios | Node.js 18+ |
| Android | Java / Retrofit 2.9 / OkHttp / Glide / Navigation | AGP 8.13.2, Gradle 8.13, compileSdk 36, minSdk 24 |

## 数据库

- 库名：`db_rubber_shop_zj_2024`（MySQL 8.0，账号 root / 密码 060109）
- 重装后恢复：`mysql -uroot -p060109 < backup_rubber_shop_full.sql`
- 全新初始化：`mysql -uroot -p060109 < init.sql`

## 启动方式

### 后端（端口 8080）
```bash
mvn clean package -DskipTests
java -jar target/shop-server-*.jar
```

### Web 管理后台（开发）
```bash
npm install
npm run dev
# 构建生产：npm run build
```

### Android
- Android Studio 打开 `android/` 目录，Clean → Rebuild
- 后端地址：`android/app/src/main/java/com/rubbershop/app/data/api/RetrofitClient.java` 中的 `BASE_URL`
- 图片地址：`android/app/src/main/java/com/rubbershop/app/util/Utils.java` 中的 `BASE_URL`

## 测试账号（密码 123456）

| 角色 | 手机号 |
|------|--------|
| 商户 | 18973619103 |
| 厂家 | 13786635062 |
| 顾客 | 18942072228 |

## 商品模型（V13）

- 三级分类：橡胶产品 → 油封/密封/管/条/棒/板 → 骨架油封/液压油封/活塞杆密封等小类
- 商品参数：品牌 + 型号 + 材质 + 规格（尺寸）
- 商品名自动生成：`分类名 品牌 型号 材质 规格`
