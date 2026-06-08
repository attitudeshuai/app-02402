package com.association.mapper;

import com.association.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员 Mapper
 *
 * @author Association Management System
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

}
