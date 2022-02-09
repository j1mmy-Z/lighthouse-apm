package com.jimmy.lighthouse.apm.common.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
@Data
public class TraceDTO {

    /**
     * segment集合
     */
    private List<SegmentDTO> segments;

    /**
     * traceID
     */
    private String traceId;

    private Long startTime;

    private Long endTime;

}
