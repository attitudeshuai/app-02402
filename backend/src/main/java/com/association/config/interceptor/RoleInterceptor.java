package com.association.config.interceptor;

import com.association.common.annotation.RequireRole;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 角色权限拦截器
 *
 * @author Association Management System
 */
@Slf4j
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 获取方法或类上的注解
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        // 没有注解则放行
        if (requireRole == null) {
            return true;
        }

        // 不要求登录则放行
        if (!requireRole.requireLogin()) {
            return true;
        }

        // 获取当前用户角色
        String currentRole = UserContext.getRole();
        if (currentRole == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 未指定角色限制则只要登录就放行
        String[] allowedRoles = requireRole.value();
        if (allowedRoles.length == 0) {
            return true;
        }

        // 检查角色权限
        boolean hasPermission = Arrays.asList(allowedRoles).contains(currentRole);
        if (!hasPermission) {
            log.warn("权限不足: userId={}, role={}, required={}", 
                    UserContext.getUserId(), currentRole, Arrays.toString(allowedRoles));
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        return true;
    }
}
