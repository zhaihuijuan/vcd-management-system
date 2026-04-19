# 测试文档（Test Document）

## 项目名称
音响店 VCD 零售出租管理系统

## 版本
v1.0.0

---

## 1. 测试环境

| 项目 | 配置 |
|------|------|
| 操作系统 | Windows 10/11 |
| JDK | 17 |
| 数据库 | SQL Server 2019/2022，端口 1433 |
| 浏览器 | Chrome / Edge 最新版 |
| 后端地址 | http://localhost:8080 |
| 测试工具 | Postman / 浏览器控制台 |

---

## 2. 用户认证测试

| 编号 | 用例 | 操作 | 预期结果 |
|------|------|------|---------|
| TC-01 | 正常登录 | POST /api/users/login，正确账号密码 | HTTP 200，返回 token 和 user |
| TC-02 | 密码错误 | POST /api/users/login，密码错误 | HTTP 500，不返回 token |
| TC-03 | 用户名不存在 | POST /api/users/login，不存在的用户名 | HTTP 500，返回错误信息 |
| TC-04 | 正常注册 | POST /api/users/register，新用户名 | HTTP 200，返回用户对象 |
| TC-05 | 重复用户名注册 | POST /api/users/register，已存在用户名 | HTTP 500，提示唯一约束 |
| TC-06 | 无 Token 访问 | GET /api/vcd，不带 Authorization | HTTP 401/403 |
| TC-07 | Token 过期访问 | 使用过期 Token 访问接口 | HTTP 401/403 |

---

## 3. VCD 管理测试

| 编号 | 用例 | 操作 | 预期结果 |
|------|------|------|---------|
| TC-08 | 获取所有 VCD | GET /api/vcd | HTTP 200，返回数组 |
| TC-09 | 新增 VCD | POST /api/vcd，填写所有字段 | HTTP 200，返回新对象含 id |
| TC-10 | 片名重复新增 | POST /api/vcd，已存在片名 | HTTP 500，唯一约束错误 |
| TC-11 | 编辑 VCD | PUT /api/vcd/{id}，修改 rentPrice | HTTP 200，返回更新后对象 |
| TC-12 | 删除 VCD | DELETE /api/vcd/{id} | HTTP 200，再查返回 404 |
| TC-13 | 搜索 VCD | GET /api/vcd/search?keyword=功夫 | HTTP 200，返回匹配列表 |
| TC-14 | 热门排行 | GET /api/vcd/popular | HTTP 200，按租出次数降序 |

---

## 4. 库存联动测试（核心业务）

| 编号 | 用例 | 前置条件 | 操作 | 预期结果 |
|------|------|---------|------|---------|
| TC-15 | 采购后库存增加 | stock=100 | POST /api/purchaseRecords，quantity=10 | stock 变为 110 |
| TC-16 | 库存充足时销售 | stock=100，rentCount=0 | POST /api/salesRecords | HTTP 200，stock 变为 99 |
| TC-17 | 库存不足时销售 | stock=5，rentCount=5 | POST /api/salesRecords | HTTP 500，提示"库存不足" |
| TC-18 | 租赁后租出增加 | stock=100，rentCount=10 | POST /api/rentalRecords | rentCount 变为 11 |
| TC-19 | 归还后租出减少 | rentCount=11，未归还记录 id=1 | POST /api/returnRecords | rentCount 变为 10，isReturned=true |
| TC-20 | 逾期自动计算 | expectedReturnDate=2024-06-05 | returnDate=2024-06-10 | overdueDays=5，status=OVERDUE |
| TC-21 | 正常归还 | expectedReturnDate=2024-06-10 | returnDate=2024-06-08 | overdueDays=0，status=NORMAL |

---

## 5. 前端页面测试

| 编号 | 用例 | 操作 | 预期结果 |
|------|------|------|---------|
| TC-22 | 登录页加载 | 访问 http://localhost:8080/login.html | 显示书店背景图和登录卡片 |
| TC-23 | 正常登录跳转 | 输入正确账号密码，点击登录 | 跳转到 manage.html |
| TC-24 | 未登录访问管理页 | 清除 localStorage，直接访问 manage.html | 自动跳转回 login.html |
| TC-25 | 菜单导航 | 点击左侧各菜单项 | 右侧内容区切换对应页面 |
| TC-26 | 新增弹窗 | 点击任意功能页"新增"按钮 | 弹出 Dialog，含表单字段 |
| TC-27 | 表单必填校验 | 不填必填字段，点击确定 | 显示红色错误提示，不提交 |
| TC-28 | 退出登录 | 点击右上角退出登录 | 清除 token，跳转登录页 |

---

## 6. 接口测试前置步骤

```bash
# 1. 注册账号
POST http://localhost:8080/api/users/register
Body: {"username": "admin", "password": "123456"}

# 2. 登录获取 Token
POST http://localhost:8080/api/users/login
Body: {"username": "admin", "password": "123456"}

# 3. 后续请求携带 Header
Authorization: Bearer <上一步返回的token>
```
