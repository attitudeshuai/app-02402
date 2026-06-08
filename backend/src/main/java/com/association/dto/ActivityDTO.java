package com.association.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动 DTO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "活动请求")
public class ActivityDTO {

    @Schema(description = "活动标题")
    @NotBlank(message = "活动标题不能为空")
    private String title;

    @Schema(description = "活动描述")
    private String description;

    @Schema(description = "封面图")
    private String coverImage;

    @Schema(description = "活动地点")
    @NotBlank(message = "活动地点不能为空")
    private String location;

    @Schema(description = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @Schema(description = "报名开始时间")
    private LocalDateTime enrollStartTime;

    @Schema(description = "报名截止时间")
    private LocalDateTime enrollEndTime;

    @Schema(description = "最大参与人数")
    private Integer maxParticipants;

    @Schema(description = "活动费用 (0为免费)")
    private BigDecimal fee;
}
