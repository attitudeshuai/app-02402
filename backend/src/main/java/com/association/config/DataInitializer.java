package com.association.config;

import cn.hutool.crypto.digest.BCrypt;
import com.association.entity.*;
import com.association.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 数据初始化器 - 初始化测试数据
 *
 * @author Association Management System
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final MemberMapper memberMapper;
    private final ActivityMapper activityMapper;
    private final NoticeMapper noticeMapper;
    private final FinanceRecordMapper financeRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) {
        // 检查是否已有数据
        Long userCount = userMapper.selectCount(null);
        if (userCount > 0) {
            log.info("数据库已有数据，跳过初始化");
            // 确保密码正确
            updatePasswords();
            return;
        }

        log.info("开始初始化测试数据...");
        
        initUsers();
        initMembers();
        initActivities();
        initNotices();
        initFinanceRecords();

        log.info("测试数据初始化完成");
    }

    /**
     * 更新密码（确保密码正确）
     */
    private void updatePasswords() {
        String password = BCrypt.hashpw("admin123");
        
        User admin = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));
        if (admin != null) {
            admin.setPassword(password);
            userMapper.updateById(admin);
        }

        User president = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "president"));
        if (president != null) {
            president.setPassword(BCrypt.hashpw("president123"));
            userMapper.updateById(president);
        }

        User member = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "member"));
        if (member != null) {
            member.setPassword(BCrypt.hashpw("member123"));
            userMapper.updateById(member);
        }

        log.info("测试账号密码已更新");
    }

    /**
     * 初始化用户
     */
    private void initUsers() {
        // 管理员
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(BCrypt.hashpw("admin123"));
        admin.setNickname("系统管理员");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        userMapper.insert(admin);

        // 会长
        User president = new User();
        president.setUsername("president");
        president.setPassword(BCrypt.hashpw("president123"));
        president.setNickname("张会长");
        president.setRole("PRESIDENT");
        president.setStatus(1);
        userMapper.insert(president);

        // 会员
        User member = new User();
        member.setUsername("member");
        member.setPassword(BCrypt.hashpw("member123"));
        member.setNickname("李会员");
        member.setRole("MEMBER");
        member.setStatus(1);
        userMapper.insert(member);

        log.info("用户数据初始化完成: admin/admin123, president/president123, member/member123");
    }

    /**
     * 初始化会员
     */
    private void initMembers() {
        Member member1 = new Member();
        member1.setUserId(2L);
        member1.setMemberNo("M202602090001");
        member1.setRealName("张会长");
        member1.setPhone("13800138001");
        member1.setCompany("科技有限公司");
        member1.setPosition("总经理");
        member1.setJoinDate(LocalDate.of(2026, 1, 1));
        member1.setExpireDate(LocalDate.of(2027, 1, 1));
        member1.setStatus(1);
        memberMapper.insert(member1);

        Member member2 = new Member();
        member2.setUserId(3L);
        member2.setMemberNo("M202602090002");
        member2.setRealName("李会员");
        member2.setPhone("13800138002");
        member2.setCompany("网络科技公司");
        member2.setPosition("产品经理");
        member2.setJoinDate(LocalDate.of(2026, 1, 15));
        member2.setExpireDate(LocalDate.of(2027, 1, 15));
        member2.setStatus(1);
        memberMapper.insert(member2);

        log.info("会员数据初始化完成");
    }

    /**
     * 初始化活动
     */
    private void initActivities() {
        Activity activity1 = new Activity();
        activity1.setTitle("2026年度会员大会");
        activity1.setDescription("一年一度的协会会员大会，欢迎全体会员参加。会议将回顾过去一年的工作成果，展望未来发展方向。");
        activity1.setLocation("市中心会议中心A厅");
        activity1.setStartTime(LocalDateTime.of(2026, 3, 15, 9, 0));
        activity1.setEndTime(LocalDateTime.of(2026, 3, 15, 17, 0));
        activity1.setEnrollStartTime(LocalDateTime.of(2026, 2, 1, 0, 0));
        activity1.setEnrollEndTime(LocalDateTime.of(2026, 3, 10, 23, 59));
        activity1.setMaxParticipants(200);
        activity1.setFee(BigDecimal.ZERO);
        activity1.setStatus(1);
        activity1.setCreatorId(2L);
        activityMapper.insert(activity1);

        Activity activity2 = new Activity();
        activity2.setTitle("行业交流沙龙");
        activity2.setDescription("邀请行业专家分享最新技术趋势和市场动态，促进会员之间的交流与合作。");
        activity2.setLocation("创业咖啡厅");
        activity2.setStartTime(LocalDateTime.of(2026, 4, 20, 14, 0));
        activity2.setEndTime(LocalDateTime.of(2026, 4, 20, 18, 0));
        activity2.setEnrollStartTime(LocalDateTime.of(2026, 3, 1, 0, 0));
        activity2.setEnrollEndTime(LocalDateTime.of(2026, 4, 15, 23, 59));
        activity2.setMaxParticipants(50);
        activity2.setFee(new BigDecimal("50.00"));
        activity2.setStatus(1);
        activity2.setCreatorId(2L);
        activityMapper.insert(activity2);

        log.info("活动数据初始化完成");
    }

    /**
     * 初始化公告
     */
    private void initNotices() {
        Notice notice1 = new Notice();
        notice1.setTitle("关于缴纳2026年度会费的通知");
        notice1.setContent("各位会员：\n\n2026年度会费缴纳工作已经开始，请各位会员在3月31日前完成缴费。\n\n缴费方式：\n1. 通过小程序在线支付\n2. 银行转账（请备注会员编号）\n\n如有疑问，请联系秘书处。\n\n协会秘书处\n2026年2月1日");
        notice1.setType(1);
        notice1.setIsTop(true);
        notice1.setStatus(1);
        notice1.setPublishTime(LocalDateTime.of(2026, 2, 1, 10, 0));
        notice1.setCreatorId(2L);
        noticeMapper.insert(notice1);

        Notice notice2 = new Notice();
        notice2.setTitle("新春祝福");
        notice2.setContent("值此新春佳节来临之际，协会向全体会员致以诚挚的问候和美好的祝福！祝大家新年快乐，事业蒸蒸日上！");
        notice2.setType(3);
        notice2.setIsTop(false);
        notice2.setStatus(1);
        notice2.setPublishTime(LocalDateTime.of(2026, 2, 8, 9, 0));
        notice2.setCreatorId(2L);
        noticeMapper.insert(notice2);

        log.info("公告数据初始化完成");
    }

    /**
     * 初始化财务记录
     */
    private void initFinanceRecords() {
        FinanceRecord record1 = new FinanceRecord();
        record1.setRecordNo("FIN20260201100001");
        record1.setType(1);
        record1.setAmount(new BigDecimal("500.00"));
        record1.setMemberId(1L);
        record1.setPaymentMethod("银行转账");
        record1.setRemark("2026年度会费");
        record1.setOperatorId(1L);
        financeRecordMapper.insert(record1);

        FinanceRecord record2 = new FinanceRecord();
        record2.setRecordNo("FIN20260205140001");
        record2.setType(3);
        record2.setAmount(new BigDecimal("10000.00"));
        record2.setPaymentMethod("银行转账");
        record2.setRemark("某企业捐赠");
        record2.setOperatorId(1L);
        financeRecordMapper.insert(record2);

        log.info("财务记录初始化完成");
    }
}
