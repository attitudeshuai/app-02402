package com.association.common.context;

import lombok.Data;

/**
 * 用户上下文 - 线程本地存储当前用户信息
 *
 * @author Association Management System
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户信息
     */
    public static void setUser(Long userId, String role) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setRole(role);
        USER_HOLDER.set(userInfo);
    }

    /**
     * 获取当前用户信息
     */
    public static UserInfo getUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static String getRole() {
        UserInfo userInfo = USER_HOLDER.get();
        return userInfo != null ? userInfo.getRole() : null;
    }

    /**
     * 清除当前用户信息
     */
    public static void clear() {
        USER_HOLDER.remove();
    }

    /**
     * 判断是否是管理员
     */
    public static boolean isAdmin() {
        return "ADMIN".equals(getRole());
    }

    /**
     * 判断是否是会长
     */
    public static boolean isPresident() {
        return "PRESIDENT".equals(getRole());
    }

    /**
     * 判断是否是会员
     */
    public static boolean isMember() {
        return "MEMBER".equals(getRole());
    }

    /**
     * 判断是否有管理权限（管理员或会长）
     */
    public static boolean hasManagePermission() {
        String role = getRole();
        return "ADMIN".equals(role) || "PRESIDENT".equals(role);
    }

    /**
     * 用户信息
     */
    @Data
    public static class UserInfo {
        private Long userId;
        private String role;
    }
}
