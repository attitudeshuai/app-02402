package com.association.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 公告 DTO
 *
 * @author Association Management System
 */
@Data
@Schema(description = "公告请求")
public class NoticeDTO {

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "类型: 1-通知 2-公告 3-新闻")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "是否置顶")
    private Boolean isTop = false;

    @Schema(description = "是否立即发布")
    private Boolean publish = false;
}
