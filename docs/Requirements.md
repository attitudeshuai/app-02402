# 协会管理小程序后端 - 需求规格说明书

> 版本: 1.0  
> 状态: 待确认  
> 最后更新: 2026-02-09

---

## 1. 项目概述

### 1.1 项目背景
为行业协会管理微信小程序提供后端 API 支持，实现协会日常运营的数字化管理。

### 1.2 项目目标
- 提供稳定、可扩展的 RESTful API 服务
- 支持多角色权限管理
- 实现协会核心业务流程数字化
- 容器化交付，便于部署和维护

### 1.3 技术选型
| 层级 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.2.x |
| 构建工具 | Maven | 3.9.x |
| ORM | MyBatis-Plus | 3.5.x |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis | 7.x |
| 接口文档 | Knife4j (Swagger) | 4.x |
| 认证 | JWT | - |

---

## 2. 角色定义与权限

### 2.1 角色清单

| 角色 | 角色码 | 说明 |
|------|--------|------|
| 管理员 | `ADMIN` | 系统超级管理员，拥有全部权限 |
| 会长 | `PRESIDENT` | 协会会长，管理会员和协会事务 |
| 会员 | `MEMBER` | 普通会员，参与协会活动 |

### 2.2 权限矩阵

| 功能模块 | 管理员 | 会长 | 会员 |
|----------|--------|------|------|
| **会员管理** | ✅ 全部 | ✅ 全部 | ❌ |
| **活动管理** | ✅ 全部 | ✅ 全部 | 👁️ 查看/参与 |
| **财务管理** | ✅ 全部 | ✅ 全部 | 👁️ 查看个人 |
| **公告通知** | ✅ 全部 | ✅ 发布/编辑 | 👁️ 查看 |
| **系统设置** | ✅ 全部 | ❌ | ❌ |

---

## 3. 功能模块详细设计

### 3.1 用户认证模块 (Auth)

#### 3.1.1 登录方式
- **微信小程序登录**: 通过 `wx.login()` 获取 code，后端换取 openid
- **账号密码登录**: 管理员后台登录（预留）

#### 3.1.2 接口清单
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/wx-login` | POST | 微信小程序登录 |
| `/api/auth/login` | POST | 账号密码登录 |
| `/api/auth/logout` | POST | 退出登录 |
| `/api/auth/refresh` | POST | 刷新Token |
| `/api/auth/profile` | GET | 获取当前用户信息 |

---

### 3.2 会员管理模块 (Member)

#### 3.2.1 功能描述
- 会员信息的增删改查
- 会员状态管理（正常/禁用/待审核）
- 会员入会申请与审批
- 会员信息导出

#### 3.2.2 数据模型
```
Member {
  id: Long              // 主键
  userId: Long          // 关联用户ID
  memberNo: String      // 会员编号
  realName: String      // 真实姓名
  phone: String         // 手机号
  company: String       // 所在企业
  position: String      // 职位
  joinDate: Date        // 入会日期
  expireDate: Date      // 有效期至
  status: Integer       // 状态: 0-待审核 1-正常 2-禁用 3-已过期
  createTime: DateTime
  updateTime: DateTime
}
```

#### 3.2.3 接口清单
| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/api/member/list` | GET | ADMIN/PRESIDENT | 会员列表（分页） |
| `/api/member/{id}` | GET | ADMIN/PRESIDENT | 会员详情 |
| `/api/member` | POST | ADMIN/PRESIDENT | 新增会员 |
| `/api/member/{id}` | PUT | ADMIN/PRESIDENT | 编辑会员 |
| `/api/member/{id}` | DELETE | ADMIN/PRESIDENT | 删除会员 |
| `/api/member/{id}/status` | PUT | ADMIN/PRESIDENT | 修改会员状态 |
| `/api/member/apply` | POST | ALL | 申请入会 |
| `/api/member/apply/{id}/approve` | POST | ADMIN/PRESIDENT | 审批入会申请 |

---

### 3.3 活动管理模块 (Activity)

#### 3.3.1 功能描述
- 活动的创建、编辑、发布、取消
- 活动报名管理
- 活动签到/打卡
- 活动费用收取

#### 3.3.2 数据模型
```
Activity {
  id: Long
  title: String           // 活动标题
  description: Text       // 活动描述
  coverImage: String      // 封面图
  location: String        // 活动地点
  startTime: DateTime     // 开始时间
  endTime: DateTime       // 结束时间
  enrollStartTime: DateTime  // 报名开始时间
  enrollEndTime: DateTime    // 报名截止时间
  maxParticipants: Integer   // 最大参与人数
  fee: Decimal            // 活动费用 (0为免费)
  status: Integer         // 状态: 0-草稿 1-报名中 2-进行中 3-已结束 4-已取消
  creatorId: Long         // 创建者ID
  createTime: DateTime
  updateTime: DateTime
}

ActivityEnrollment {
  id: Long
  activityId: Long
  memberId: Long
  paymentStatus: Integer  // 支付状态: 0-待支付 1-已支付 2-已退款
  paymentTime: DateTime
  checkInStatus: Integer  // 签到状态: 0-未签到 1-已签到
  checkInTime: DateTime
  createTime: DateTime
}
```

#### 3.3.3 接口清单
| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/api/activity/list` | GET | ALL | 活动列表 |
| `/api/activity/{id}` | GET | ALL | 活动详情 |
| `/api/activity` | POST | ADMIN/PRESIDENT | 创建活动 |
| `/api/activity/{id}` | PUT | ADMIN/PRESIDENT | 编辑活动 |
| `/api/activity/{id}` | DELETE | ADMIN/PRESIDENT | 删除活动 |
| `/api/activity/{id}/publish` | POST | ADMIN/PRESIDENT | 发布活动 |
| `/api/activity/{id}/cancel` | POST | ADMIN/PRESIDENT | 取消活动 |
| `/api/activity/{id}/enroll` | POST | MEMBER | 报名活动 |
| `/api/activity/{id}/enroll/cancel` | POST | MEMBER | 取消报名 |
| `/api/activity/{id}/check-in` | POST | MEMBER | 活动签到 |
| `/api/activity/{id}/enrollments` | GET | ADMIN/PRESIDENT | 报名列表 |

---

### 3.4 财务管理模块 (Finance)

#### 3.4.1 功能描述
- 会费收取与记录
- 活动费用收取与记录
- 收支明细查询
- 财务统计报表

#### 3.4.2 数据模型
```
FinanceRecord {
  id: Long
  recordNo: String        // 流水号
  type: Integer           // 类型: 1-会费 2-活动费 3-捐赠 4-其他收入 5-支出
  amount: Decimal         // 金额
  memberId: Long          // 关联会员
  activityId: Long        // 关联活动（如有）
  paymentMethod: String   // 支付方式
  transactionId: String   // 第三方交易号
  remark: String          // 备注
  operatorId: Long        // 操作人
  createTime: DateTime
}
```

#### 3.4.3 接口清单
| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/api/finance/records` | GET | ADMIN/PRESIDENT | 财务记录列表 |
| `/api/finance/record/{id}` | GET | ADMIN/PRESIDENT | 记录详情 |
| `/api/finance/record` | POST | ADMIN/PRESIDENT | 新增记录 |
| `/api/finance/statistics` | GET | ADMIN/PRESIDENT | 财务统计 |
| `/api/finance/my-records` | GET | MEMBER | 我的缴费记录 |
| `/api/finance/pay/membership` | POST | MEMBER | 缴纳会费 |
| `/api/finance/pay/activity/{id}` | POST | MEMBER | 缴纳活动费 |

---

### 3.5 公告通知模块 (Notice)

#### 3.5.1 功能描述
- 公告的发布、编辑、删除
- 公告列表与详情查看
- 公告已读状态追踪

#### 3.5.2 数据模型
```
Notice {
  id: Long
  title: String           // 标题
  content: Text           // 内容
  type: Integer           // 类型: 1-通知 2-公告 3-新闻
  isTop: Boolean          // 是否置顶
  status: Integer         // 状态: 0-草稿 1-已发布
  publishTime: DateTime   // 发布时间
  creatorId: Long
  createTime: DateTime
  updateTime: DateTime
}

NoticeRead {
  id: Long
  noticeId: Long
  memberId: Long
  readTime: DateTime
}
```

#### 3.5.3 接口清单
| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/api/notice/list` | GET | ALL | 公告列表 |
| `/api/notice/{id}` | GET | ALL | 公告详情 |
| `/api/notice` | POST | ADMIN/PRESIDENT | 发布公告 |
| `/api/notice/{id}` | PUT | ADMIN/PRESIDENT | 编辑公告 |
| `/api/notice/{id}` | DELETE | ADMIN/PRESIDENT | 删除公告 |
| `/api/notice/{id}/read` | POST | MEMBER | 标记已读 |

---

## 4. 第三方服务集成

### 4.1 微信小程序登录
- **实现方式**: 真实对接微信开放平台
- **所需配置**: AppID, AppSecret
- **说明**: 需要在配置文件中配置小程序凭证

### 4.2 支付服务 (模拟)
- **实现方式**: 模拟支付流程
- **模拟行为**: 
  - 调用支付接口后直接返回成功
  - 生成模拟交易号
  - 记录支付流水
- **扩展预留**: 提供 `PaymentService` 接口，可替换为微信支付实现

### 4.3 短信通知服务 (模拟)
- **实现方式**: 模拟短信发送
- **模拟行为**:
  - 短信内容记录到日志和数据库
  - 返回模拟发送成功
- **扩展预留**: 提供 `SmsService` 接口，可替换为阿里云短信/腾讯云短信实现

---

## 5. 非功能性需求

### 5.1 安全性
- JWT Token 认证，有效期 24 小时
- 接口权限校验（基于注解）
- 敏感数据加密存储（如手机号）
- SQL 注入防护（MyBatis-Plus 参数化）
- XSS 防护

### 5.2 性能
- 分页查询默认每页 10 条，最大 100 条
- Redis 缓存热点数据
- 数据库连接池（HikariCP）

### 5.3 可维护性
- 统一响应格式
- 全局异常处理
- 日志规范（Logback）
- 接口文档自动生成（Knife4j）

### 5.4 容器化
- 提供 Dockerfile 和 docker-compose.yml
- 支持 ARM64 / AMD64 双架构
- 一键启动：`docker compose up --build -d`

---

## 6. 交付标准

### 6.1 项目结构
```
backend/
├── src/main/java/com/association/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── service/         # 服务层
│   ├── mapper/          # MyBatis Mapper
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── vo/              # 视图对象
│   ├── common/          # 公共组件
│   └── util/            # 工具类
├── src/main/resources/
│   ├── mapper/          # XML映射文件
│   └── application.yml  # 配置文件
├── Dockerfile
└── pom.xml
```

### 6.2 访问入口
- API 服务: `http://localhost:8080`
- 接口文档: `http://localhost:8080/doc.html`

### 6.3 测试账号
| 角色 | 账号 | 密码 |
|------|------|------|
| 管理员 | admin | admin123 |
| 会长 | president | president123 |
| 会员 | member | member123 |

---

## 7. 冲突解决记录

| 日期 | 冲突描述 | AI 分析 | 用户决策 |
|------|----------|---------|----------|
| 2026-02-09 | 需求初始澄清 | 需要明确角色、功能模块、第三方服务 | 用户提供完整信息 |

---

## 8. 确认签署

- [ ] 用户确认以上需求无误
- [ ] 可以进入 `/plan` 阶段

---

> ⚠️ **请确认**: 以上需求是否符合您的预期？如有修改请指出，确认无误后回复 **"确认"** 进入规划阶段。
