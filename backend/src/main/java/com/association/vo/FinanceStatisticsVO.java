package com.association.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 财务统计 VO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "财务统计")
public class FinanceStatisticsVO {

    @Schema(description = "总收入")
    private BigDecimal totalIncome;

    @Schema(description = "总支出")
    private BigDecimal totalExpense;

    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "会费收入")
    private BigDecimal membershipIncome;

    @Schema(description = "活动费收入")
    private BigDecimal activityIncome;

    @Schema(description = "捐赠收入")
    private BigDecimal donationIncome;

    @Schema(description = "其他收入")
    private BigDecimal otherIncome;
}
