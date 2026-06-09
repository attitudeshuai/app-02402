package com.association.service;

import com.association.dto.ActivityCommentDTO;
import com.association.dto.PageQuery;
import com.association.entity.ActivityComment;
import com.association.vo.ActivityCommentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 活动评论服务接口
 *
 * @author Association Management System
 */
public interface ActivityCommentService extends IService<ActivityComment> {

    /**
     * 分页查询活动评论列表（管理员后台）
     */
    IPage<ActivityCommentVO> pageList(PageQuery query, Long activityId, String sortBy);

    /**
     * 获取活动评论列表
     */
    IPage<ActivityCommentVO> getCommentsByActivityId(Long activityId, PageQuery query);

    /**
     * 获取我的评论
     */
    ActivityCommentVO getMyComment(Long activityId);

    /**
     * 发表评论
     */
    Long createComment(Long activityId, ActivityCommentDTO dto);

    /**
     * 编辑评论
     */
    void updateComment(Long activityId, ActivityCommentDTO dto);

    /**
     * 删除评论
     */
    void deleteComment(Long id);

    /**
     * 获取活动平均评分
     */
    Double getAverageRating(Long activityId);

    /**
     * 获取活动评论数量
     */
    Integer getCommentCount(Long activityId);
}
