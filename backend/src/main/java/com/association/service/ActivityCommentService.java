package com.association.service;

import com.association.dto.ActivityCommentDTO;
import com.association.dto.PageQuery;
import com.association.entity.ActivityComment;
import com.association.vo.ActivityCommentVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ActivityCommentService extends IService<ActivityComment> {

    Long create(Long activityId, ActivityCommentDTO dto);

    void update(Long commentId, ActivityCommentDTO dto);

    void delete(Long commentId);

    ActivityCommentVO getMyComment(Long activityId);

    List<ActivityCommentVO> listByActivity(Long activityId);

    IPage<ActivityCommentVO> pageAdminList(PageQuery query, Long activityId, String sortOrder);
}
