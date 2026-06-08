package com.association.controller;

import com.association.common.annotation.RequireRole;
import com.association.common.result.PageResult;
import com.association.common.result.Result;
import com.association.dto.MemberApplyDTO;
import com.association.dto.MemberDTO;
import com.association.dto.PageQuery;
import com.association.service.MemberService;
import com.association.vo.MemberVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 会员管理控制器
 *
 * @author Association Management System
 */
@Tag(name = "会员管理", description = "会员CRUD、入会申请、审批等接口")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "会员列表（分页）")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/list")
    public PageResult<MemberVO> list(PageQuery query) {
        return PageResult.success(memberService.pageList(query));
    }

    @Operation(summary = "会员详情")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/{id}")
    public Result<MemberVO> detail(@PathVariable Long id) {
        return Result.success(memberService.getDetail(id));
    }

    @Operation(summary = "新增会员")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PostMapping
    public Result<Long> add(@Valid @RequestBody MemberDTO dto) {
        return Result.success(memberService.add(dto));
    }

    @Operation(summary = "编辑会员")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MemberDTO dto) {
        memberService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除会员")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return Result.success();
    }

    @Operation(summary = "修改会员状态")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态: 1-正常 2-禁用") @RequestParam Integer status) {
        memberService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "申请入会")
    @PostMapping("/apply")
    public Result<Long> apply(@Valid @RequestBody MemberApplyDTO dto) {
        return Result.success(memberService.apply(dto));
    }

    @Operation(summary = "审批入会申请")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PostMapping("/apply/{id}/approve")
    public Result<Void> approve(
            @PathVariable Long id,
            @Parameter(description = "是否通过") @RequestParam Boolean approved,
            @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        memberService.approve(id, approved, remark);
        return Result.success();
    }
}
