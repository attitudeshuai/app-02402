package com.association.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author Association Management System
 */
@Getter
public enum ResultCode {

    // ==================== 成功 ====================
    SUCCESS(200, "操作成功"),

    // ==================== 客户端错误 4xx ====================
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "数据冲突"),
    VALIDATION_ERROR(422, "参数校验失败"),

    // ==================== 服务端错误 5xx ====================
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // ==================== 业务错误 1xxx ====================
    // 认证相关 10xx
    LOGIN_FAILED(1001, "登录失败"),
    TOKEN_INVALID(1002, "Token无效"),
    TOKEN_EXPIRED(1003, "Token已过期"),
    ACCOUNT_DISABLED(1004, "账号已被禁用"),
    ACCOUNT_NOT_FOUND(1005, "账号不存在"),
    PASSWORD_ERROR(1006, "密码错误"),
    WECHAT_LOGIN_FAILED(1007, "微信登录失败"),

    // 会员相关 11xx
    MEMBER_NOT_FOUND(1101, "会员不存在"),
    MEMBER_ALREADY_EXISTS(1102, "会员已存在"),
    MEMBER_STATUS_INVALID(1103, "会员状态异常"),
    MEMBER_APPLY_PENDING(1104, "入会申请审核中"),
    MEMBER_EXPIRED(1105, "会员已过期"),

    // 活动相关 12xx
    ACTIVITY_NOT_FOUND(1201, "活动不存在"),
    ACTIVITY_NOT_STARTED(1202, "活动报名未开始"),
    ACTIVITY_ENDED(1203, "活动报名已结束"),
    ACTIVITY_FULL(1204, "活动名额已满"),
    ACTIVITY_ALREADY_ENROLLED(1205, "已报名该活动"),
    ACTIVITY_NOT_ENROLLED(1206, "未报名该活动"),
    ACTIVITY_ALREADY_CHECKED_IN(1207, "已签到"),
    ACTIVITY_CHECK_IN_NOT_ALLOWED(1208, "当前不允许签到"),
    ACTIVITY_COMMENT_NOT_ALLOWED(1209, "活动未结束，暂不能评论"),
    ACTIVITY_ALREADY_COMMENTED(1210, "已评论过该活动"),
    ACTIVITY_COMMENT_NOT_FOUND(1211, "评论不存在"),
    ACTIVITY_COMMENT_NOT_OWNER(1212, "只能编辑自己的评论"),

    // 财务相关 13xx
    PAYMENT_FAILED(1301, "支付失败"),
    PAYMENT_NOT_FOUND(1302, "支付记录不存在"),
    ALREADY_PAID(1303, "已完成支付"),
    REFUND_FAILED(1304, "退款失败"),

    // 公告相关 14xx
    NOTICE_NOT_FOUND(1401, "公告不存在");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 消息
     */
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
