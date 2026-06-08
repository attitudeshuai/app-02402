package com.association.service.impl;

import com.association.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 模拟短信服务实现
 * 
 * 说明：
 * - 当前为模拟实现，短信内容记录到日志
 * - 生产环境可替换为阿里云短信/腾讯云短信实现
 * 
 * 扩展方式：
 * 1. 实现 SmsService 接口
 * 2. 使用 @Primary 或 @ConditionalOnProperty 注解控制实现类切换
 *
 * @author Association Management System
 */
@Slf4j
@Service
public class MockSmsServiceImpl implements SmsService {

    @Override
    public boolean send(String phone, String content) {
        log.info("====================================");
        log.info("【模拟短信】");
        log.info("收件人: {}", phone);
        log.info("内容: {}", content);
        log.info("====================================");
        
        // 模拟发送延迟
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("【模拟短信】发送成功");
        return true;
    }

    @Override
    public boolean sendTemplate(String phone, String templateId, String... params) {
        log.info("====================================");
        log.info("【模拟短信-模板】");
        log.info("收件人: {}", phone);
        log.info("模板ID: {}", templateId);
        log.info("参数: {}", Arrays.toString(params));
        log.info("====================================");
        
        // 模拟发送延迟
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("【模拟短信】发送成功");
        return true;
    }
}
