package com.association.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 入会申请 DTO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "入会申请请求")
public class MemberApplyDTO {

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

    @Schema(description = "申请备注")
    private String remark;
}
