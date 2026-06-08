package com.association.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告 VO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "公告信息")
public class NoticeVO {

    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "类型: 1-通知 2-公告 3-新闻")
    private Integer type;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "状态: 0-草稿 1-已发布")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "创建者ID")
    private Long creatorId;

    @Schema(description = "创建者名称")
    private String creatorName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "是否已读（当前用户）")
    private Boolean read;

    public String getTypeName() {
        if (type == null) return "";
        return switch (type) {
            case 1 -> "通知";
            case 2 -> "公告";
            case 3 -> "新闻";
            default -> "未知";
        };
    }

    public String getStatusName() {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "草稿";
            case 1 -> "已发布";
            default -> "未知";
        };
    }
}
