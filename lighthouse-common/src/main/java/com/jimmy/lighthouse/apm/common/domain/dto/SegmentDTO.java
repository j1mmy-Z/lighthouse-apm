package com.jimmy.lighthouse.apm.common.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
@Data
public class SegmentDTO {

    /**
     * segmentId
     */
    private String segmentId;

    /**
     * 父segmentId
     */
    private String parentSegmentId;

    /**
     * span集合
     */
    private List<SpanDTO> spans;

    private Long startTime;

    private Long endTime;

    /**
     * 父spanId
     */
    private Long parentSpanId;

    /**
     * @see com.jimmy.lighthouse.apm.common.enumeration.SegmentType
     */
    private Integer segmentType;

    private String serviceName;

    private String instanceName;

    private String ipAddress;

}
