package com.association.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.dto.FinanceRecordDTO;
import com.association.dto.PageQuery;
import com.association.entity.Activity;
import com.association.entity.ActivityEnrollment;
import com.association.entity.FinanceRecord;
import com.association.entity.Member;
import com.association.mapper.ActivityEnrollmentMapper;
import com.association.mapper.FinanceRecordMapper;
import com.association.service.*;
import com.association.vo.FinanceRecordVO;
import com.association.vo.FinanceStatisticsVO;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 财务服务实现
 *
 * @author Association Management System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceServiceImpl extends ServiceImpl<FinanceRecordMapper, FinanceRecord> implements FinanceService {

    private final MemberService memberService;
    private final ActivityService activityService;
    private final ActivityEnrollmentMapper enrollmentMapper;
    private final PaymentService paymentService;

    // 默认会费金额（可配置化）
    private static final BigDecimal MEMBERSHIP_FEE = new BigDecimal("100.00");

    @Override
    public IPage<FinanceRecordVO> pageList(PageQuery query) {
        Page<FinanceRecord> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();

        if (query.getStatus() != null) {
            wrapper.eq(FinanceRecord::getType, query.getStatus());
        }

        wrapper.orderByDesc(FinanceRecord::getCreateTime);

        Page<FinanceRecord> result = page(page, wrapper);

        return result.convert(this::convertToVO);
    }

    @Override
    public FinanceRecordVO getDetail(Long id) {
        FinanceRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "财务记录不存在");
        }
        return convertToVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(FinanceRecordDTO dto) {
        FinanceRecord record = BeanUtil.copyProperties(dto, FinanceRecord.class);
        record.setRecordNo(generateRecordNo());
        record.setOperatorId(UserContext.getUserId());

        save(record);
        log.info("新增财务记录: id={}, recordNo={}, type={}, amount={}", 
                record.getId(), record.getRecordNo(), record.getType(), record.getAmount());
        return record.getId();
    }

    @Override
    public FinanceStatisticsVO getStatistics() {
        FinanceStatisticsVO vo = new FinanceStatisticsVO();

        vo.setTotalIncome(baseMapper.sumIncome());
        vo.setTotalExpense(baseMapper.sumExpense());
        vo.setBalance(vo.getTotalIncome().subtract(vo.getTotalExpense()));

        vo.setMembershipIncome(baseMapper.sumByType(1));
        vo.setActivityIncome(baseMapper.sumByType(2));
        vo.setDonationIncome(baseMapper.sumByType(3));
        vo.setOtherIncome(baseMapper.sumByType(4));

        return vo;
    }

    @Override
    public List<FinanceRecordVO> getMyRecords() {
        Long userId = UserContext.getUserId();
        Member member = memberService.getByUserId(userId);

        if (member == null) {
            return List.of();
        }

        List<FinanceRecord> records = list(new LambdaQueryWrapper<FinanceRecord>()
                .eq(FinanceRecord::getMemberId, member.getId())
                .orderByDesc(FinanceRecord::getCreateTime));

        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payMembership() {
        Long userId = UserContext.getUserId();
        Member member = memberService.getByUserId(userId);

        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND, "请先完成会员申请");
        }

        // 调用支付服务
        String orderNo = generateRecordNo();
        String transactionId = paymentService.pay(orderNo, MEMBERSHIP_FEE, "会员费缴纳");

        // 创建财务记录
        FinanceRecord record = new FinanceRecord();
        record.setRecordNo(orderNo);
        record.setType(1); // 会费
        record.setAmount(MEMBERSHIP_FEE);
        record.setMemberId(member.getId());
        record.setPaymentMethod("模拟支付");
        record.setTransactionId(transactionId);
        record.setRemark("会员费缴纳");
        record.setOperatorId(userId);

        save(record);
        log.info("会费缴纳成功: memberId={}, amount={}", member.getId(), MEMBERSHIP_FEE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payActivity(Long activityId) {
        Long userId = UserContext.getUserId();

        // 检查活动
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_FOUND);
        }

        if (activity.getFee() == null || activity.getFee().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "该活动无需缴费");
        }

        // 检查报名记录
        ActivityEnrollment enrollment = enrollmentMapper.selectOne(
                new LambdaQueryWrapper<ActivityEnrollment>()
                        .eq(ActivityEnrollment::getActivityId, activityId)
                        .eq(ActivityEnrollment::getUserId, userId));

        if (enrollment == null) {
            throw new BusinessException(ResultCode.ACTIVITY_NOT_ENROLLED);
        }

        if (enrollment.getPaymentStatus() == 1) {
            throw new BusinessException(ResultCode.ALREADY_PAID);
        }

        // 调用支付服务
        String orderNo = generateRecordNo();
        String transactionId = paymentService.pay(orderNo, activity.getFee(), "活动费-" + activity.getTitle());

        // 更新报名记录
        enrollment.setPaymentStatus(1);
        enrollment.setPaymentTime(LocalDateTime.now());
        enrollment.setTransactionId(transactionId);
        enrollmentMapper.updateById(enrollment);

        // 创建财务记录
        Member member = memberService.getByUserId(userId);
        
        FinanceRecord record = new FinanceRecord();
        record.setRecordNo(orderNo);
        record.setType(2); // 活动费
        record.setAmount(activity.getFee());
        record.setMemberId(member != null ? member.getId() : null);
        record.setActivityId(activityId);
        record.setPaymentMethod("模拟支付");
        record.setTransactionId(transactionId);
        record.setRemark("活动费-" + activity.getTitle());
        record.setOperatorId(userId);

        save(record);
        log.info("活动费缴纳成功: activityId={}, userId={}, amount={}", activityId, userId, activity.getFee());
    }

    /**
     * 生成流水号
     */
    private String generateRecordNo() {
        String prefix = "FIN" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + IdUtil.getSnowflakeNextIdStr().substring(10);
    }

    /**
     * 转换为 VO
     */
    private FinanceRecordVO convertToVO(FinanceRecord record) {
        FinanceRecordVO vo = BeanUtil.copyProperties(record, FinanceRecordVO.class);

        if (record.getMemberId() != null) {
            Member member = memberService.getById(record.getMemberId());
            if (member != null) {
                vo.setMemberName(member.getRealName());
            }
        }

        if (record.getActivityId() != null) {
            Activity activity = activityService.getById(record.getActivityId());
            if (activity != null) {
                vo.setActivityName(activity.getTitle());
            }
        }

        return vo;
    }
}
