package com.association.service;

import com.association.dto.LoginDTO;
import com.association.dto.WxLoginDTO;
import com.association.vo.LoginVO;
import com.association.vo.UserVO;

/**
 * 认证服务接口
 *
 * @author Association Management System
 */
public interface AuthService {

    /**
     * 账号密码登录
     *
     * @param dto 登录请求
     * @param ip  客户端IP
     * @return 登录响应
     */
    LoginVO login(LoginDTO dto, String ip);

    /**
     * 微信小程序登录
     *
     * @param dto 登录请求
     * @param ip  客户端IP
     * @return 登录响应
     */
    LoginVO wxLogin(WxLoginDTO dto, String ip);

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    UserVO getProfile();

    /**
     * 刷新 Token
     *
     * @param token 原 Token
     * @return 新 Token
     */
    String refreshToken(String token);
}
