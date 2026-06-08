package com.association.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求 DTO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "微信登录请求")
public class WxLoginDTO {

    @Schema(description = "微信登录code", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;
}
