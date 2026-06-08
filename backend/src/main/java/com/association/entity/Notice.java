package com.association.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告实体
 *
 * @author Association Management System
 */
@Data
@TableName("biz_notice")
@Schema(description = "公告实体")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "公告ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "类型: 1-通知 2-公告 3-新闻")
    private Integer type;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "状态: 0-草稿 1-已发布")
    private Integer status;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "创建者ID")
    private Long creatorId;

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
