package com.association.service;

import com.association.dto.ActivityDTO;
import com.association.dto.PageQuery;
import com.association.entity.Activity;
import com.association.vo.ActivityVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 活动服务接口
 *
 * @author Association Management System
 */
public interface ActivityService extends IService<Activity> {

    /**
     * 分页查询活动列表
     */
    IPage<ActivityVO> pageList(PageQuery query);

    /**
     * 获取活动详情
     */
    ActivityVO getDetail(Long id);

    /**
     * 创建活动
     */
    Long create(ActivityDTO dto);

    /**
     * 编辑活动
     */
    void update(Long id, ActivityDTO dto);

    /**
     * 删除活动
     */
    void delete(Long id);

    /**
     * 发布活动
     */
    void publish(Long id);

    /**
     * 取消活动
     */
    void cancel(Long id);

    /**
     * 报名活动
     */
    void enroll(Long activityId);

    /**
     * 取消报名
     */
    void cancelEnroll(Long activityId);

    /**
     * 活动签到
     */
    void checkIn(Long activityId);

    /**
     * 获取活动报名列表
     */
    List<?> getEnrollments(Long activityId);

    /**
     * 获取我的活动列表
     */
    List<ActivityVO> getMyActivities();
}
