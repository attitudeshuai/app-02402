package com.association.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "活动评论请求")
public class ActivityReviewDTO {

    @Schema(description = "评分: 1-5星")
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer rating;

    @Schema(description = "评论内容")
    @Size(max = 1000, message = "评论内容不能超过1000字")
    private String content;
}
