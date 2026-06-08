package com.association.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财务记录实体
 *
 * @author Association Management System
 */
@Data
@TableName("biz_finance_record")
@Schema(description = "财务记录实体")
public class FinanceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "流水号")
    private String recordNo;

    @Schema(description = "类型: 1-会费 2-活动费 3-捐赠 4-其他收入 5-支出")
    private Integer type;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "关联会员ID")
    private Long memberId;

    @Schema(description = "关联活动ID")
    private Long activityId;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "第三方交易号")
    private String transactionId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "删除标记")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
