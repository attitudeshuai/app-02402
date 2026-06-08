package com.association.controller;

import com.association.common.result.Result;
import com.association.dto.LoginDTO;
import com.association.dto.WxLoginDTO;
import com.association.service.AuthService;
import com.association.vo.LoginVO;
import com.association.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author Association Management System
 */
@Tag(name = "认证管理", description = "登录、登出、Token刷新等接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);
        LoginVO vo = authService.login(dto, ip);
        return Result.success(vo);
    }

    @Operation(summary = "微信小程序登录")
    @PostMapping("/wx-login")
    public Result<LoginVO> wxLogin(@Valid @RequestBody WxLoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);
        LoginVO vo = authService.wxLogin(dto, ip);
        return Result.success(vo);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        UserVO vo = authService.getProfile();
        return Result.success(vo);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String newToken = authService.refreshToken(token);
        return Result.success(newToken);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // 客户端删除 Token 即可，服务端无需处理
        // 如需实现 Token 黑名单，可在此处添加逻辑
        return Result.success();
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
