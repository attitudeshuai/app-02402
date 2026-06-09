package com.association.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.dto.ActivityCommentDTO;
import com.association.dto.PageQuery;
import com.association.entity.*;
import com.association.mapper.ActivityCommentMapper;
import com.association.mapper.ActivityEnrollmentMapper;
import com.association.service.*;
import com.association.vo.ActivityCommentVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityCommentServiceImpl extends ServiceImpl<ActivityCommentMapper, ActivityComment> implements ActivityCommentService {

    private final ActivityService activityService;
    private final ActivityEnrollmentMapper enrollmentMapper;
    private final MemberService memberService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long activityId, ActivityCommentDTO dto) {
        Long userId = UserContext.getUserId();

        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        if (activity.getStatus() != 3) {
            throw new BusinessException(ResultCode.COMMENT_NOT_ALLOWED);
        }

        ActivityEnrollment enrollment = enrollmentMapper.selectOne(
                new LambdaQueryWrapper<ActivityEnrollment>()
                        .eq(ActivityEnrollment::getActivityId, activityId)
                        .eq(ActivityEnrollment::getUserId, userId));
        if (enrollment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_ENROLLED);
        }

        ActivityComment existing = getOne(
                new LambdaQueryWrapper<ActivityComment>()
                        .eq(ActivityComment::getActivityId, activityId)
                        .eq(ActivityComment::getUserId, userId));
        if (existing != null) {
            throw new BusinessException(ResultCode.COMMENT_ALREADY_EXISTS);
        }

        Member member = memberService.getByUserId(userId);

        ActivityComment comment = new ActivityComment();
        comment.setActivityId(activityId);
        comment.setUserId(userId);
        comment.setMemberId(member != null ? member.getId() : null);
        comment.setRating(dto.getRating());
        comment.setContent(dto.getContent());

        save(comment);
        log.info("创建活动评论: activityId={}, userId={}, commentId={}", activityId, userId, comment.getId());
        return comment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long commentId, ActivityCommentDTO dto) {
        Long userId = UserContext.getUserId();

        ActivityComment comment = getById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.COMMENT_NOT_OWNER);
        }

        comment.setRating(dto.getRating());
        comment.setContent(dto.getContent());
        updateById(comment);
        log.info("更新活动评论: commentId={}, userId={}", commentId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long commentId) {
        Long userId = UserContext.getUserId();

        ActivityComment comment = getById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }

        if (!comment.getUserId().equals(userId) && !UserContext.hasManagePermission()) {
            throw new BusinessException(ResultCode.COMMENT_NOT_OWNER);
        }

        removeById(commentId);
        log.info("删除活动评论: commentId={}, userId={}", commentId, userId);
    }

    @Override
    public ActivityCommentVO getMyComment(Long activityId) {
        Long userId = UserContext.getUserId();

        ActivityComment comment = getOne(
                new LambdaQueryWrapper<ActivityComment>()
                        .eq(ActivityComment::getActivityId, activityId)
                        .eq(ActivityComment::getUserId, userId));
        if (comment == null) {
            return null;
        }
        return convertToVO(comment, userId);
    }

    @Override
    public List<ActivityCommentVO> listByActivity(Long activityId) {
        Long userId = UserContext.getUserId();

        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        List<ActivityComment> comments = list(
                new LambdaQueryWrapper<ActivityComment>()
                        .eq(ActivityComment::getActivityId, activityId)
                        .orderByDesc(ActivityComment::getCreateTime));

        return comments.stream()
                .map(c -> convertToVO(c, userId))
                .collect(Collectors.toList());
    }

    @Override
    public IPage<ActivityCommentVO> pageAdminList(PageQuery query, Long activityId, String sortOrder) {
        Page<ActivityComment> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<ActivityComment> wrapper = new LambdaQueryWrapper<>();

        if (activityId != null) {
            wrapper.eq(ActivityComment::getActivityId, activityId);
        }

        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(ActivityComment::getContent, query.getKeyword()));
        }

        if ("asc".equalsIgnoreCase(sortOrder)) {
            wrapper.orderByAsc(ActivityComment::getRating);
        } else {
            wrapper.orderByDesc(ActivityComment::getRating);
        }
        wrapper.orderByDesc(ActivityComment::getCreateTime);

        Page<ActivityComment> result = page(page, wrapper);

        Long currentUserId = UserContext.getUserId();
        return result.convert(comment -> convertToVO(comment, currentUserId));
    }

    private ActivityCommentVO convertToVO(ActivityComment comment, Long currentUserId) {
        ActivityCommentVO vo = BeanUtil.copyProperties(comment, ActivityCommentVO.class);

        User user = userService.getById(comment.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        if (comment.getMemberId() != null) {
            Member member = memberService.getById(comment.getMemberId());
            if (member != null) {
                vo.setRealName(member.getRealName());
            }
        }

        Activity activity = activityService.getById(comment.getActivityId());
        if (activity != null) {
            vo.setActivityTitle(activity.getTitle());
        }

        vo.setIsOwner(currentUserId != null && currentUserId.equals(comment.getUserId()));
        return vo;
    }
}
