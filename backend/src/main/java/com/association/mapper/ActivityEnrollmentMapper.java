package com.association.mapper;

import com.association.entity.ActivityEnrollment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 活动报名 Mapper
 *
 * @author Association Management System
 */
@Mapper
public interface ActivityEnrollmentMapper extends BaseMapper<ActivityEnrollment> {

    /**
     * 统计活动报名人数
     */
    @Select("SELECT COUNT(*) FROM biz_activity_enrollment WHERE activity_id = #{activityId}")
    int countByActivityId(@Param("activityId") Long activityId);

    /**
     * 统计活动已支付人数
     */
    @Select("SELECT COUNT(*) FROM biz_activity_enrollment WHERE activity_id = #{activityId} AND payment_status = 1")
    int countPaidByActivityId(@Param("activityId") Long activityId);
}
