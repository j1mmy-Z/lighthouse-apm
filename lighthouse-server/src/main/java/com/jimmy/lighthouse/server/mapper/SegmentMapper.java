package com.jimmy.lighthouse.server.mapper;

import com.jimmy.lighthouse.server.domain.SegmentDO;

import java.util.List;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
public interface SegmentMapper {


    /**
     * 批量插入Segment
     */
    int batchSaveSegment(List<SegmentDO> list);

}
