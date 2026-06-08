package com.association.service;

/**
 * 微信服务接口
 *
 * @author Association Management System
 */
public interface WechatService {

    /**
     * 通过 code 获取 OpenID
     *
     * @param code 微信登录code
     * @return OpenID
     */
    String getOpenId(String code);
}
