package com.association.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应结果
 *
 * @author Association Management System
 */
@Data
@Schema(description = "分页响应结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态码", example = "200")
    private int code;

    @Schema(description = "消息", example = "操作成功")
    private String message;

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "总记录数", example = "100")
    private long total;

    @Schema(description = "当前页码", example = "1")
    private long pageNum;

    @Schema(description = "每页大小", example = "10")
    private long pageSize;

    @Schema(description = "总页数", example = "10")
    private long pages;

    @Schema(description = "时间戳", example = "1707465600000")
    private long timestamp;

    public PageResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public PageResult(int code, String message, List<T> list, long total, long pageNum, long pageSize) {
        this.code = code;
        this.message = message;
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应
     */
    public static <T> PageResult<T> success(IPage<T> page) {
        return new PageResult<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
    }

    /**
     * 成功响应（带自定义列表）
     */
    public static <T> PageResult<T> success(List<T> list, long total, long pageNum, long pageSize) {
        return new PageResult<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                list,
                total,
                pageNum,
                pageSize
        );
    }

    /**
     * 空数据响应
     */
    public static <T> PageResult<T> empty(long pageNum, long pageSize) {
        return new PageResult<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                Collections.emptyList(),
                0,
                pageNum,
                pageSize
        );
    }

    /**
     * 失败响应
     */
    public static <T> PageResult<T> fail(ResultCode resultCode) {
        PageResult<T> result = new PageResult<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setList(Collections.emptyList());
        return result;
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}
