package com.association.mapper;

import com.association.entity.ActivityComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 活动评论 Mapper
 *
 * @author Association Management System
 */
@Mapper
public interface ActivityCommentMapper extends BaseMapper<ActivityComment> {

    /**
     * 统计活动评论数量
     */
    @Select("SELECT COUNT(*) FROM biz_activity_comment WHERE activity_id = #{activityId} AND deleted = 0")
    int countByActivityId(@Param("activityId") Long activityId);

    /**
     * 计算活动平均评分
     */
    @Select("SELECT AVG(rating) FROM biz_activity_comment WHERE activity_id = #{activityId} AND deleted = 0")
    Double avgRatingByActivityId(@Param("activityId") Long activityId);
}
