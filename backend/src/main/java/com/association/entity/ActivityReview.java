package com.association.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("biz_activity_review")
@Schema(description = "活动评论实体")
public class ActivityReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "评论ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "评分: 1-5星")
    private Integer rating;

    @Schema(description = "评论内容")
    private String content;

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
