package com.association.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 活动评论查询参数
 *
 * @author Association Management System
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "活动评论查询参数")
public class ActivityCommentQuery extends PageQuery {

    @Schema(description = "活动ID（按活动筛选）")
    private Long activityId;

    @Schema(description = "评分（按评分筛选）")
    private Integer rating;

    @Schema(description = "排序字段: createTime-时间 rating-评分")
    private String sortBy;

    @Schema(description = "排序方向: asc-升序 desc-降序")
    private String sortOrder;
}
