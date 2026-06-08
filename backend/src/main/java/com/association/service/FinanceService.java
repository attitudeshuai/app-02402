package com.association.service;

import com.association.dto.FinanceRecordDTO;
import com.association.dto.PageQuery;
import com.association.entity.FinanceRecord;
import com.association.vo.FinanceRecordVO;
import com.association.vo.FinanceStatisticsVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 财务服务接口
 *
 * @author Association Management System
 */
public interface FinanceService extends IService<FinanceRecord> {

    /**
     * 分页查询财务记录
     */
    IPage<FinanceRecordVO> pageList(PageQuery query);

    /**
     * 获取记录详情
     */
    FinanceRecordVO getDetail(Long id);

    /**
     * 新增财务记录
     */
    Long add(FinanceRecordDTO dto);

    /**
     * 获取财务统计
     */
    FinanceStatisticsVO getStatistics();

    /**
     * 获取我的缴费记录
     */
    List<FinanceRecordVO> getMyRecords();

    /**
     * 缴纳会费
     */
    void payMembership();

    /**
     * 缴纳活动费
     */
    void payActivity(Long activityId);
}
