package com.association.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.dto.ActivityDTO;
import com.association.dto.PageQuery;
import com.association.entity.Activity;
import com.association.entity.ActivityEnrollment;
import com.association.entity.Member;
import com.association.mapper.ActivityCommentMapper;
import com.association.mapper.ActivityEnrollmentMapper;
import com.association.mapper.ActivityMapper;
import com.association.service.ActivityService;
import com.association.service.MemberService;
import com.association.service.PaymentService;
import com.association.vo.ActivityVO;
import com.association.vo.EnrollmentVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动服务实现
 *
 * @author Association Management System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final ActivityEnrollmentMapper enrollmentMapper;
    private final ActivityCommentMapper commentMapper;
    private final MemberService memberService;
    private final PaymentService paymentService;

    @Override
    public IPage<ActivityVO> pageList(PageQuery query) {
        Page<Activity> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(Activity::getTitle, query.getKeyword());
        }

        // 状态过滤
        if (query.getStatus() != null) {
            wrapper.eq(Activity::getStatus, query.getStatus());
        } else {
            // 默认不显示草稿（除非是管理员）
            if (!UserContext.hasManagePermission()) {
                wrapper.ne(Activity::getStatus, 0);
            }
        }

        wrapper.orderByDesc(Activity::getCreateTime);

        Page<Activity> result = page(page, wrapper);

        Long userId = UserContext.getUserId();
        return result.convert(activity -> {
            ActivityVO vo = BeanUtil.copyProperties(activity, ActivityVO.class);
            vo.setEnrolledCount(enrollmentMapper.countByActivityId(activity.getId()));
            vo.setCommentCount(commentMapper.countByActivityId(activity.getId()));
            vo.setAverageRating(commentMapper.getAverageRating(activity.getId()));
            
            if (userId != null) {
                ActivityEnrollment enrollment = getEnrollment(activity.getId(), userId);
                if (enrollment != null) {
                    vo.setEnrolled(true);
                    vo.setPaid(enrollment.getPaymentStatus() == 1);
                    vo.setCheckedIn(enrollment.getCheckInStatus() == 1);
                } else {
                    vo.setEnrolled(false);
                    vo.setPaid(false);
                    vo.setCheckedIn(false);
                }
            }
            return vo;
        });
    }

    @Override
    public ActivityVO getDetail(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        ActivityVO vo = BeanUtil.copyProperties(activity, ActivityVO.class);
        vo.setEnrolledCount(enrollmentMapper.countByActivityId(id));
        vo.setCommentCount(commentMapper.countByActivityId(id));
        vo.setAverageRating(commentMapper.getAverageRating(id));

        Long userId = UserContext.getUserId();
        if (userId != null) {
            ActivityEnrollment enrollment = getEnrollment(id, userId);
            if (enrollment != null) {
                vo.setEnrolled(true);
                vo.setPaid(enrollment.getPaymentStatus() == 1);
                vo.setCheckedIn(enrollment.getCheckInStatus() == 1);
            }
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ActivityDTO dto) {
        Activity activity = BeanUtil.copyProperties(dto, Activity.class);
        activity.setStatus(0); // 草稿状态
        activity.setCreatorId(UserContext.getUserId());

        if (activity.getFee() == null) {
            activity.setFee(BigDecimal.ZERO);
        }

        save(activity);
        log.info("创建活动: id={}, title={}", activity.getId(), activity.getTitle());
        return activity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ActivityDTO dto) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        // 已发布的活动不能修改关键信息
        if (activity.getStatus() > 0) {
            // 只允许修改部分字段
            activity.setDescription(dto.getDescription());
            activity.setCoverImage(dto.getCoverImage());
        } else {
            BeanUtil.copyProperties(dto, activity, "id", "status", "creatorId");
        }

        updateById(activity);
        log.info("更新活动: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        // 已有报名的活动不能删除
        int enrollCount = enrollmentMapper.countByActivityId(id);
        if (enrollCount > 0) {
            throw new BusinessException(ResultCode.CONFLICT, "活动已有报名，无法删除");
        }

        removeById(id);
        log.info("删除活动: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        if (activity.getStatus() != 0) {
            throw new BusinessException(ResultCode.CONFLICT, "活动状态不正确");
        }

        activity.setStatus(1); // 报名中
        updateById(activity);
        log.info("发布活动: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        activity.setStatus(4); // 已取消
        updateById(activity);
        log.info("取消活动: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enroll(Long activityId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        // 检查活动状态
        if (activity.getStatus() != 1) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_STARTED);
        }

        // 检查报名时间
        LocalDateTime now = LocalDateTime.now();
        if (activity.getEnrollStartTime() != null && now.isBefore(activity.getEnrollStartTime())) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_STARTED);
        }
        if (activity.getEnrollEndTime() != null && now.isAfter(activity.getEnrollEndTime())) {
            throw new BusinessException(ResultCode.ACTIVITY_ENDED);
        }

        // 检查名额
        if (activity.getMaxParticipants() != null && activity.getMaxParticipants() > 0) {
            int count = enrollmentMapper.countByActivityId(activityId);
            if (count >= activity.getMaxParticipants()) {
                throw new BusinessException(ResultCode.ACTIVITY_FULL);
            }
        }

        Long userId = UserContext.getUserId();
        
        // 检查是否已报名
        ActivityEnrollment existing = getEnrollment(activityId, userId);
        if (existing != null) {
            throw new BusinessException(ResultCode.ACTIVITY_ALREADY_ENROLLED);
        }

        // 获取会员信息
        Member member = memberService.getByUserId(userId);

        // 创建报名记录
        ActivityEnrollment enrollment = new ActivityEnrollment();
        enrollment.setActivityId(activityId);
        enrollment.setUserId(userId);
        enrollment.setMemberId(member != null ? member.getId() : null);
        
        // 如果是免费活动，直接标记为已支付
        if (activity.getFee() == null || activity.getFee().compareTo(BigDecimal.ZERO) == 0) {
            enrollment.setPaymentStatus(1);
            enrollment.setPaymentTime(LocalDateTime.now());
        } else {
            enrollment.setPaymentStatus(0);
        }
        enrollment.setCheckInStatus(0);

        enrollmentMapper.insert(enrollment);
        log.info("活动报名: activityId={}, userId={}", activityId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelEnroll(Long activityId) {
        Long userId = UserContext.getUserId();
        
        ActivityEnrollment enrollment = getEnrollment(activityId, userId);
        if (enrollment == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_ENROLLED);
        }

        // 已支付的不能直接取消
        if (enrollment.getPaymentStatus() == 1) {
            throw new BusinessException(ResultCode.CONFLICT, "已支付的报名请联系管理员处理");
        }

        enrollmentMapper.deleteById(enrollment.getId());
        log.info("取消报名: activityId={}, userId={}", activityId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Long activityId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        // 检查活动状态（报名中或进行中可以签到）
        if (activity.getStatus() != 1 && activity.getStatus() != 2) {
            throw new BusinessException(ResultCode.ACTIVITY_CHECK_IN_NOT_ALLOWED);
        }

        Long userId = UserContext.getUserId();
        ActivityEnrollment enrollment = getEnrollment(activityId, userId);
        if (enrollment == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_ENROLLED);
        }

        if (enrollment.getCheckInStatus() == 1) {
            throw new BusinessException(ResultCode.ACTIVITY_ALREADY_CHECKED_IN);
        }

        // 检查是否需要支付
        if (activity.getFee() != null && activity.getFee().compareTo(BigDecimal.ZERO) > 0) {
            if (enrollment.getPaymentStatus() != 1) {
                throw new BusinessException(ResultCode.ACTIVITY_CHECK_IN_NOT_ALLOWED, "请先完成支付");
            }
        }

        enrollment.setCheckInStatus(1);
        enrollment.setCheckInTime(LocalDateTime.now());
        enrollmentMapper.updateById(enrollment);
        log.info("活动签到: activityId={}, userId={}", activityId, userId);
    }

    @Override
    public List<?> getEnrollments(Long activityId) {
        List<ActivityEnrollment> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<ActivityEnrollment>()
                        .eq(ActivityEnrollment::getActivityId, activityId)
                        .orderByDesc(ActivityEnrollment::getCreateTime));

        return enrollments.stream().map(e -> {
            EnrollmentVO vo = BeanUtil.copyProperties(e, EnrollmentVO.class);
            if (e.getMemberId() != null) {
                Member member = memberService.getById(e.getMemberId());
                if (member != null) {
                    vo.setMemberName(member.getRealName());
                    vo.setMemberPhone(member.getPhone());
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ActivityVO> getMyActivities() {
        Long userId = UserContext.getUserId();

        List<ActivityEnrollment> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<ActivityEnrollment>()
                        .eq(ActivityEnrollment::getUserId, userId)
                        .orderByDesc(ActivityEnrollment::getCreateTime));

        return enrollments.stream().map(e -> {
            Activity activity = getById(e.getActivityId());
            if (activity == null) return null;
            
            ActivityVO vo = BeanUtil.copyProperties(activity, ActivityVO.class);
            vo.setEnrolled(true);
            vo.setPaid(e.getPaymentStatus() == 1);
            vo.setCheckedIn(e.getCheckInStatus() == 1);
            return vo;
        }).filter(v -> v != null).collect(Collectors.toList());
    }

    /**
     * 获取用户的报名记录
     */
    private ActivityEnrollment getEnrollment(Long activityId, Long userId) {
        return enrollmentMapper.selectOne(
                new LambdaQueryWrapper<ActivityEnrollment>()
                        .eq(ActivityEnrollment::getActivityId, activityId)
                        .eq(ActivityEnrollment::getUserId, userId));
    }
}
