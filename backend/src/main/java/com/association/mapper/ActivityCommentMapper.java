package com.association.mapper;

import com.association.entity.ActivityComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ActivityCommentMapper extends BaseMapper<ActivityComment> {

    @Select("SELECT COUNT(*) FROM biz_activity_comment WHERE activity_id = #{activityId}")
    int countByActivityId(@Param("activityId") Long activityId);

    @Select("SELECT AVG(rating) FROM biz_activity_comment WHERE activity_id = #{activityId}")
    Double getAverageRating(@Param("activityId") Long activityId);
}
