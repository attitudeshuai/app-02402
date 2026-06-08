package com.association.service;

import com.association.dto.MemberApplyDTO;
import com.association.dto.MemberDTO;
import com.association.dto.PageQuery;
import com.association.entity.Member;
import com.association.vo.MemberVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员服务接口
 *
 * @author Association Management System
 */
public interface MemberService extends IService<Member> {

    /**
     * 分页查询会员列表
     */
    IPage<MemberVO> pageList(PageQuery query);

    /**
     * 获取会员详情
     */
    MemberVO getDetail(Long id);

    /**
     * 新增会员
     */
    Long add(MemberDTO dto);

    /**
     * 编辑会员
     */
    void update(Long id, MemberDTO dto);

    /**
     * 删除会员
     */
    void delete(Long id);

    /**
     * 修改会员状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 申请入会
     */
    Long apply(MemberApplyDTO dto);

    /**
     * 审批入会申请
     */
    void approve(Long id, Boolean approved, String remark);

    /**
     * 根据用户ID获取会员信息
     */
    Member getByUserId(Long userId);

    /**
     * 生成会员编号
     */
    String generateMemberNo();
}
