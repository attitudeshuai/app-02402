package com.association.controller;

import com.association.common.annotation.RequireRole;
import com.association.common.result.PageResult;
import com.association.common.result.Result;
import com.association.dto.ActivityReviewDTO;
import com.association.dto.PageQuery;
import com.association.service.ActivityReviewService;
import com.association.vo.ActivityReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "活动评论", description = "活动评论发表、编辑、查询接口")
@RestController
@RequestMapping("/api/activity/review")
@RequiredArgsConstructor
public class ActivityReviewController {

    private final ActivityReviewService activityReviewService;

    @Operation(summary = "发表评论")
    @PostMapping("/{activityId}")
    public Result<Long> create(@PathVariable Long activityId, @Valid @RequestBody ActivityReviewDTO dto) {
        return Result.success(activityReviewService.create(activityId, dto));
    }

    @Operation(summary = "编辑评论")
    @PutMapping("/{reviewId}")
    public Result<Void> update(@PathVariable Long reviewId, @Valid @RequestBody ActivityReviewDTO dto) {
        activityReviewService.update(reviewId, dto);
        return Result.success();
    }

    @Operation(summary = "查看我的评论")
    @GetMapping("/my/{activityId}")
    public Result<ActivityReviewVO> getMyReview(@PathVariable Long activityId) {
        return Result.success(activityReviewService.getMyReview(activityId));
    }

    @Operation(summary = "评论列表（管理员）")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/list")
    public PageResult<ActivityReviewVO> list(PageQuery query,
                                             @RequestParam(required = false) Long activityId,
                                             @RequestParam(required = false) String ratingOrder) {
        return PageResult.success(activityReviewService.pageList(query, activityId, ratingOrder));
    }
}
