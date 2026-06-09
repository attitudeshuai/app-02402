package com.association.controller;

import com.association.common.annotation.RequireRole;
import com.association.common.result.PageResult;
import com.association.common.result.Result;
import com.association.dto.ActivityCommentDTO;
import com.association.dto.PageQuery;
import com.association.service.ActivityCommentService;
import com.association.vo.ActivityCommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "活动评论管理", description = "活动评论、评分相关接口")
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityCommentController {

    private final ActivityCommentService commentService;

    @Operation(summary = "发表活动评论")
    @PostMapping("/{id}/comment")
    public Result<Long> create(@PathVariable Long id, @Valid @RequestBody ActivityCommentDTO dto) {
        return Result.success(commentService.create(id, dto));
    }

    @Operation(summary = "编辑我的评论")
    @PutMapping("/comment/{commentId}")
    public Result<Void> update(@PathVariable Long commentId, @Valid @RequestBody ActivityCommentDTO dto) {
        commentService.update(commentId, dto);
        return Result.success();
    }

    @Operation(summary = "删除我的评论")
    @DeleteMapping("/comment/{commentId}")
    public Result<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return Result.success();
    }

    @Operation(summary = "获取我对某活动的评论")
    @GetMapping("/{id}/comment/my")
    public Result<ActivityCommentVO> getMyComment(@PathVariable Long id) {
        return Result.success(commentService.getMyComment(id));
    }

    @Operation(summary = "获取活动评论列表")
    @GetMapping("/{id}/comments")
    public Result<List<ActivityCommentVO>> listByActivity(@PathVariable Long id) {
        return Result.success(commentService.listByActivity(id));
    }

    @Operation(summary = "后台评论列表")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/admin/comments")
    public PageResult<ActivityCommentVO> adminList(
            PageQuery query,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        return PageResult.success(commentService.pageAdminList(query, activityId, sortOrder));
    }

    @Operation(summary = "管理员删除评论")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @DeleteMapping("/admin/comment/{commentId}")
    public Result<Void> adminDelete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return Result.success();
    }
}
