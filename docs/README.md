# 音响店 VCD 零售出租管理系统

> 数据库课程设计项目 | Spring Boot 3.5.0 + Vue 3 + SQL Server

---

## 项目简介

本系统面向音响店日常经营，实现 VCD 碟片的进货、出租、销售、归还全流程数字化管理，提供首页数据看板直观展示经营状况，通过 JWT 认证保障系统安全。

---

## 技术栈

| 端 | 技术 |
|----|------|
| 后端 | Spring Boot 3.5.0、Spring Security、Spring Data JPA、jjwt 0.11.5 |
| 数据库 | SQL Server 2019/2022 |
| 前端 | Vue 3、Element Plus、Axios、Vite |
| 运行环境 | JDK 17、Maven 3.6+ |
| 容器化 | Docker、Docker Compose |

---

## 项目结构

```
vcd-backend/          后端 Spring Boot 项目
├── pom.xml
├── src/main/
│   ├── java/top/tatobamail/backend/
│   │   ├── BackendApplication.java
│   │   ├── config/        安全配置、JWT过滤器
│   │   ├── User/          用户模块
│   │   ├── Vcd/           VCD模块
│   │   ├── VcdCategories/ 分类模块
│   │   ├── VcdInventory/  库存模块
│   │   ├── customers/     客户模块
│   │   ├── purchaseRecords/ 采购记录
│   │   ├── RentalRecords/ 租赁记录
│   │   ├── ReturnRecords/ 归还记录
│   │   ├── SalesRecords/  销售记录
│   │   └── util/          JWT工具类
│   └── resources/
│       ├── application.properties
│       └── dist/          前端打包文件

vcd-frontend/         前端 Vue3 项目
├── login.html
├── manage.html
└── src/
    ├── views/         各功能页面
    ├── api/           接口封装
    └── router/        路由配置

docs/                 项目文档
├── requirements.md   需求文档
├── design.md         设计文档
├── test.md           测试文档
└── README.md         本文件
```

---

## 快速启动

### 环境要求

- JDK 17
- Maven 3.6+
- SQL Server 2019/2022（端口 1433，用户名 sa，密码 123456）
- Node.js 18+（仅前端开发时需要）

### 第一步：准备数据库

在 SSMS 中执行：

```sql
-- 如果数据库不存在则创建
CREATE DATABASE vcddb;
```

然后执行项目根目录下的 `script.sql` 导入表结构和初始数据。

### 第二步：构建前端（可选，dist 已内置）

```bash
cd vcd-frontend
npm install
npm run build
# 构建产物自动输出到 vcd-backend/src/main/resources/dist/
```

### 第三步：启动后端

**方式一：IDEA 运行**
1. 用 IDEA 打开 `vcd-backend` 文件夹（选择 pom.xml 作为项目）
2. 等待 Maven 下载依赖
3. 右键 `BackendApplication.java` → Run

**方式二：命令行**
```bash
cd vcd-backend
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 第四步：访问系统

浏览器打开：`http://localhost:8080/login.html`

### 第五步：注册账号并登录

首次使用需注册账号（数据库中的初始用户密码为 BCrypt 加密，原始密码未知）：

```bash
POST http://localhost:8080/api/users/register
Content-Type: application/json

{"username": "admin", "password": "123456"}
```

然后用 `admin` / `123456` 登录。

---

## 功能模块

| 菜单 | 功能说明 |
|------|---------|
| 首页 | 今日销售额、租赁收入、库存总量、逾期数量、热门排行、待归还列表 |
| VCD 管理 | 新增/编辑/删除/搜索 VCD，关联分类 |
| 分类管理 | 管理 VCD 分类（动作片、喜剧片等 20 种） |
| 库存管理 | 查看/修改各 VCD 库存，可用库存实时计算 |
| 客户管理 | 新增/编辑/删除/搜索客户信息 |
| 采购记录 | 新增采购，自动增加对应 VCD 库存 |
| 租赁记录 | 新增租赁，自动减少可用库存 |
| 归还记录 | 新增归还，自动恢复库存，计算逾期费用 |
| 销售记录 | 新增销售，检查库存后自动减少库存 |
| 用户管理 | 新增/删除系统用户，修改密码 |

---

## 常见问题

**Q: 启动报错 "Failed to configure a DataSource"**
A: `application.properties` 未被识别。确认用 IDEA 打开的是 `pom.xml` 而非文件夹，或检查 `src/main/resources/` 目录下是否存在 `application.properties`。

**Q: 数据库连接失败**
A: 检查 SQL Server 服务是否启动，TCP/IP 协议是否启用，端口是否为 1433，sa 密码是否为 123456。

**Q: 操作提示"VCD库存不存在"**
A: 新增 VCD 后，需先在「库存管理」中为该 VCD 创建库存记录，才能进行采购/租赁/销售操作。

**Q: 端口 8080 被占用**
A: 在 cmd 执行 `netstat -ano | findstr :8080` 找到占用进程，用任务管理器结束该进程。

---

## 数据库说明

共 9 张表，外键关系如下：

```
vcd_categories → vcd → vcd_inventory
                  ↓
         purchase_records
         rental_records → return_records
         sales_records
                  ↓
              customers
```

---

## 文档索引

- [需求文档](requirements.md)
- [设计文档](design.md)
- [测试文档](test.md)
- [Docker 部署指南](docker-guide.md)

---

## Docker 部署（推荐）

### 环境要求

- Docker Desktop（Windows/Mac）或 Docker Engine（Linux）
- 无需单独安装 JDK、Maven、SQL Server

### 一键启动

```bash
# 在项目根目录执行
docker-compose up -d --build
```

首次执行会自动完成：
1. 拉取 SQL Server 2022 镜像
2. 用 Maven 多阶段构建编译后端 jar 包
3. 启动数据库容器，等待健康检查通过
4. 启动后端容器，连接数据库

### 访问系统

```
http://localhost:8080/login.html
```

### 常用命令

```bash
# 查看运行状态
docker-compose ps

# 查看后端实时日志
docker-compose logs -f backend

# 查看数据库日志
docker-compose logs -f sqlserver

# 停止服务（保留数据）
docker-compose down

# 停止并清空数据库数据
docker-compose down -v

# 重新构建后端镜像
docker-compose up -d --build backend
```

### 容器架构

```
docker-compose
  ├── vcd-sqlserver（SQL Server 2022）
  │     端口 1433，数据卷持久化
  │
  └── vcd-backend（Spring Boot）
        端口 8080
        等待 sqlserver 健康检查通过后启动
```
