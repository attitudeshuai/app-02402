package com.association.mapper;

import com.association.entity.ActivityReview;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ActivityReviewMapper extends BaseMapper<ActivityReview> {

    @Select("SELECT COUNT(*) FROM biz_activity_review WHERE activity_id = #{activityId} AND deleted = 0")
    int countByActivityId(@Param("activityId") Long activityId);

    @Select("SELECT IFNULL(AVG(rating), 0) FROM biz_activity_review WHERE activity_id = #{activityId} AND deleted = 0")
    Double avgRatingByActivityId(@Param("activityId") Long activityId);
}
