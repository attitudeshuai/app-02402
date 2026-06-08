# 协会管理小程序后端

> 基于 Spring Boot 3.2 的行业协会管理系统后端 API 服务

---

## 1. How to Run

```bash
docker compose up --build -d
```

等待约 60 秒后服务启动完成。查看日志确认启动成功：

```bash
docker compose logs -f backend
```

停止服务：

```bash
docker compose down
```

---

## 2. Services

| 服务 | 地址 | 说明 |
|------|------|------|
| Backend API | http://localhost:8080 | 后端 API 服务 |
| API Docs | http://localhost:8080/doc.html | Knife4j 接口文档 |
| MySQL | localhost:3306 | 数据库 (内部) |
| Redis | localhost:6379 | 缓存 (内部) |

---

## 3. 测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 (ADMIN) | admin | admin123 |
| 会长 (PRESIDENT) | president | president123 |
| 会员 (MEMBER) | member | member123 |

登录测试：

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## 4. 题目内容

帮我创建一个基于Spring Boot的Java后端项目，为协会管理小程序提供API支持。项目采用Maven进行依赖管理，使用MyBatis-Plus作为ORM框架，集成了多种第三方服务，支持多角色登录和完整的业务流程。

---

## 项目简介

本系统为行业协会管理微信小程序提供后端支持，实现以下核心功能：

- **用户认证**: 微信小程序登录 + 账号密码登录，JWT Token 认证
- **会员管理**: 入会申请、审批、状态管理、会员信息 CRUD
- **活动管理**: 活动发布、报名、缴费、签到打卡
- **财务管理**: 会费/活动费收取、收支记录、财务统计
- **公告通知**: 公告发布、置顶、已读追踪

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.2.2 |
| 构建 | Maven | 3.9.x |
| ORM | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| 文档 | Knife4j | 4.4.0 |
| 认证 | JWT (jjwt) | 0.12.3 |
| 工具 | Hutool | 5.8.25 |
| 容器 | Docker | - |

---

## 角色与权限

| 角色 | 会员管理 | 活动管理 | 财务管理 | 公告通知 |
|------|----------|----------|----------|----------|
| **管理员 (ADMIN)** | ✅ 全部 | ✅ 全部 | ✅ 全部 | ✅ 全部 |
| **会长 (PRESIDENT)** | ✅ 全部 | ✅ 全部 | ✅ 全部 | ✅ 发布/编辑 |
| **会员 (MEMBER)** | ❌ | 👁️ 查看/参与/缴费/打卡 | 👁️ 查看个人 | 👁️ 查看 |

---

## API 接口概览

### 认证模块 `/api/auth`

| 方法 | 接口 | 说明 | 权限 |
|------|------|------|------|
| POST | /login | 账号密码登录 | 公开 |
| POST | /wx-login | 微信小程序登录 | 公开 |
| GET | /profile | 获取当前用户信息 | 登录 |
| POST | /refresh | 刷新 Token | 登录 |
| POST | /logout | 退出登录 | 登录 |

### 会员模块 `/api/member`

| 方法 | 接口 | 说明 | 权限 |
|------|------|------|------|
| GET | /list | 会员列表（分页） | ADMIN/PRESIDENT |
| GET | /{id} | 会员详情 | ADMIN/PRESIDENT |
| POST | / | 新增会员 | ADMIN/PRESIDENT |
| PUT | /{id} | 编辑会员 | ADMIN/PRESIDENT |
| DELETE | /{id} | 删除会员 | ADMIN/PRESIDENT |
| PUT | /{id}/status | 修改会员状态 | ADMIN/PRESIDENT |
| POST | /apply | 申请入会 | 公开 |
| POST | /apply/{id}/approve | 审批入会申请 | ADMIN/PRESIDENT |

### 活动模块 `/api/activity`

| 方法 | 接口 | 说明 | 权限 |
|------|------|------|------|
| GET | /list | 活动列表 | 登录 |
| GET | /{id} | 活动详情 | 登录 |
| POST | / | 创建活动 | ADMIN/PRESIDENT |
| PUT | /{id} | 编辑活动 | ADMIN/PRESIDENT |
| DELETE | /{id} | 删除活动 | ADMIN/PRESIDENT |
| POST | /{id}/publish | 发布活动 | ADMIN/PRESIDENT |
| POST | /{id}/cancel | 取消活动 | ADMIN/PRESIDENT |
| POST | /{id}/enroll | 报名活动 | 登录 |
| POST | /{id}/enroll/cancel | 取消报名 | 登录 |
| POST | /{id}/check-in | 活动签到 | 登录 |
| GET | /{id}/enrollments | 报名列表 | ADMIN/PRESIDENT |
| GET | /my | 我的活动 | 登录 |

### 财务模块 `/api/finance`

| 方法 | 接口 | 说明 | 权限 |
|------|------|------|------|
| GET | /records | 财务记录列表 | ADMIN/PRESIDENT |
| GET | /record/{id} | 记录详情 | ADMIN/PRESIDENT |
| POST | /record | 新增记录 | ADMIN/PRESIDENT |
| GET | /statistics | 财务统计 | ADMIN/PRESIDENT |
| GET | /my-records | 我的缴费记录 | 登录 |
| POST | /pay/membership | 缴纳会费 | 登录 |
| POST | /pay/activity/{id} | 缴纳活动费 | 登录 |

### 公告模块 `/api/notice`

| 方法 | 接口 | 说明 | 权限 |
|------|------|------|------|
| GET | /list | 公告列表 | 登录 |
| GET | /{id} | 公告详情 | 登录 |
| POST | / | 发布公告 | ADMIN/PRESIDENT |
| PUT | /{id} | 编辑公告 | ADMIN/PRESIDENT |
| DELETE | /{id} | 删除公告 | ADMIN/PRESIDENT |
| POST | /{id}/read | 标记已读 | 登录 |
| GET | /unread-count | 未读数量 | 登录 |

---

## 项目结构

```
backend/
├── src/main/java/com/association/
│   ├── AssociationApplication.java  # 启动类
│   ├── config/                      # 配置类 (6个文件)
│   │   ├── DataInitializer.java     # 数据初始化
│   │   ├── Knife4jConfig.java       # 接口文档配置
│   │   ├── MybatisPlusConfig.java   # MyBatis-Plus 配置
│   │   ├── RedisConfig.java         # Redis 配置
│   │   ├── WebMvcConfig.java        # Web MVC 配置
│   │   └── interceptor/             # 拦截器
│   │       ├── AuthInterceptor.java # 认证拦截器
│   │       └── RoleInterceptor.java # 角色拦截器
│   ├── controller/                  # 控制器 (6个文件)
│   │   ├── AuthController.java
│   │   ├── MemberController.java
│   │   ├── ActivityController.java
│   │   ├── FinanceController.java
│   │   ├── NoticeController.java
│   │   └── IndexController.java
│   ├── service/                     # 服务层接口 (9个文件)
│   │   └── impl/                    # 服务实现 (9个文件)
│   ├── mapper/                      # MyBatis Mapper (7个文件)
│   ├── entity/                      # 实体类 (7个文件)
│   ├── dto/                         # 数据传输对象 (9个文件)
│   ├── vo/                          # 视图对象 (9个文件)
│   ├── common/                      # 公共组件
│   │   ├── annotation/              # 自定义注解
│   │   ├── context/                 # 用户上下文
│   │   ├── exception/               # 异常处理
│   │   └── result/                  # 统一响应
│   └── util/                        # 工具类 (2个文件)
│       ├── JwtUtil.java
│       └── RedisUtil.java
├── src/main/resources/
│   ├── application.yml              # 配置文件
│   └── db/                          # 数据库脚本
│       ├── 01-schema.sql            # 表结构
│       └── 02-data.sql              # 测试数据
├── Dockerfile                       # Docker 多阶段构建
└── pom.xml                          # Maven 配置
```

**统计**: 71 个 Java 源文件

---

## 第三方服务集成

### 微信小程序登录

- **实现方式**: 真实对接微信开放平台 API
- **配置**: 在 `application.yml` 配置 `app.wechat.app-id` 和 `app.wechat.app-secret`
- **测试模式**: AppID 以 `wx_test` 开头时启用模拟模式

### 支付服务 (模拟)

- **当前实现**: `MockPaymentServiceImpl` 模拟支付流程
- **行为**: 直接返回成功，生成模拟交易号
- **扩展**: 实现 `PaymentService` 接口，替换为微信支付

### 短信服务 (模拟)

- **当前实现**: `MockSmsServiceImpl` 记录到日志
- **扩展**: 实现 `SmsService` 接口，替换为阿里云/腾讯云短信

---

## 冲突解决记录

| 日期 | 冲突描述 | AI 分析 | 用户决策 |
|------|----------|---------|----------|
| 2026-02-09 | 需求初始澄清 - 协会类型、角色定义、功能模块 | 需要明确角色、功能模块、第三方服务范围 | 用户提供完整信息：行业协会、三角色、四模块 |

---

## 文档索引

| 文档 | 路径 | 说明 |
|------|------|------|
| 需求规格 | `docs/Requirements.md` | 完整需求定义 |
| 开发路线 | `docs/Roadmap.md` | 开发计划与进度 |
| 审计报告 | `docs/AuditReport.md` | 第三方视角审计 |
| 自测报告 | `docs/SelfTestReport.md` | 质量自测报告 |
| API测试报告 | `docs/ApiTestReport.md` | 接口自测报告 (45/45通过) |

## 接口自测

```bash
# 运行自测脚本
./test-api.sh
```

测试结果: ✅ **45/45 全部通过**

---

## License

MIT
