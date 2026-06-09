package com.association.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "活动评论信息")
public class ActivityReviewVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "评论人姓名")
    private String reviewerName;

    @Schema(description = "评论人头像")
    private String reviewerAvatar;

    @Schema(description = "评分: 1-5星")
    private Integer rating;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
