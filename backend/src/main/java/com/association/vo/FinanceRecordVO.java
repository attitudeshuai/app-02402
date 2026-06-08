package com.association.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务记录 VO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "财务记录")
public class FinanceRecordVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "流水号")
    private String recordNo;

    @Schema(description = "类型: 1-会费 2-活动费 3-捐赠 4-其他收入 5-支出")
    private Integer type;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "会员姓名")
    private String memberName;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "交易号")
    private String transactionId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "操作人")
    private String operatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public String getTypeName() {
        if (type == null) return "";
        return switch (type) {
            case 1 -> "会费";
            case 2 -> "活动费";
            case 3 -> "捐赠";
            case 4 -> "其他收入";
            case 5 -> "支出";
            default -> "未知";
        };
    }
}
