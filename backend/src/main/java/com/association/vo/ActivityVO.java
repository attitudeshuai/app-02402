package com.association.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动 VO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "活动信息")
public class ActivityVO {

    @Schema(description = "活动ID")
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

    @Schema(description = "已报名人数")
    private Integer enrolledCount;

    @Schema(description = "活动费用")
    private BigDecimal fee;

    @Schema(description = "状态: 0-草稿 1-报名中 2-进行中 3-已结束 4-已取消")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "创建者ID")
    private Long creatorId;

    @Schema(description = "创建者名称")
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "是否已报名（当前用户）")
    private Boolean enrolled;

    @Schema(description = "是否已支付（当前用户）")
    private Boolean paid;

    @Schema(description = "是否已签到（当前用户）")
    private Boolean checkedIn;

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "草稿";
            case 1 -> "报名中";
            case 2 -> "进行中";
            case 3 -> "已结束";
            case 4 -> "已取消";
            default -> "未知";
        };
    }
}
