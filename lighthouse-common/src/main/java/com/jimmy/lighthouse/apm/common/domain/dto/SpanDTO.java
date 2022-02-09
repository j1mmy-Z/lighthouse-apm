package com.jimmy.lighthouse.apm.common.domain.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
@Data
public class SpanDTO {

    /**
     * 所属Segment的id
     */
    private String segmentId;

    /**
     * 当前span的id
     */
    private String spanId;

    /**
     * 父span的id
     */
    private String parentSpanId;

    /**
     * 当前span的属性
     */
    private Map<String, String> props;

    /**
     * 操作名称
     */
    private String operationName;

    /**
     * 所属组件的名称
     */
    private String componentName;

    /**
     * 类型
     * @see com.jimmy.lighthouse.apm.common.enumeration.SpanType
     */
    private int spanType;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

}
