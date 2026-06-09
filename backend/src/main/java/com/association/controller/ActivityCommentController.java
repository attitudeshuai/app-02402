package com.association.controller;

import com.association.common.annotation.RequireRole;
import com.association.common.result.PageResult;
import com.association.common.result.Result;
import com.association.dto.ActivityCommentDTO;
import com.association.dto.PageQuery;
import com.association.service.ActivityCommentService;
import com.association.vo.ActivityCommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 活动评论控制器
 *
 * @author Association Management System
 */
@Tag(name = "活动评论", description = "活动评论相关接口")
@RestController
@RequestMapping("/api/activity-comment")
@RequiredArgsConstructor
public class ActivityCommentController {

    private final ActivityCommentService activityCommentService;

    @Operation(summary = "评论列表（管理员后台）")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/list")
    public PageResult<ActivityCommentVO> list(
            PageQuery query,
            @Parameter(description = "活动ID") @RequestParam(required = false) Long activityId,
            @Parameter(description = "排序方式: rating_asc-评分升序, rating_desc-评分降序, create_time_asc-创建时间升序, 默认创建时间降序") @RequestParam(required = false) String sortBy) {
        return PageResult.success(activityCommentService.pageList(query, activityId, sortBy));
    }

    @Operation(summary = "活动评论列表")
    @GetMapping("/activity/{activityId}")
    public PageResult<ActivityCommentVO> getCommentsByActivity(
            @PathVariable Long activityId,
            PageQuery query) {
        return PageResult.success(activityCommentService.getCommentsByActivityId(activityId, query));
    }

    @Operation(summary = "获取我的评论")
    @GetMapping("/my/{activityId}")
    public Result<ActivityCommentVO> getMyComment(@PathVariable Long activityId) {
        return Result.success(activityCommentService.getMyComment(activityId));
    }

    @Operation(summary = "发表评论")
    @PostMapping("/{activityId}")
    public Result<Long> create(
            @PathVariable Long activityId,
            @Valid @RequestBody ActivityCommentDTO dto) {
        return Result.success(activityCommentService.createComment(activityId, dto));
    }

    @Operation(summary = "编辑评论")
    @PutMapping("/{activityId}")
    public Result<Void> update(
            @PathVariable Long activityId,
            @Valid @RequestBody ActivityCommentDTO dto) {
        activityCommentService.updateComment(activityId, dto);
        return Result.success();
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        activityCommentService.deleteComment(id);
        return Result.success();
    }

    @Operation(summary = "获取活动评分统计")
    @GetMapping("/stats/{activityId}")
    public Result<Map<String, Object>> getStats(@PathVariable Long activityId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", activityCommentService.getAverageRating(activityId));
        stats.put("commentCount", activityCommentService.getCommentCount(activityId));
        return Result.success(stats);
    }
}
