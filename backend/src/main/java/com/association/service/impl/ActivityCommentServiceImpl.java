package com.association.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.dto.ActivityCommentDTO;
import com.association.dto.ActivityCommentQuery;
import com.association.entity.Activity;
import com.association.entity.ActivityComment;
import com.association.entity.ActivityEnrollment;
import com.association.entity.Member;
import com.association.entity.User;
import com.association.mapper.ActivityCommentMapper;
import com.association.mapper.ActivityEnrollmentMapper;
import com.association.mapper.ActivityMapper;
import com.association.service.ActivityCommentService;
import com.association.service.MemberService;
import com.association.service.UserService;
import com.association.vo.ActivityCommentVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 活动评论服务实现
 *
 * @author Association Management System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityCommentServiceImpl extends ServiceImpl<ActivityCommentMapper, ActivityComment> implements ActivityCommentService {

    private final ActivityMapper activityMapper;
    private final ActivityEnrollmentMapper enrollmentMapper;
    private final MemberService memberService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long activityId, ActivityCommentDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 校验活动是否存在
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        // 必须是已结束的活动
        if (activity.getStatus() == null || activity.getStatus() != 3) {
            throw new BusinessException(ResultCode.ACTIVITY_COMMENT_NOT_ALLOWED);
        }

        // 必须报名过该活动
        ActivityEnrollment enrollment = enrollmentMapper.selectOne(
                new LambdaQueryWrapper<ActivityEnrollment>()
                        .eq(ActivityEnrollment::getActivityId, activityId)
                        .eq(ActivityEnrollment::getUserId, userId));
        if (enrollment == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_ENROLLED);
        }

        // 一个会员对同一个活动只能评一次
        ActivityComment existing = getOne(new LambdaQueryWrapper<ActivityComment>()
                .eq(ActivityComment::getActivityId, activityId)
                .eq(ActivityComment::getUserId, userId));
        if (existing != null) {
            throw new BusinessException(ResultCode.ACTIVITY_ALREADY_COMMENTED);
        }

        ActivityComment comment = new ActivityComment();
        comment.setActivityId(activityId);
        comment.setUserId(userId);
        comment.setMemberId(enrollment.getMemberId());
        comment.setRating(dto.getRating());
        comment.setContent(dto.getContent());

        save(comment);
        log.info("发表活动评论: activityId={}, userId={}, rating={}", activityId, userId, dto.getRating());
        return comment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ActivityCommentDTO dto) {
        ActivityComment comment = getById(id);
        if (comment == null) {
            throw new BusinessException(ResultCode.ACTIVITY_COMMENT_NOT_FOUND);
        }

        Long userId = UserContext.getUserId();
        if (userId == null || !userId.equals(comment.getUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能编辑自己的评论");
        }

        comment.setRating(dto.getRating());
        comment.setContent(dto.getContent());
        updateById(comment);
        log.info("更新活动评论: id={}, userId={}", id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ActivityComment comment = getById(id);
        if (comment == null) {
            throw new BusinessException(ResultCode.ACTIVITY_COMMENT_NOT_FOUND);
        }
        removeById(id);
        log.info("删除活动评论: id={}", id);
    }

    @Override
    public List<ActivityCommentVO> listByActivity(Long activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        List<ActivityComment> comments = list(new LambdaQueryWrapper<ActivityComment>()
                .eq(ActivityComment::getActivityId, activityId)
                .orderByDesc(ActivityComment::getCreateTime));

        return convertToVOList(comments);
    }

    @Override
    public IPage<ActivityCommentVO> pageList(ActivityCommentQuery query) {
        Page<ActivityComment> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<ActivityComment> wrapper = new LambdaQueryWrapper<>();

        if (query.getActivityId() != null) {
            wrapper.eq(ActivityComment::getActivityId, query.getActivityId());
        }
        if (query.getRating() != null) {
            wrapper.eq(ActivityComment::getRating, query.getRating());
        }

        // 排序：默认按创建时间倒序；支持按评分排序
        boolean asc = "asc".equalsIgnoreCase(query.getSortOrder());
        if ("rating".equalsIgnoreCase(query.getSortBy())) {
            if (asc) {
                wrapper.orderByAsc(ActivityComment::getRating);
            } else {
                wrapper.orderByDesc(ActivityComment::getRating);
            }
            wrapper.orderByDesc(ActivityComment::getCreateTime);
        } else {
            if (asc) {
                wrapper.orderByAsc(ActivityComment::getCreateTime);
            } else {
                wrapper.orderByDesc(ActivityComment::getCreateTime);
            }
        }

        Page<ActivityComment> result = page(page, wrapper);

        List<ActivityCommentVO> voList = convertToVOList(result.getRecords());

        Page<ActivityCommentVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ActivityCommentVO getMyComment(Long activityId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return null;
        }
        ActivityComment comment = getOne(new LambdaQueryWrapper<ActivityComment>()
                .eq(ActivityComment::getActivityId, activityId)
                .eq(ActivityComment::getUserId, userId));
        if (comment == null) {
            return null;
        }
        List<ActivityCommentVO> list = convertToVOList(java.util.Collections.singletonList(comment));
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 批量转换为 VO，填充活动标题、会员姓名、用户昵称
     */
    private List<ActivityCommentVO> convertToVOList(List<ActivityComment> comments) {
        if (comments == null || comments.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        Set<Long> activityIds = comments.stream().map(ActivityComment::getActivityId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<Long> memberIds = comments.stream().map(ActivityComment::getMemberId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        Set<Long> userIds = comments.stream().map(ActivityComment::getUserId)
                .filter(java.util.Objects::nonNull).collect(Collectors.toSet());

        Map<Long, String> activityTitleMap = new HashMap<>();
        if (!activityIds.isEmpty()) {
            List<Activity> activities = activityMapper.selectBatchIds(activityIds);
            for (Activity a : activities) {
                activityTitleMap.put(a.getId(), a.getTitle());
            }
        }

        Map<Long, Member> memberMap = new HashMap<>();
        if (!memberIds.isEmpty()) {
            List<Member> members = memberService.listByIds(memberIds);
            for (Member m : members) {
                memberMap.put(m.getId(), m);
            }
        }

        Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            for (User u : users) {
                userMap.put(u.getId(), u);
            }
        }

        return comments.stream().map(c -> {
            ActivityCommentVO vo = BeanUtil.copyProperties(c, ActivityCommentVO.class);
            if (c.getActivityId() != null) {
                vo.setActivityTitle(activityTitleMap.get(c.getActivityId()));
            }
            if (c.getMemberId() != null) {
                Member m = memberMap.get(c.getMemberId());
                if (m != null) {
                    vo.setMemberName(m.getRealName());
                }
            }
            if (c.getUserId() != null) {
                User u = userMap.get(c.getUserId());
                if (u != null) {
                    vo.setUserNickname(u.getNickname());
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
