package com.association.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.dto.MemberApplyDTO;
import com.association.dto.MemberDTO;
import com.association.dto.PageQuery;
import com.association.entity.Member;
import com.association.entity.User;
import com.association.mapper.MemberMapper;
import com.association.service.MemberService;
import com.association.service.UserService;
import com.association.vo.MemberVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 会员服务实现
 *
 * @author Association Management System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    private final UserService userService;

    @Override
    public IPage<MemberVO> pageList(PageQuery query) {
        Page<Member> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w
                    .like(Member::getRealName, query.getKeyword())
                    .or().like(Member::getMemberNo, query.getKeyword())
                    .or().like(Member::getPhone, query.getKeyword())
                    .or().like(Member::getCompany, query.getKeyword()));
        }

        // 状态过滤
        if (query.getStatus() != null) {
            wrapper.eq(Member::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(Member::getCreateTime);

        Page<Member> result = page(page, wrapper);

        return result.convert(member -> BeanUtil.copyProperties(member, MemberVO.class));
    }

    @Override
    public MemberVO getDetail(Long id) {
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        return BeanUtil.copyProperties(member, MemberVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(MemberDTO dto) {
        Member member = BeanUtil.copyProperties(dto, Member.class);
        member.setMemberNo(generateMemberNo());
        member.setJoinDate(LocalDate.now());
        member.setStatus(1); // 直接设为正常状态

        save(member);
        log.info("新增会员: id={}, memberNo={}", member.getId(), member.getMemberNo());
        return member.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MemberDTO dto) {
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        BeanUtil.copyProperties(dto, member, "id", "memberNo", "userId", "joinDate", "status");
        updateById(member);
        log.info("更新会员: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        removeById(id);
        log.info("删除会员: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        member.setStatus(status);
        updateById(member);
        log.info("修改会员状态: id={}, status={}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long apply(MemberApplyDTO dto) {
        Long userId = UserContext.getUserId();

        // 检查是否已有申请
        Member existing = getByUserId(userId);
        if (existing != null) {
            if (existing.getStatus() == 0) {
                throw new BusinessException(ResultCode.MEMBER_APPLY_PENDING);
            } else if (existing.getStatus() == 1) {
                throw new BusinessException(ResultCode.MEMBER_ALREADY_EXISTS);
            }
        }

        Member member = BeanUtil.copyProperties(dto, Member.class);
        member.setUserId(userId);
        member.setMemberNo(generateMemberNo());
        member.setStatus(0); // 待审核

        save(member);
        log.info("入会申请: userId={}, memberId={}", userId, member.getId());
        return member.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, Boolean approved, String remark) {
        Member member = getById(id);
        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        if (member.getStatus() != 0) {
            throw new BusinessException(ResultCode.MEMBER_STATUS_INVALID, "该申请已处理");
        }

        if (approved) {
            member.setStatus(1);
            member.setJoinDate(LocalDate.now());
            // 默认有效期一年
            member.setExpireDate(LocalDate.now().plusYears(1));
            
            // 更新用户角色为会员
            if (member.getUserId() != null) {
                User user = userService.getById(member.getUserId());
                if (user != null && "MEMBER".equals(user.getRole())) {
                    // 角色已经是会员，无需更新
                }
            }
        } else {
            member.setStatus(2); // 禁用/拒绝
        }

        if (StrUtil.isNotBlank(remark)) {
            member.setRemark(remark);
        }

        updateById(member);
        log.info("审批入会申请: id={}, approved={}", id, approved);
    }

    @Override
    public Member getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<Member>()
                .eq(Member::getUserId, userId)
                .orderByDesc(Member::getCreateTime)
                .last("LIMIT 1"));
    }

    @Override
    public String generateMemberNo() {
        // 使用日期前缀 + 雪花ID后8位，确保唯一性
        String prefix = "M" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String snowflakeId = cn.hutool.core.util.IdUtil.getSnowflakeNextIdStr();
        // 取后8位
        String suffix = snowflakeId.length() > 8 
                ? snowflakeId.substring(snowflakeId.length() - 8) 
                : snowflakeId;
        return prefix + suffix;
    }
}
