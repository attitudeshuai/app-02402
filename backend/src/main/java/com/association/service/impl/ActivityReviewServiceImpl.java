package com.association.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.dto.ActivityReviewDTO;
import com.association.dto.PageQuery;
import com.association.entity.Activity;
import com.association.entity.ActivityEnrollment;
import com.association.entity.ActivityReview;
import com.association.entity.Member;
import com.association.entity.User;
import com.association.mapper.ActivityEnrollmentMapper;
import com.association.mapper.ActivityMapper;
import com.association.mapper.ActivityReviewMapper;
import com.association.mapper.MemberMapper;
import com.association.mapper.UserMapper;
import com.association.service.ActivityReviewService;
import com.association.vo.ActivityReviewVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityReviewServiceImpl extends ServiceImpl<ActivityReviewMapper, ActivityReview> implements ActivityReviewService {

    private final ActivityMapper activityMapper;
    private final ActivityEnrollmentMapper enrollmentMapper;
    private final MemberMapper memberMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long activityId, ActivityReviewDTO dto) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }
        if (activity.getStatus() != 3) {
            throw new BusinessException(ResultCode.REVIEW_NOT_ALLOWED);
        }
        Long userId = UserContext.getUserId();
        ActivityEnrollment enrollment = getEnrollment(activityId, userId);
        if (enrollment == null) {
            throw new BusinessException(ResultCode.REVIEW_NOT_ENROLLED);
        }
        ActivityReview existing = getByActivityAndUser(activityId, userId);
        if (existing != null) {
            throw new BusinessException(ResultCode.REVIEW_ALREADY_EXISTS);
        }
        Member member = memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getUserId, userId));
        ActivityReview review = new ActivityReview();
        review.setActivityId(activityId);
        review.setUserId(userId);
        review.setMemberId(member != null ? member.getId() : null);
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        save(review);
        log.info("创建活动评论: activityId={}, userId={}, rating={}", activityId, userId, dto.getRating());
        return review.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long reviewId, ActivityReviewDTO dto) {
        ActivityReview review = getById(reviewId);
        if (review == null) {
            throw new BusinessException(ResultCode.REVIEW_NOT_FOUND);
        }
        Long userId = UserContext.getUserId();
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.REVIEW_NOT_OWNER);
        }
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        updateById(review);
        log.info("更新活动评论: reviewId={}, userId={}", reviewId, userId);
    }

    @Override
    public ActivityReviewVO getMyReview(Long activityId) {
        Long userId = UserContext.getUserId();
        ActivityReview review = getByActivityAndUser(activityId, userId);
        if (review == null) {
            return null;
        }
        return toVO(review);
    }

    @Override
    public IPage<ActivityReviewVO> pageList(PageQuery query, Long activityId, String ratingOrder) {
        Page<ActivityReview> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ActivityReview> wrapper = new LambdaQueryWrapper<>();
        if (activityId != null) {
            wrapper.eq(ActivityReview::getActivityId, activityId);
        }
        if ("asc".equalsIgnoreCase(ratingOrder)) {
            wrapper.orderByAsc(ActivityReview::getRating);
        } else if ("desc".equalsIgnoreCase(ratingOrder)) {
            wrapper.orderByDesc(ActivityReview::getRating);
        } else {
            wrapper.orderByDesc(ActivityReview::getCreateTime);
        }
        Page<ActivityReview> result = page(page, wrapper);
        Map<Long, Activity> activityMap = buildActivityMap(result.getRecords());
        Map<Long, User> userMap = buildUserMap(result.getRecords());
        return result.convert(review -> toVO(review, activityMap, userMap));
    }

    private ActivityReview getByActivityAndUser(Long activityId, Long userId) {
        return getOne(new LambdaQueryWrapper<ActivityReview>()
                .eq(ActivityReview::getActivityId, activityId)
                .eq(ActivityReview::getUserId, userId));
    }

    private ActivityEnrollment getEnrollment(Long activityId, Long userId) {
        return enrollmentMapper.selectOne(
                new LambdaQueryWrapper<ActivityEnrollment>()
                        .eq(ActivityEnrollment::getActivityId, activityId)
                        .eq(ActivityEnrollment::getUserId, userId));
    }

    private ActivityReviewVO toVO(ActivityReview review) {
        return toVO(review,
                Collections.singletonMap(review.getActivityId(), activityMapper.selectById(review.getActivityId())),
                Collections.singletonMap(review.getUserId(), userMapper.selectById(review.getUserId())));
    }

    private ActivityReviewVO toVO(ActivityReview review, Map<Long, Activity> activityMap, Map<Long, User> userMap) {
        ActivityReviewVO vo = BeanUtil.copyProperties(review, ActivityReviewVO.class);
        Activity activity = activityMap.get(review.getActivityId());
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
        }
        User user = userMap.get(review.getUserId());
        if (user != null) {
            vo.setReviewerName(user.getNickname());
            vo.setReviewerAvatar(user.getAvatar());
        }
        return vo;
    }

    private Map<Long, Activity> buildActivityMap(List<ActivityReview> reviews) {
        Set<Long> activityIds = reviews.stream().map(ActivityReview::getActivityId).collect(Collectors.toSet());
        if (activityIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return activityMapper.selectBatchIds(activityIds).stream()
                .collect(Collectors.toMap(Activity::getId, Function.identity()));
    }

    private Map<Long, User> buildUserMap(List<ActivityReview> reviews) {
        Set<Long> userIds = reviews.stream().map(ActivityReview::getUserId).collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
