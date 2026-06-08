package com.association.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.dto.LoginDTO;
import com.association.dto.WxLoginDTO;
import com.association.entity.User;
import com.association.service.AuthService;
import com.association.service.UserService;
import com.association.service.WechatService;
import com.association.util.JwtUtil;
import com.association.vo.LoginVO;
import com.association.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现
 *
 * @author Association Management System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final WechatService wechatService;
    private final JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto, String ip) {
        // 查询用户
        User user = userService.getByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }

        // 验证密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 检查状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 更新登录信息
        userService.updateLastLogin(user.getId(), ip);

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        return buildLoginVO(user, token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO wxLogin(WxLoginDTO dto, String ip) {
        // 获取微信 OpenID
        String openId = wechatService.getOpenId(dto.getCode());

        // 查询用户
        User user = userService.getByOpenId(openId);

        if (user == null) {
            // 新用户注册
            user = new User();
            user.setOpenId(openId);
            user.setNickname(dto.getNickname() != null ? dto.getNickname() : "微信用户");
            user.setAvatar(dto.getAvatar());
            user.setRole("MEMBER");  // 默认为会员角色
            user.setStatus(1);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(ip);
            userService.save(user);
            log.info("新用户注册: openId={}, nickname={}", openId, user.getNickname());
        } else {
            // 检查状态
            if (user.getStatus() == 0) {
                throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
            }
            // 更新用户信息
            if (dto.getNickname() != null) {
                user.setNickname(dto.getNickname());
            }
            if (dto.getAvatar() != null) {
                user.setAvatar(dto.getAvatar());
            }
            userService.updateLastLogin(user.getId(), ip);
        }

        // 生成 Token
        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        return buildLoginVO(user, token);
    }

    @Override
    public UserVO getProfile() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.ACCOUNT_NOT_FOUND);
        }

        UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
        vo.setRoleName(getRoleName(user.getRole()));
        return vo;
    }

    @Override
    public String refreshToken(String token) {
        return jwtUtil.refreshToken(token);
    }

    /**
     * 构建登录响应
     */
    private LoginVO buildLoginVO(User user, String token) {
        return LoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .roleName(getRoleName(user.getRole()))
                .build();
    }

    /**
     * 获取角色名称
     */
    private String getRoleName(String role) {
        return switch (role) {
            case "ADMIN" -> "管理员";
            case "PRESIDENT" -> "会长";
            case "MEMBER" -> "会员";
            default -> "未知";
        };
    }
}
