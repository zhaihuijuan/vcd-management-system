# 音响店 VCD 零售出租管理系统

> 数据库课程设计项目 | Spring Boot 3.5.0 + Vue 3 + SQL Server + Docker

---

## 项目简介

本系统面向音响店日常经营，实现 VCD 碟片的进货、出租、销售、归还全流程数字化管理，提供首页数据看板直观展示经营状况，通过 JWT 认证保障系统安全访问。

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
项目根目录/
├── docker-compose.yml        Docker 完整容器化编排文件
├── script.sql                数据库建表及初始数据脚本
├── drop_tables.sql           数据库清空脚本
│
├── deploy/                   快速部署包（方式一 Docker 用）
│   ├── backend-0.0.1-SNAPSHOT.jar
│   └── script.sql
│
├── vcd-backend/              后端 Spring Boot 项目
│   ├── Dockerfile            后端镜像构建文件
│   ├── pom.xml
│   └── src/main/
│       ├── java/top/tatobamail/backend/
│       │   ├── config/       安全配置、JWT过滤器
│       │   ├── User/         用户模块
│       │   ├── Vcd/          VCD模块
│       │   ├── VcdCategories/分类模块
│       │   ├── VcdInventory/ 库存模块
│       │   ├── customers/    客户模块
│       │   ├── purchaseRecords/
│       │   ├── RentalRecords/
│       │   ├── ReturnRecords/
│       │   └── SalesRecords/
│       └── resources/
│           ├── application.properties
│           └── dist/         前端打包文件
│
├── vcd-frontend/             前端 Vue3 项目
│   ├── login.html
│   ├── manage.html
│   └── src/
│       ├── views/            各功能页面
│       ├── api/              接口封装
│       └── router/           路由配置
│
└── docs/                     项目文档
    ├── README.md             本文件
    ├── requirements.md       需求文档
    ├── design.md             设计文档
    ├── test.md               测试文档
    └── docker-guide.md       Docker 部署详细指南
```

---

## 三种启动方式

### 方式一：传统本地运行

**环境要求：** JDK 17、Maven 3.6+、SQL Server 2019/2022

```bash
# 1. SSMS 中建库并执行 script.sql
# 2. 启动后端
cd vcd-backend
mvn clean package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
# 3. 访问 http://localhost:8080/login.html
```

---

### 方式二：Docker 仅数据库容器化（轻量）

**环境要求：** JDK 17 + Docker Desktop

只需 `deploy/` 目录下的两个文件即可运行，无需安装 SQL Server。

```bash
# 1. 用 Docker 启动数据库
docker run -d --name vcd-sqlserver ^
  -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=123456" ^
  -p 1433:1433 ^
  mcr.microsoft.com/mssql/server:2022-latest

# 2. 用 SSMS 连接 localhost,1433 执行 deploy/script.sql

# 3. 运行 jar 包
cd deploy
java -jar backend-0.0.1-SNAPSHOT.jar

# 4. 访问 http://localhost:8080/login.html
```

---

### 方式三：Docker Compose 完整容器化（推荐）

**环境要求：** 仅需 Docker Desktop，无需 JDK 和 SQL Server

```bash
# 在项目根目录执行，一键启动所有服务
docker-compose up -d --build

# 查看启动状态
docker-compose ps

# 访问 http://localhost:8080/login.html
```

> 详细步骤见 [Docker 部署指南](docker-guide.md)

---

## 首次登录

任意方式启动后，注册账号：

```
POST http://localhost:8080/api/users/register
{"username": "admin", "password": "123456"}
```

用 `admin` / `123456` 登录。

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
A: 确认用 IDEA 打开的是 `pom.xml` 而非文件夹，`src/main/resources/` 下需有 `application.properties`。

**Q: 数据库连接失败**
A: 确认 SQL Server 服务已启动，TCP/IP 协议已启用，端口 1433，sa 密码正确。

**Q: 操作提示"VCD库存不存在"**
A: 新增 VCD 后需先在「库存管理」中为该 VCD 创建库存记录。

**Q: 端口 8080 被占用**
A: `netstat -ano | findstr :8080` 找到进程，任务管理器结束它。

---

## 文档索引

- [需求文档](requirements.md)
- [设计文档](design.md)
- [测试文档](test.md)
- [Docker 部署详细指南](docker-guide.md)
