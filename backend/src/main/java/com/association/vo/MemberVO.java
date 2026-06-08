package com.association.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员 VO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "会员信息")
public class MemberVO {

    @Schema(description = "会员ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "会员编号")
    private String memberNo;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "所在企业")
    private String company;

    @Schema(description = "职位")
    private String position;

    @Schema(description = "入会日期")
    private LocalDate joinDate;

    @Schema(description = "有效期至")
    private LocalDate expireDate;

    @Schema(description = "状态: 0-待审核 1-正常 2-禁用 3-已过期")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "待审核";
            case 1 -> "正常";
            case 2 -> "禁用";
            case 3 -> "已过期";
            default -> "未知";
        };
    }
}
