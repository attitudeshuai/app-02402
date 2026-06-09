package com.association.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "活动评论信息")
public class ActivityCommentVO {

    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "活动ID")
    private Long activityId;

    @Schema(description = "活动标题")
    private String activityTitle;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "评论人昵称")
    private String nickname;

    @Schema(description = "评论人头像")
    private String avatar;

    @Schema(description = "评论人真实姓名")
    private String realName;

    @Schema(description = "评分: 1-5星")
    private Integer rating;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "是否是当前用户的评论")
    private Boolean isOwner;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
