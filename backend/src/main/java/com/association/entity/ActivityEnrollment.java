package com.association.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 活动报名实体
 *
 * @author Association Management System
 */
@Data
@TableName("biz_activity_enrollment")
@Schema(description = "活动报名实体")
public class ActivityEnrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "报名ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "支付状态: 0-待支付 1-已支付 2-已退款")
    private Integer paymentStatus;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "交易号")
    private String transactionId;

    @Schema(description = "签到状态: 0-未签到 1-已签到")
    private Integer checkInStatus;

    @Schema(description = "签到时间")
    private LocalDateTime checkInTime;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
