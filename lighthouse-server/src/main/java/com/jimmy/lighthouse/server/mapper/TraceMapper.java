package com.jimmy.lighthouse.server.mapper;

import com.jimmy.lighthouse.server.domain.TraceDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
public interface TraceMapper {

    /**
     * 批量插入Trace
     */
    int batchSaveTrace(List<TraceDO> list);

    /**
     * 根据traceId查询
     */
    TraceDO selectByPrimaryKey(@Param("id") String id);
}
