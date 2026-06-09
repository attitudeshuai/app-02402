package com.association.controller;

import com.association.common.annotation.RequireRole;
import com.association.common.result.PageResult;
import com.association.common.result.Result;
import com.association.dto.ActivityCommentDTO;
import com.association.dto.ActivityCommentQuery;
import com.association.dto.ActivityDTO;
import com.association.dto.PageQuery;
import com.association.service.ActivityCommentService;
import com.association.service.ActivityService;
import com.association.vo.ActivityCommentVO;
import com.association.vo.ActivityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动管理控制器
 *
 * @author Association Management System
 */
@Tag(name = "活动管理", description = "活动CRUD、报名、签到等接口")
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityCommentService activityCommentService;

    @Operation(summary = "活动列表")
    @GetMapping("/list")
    public PageResult<ActivityVO> list(PageQuery query) {
        return PageResult.success(activityService.pageList(query));
    }

    @Operation(summary = "活动详情")
    @GetMapping("/{id}")
    public Result<ActivityVO> detail(@PathVariable Long id) {
        return Result.success(activityService.getDetail(id));
    }

    @Operation(summary = "创建活动")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ActivityDTO dto) {
        return Result.success(activityService.create(dto));
    }

    @Operation(summary = "编辑活动")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ActivityDTO dto) {
        activityService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除活动")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        activityService.delete(id);
        return Result.success();
    }

    @Operation(summary = "发布活动")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        activityService.publish(id);
        return Result.success();
    }

    @Operation(summary = "取消活动")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        activityService.cancel(id);
        return Result.success();
    }

    @Operation(summary = "报名活动")
    @PostMapping("/{id}/enroll")
    public Result<Void> enroll(@PathVariable Long id) {
        activityService.enroll(id);
        return Result.success();
    }

    @Operation(summary = "取消报名")
    @PostMapping("/{id}/enroll/cancel")
    public Result<Void> cancelEnroll(@PathVariable Long id) {
        activityService.cancelEnroll(id);
        return Result.success();
    }

    @Operation(summary = "活动签到")
    @PostMapping("/{id}/check-in")
    public Result<Void> checkIn(@PathVariable Long id) {
        activityService.checkIn(id);
        return Result.success();
    }

    @Operation(summary = "报名列表")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/{id}/enrollments")
    public Result<List<?>> getEnrollments(@PathVariable Long id) {
        return Result.success(activityService.getEnrollments(id));
    }

    @Operation(summary = "我的活动")
    @GetMapping("/my")
    public Result<List<ActivityVO>> getMyActivities() {
        return Result.success(activityService.getMyActivities());
    }

    // ==================== 活动评论 ====================

    @Operation(summary = "发表活动评论")
    @PostMapping("/{id}/comment")
    public Result<Long> createComment(@PathVariable Long id, @Valid @RequestBody ActivityCommentDTO dto) {
        return Result.success(activityCommentService.create(id, dto));
    }

    @Operation(summary = "编辑自己的活动评论")
    @PutMapping("/comment/{commentId}")
    public Result<Void> updateComment(@PathVariable Long commentId, @Valid @RequestBody ActivityCommentDTO dto) {
        activityCommentService.update(commentId, dto);
        return Result.success();
    }

    @Operation(summary = "删除活动评论")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @DeleteMapping("/comment/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId) {
        activityCommentService.delete(commentId);
        return Result.success();
    }

    @Operation(summary = "活动评论列表")
    @GetMapping("/{id}/comments")
    public Result<List<ActivityCommentVO>> listComments(@PathVariable Long id) {
        return Result.success(activityCommentService.listByActivity(id));
    }

    @Operation(summary = "我对活动的评论")
    @GetMapping("/{id}/comment/my")
    public Result<ActivityCommentVO> getMyComment(@PathVariable Long id) {
        return Result.success(activityCommentService.getMyComment(id));
    }

    @Operation(summary = "评论列表（管理端，支持按活动筛选、按评分排序）")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/comments")
    public PageResult<ActivityCommentVO> pageComments(ActivityCommentQuery query) {
        return PageResult.success(activityCommentService.pageList(query));
    }
}
