package com.association.service.impl;

import cn.hutool.core.util.IdUtil;
import com.association.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 模拟支付服务实现
 * 
 * 说明：
 * - 当前为模拟实现，直接返回成功
 * - 生产环境可替换为微信支付实现
 * 
 * 扩展方式：
 * 1. 实现 PaymentService 接口
 * 2. 使用 @Primary 或 @ConditionalOnProperty 注解控制实现类切换
 *
 * @author Association Management System
 */
@Slf4j
@Service
public class MockPaymentServiceImpl implements PaymentService {

    @Override
    public String pay(String orderNo, BigDecimal amount, String description) {
        // 模拟支付过程
        log.info("【模拟支付】订单号: {}, 金额: {}, 描述: {}", orderNo, amount, description);
        
        // 模拟网络延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 生成模拟交易号
        String transactionId = "MOCK_" + IdUtil.getSnowflakeNextIdStr();
        
        log.info("【模拟支付】支付成功，交易号: {}", transactionId);
        return transactionId;
    }

    @Override
    public boolean queryPaymentStatus(String transactionId) {
        // 模拟查询
        log.info("【模拟支付】查询支付状态，交易号: {}", transactionId);
        
        // 模拟：所有交易都是成功的
        return true;
    }

    @Override
    public boolean refund(String transactionId, BigDecimal amount) {
        // 模拟退款
        log.info("【模拟支付】申请退款，交易号: {}, 金额: {}", transactionId, amount);
        
        // 模拟：退款总是成功
        log.info("【模拟支付】退款成功");
        return true;
    }
}
