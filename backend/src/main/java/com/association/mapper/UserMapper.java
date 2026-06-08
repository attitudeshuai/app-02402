package com.association.mapper;

import com.association.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 *
 * @author Association Management System
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
