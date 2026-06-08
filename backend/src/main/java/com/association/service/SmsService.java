package com.association.service;

/**
 * 短信服务接口
 * 
 * 说明：当前为模拟实现，提供接口预留真实对接
 *
 * @author Association Management System
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @param phone   手机号
     * @param content 短信内容
     * @return 是否发送成功
     */
    boolean send(String phone, String content);

    /**
     * 发送模板短信
     *
     * @param phone      手机号
     * @param templateId 模板ID
     * @param params     模板参数
     * @return 是否发送成功
     */
    boolean sendTemplate(String phone, String templateId, String... params);
}
