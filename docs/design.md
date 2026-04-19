# 设计文档（Design Document）

## 项目名称
音响店 VCD 零售出租管理系统

## 版本
v1.0.0

---

## 1. 系统架构

### 1.1 整体架构

本系统采用前后端分离架构，前端打包后作为静态资源内嵌于后端 jar 包，由 Spring Boot 统一托管。

```
浏览器
  │  HTTP / REST API
  ▼
Spring Boot 后端（端口 8080）
  ├── 静态资源服务（/dist/）→ 前端 Vue3 页面
  ├── REST API（/api/**）→ 业务逻辑处理
  └── Spring Security + JWT → 认证鉴权
        │
        ▼
  SQL Server 数据库（端口 1433，库名 vcddb）
```

### 1.2 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.5.0 |
| 运行环境 | JDK | 17 |
| ORM | Spring Data JPA + Hibernate | 随 Boot 3.5.0 |
| 安全认证 | Spring Security + jjwt | 6.5.0 / 0.11.5 |
| 数据库 | SQL Server | 2019 / 2022 |
| 数据库驱动 | mssql-jdbc | 12.4.2.jre11 |
| 连接池 | HikariCP | 随 Boot 3.5.0 |
| 代码简化 | Lombok | 1.18.30 |
| 前端框架 | Vue 3 | 3.x |
| UI 组件库 | Element Plus | 2.x |
| HTTP 客户端 | Axios | 1.x |
| 构建工具 | Vite | 5.x |
| 容器化 | Docker + Docker Compose | 最新稳定版 |

---

## 2. 后端模块设计

### 2.1 包结构

```
top.tatobamail.backend
├── BackendApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── JwtAuthenticationFilter.java
│   └── WebMvcConfig.java
├── util/
│   └── JwtUtil.java
├── User/
├── Vcd/
├── VcdCategories/
├── VcdInventory/
├── customers/
├── purchaseRecords/
├── RentalRecords/
├── ReturnRecords/
└── SalesRecords/
```

每个业务模块遵循四层结构：Entity → Repository → Service/ServiceImpl → Controller

### 2.2 数据库表设计

#### users（用户表）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, IDENTITY | 主键 |
| username | varchar(50) | NOT NULL, UNIQUE | 用户名 |
| password | varchar(255) | NOT NULL | BCrypt 加密密码 |
| created_at | datetime2(6) | NOT NULL | 创建时间 |
| updated_at | datetime2(6) | NOT NULL | 更新时间 |

#### vcd_categories（分类表）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, IDENTITY | 主键 |
| name | varchar(50) | NOT NULL, UNIQUE | 分类名称 |
| description | varchar(255) | NOT NULL | 分类描述 |

#### vcd（VCD 信息表）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, IDENTITY | 主键 |
| title | varchar(50) | NOT NULL, UNIQUE | 片名 |
| actor | varchar(255) | NOT NULL | 主演 |
| director | varchar(255) | NOT NULL | 导演 |
| publish_year | varchar(255) | NOT NULL | 出版年份 |
| time | varchar(255) | NOT NULL | 时长 |
| rent_price | real | NOT NULL | 租赁价格 |
| sales_price | real | NOT NULL | 销售价格 |
| description | varchar(255) | NOT NULL | 简介 |
| category_id | bigint | FK→vcd_categories.id | 分类外键 |

#### vcd_inventory（库存表）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, IDENTITY | 主键 |
| vcd_id | bigint | FK→vcd.id, UNIQUE | VCD 外键（一对一） |
| stock | int | NOT NULL | 总库存数量 |
| rent_count | int | NOT NULL | 已租出数量 |
| created_at | datetime2(6) | NOT NULL | 创建时间 |
| updated_at | datetime2(6) | NOT NULL | 更新时间 |

#### customers（客户表）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, IDENTITY | 主键 |
| name | varchar(50) | NOT NULL | 客户姓名 |
| phone_number | varchar(255) | NOT NULL | 电话号码 |

#### purchase_records（采购记录表）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, IDENTITY | 主键 |
| vcd_id | bigint | FK→vcd.id | VCD 外键 |
| price | real | NOT NULL | 采购单价 |
| quantity | int | NOT NULL | 采购数量 |
| purchase_date | date | NOT NULL | 采购日期 |
| created_at | datetime2(6) | NOT NULL | 创建时间 |
| updated_at | datetime2(6) | NOT NULL | 更新时间 |

#### rental_records（租赁记录表）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, IDENTITY | 主键 |
| vcd_id | bigint | FK→vcd.id | VCD 外键 |
| customer_id | bigint | FK→customers.id | 客户外键 |
| deposit | real | NOT NULL | 押金 |
| rental_date | date | NOT NULL | 租赁日期 |
| expected_return_date | date | NULL | 预计归还日期 |
| is_returned | bit | NOT NULL | 是否已归还 |
| created_at | datetime2(6) | NOT NULL | 创建时间 |
| updated_at | datetime2(6) | NOT NULL | 更新时间 |

#### return_records（归还记录表）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, IDENTITY | 主键 |
| rental_id | bigint | FK→rental_records.id | 租赁记录外键 |
| vcd_id | bigint | FK→vcd.id | VCD 外键 |
| customer_id | bigint | FK→customers.id | 客户外键 |
| return_date | date | NOT NULL | 归还日期 |
| status | varchar(20) | CHECK(NORMAL/OVERDUE/DAMAGED) | 归还状态 |
| overdue_days | int | NOT NULL | 逾期天数 |
| overdue_fee | real | NOT NULL | 逾期费用 |
| deposit_refund | real | NOT NULL | 押金退还金额 |
| price | real | NOT NULL | 实际收费 |
| created_at | datetime2(6) | NOT NULL | 创建时间 |
| updated_at | datetime2(6) | NOT NULL | 更新时间 |

#### sales_records（销售记录表）
| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, IDENTITY | 主键 |
| vcd_id | bigint | FK→vcd.id | VCD 外键 |
| customer_id | bigint | FK→customers.id | 客户外键 |
| price | real | NOT NULL | 销售价格 |
| sale_date | date | NOT NULL | 销售日期 |
| created_at | datetime2(6) | NOT NULL | 创建时间 |
| updated_at | datetime2(6) | NOT NULL | 更新时间 |

---

## 3. API 接口设计

### 3.1 认证接口

| 方法 | 路径 | 说明 | 需认证 |
|------|------|------|--------|
| POST | /api/users/login | 用户登录，返回 JWT token | 否 |
| POST | /api/users/register | 用户注册 | 否 |
| GET | /api/users | 获取所有用户 | 是 |
| GET | /api/users/{id} | 根据 ID 获取用户 | 是 |
| PUT | /api/users/{id} | 更新用户信息 | 是 |
| DELETE | /api/users/{id} | 删除用户 | 是 |

**登录响应示例：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": { "id": 1, "username": "admin" }
}
```

### 3.2 VCD 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/vcd | 获取所有 VCD |
| GET | /api/vcd/page | 分页获取 VCD |
| GET | /api/vcd/popular | 热门 VCD 排行 |
| GET | /api/vcd/search?keyword= | 搜索 VCD |
| GET | /api/vcd/{id} | 根据 ID 获取 VCD |
| POST | /api/vcd | 新增 VCD |
| PUT | /api/vcd/{id} | 更新 VCD |
| DELETE | /api/vcd/{id} | 删除 VCD |

### 3.3 其他业务接口

| 模块 | 基础路径 | 支持操作 |
|------|---------|---------|
| VCD 分类 | /api/vcdCategories | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| 库存 | /api/vcdInventory | GET, GET/{id}, GET/vcd/{vcdId}, POST, PUT/{id}, DELETE/{id} |
| 客户 | /api/customers | GET, GET/{id}, GET/search, POST, PUT/{id}, DELETE/{id} |
| 采购记录 | /api/purchaseRecords | GET, GET/{id}, POST, DELETE/{id} |
| 租赁记录 | /api/rentalRecords | GET, GET/{id}, POST, DELETE/{id} |
| 归还记录 | /api/returnRecords | GET, GET/{id}, POST, DELETE/{id} |
| 销售记录 | /api/salesRecords | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |

---

## 4. 安全设计

### 4.1 JWT 认证流程

```
客户端                        服务端
  │── POST /api/users/login ──▶│ 验证密码（BCrypt.matches）
  │                            │ 生成 JWT（HS256，24h）
  │◀── {token, user} ──────────│
  │ localStorage.setItem(token)│
  │── GET /api/vcd ────────────▶│ JwtAuthenticationFilter 解析 token
  │   Authorization: Bearer xx │ 设置 SecurityContext
  │◀── [VCD列表] ──────────────│
```

### 4.2 放行路径（无需 Token）

- `POST /api/users/login`、`POST /api/users/register`
- `/`、`/login.html`、`/manage.html`、`/assets/**`、`/*.ico`

### 4.3 密码安全

- 注册：`BCryptPasswordEncoder` 加密，cost=10
- 登录：`passwordEncoder.matches()` 比对，不可逆

---

## 5. 前端设计

### 5.1 页面结构

```
login.html  →  LoginApp.vue（登录表单）

manage.html →  ManageApp.vue（主框架）
                └── router-view
                      ├── HomeView.vue        首页数据看板
                      ├── VcdView.vue         VCD 管理
                      ├── CategoriesView.vue  分类管理
                      ├── InventoryView.vue   库存管理
                      ├── CustomersView.vue   客户管理
                      ├── PurchaseView.vue    采购记录
                      ├── RentalView.vue      租赁记录
                      ├── ReturnView.vue      归还记录
                      ├── SalesView.vue       销售记录
                      └── UsersView.vue       用户管理
```

### 5.2 UI 风格

- 主色调：暖米色系（`#c8a97e`、`#f5f0e8`）
- 登录页：全屏书店背景图，右侧米色卡片登录框，含"欢迎回来"标题
- 顶部导航：浅米色背景，系统名称左对齐，退出登录右对齐
- 左侧菜单：白色背景，可折叠，激活项高亮显示
- 表格：斑马纹，操作列含编辑（蓝色）和删除（红色）按钮
- 弹窗：Element Plus Dialog，表单验证红色提示

### 5.3 Token 处理逻辑

```javascript
// 登录成功后存储
localStorage.setItem('jwt_token', token)

// 请求拦截器自动附加
config.headers['Authorization'] = `Bearer ${token}`

// 过期检测
function isTokenExpired(token) {
  const payload = JSON.parse(atob(token.split('.')[1]))
  return payload.exp * 1000 < Date.now()
}
```

---

## 6. 核心业务逻辑

### 6.1 库存联动（Service 层触发器）

```
采购记录创建  →  inventory.stock += quantity

销售记录创建  →  检查 (stock - rentCount) > 0，否则抛出"VCD库存不足"
              →  inventory.stock -= 1

租赁记录创建  →  检查 (stock - rentCount) > 0，否则抛出"库存不足"
              →  inventory.rentCount += 1

归还记录创建  →  inventory.rentCount -= 1
              →  rental.isReturned = true
```

### 6.2 逾期天数自动计算

```java
if (returnDate.isAfter(expectedReturnDate)) {
    overdueDays = ChronoUnit.DAYS.between(expectedReturnDate, returnDate);
    status = ReturnStatus.OVERDUE;
} else {
    overdueDays = 0;
    status = ReturnStatus.NORMAL;
}
```

---

## 7. 配置说明

| 配置项 | 值 | 说明 |
|--------|-----|------|
| server.port | 8080 | 服务端口 |
| spring.datasource.url | jdbc:sqlserver://localhost:1433;databaseName=vcddb | 数据库连接 |
| spring.datasource.username | sa | 数据库用户名 |
| spring.datasource.password | 123456 | 数据库密码 |
| spring.jpa.hibernate.ddl-auto | update | 自动更新表结构 |
| jwt.expiration | 86400000 | Token 有效期（24小时，毫秒） |
| spring.web.resources.static-locations | classpath:/dist/ | 前端静态资源路径 |

---

## 8. Docker 容器化部署

### 8.1 容器化架构

```
docker-compose
  ├── vcd-sqlserver（SQL Server 2022 容器）
  │     端口：1433
  │     数据卷：sqlserver_data（持久化）
  │
  └── vcd-backend（Spring Boot 应用容器）
        端口：8080
        依赖：sqlserver 健康检查通过后启动
```

### 8.2 Dockerfile（多阶段构建）

```dockerfile
# 构建阶段：Maven + JDK 17 编译打包
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B   # 利用层缓存加速
COPY src ./src
RUN mvn clean package -DskipTests -B

# 运行阶段：仅保留 JRE，镜像更小
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**多阶段构建优势**：最终镜像只包含 JRE 和 jar 包，不含 Maven 和源码，体积更小，安全性更高。

### 8.3 docker-compose.yml 服务编排

| 服务 | 镜像 | 端口 | 说明 |
|------|------|------|------|
| sqlserver | mcr.microsoft.com/mssql/server:2022-latest | 1433 | SQL Server 2022，数据持久化 |
| backend | 本地构建（vcd-backend/Dockerfile） | 8080 | Spring Boot 应用 |

关键设计：
- `depends_on + healthcheck`：后端等待数据库健康检查通过后才启动，避免连接失败
- `volumes: sqlserver_data`：数据库数据持久化，容器重启不丢数据
- `networks: vcd-network`：两个服务在同一网络，后端通过服务名 `sqlserver` 访问数据库
- `restart: on-failure`：后端异常退出时自动重启

### 8.4 Docker 部署步骤

```bash
# 1. 确保已安装 Docker Desktop（Windows）

# 2. 在项目根目录执行（首次构建需要几分钟）
docker-compose up -d --build

# 3. 查看容器状态
docker-compose ps

# 4. 查看后端日志
docker-compose logs -f backend

# 5. 访问系统
# 浏览器打开 http://localhost:8080/login.html

# 6. 停止服务
docker-compose down

# 7. 停止并删除数据卷（清空数据库）
docker-compose down -v
```
