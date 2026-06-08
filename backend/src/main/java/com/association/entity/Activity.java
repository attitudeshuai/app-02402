package com.association.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动实体
 *
 * @author Association Management System
 */
@Data
@TableName("biz_activity")
@Schema(description = "活动实体")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "活动ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "活动标题")
    private String title;

    @Schema(description = "活动描述")
    private String description;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "活动地点")
    private String location;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "报名开始时间")
    private LocalDateTime enrollStartTime;

    @Schema(description = "报名截止时间")
    private LocalDateTime enrollEndTime;

    @Schema(description = "最大参与人数")
    private Integer maxParticipants;

    @Schema(description = "活动费用")
    private BigDecimal fee;

    @Schema(description = "状态: 0-草稿 1-报名中 2-进行中 3-已结束 4-已取消")
    private Integer status;

    @Schema(description = "创建者ID")
    private Long creatorId;

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
