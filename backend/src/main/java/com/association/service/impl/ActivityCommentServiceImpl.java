package com.association.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.dto.ActivityCommentDTO;
import com.association.dto.PageQuery;
import com.association.entity.Activity;
import com.association.entity.ActivityComment;
import com.association.entity.ActivityEnrollment;
import com.association.entity.Member;
import com.association.entity.User;
import com.association.mapper.ActivityCommentMapper;
import com.association.mapper.ActivityEnrollmentMapper;
import com.association.service.ActivityCommentService;
import com.association.service.ActivityService;
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

/**
 * 活动评论服务实现
 *
 * @author Association Management System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityCommentServiceImpl extends ServiceImpl<ActivityCommentMapper, ActivityComment> implements ActivityCommentService {

    private final ActivityService activityService;
    private final ActivityEnrollmentMapper enrollmentMapper;
    private final MemberService memberService;
    private final UserService userService;

    @Override
    public IPage<ActivityCommentVO> pageList(PageQuery query, Long activityId, String sortBy) {
        Page<ActivityComment> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<ActivityComment> wrapper = new LambdaQueryWrapper<>();

        // 按活动筛选
        if (activityId != null) {
            wrapper.eq(ActivityComment::getActivityId, activityId);
        }

        // 关键词搜索（搜索评论内容）
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(ActivityComment::getContent, query.getKeyword());
        }

        // 排序
        if ("rating_asc".equals(sortBy)) {
            wrapper.orderByAsc(ActivityComment::getRating);
        } else if ("rating_desc".equals(sortBy)) {
            wrapper.orderByDesc(ActivityComment::getRating);
        } else if ("create_time_asc".equals(sortBy)) {
            wrapper.orderByAsc(ActivityComment::getCreateTime);
        } else {
            wrapper.orderByDesc(ActivityComment::getCreateTime);
        }

        Page<ActivityComment> result = page(page, wrapper);

        return result.convert(this::convertToVO);
    }

    @Override
    public IPage<ActivityCommentVO> getCommentsByActivityId(Long activityId, PageQuery query) {
        Page<ActivityComment> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<ActivityComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityComment::getActivityId, activityId)
               .orderByDesc(ActivityComment::getCreateTime);

        Page<ActivityComment> result = page(page, wrapper);

        return result.convert(this::convertToVO);
    }

    @Override
    public ActivityCommentVO getMyComment(Long activityId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return null;
        }

        ActivityComment comment = getOne(
                new LambdaQueryWrapper<ActivityComment>()
                        .eq(ActivityComment::getActivityId, activityId)
                        .eq(ActivityComment::getUserId, userId));

        return comment != null ? convertToVO(comment) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(Long activityId, ActivityCommentDTO dto) {
        Long userId = UserContext.getUserId();

        // 检查活动是否存在
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        // 检查活动状态是否已结束（状态3表示已结束）
        if (activity.getStatus() != 3) {
            throw new BusinessException(ResultCode.ACTIVITY_COMMENT_NOT_ALLOWED);
        }

        // 检查是否已报名该活动
        ActivityEnrollment enrollment = enrollmentMapper.selectOne(
                new LambdaQueryWrapper<ActivityEnrollment>()
                        .eq(ActivityEnrollment::getActivityId, activityId)
                        .eq(ActivityEnrollment::getUserId, userId));
        if (enrollment == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_ENROLLED);
        }

        // 检查是否已评论过
        ActivityComment existingComment = getOne(
                new LambdaQueryWrapper<ActivityComment>()
                        .eq(ActivityComment::getActivityId, activityId)
                        .eq(ActivityComment::getUserId, userId));
        if (existingComment != null) {
            throw new BusinessException(ResultCode.ACTIVITY_ALREADY_COMMENTED);
        }

        // 获取会员信息
        Member member = memberService.getByUserId(userId);

        // 创建评论
        ActivityComment comment = new ActivityComment();
        comment.setActivityId(activityId);
        comment.setUserId(userId);
        comment.setMemberId(member != null ? member.getId() : null);
        comment.setRating(dto.getRating());
        comment.setContent(dto.getContent());

        save(comment);
        log.info("发表活动评论: activityId={}, userId={}, rating={}", activityId, userId, dto.getRating());

        return comment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateComment(Long activityId, ActivityCommentDTO dto) {
        Long userId = UserContext.getUserId();

        // 检查是否已评论过
        ActivityComment comment = getOne(
                new LambdaQueryWrapper<ActivityComment>()
                        .eq(ActivityComment::getActivityId, activityId)
                        .eq(ActivityComment::getUserId, userId));
        if (comment == null) {
            throw new BusinessException(ResultCode.ACTIVITY_COMMENT_NOT_FOUND);
        }

        // 检查是否是自己的评论（理论上上面的查询已经保证了，但再加一层检查）
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ACTIVITY_COMMENT_NOT_OWNER);
        }

        // 更新评论
        comment.setRating(dto.getRating());
        comment.setContent(dto.getContent());

        updateById(comment);
        log.info("更新活动评论: commentId={}, activityId={}, userId={}", comment.getId(), activityId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long id) {
        ActivityComment comment = getById(id);
        if (comment == null) {
            throw new BusinessException(ResultCode.ACTIVITY_COMMENT_NOT_FOUND);
        }

        // 检查是否是自己的评论或管理员
        Long userId = UserContext.getUserId();
        boolean isAdmin = UserContext.hasManagePermission();
        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ACTIVITY_COMMENT_NOT_OWNER);
        }

        removeById(id);
        log.info("删除活动评论: commentId={}, activityId={}, userId={}", id, comment.getActivityId(), userId);
    }

    @Override
    public Double getAverageRating(Long activityId) {
        return baseMapper.avgRatingByActivityId(activityId);
    }

    @Override
    public Integer getCommentCount(Long activityId) {
        return baseMapper.countByActivityId(activityId);
    }

    /**
     * 转换为VO
     */
    private ActivityCommentVO convertToVO(ActivityComment comment) {
        ActivityCommentVO vo = BeanUtil.copyProperties(comment, ActivityCommentVO.class);

        // 获取会员信息
        if (comment.getMemberId() != null) {
            Member member = memberService.getById(comment.getMemberId());
            if (member != null) {
                vo.setMemberName(member.getRealName());
            }
        }

        // 获取用户头像
        if (comment.getUserId() != null) {
            User user = userService.getById(comment.getUserId());
            if (user != null) {
                vo.setMemberAvatar(user.getAvatar());
                // 如果没有会员姓名，用昵称代替
                if (vo.getMemberName() == null && user.getNickname() != null) {
                    vo.setMemberName(user.getNickname());
                }
            }
        }

        // 获取活动标题
        if (comment.getActivityId() != null) {
            Activity activity = activityService.getById(comment.getActivityId());
            if (activity != null) {
                vo.setActivityTitle(activity.getTitle());
            }
        }

        return vo;
    }
}
