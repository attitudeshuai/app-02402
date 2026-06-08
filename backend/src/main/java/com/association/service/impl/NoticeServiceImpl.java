package com.association.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.association.common.context.UserContext;
import com.association.common.exception.BusinessException;
import com.association.common.result.ResultCode;
import com.association.dto.NoticeDTO;
import com.association.dto.PageQuery;
import com.association.entity.Notice;
import com.association.entity.NoticeRead;
import com.association.mapper.NoticeMapper;
import com.association.mapper.NoticeReadMapper;
import com.association.service.NoticeService;
import com.association.vo.NoticeVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 公告服务实现
 *
 * @author Association Management System
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeReadMapper noticeReadMapper;

    @Override
    public IPage<NoticeVO> pageList(PageQuery query) {
        Page<Notice> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(Notice::getTitle, query.getKeyword());
        }

        // 状态过滤
        if (query.getStatus() != null) {
            wrapper.eq(Notice::getStatus, query.getStatus());
        } else {
            // 非管理员只能看已发布的
            if (!UserContext.hasManagePermission()) {
                wrapper.eq(Notice::getStatus, 1);
            }
        }

        // 置顶优先，然后按发布时间倒序
        wrapper.orderByDesc(Notice::getIsTop)
                .orderByDesc(Notice::getPublishTime);

        Page<Notice> result = page(page, wrapper);

        Long userId = UserContext.getUserId();
        return result.convert(notice -> {
            NoticeVO vo = BeanUtil.copyProperties(notice, NoticeVO.class);
            
            // 查询是否已读
            if (userId != null) {
                NoticeRead read = noticeReadMapper.selectOne(
                        new LambdaQueryWrapper<NoticeRead>()
                                .eq(NoticeRead::getNoticeId, notice.getId())
                                .eq(NoticeRead::getUserId, userId));
                vo.setRead(read != null);
            }
            return vo;
        });
    }

    @Override
    public NoticeVO getDetail(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOTICE_NOT_FOUND);
        }

        NoticeVO vo = BeanUtil.copyProperties(notice, NoticeVO.class);

        Long userId = UserContext.getUserId();
        if (userId != null) {
            NoticeRead read = noticeReadMapper.selectOne(
                    new LambdaQueryWrapper<NoticeRead>()
                            .eq(NoticeRead::getNoticeId, id)
                            .eq(NoticeRead::getUserId, userId));
            vo.setRead(read != null);
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(NoticeDTO dto) {
        Notice notice = BeanUtil.copyProperties(dto, Notice.class);
        notice.setCreatorId(UserContext.getUserId());

        if (Boolean.TRUE.equals(dto.getPublish())) {
            notice.setStatus(1);
            notice.setPublishTime(LocalDateTime.now());
        } else {
            notice.setStatus(0);
        }

        save(notice);
        log.info("发布公告: id={}, title={}", notice.getId(), notice.getTitle());
        return notice.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, NoticeDTO dto) {
        Notice notice = getById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOTICE_NOT_FOUND);
        }

        BeanUtil.copyProperties(dto, notice, "id", "creatorId", "status", "publishTime");

        // 如果要发布
        if (Boolean.TRUE.equals(dto.getPublish()) && notice.getStatus() == 0) {
            notice.setStatus(1);
            notice.setPublishTime(LocalDateTime.now());
        }

        updateById(notice);
        log.info("更新公告: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOTICE_NOT_FOUND);
        }

        removeById(id);
        
        // 删除已读记录
        noticeReadMapper.delete(new LambdaQueryWrapper<NoticeRead>()
                .eq(NoticeRead::getNoticeId, id));
        
        log.info("删除公告: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id) {
        Notice notice = getById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOTICE_NOT_FOUND);
        }

        Long userId = UserContext.getUserId();

        // 检查是否已读
        NoticeRead existing = noticeReadMapper.selectOne(
                new LambdaQueryWrapper<NoticeRead>()
                        .eq(NoticeRead::getNoticeId, id)
                        .eq(NoticeRead::getUserId, userId));

        if (existing == null) {
            NoticeRead read = new NoticeRead();
            read.setNoticeId(id);
            read.setUserId(userId);
            read.setReadTime(LocalDateTime.now());
            noticeReadMapper.insert(read);
        }
    }

    @Override
    public int getUnreadCount() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return 0;
        }

        // 已发布的公告数量
        long totalPublished = count(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getStatus, 1));

        // 已读数量
        long readCount = noticeReadMapper.selectCount(
                new LambdaQueryWrapper<NoticeRead>()
                        .eq(NoticeRead::getUserId, userId));

        return (int) Math.max(0, totalPublished - readCount);
    }
}
