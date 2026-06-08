package com.association.controller;

import com.association.common.annotation.RequireRole;
import com.association.common.result.PageResult;
import com.association.common.result.Result;
import com.association.dto.FinanceRecordDTO;
import com.association.dto.PageQuery;
import com.association.service.FinanceService;
import com.association.vo.FinanceRecordVO;
import com.association.vo.FinanceStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 财务管理控制器
 *
 * @author Association Management System
 */
@Tag(name = "财务管理", description = "财务记录、统计、缴费等接口")
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @Operation(summary = "财务记录列表")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/records")
    public PageResult<FinanceRecordVO> list(PageQuery query) {
        return PageResult.success(financeService.pageList(query));
    }

    @Operation(summary = "财务记录详情")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/record/{id}")
    public Result<FinanceRecordVO> detail(@PathVariable Long id) {
        return Result.success(financeService.getDetail(id));
    }

    @Operation(summary = "新增财务记录")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @PostMapping("/record")
    public Result<Long> add(@Valid @RequestBody FinanceRecordDTO dto) {
        return Result.success(financeService.add(dto));
    }

    @Operation(summary = "财务统计")
    @RequireRole({"ADMIN", "PRESIDENT"})
    @GetMapping("/statistics")
    public Result<FinanceStatisticsVO> statistics() {
        return Result.success(financeService.getStatistics());
    }

    @Operation(summary = "我的缴费记录")
    @GetMapping("/my-records")
    public Result<List<FinanceRecordVO>> myRecords() {
        return Result.success(financeService.getMyRecords());
    }

    @Operation(summary = "缴纳会费")
    @PostMapping("/pay/membership")
    public Result<Void> payMembership() {
        financeService.payMembership();
        return Result.success();
    }

    @Operation(summary = "缴纳活动费")
    @PostMapping("/pay/activity/{id}")
    public Result<Void> payActivity(@PathVariable Long id) {
        financeService.payActivity(id);
        return Result.success();
    }
}
