package com.association.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报名信息 VO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "报名信息")
public class EnrollmentVO {

    @Schema(description = "报名ID")
    private Long id;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "会员姓名")
    private String memberName;

    @Schema(description = "会员手机号")
    private String memberPhone;

    @Schema(description = "支付状态: 0-待支付 1-已支付 2-已退款")
    private Integer paymentStatus;

    @Schema(description = "支付状态名称")
    private String paymentStatusName;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "签到状态: 0-未签到 1-已签到")
    private Integer checkInStatus;

    @Schema(description = "签到时间")
    private LocalDateTime checkInTime;

    @Schema(description = "报名时间")
    private LocalDateTime createTime;

    public String getPaymentStatusName() {
        if (paymentStatus == null) return "";
        return switch (paymentStatus) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已退款";
            default -> "未知";
        };
    }
}
