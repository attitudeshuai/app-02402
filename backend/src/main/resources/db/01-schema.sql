-- =====================================
-- 协会管理系统 - 数据库初始化脚本
-- =====================================

-- 使用数据库
USE association_db;

-- =====================================
-- 1. 用户表
-- =====================================
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `password` VARCHAR(255) DEFAULT NULL COMMENT '密码',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `open_id` VARCHAR(100) DEFAULT NULL COMMENT '微信OpenID',
    `union_id` VARCHAR(100) DEFAULT NULL COMMENT '微信UnionID',
    `role` VARCHAR(20) NOT NULL DEFAULT 'MEMBER' COMMENT '角色: ADMIN-管理员, PRESIDENT-会长, MEMBER-会员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_open_id` (`open_id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================
-- 2. 会员表
-- =====================================
CREATE TABLE IF NOT EXISTS `biz_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会员ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '关联用户ID',
    `member_no` VARCHAR(50) NOT NULL COMMENT '会员编号',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `company` VARCHAR(200) DEFAULT NULL COMMENT '所在企业',
    `position` VARCHAR(100) DEFAULT NULL COMMENT '职位',
    `join_date` DATE DEFAULT NULL COMMENT '入会日期',
    `expire_date` DATE DEFAULT NULL COMMENT '有效期至',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核 1-正常 2-禁用 3-已过期',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_member_no` (`member_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员表';

-- =====================================
-- 3. 活动表
-- =====================================
CREATE TABLE IF NOT EXISTS `biz_activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
    `description` TEXT DEFAULT NULL COMMENT '活动描述',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图',
    `location` VARCHAR(300) DEFAULT NULL COMMENT '活动地点',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `enroll_start_time` DATETIME DEFAULT NULL COMMENT '报名开始时间',
    `enroll_end_time` DATETIME DEFAULT NULL COMMENT '报名截止时间',
    `max_participants` INT DEFAULT NULL COMMENT '最大参与人数',
    `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '活动费用',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿 1-报名中 2-进行中 3-已结束 4-已取消',
    `creator_id` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动表';

-- =====================================
-- 4. 活动报名表
-- =====================================
CREATE TABLE IF NOT EXISTS `biz_activity_enrollment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `payment_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态: 0-待支付 1-已支付 2-已退款',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '交易号',
    `check_in_status` TINYINT NOT NULL DEFAULT 0 COMMENT '签到状态: 0-未签到 1-已签到',
    `check_in_time` DATETIME DEFAULT NULL COMMENT '签到时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动报名表';

-- =====================================
-- 5. 财务记录表
-- =====================================
CREATE TABLE IF NOT EXISTS `biz_finance_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `record_no` VARCHAR(50) NOT NULL COMMENT '流水号',
    `type` TINYINT NOT NULL COMMENT '类型: 1-会费 2-活动费 3-捐赠 4-其他收入 5-支出',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `member_id` BIGINT DEFAULT NULL COMMENT '关联会员ID',
    `activity_id` BIGINT DEFAULT NULL COMMENT '关联活动ID',
    `payment_method` VARCHAR(50) DEFAULT NULL COMMENT '支付方式',
    `transaction_id` VARCHAR(100) DEFAULT NULL COMMENT '第三方交易号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_no` (`record_no`),
    KEY `idx_type` (`type`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='财务记录表';

-- =====================================
-- 6. 公告表
-- =====================================
CREATE TABLE IF NOT EXISTS `biz_notice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型: 1-通知 2-公告 3-新闻',
    `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布',
    `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    `creator_id` BIGINT DEFAULT NULL COMMENT '创建者ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`type`),
    KEY `idx_is_top` (`is_top`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- =====================================
-- 7. 公告已读记录表
-- =====================================
CREATE TABLE IF NOT EXISTS `biz_notice_read` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `notice_id` BIGINT NOT NULL COMMENT '公告ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告已读记录表';

-- =====================================
-- 8. 活动评论表
-- =====================================
CREATE TABLE IF NOT EXISTS `biz_activity_review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `member_id` BIGINT DEFAULT NULL COMMENT '会员ID',
    `rating` TINYINT NOT NULL COMMENT '评分: 1-5星',
    `content` VARCHAR(1000) DEFAULT NULL COMMENT '评论内容',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动评论表';
