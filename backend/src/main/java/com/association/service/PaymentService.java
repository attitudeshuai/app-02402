package com.association.service;

import java.math.BigDecimal;

/**
 * 支付服务接口
 * 
 * 说明：当前为模拟实现，提供接口预留真实对接
 *
 * @author Association Management System
 */
public interface PaymentService {

    /**
     * 发起支付
     *
     * @param orderNo 订单号
     * @param amount  金额
     * @param description 描述
     * @return 支付结果（模拟返回交易号）
     */
    String pay(String orderNo, BigDecimal amount, String description);

    /**
     * 查询支付状态
     *
     * @param transactionId 交易号
     * @return 支付状态
     */
    boolean queryPaymentStatus(String transactionId);

    /**
     * 申请退款
     *
     * @param transactionId 原交易号
     * @param amount 退款金额
     * @return 退款结果
     */
    boolean refund(String transactionId, BigDecimal amount);
}
