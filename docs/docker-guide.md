# Docker 部署指南

> 适用于：音响店 VCD 零售出租管理系统
> 面向人群：零基础用户，按步骤操作即可

---

## 一、什么是 Docker？

Docker 是一个容器化工具，把程序和运行环境打包成"容器"，别人只需装 Docker，一条命令就能运行，不需要手动安装 JDK、SQL Server 等环境。

本项目支持**两种 Docker 部署方式**，可根据需求选择：

| 方式 | 说明 | 需要 JDK | 适合场景 |
|------|------|---------|---------|
| 方式一：仅数据库容器化 | Docker 只跑数据库，后端直接运行 jar 包 | 是（JDK 17） | 快速演示、轻量部署 |
| 方式二：完整容器化 | 数据库和后端都在 Docker 容器中 | 否 | 完整交付、生产部署 |

---

## 二、安装 Docker Desktop

1. 访问官网：https://www.docker.com/products/docker-desktop/
2. 下载 `Docker Desktop Installer.exe`，双击安装，重启电脑
3. 桌面双击 Docker 图标启动，等待右下角状态变绿（Running）

验证安装：

```bash
docker --version
docker-compose --version
```

看到版本号即成功。

---

## 三、方式一：仅数据库容器化（jar 包直接运行）

### 所需文件（位于 `deploy/` 目录）

```
deploy/
├── backend-0.0.1-SNAPSHOT.jar   # 后端程序
└── script.sql                   # 数据库初始化脚本
```

### 第一步：用 Docker 启动 SQL Server 数据库

打开 cmd，执行：

```bash
docker run -d ^
  --name vcd-sqlserver ^
  -e "ACCEPT_EULA=Y" ^
  -e "SA_PASSWORD=123456" ^
  -e "MSSQL_PID=Express" ^
  -p 1433:1433 ^
  mcr.microsoft.com/mssql/server:2022-latest
```

等待约 30 秒，数据库启动完成。

### 第二步：初始化数据库

用 SSMS 连接：
- 服务器名称：`localhost,1433`
- 登录名：`sa`，密码：`123456`

连接后执行 `deploy/script.sql` 文件内容。

### 第三步：运行后端 jar 包

确保本机已安装 JDK 17，然后执行：

```bash
cd D:\AI\Kiroprojects\ruanjianjiagou\deploy
java -jar backend-0.0.1-SNAPSHOT.jar
```

看到 `Started BackendApplication` 即启动成功。

### 第四步：访问系统

浏览器打开：`http://localhost:8080/login.html`

### 停止数据库容器

```bash
docker stop vcd-sqlserver
docker start vcd-sqlserver   # 下次重新启动
```

---

## 四、方式二：完整容器化（docker-compose 一键启动）

### 所需文件（位于项目根目录）

```
项目根目录/
├── docker-compose.yml      # 服务编排文件
├── script.sql              # 数据库初始化脚本
└── vcd-backend/
    └── Dockerfile          # 后端镜像构建文件
```

### 第一步：进入项目根目录

```bash
cd D:\AI\Kiroprojects\ruanjianjiagou
```

### 第二步：一键启动所有服务

```bash
docker-compose up -d --build
```

首次执行会自动完成：
1. 下载 SQL Server 2022 镜像（约 1-2 GB，需等待）
2. 用 Maven 编译后端代码打包（约 3-5 分钟）
3. 启动数据库容器，等待健康检查通过
4. 启动后端容器，自动连接数据库

### 第三步：确认启动成功

```bash
docker-compose ps
```

看到两个容器都是 `Up` 状态即成功：

```
NAME              STATUS          PORTS
vcd-sqlserver     Up (healthy)    0.0.0.0:1433->1433/tcp
vcd-backend       Up              0.0.0.0:8080->8080/tcp
```

### 第四步：初始化数据库

用 SSMS 连接：
- 服务器名称：`localhost,1433`
- 登录名：`sa`，密码：`123456Aa!`（完整容器化模式密码）

执行 `script.sql` 初始化数据。

### 第五步：访问系统

浏览器打开：`http://localhost:8080/login.html`

---

## 五、注册账号

两种方式启动后，首次使用需注册账号：

```
POST http://localhost:8080/api/users/register
Content-Type: application/json

{"username": "admin", "password": "123456"}
```

然后用 `admin` / `123456` 登录。

---

## 六、日常管理命令

### 方式一（单容器）

```bash
# 启动数据库容器
docker start vcd-sqlserver

# 停止数据库容器
docker stop vcd-sqlserver

# 查看容器状态
docker ps

# 查看日志
docker logs vcd-sqlserver
```

### 方式二（docker-compose）

```bash
# 启动（已构建过，无需重新构建）
docker-compose up -d

# 停止（数据保留）
docker-compose down

# 查看状态
docker-compose ps

# 查看后端日志
docker-compose logs -f backend

# 重启后端
docker-compose restart backend

# 代码修改后重新构建
docker-compose up -d --build backend
```

---

## 七、常见问题

**Q: 端口 1433 被占用？**
A: 本机已安装 SQL Server。停止本机 SQL Server 服务，或修改 docker-compose.yml 中端口映射为 `"1434:1433"`。

**Q: 方式二后端容器启动后退出？**
A: 数据库还没准备好。执行 `docker-compose logs backend` 查看错误，等 sqlserver 变为 healthy 后执行 `docker-compose restart backend`。

**Q: Docker Desktop 报 WSL 错误？**
A: 以管理员身份打开 PowerShell 执行 `wsl --install`，重启电脑。

**Q: 方式一和方式二的 sa 密码不同？**
A: 是的。方式一密码为 `123456`，方式二密码为 `123456Aa!`（SQL Server 容器要求密码含大小写字母和数字）。

---

## 八、两种方式对比总结

| 对比项 | 方式一（仅DB容器化） | 方式二（完整容器化） |
|--------|-------------------|-------------------|
| 需要 JDK | 是（JDK 17） | 否 |
| 启动步骤 | 2步（docker run + java -jar） | 1步（docker-compose up） |
| 所需文件 | deploy/ 目录的 jar + sql | 完整项目源码 |
| 数据库密码 | 123456 | 123456Aa! |
| 适合场景 | 快速演示 | 完整交付部署 |
| 技术含量 | Docker 基础用法 | Docker Compose 服务编排 |
