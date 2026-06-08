package com.association.controller;

import com.association.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 首页控制器
 *
 * @author Association Management System
 */
@Tag(name = "首页", description = "系统信息接口")
@RestController
public class IndexController {

    @Value("${spring.application.name:association-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Operation(summary = "系统信息")
    @GetMapping("/")
    public Result<Map<String, Object>> index() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", "协会管理系统 API");
        info.put("version", "1.0.0");
        info.put("description", "行业协会管理微信小程序后端服务");
        info.put("profile", activeProfile);
        info.put("time", LocalDateTime.now().toString());
        info.put("docs", "/doc.html");
        info.put("health", "/actuator/health");
        return Result.success(info);
    }

    @Operation(summary = "API 信息")
    @GetMapping("/api")
    public Result<Map<String, Object>> api() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("auth", "/api/auth/* - 认证管理");
        info.put("member", "/api/member/* - 会员管理");
        info.put("activity", "/api/activity/* - 活动管理");
        info.put("finance", "/api/finance/* - 财务管理");
        info.put("notice", "/api/notice/* - 公告通知");
        return Result.success(info);
    }
}
