package com.association.controller;

import com.association.common.annotation.RequireRole;
import com.association.common.result.PageResult;
import com.association.common.result.Result;
import com.association.dto.NoticeDTO;
import com.association.dto.PageQuery;
import com.association.service.NoticeService;
import com.association.vo.NoticeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公告通知控制器
 *
 * @author Association Management System
 */
@Tag(name = "公告通知", description = "公告CRUD、已读标记等接口")
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "公告列表")
    @GetMapping("/list")
    public PageResult<NoticeVO> list(PageQuery query) {
        return PageResult.success(noticeService.pageList(query));
    }

    @Operation(summary = "公告详情")
    @GetMapping("/{id}")
    public Result<NoticeVO> detail(@PathVariable Long id) {
        return Result.success(noticeService.getDetail(id));
    }

    @Operation(summary = "发布公告")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PostMapping
    public Result<Long> create(@Valid @RequestBody NoticeDTO dto) {
        return Result.success(noticeService.create(dto));
    }

    @Operation(summary = "编辑公告")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody NoticeDTO dto) {
        noticeService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除公告")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success();
    }

    @Operation(summary = "标记已读")
    @PostMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        noticeService.markRead(id);
        return Result.success();
    }

    @Operation(summary = "未读公告数量")
    @GetMapping("/unread-count")
    public Result<Integer> unreadCount() {
        return Result.success(noticeService.getUnreadCount());
    }
}
