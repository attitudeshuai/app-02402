package com.association.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员实体
 *
 * @author Association Management System
 */
@Data
@TableName("biz_member")
@Schema(description = "会员实体")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会员ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "关联用户ID")
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

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "删除标记")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
