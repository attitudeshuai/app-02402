package com.association.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * 会员 DTO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "会员请求")
public class MemberDTO {

    @Schema(description = "真实姓名")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Schema(description = "手机号")
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "所在企业")
    private String company;

    @Schema(description = "职位")
    private String position;

    @Schema(description = "有效期至")
    private LocalDate expireDate;

    @Schema(description = "备注")
    private String remark;
}
