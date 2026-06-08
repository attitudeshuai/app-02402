package com.association.mapper;

import com.association.entity.FinanceRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 财务记录 Mapper
 *
 * @author Association Management System
 */
@Mapper
public interface FinanceRecordMapper extends BaseMapper<FinanceRecord> {

    /**
     * 统计总收入
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM biz_finance_record WHERE type IN (1,2,3,4) AND deleted = 0")
    BigDecimal sumIncome();

    /**
     * 统计总支出
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM biz_finance_record WHERE type = 5 AND deleted = 0")
    BigDecimal sumExpense();

    /**
     * 按类型统计金额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM biz_finance_record WHERE type = #{type} AND deleted = 0")
    BigDecimal sumByType(@Param("type") Integer type);
}
