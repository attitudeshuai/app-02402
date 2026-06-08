package com.association.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 微信服务实现
 * 
 * 说明：当前为模拟实现 + 真实接口预留
 * - 测试环境：生成模拟 OpenID
 * - 生产环境：配置真实 AppID/AppSecret 后自动使用真实接口
 *
 * @author Association Management System
 */
@Slf4j
@Service
public class WechatServiceImpl implements WechatService {

    @Value("${app.wechat.app-id}")
    private String appId;

    @Value("${app.wechat.app-secret}")
    private String appSecret;

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    public String getOpenId(String code) {
        // 判断是否为测试环境（AppID 以 wx_test 开头）
        if (appId.startsWith("wx_test")) {
            log.info("微信登录[模拟模式]: code={}", code);
            // 模拟生成 OpenID
            String mockOpenId = "mock_openid_" + IdUtil.simpleUUID().substring(0, 16);
            log.info("微信登录[模拟模式]: 生成模拟 OpenID={}", mockOpenId);
            return mockOpenId;
        }

        // 真实微信登录
        log.info("微信登录[真实模式]: code={}", code);
        
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                WX_LOGIN_URL, appId, appSecret, code);

        try {
            String response = HttpUtil.get(url, 5000);
            log.debug("微信登录响应: {}", response);

            JSONObject json = JSONUtil.parseObj(response);

            // 检查错误
            if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
                log.error("微信登录失败: errcode={}, errmsg={}", 
                        json.getInt("errcode"), json.getStr("errmsg"));
                throw new BusinessException(ResultCode.WECHAT_LOGIN_FAILED, json.getStr("errmsg"));
            }

            String openId = json.getStr("openid");
            if (openId == null) {
                throw new BusinessException(ResultCode.WECHAT_LOGIN_FAILED, "获取OpenID失败");
            }

            log.info("微信登录成功: openId={}", openId);
            return openId;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录异常", e);
            throw new BusinessException(ResultCode.WECHAT_LOGIN_FAILED, "微信服务异常");
        }
    }
}
