package com.association.service;

import com.association.dto.ActivityCommentDTO;
import com.association.dto.ActivityCommentQuery;
import com.association.entity.ActivityComment;
import com.association.vo.ActivityCommentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 活动评论服务接口
 *
 * @author Association Management System
 */
public interface ActivityCommentService extends IService<ActivityComment> {

    /**
     * 发表评论
     */
    Long create(Long activityId, ActivityCommentDTO dto);

    /**
     * 编辑自己的评论
     */
    void update(Long id, ActivityCommentDTO dto);

    /**
     * 删除评论（管理员）
     */
    void delete(Long id);

    /**
     * 获取活动评论列表（按活动）
     */
    List<ActivityCommentVO> listByActivity(Long activityId);

    /**
     * 管理员分页查询所有评论（支持按活动筛选、按评分排序）
     */
    IPage<ActivityCommentVO> pageList(ActivityCommentQuery query);

    /**
     * 获取当前用户对某个活动的评论
     */
    ActivityCommentVO getMyComment(Long activityId);
}
