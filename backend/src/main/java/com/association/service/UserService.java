package com.association.service;

import com.association.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 *
 * @author Association Management System
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    User getByUsername(String username);

    /**
     * 根据 OpenID 查询用户
     *
     * @param openId 微信OpenID
     * @return 用户
     */
    User getByOpenId(String openId);

    /**
     * 更新最后登录信息
     *
     * @param userId 用户ID
     * @param ip     登录IP
     */
    void updateLastLogin(Long userId, String ip);
}
