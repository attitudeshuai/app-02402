package com.association.common.annotation;

import java.lang.annotation.*;

/**
 * 角色权限注解
 *
 * @author Association Management System
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 允许访问的角色列表
     */
    String[] value() default {};

    /**
     * 是否要求登录（默认true）
     */
    boolean requireLogin() default true;
}
