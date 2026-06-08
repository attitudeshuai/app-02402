package com.association.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务记录 DTO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "财务记录请求")
public class FinanceRecordDTO {

    @Schema(description = "类型: 1-会费 2-活动费 3-捐赠 4-其他收入 5-支出")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "金额")
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "关联会员ID")
    private Long memberId;

    @Schema(description = "关联活动ID")
    private Long activityId;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "备注")
    private String remark;
}
