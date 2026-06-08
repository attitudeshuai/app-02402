package com.association.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告已读记录实体
 *
 * @author Association Management System
 */
@Data
@TableName("biz_notice_read")
@Schema(description = "公告已读记录实体")
public class NoticeRead implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "公告ID")
    private Long noticeId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "阅读时间")
    private LocalDateTime readTime;
}
