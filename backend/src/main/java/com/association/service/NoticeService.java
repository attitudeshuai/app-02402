package com.association.service;

import com.association.dto.NoticeDTO;
import com.association.dto.PageQuery;
import com.association.entity.Notice;
import com.association.vo.NoticeVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 公告服务接口
 *
 * @author Association Management System
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 分页查询公告列表
     */
    IPage<NoticeVO> pageList(PageQuery query);

    /**
     * 获取公告详情
     */
    NoticeVO getDetail(Long id);

    /**
     * 发布公告
     */
    Long create(NoticeDTO dto);

    /**
     * 编辑公告
     */
    void update(Long id, NoticeDTO dto);

    /**
     * 删除公告
     */
    void delete(Long id);

    /**
     * 标记已读
     */
    void markRead(Long id);

    /**
     * 获取未读公告数量
     */
    int getUnreadCount();
}
