package com.association.service;

import com.association.dto.ActivityReviewDTO;
import com.association.dto.PageQuery;
import com.association.entity.ActivityReview;
import com.association.vo.ActivityReviewVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ActivityReviewService extends IService<ActivityReview> {
    Long create(Long activityId, ActivityReviewDTO dto);
    void update(Long reviewId, ActivityReviewDTO dto);
    ActivityReviewVO getMyReview(Long activityId);
    IPage<ActivityReviewVO> pageList(PageQuery query, Long activityId, String ratingOrder);
}
