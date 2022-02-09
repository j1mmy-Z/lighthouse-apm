package com.jimmy.lighthouse.apm.agent.trace;

import com.google.common.collect.Maps;
import com.jimmy.lighthouse.apm.agent.trace.enumeration.SpanType;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 */
public class Span {

    /**
     * 所属Segment的id
     */
    @Getter
    private final String segmentId;

    /**
     * 当前span的id
     */
    @Getter
    private final String spanId;

    /**
     * 父span的id
     */
    @Getter
    private final String parentSpanId;

    /**
     * 当前span的属性
     */
    @Getter
    private final Map<String, String> props;

    /**
     * 操作名称
     */
    @Getter
    private final String operationName;

    /**
     * 类型
     */
    @Getter
    private final SpanType spanType;

    /**
     * 开始时间
     */
    @Getter
    private final Long startTime;

    /**
     * 结束时间
     */
    @Getter
    private Long endTime;

    /**
     * 所属组件的名称
     */
    @Getter
    private final String componentName;

    private final AtomicInteger nextChildId;


    public Span(String operationName, String componentName, String spanId, String parentSpanId, String segmentId, SpanType type) {
        this.operationName = operationName;
        this.componentName = componentName;
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.segmentId = segmentId;
        this.spanType = type;
        this.startTime = System.currentTimeMillis();
        nextChildId = new AtomicInteger(0);
        props = Maps.newHashMap();
    }

    public String generateChildSpanId() {
        return spanId + "." + nextChildId.incrementAndGet();
    }

    public void end() {
        this.endTime = System.currentTimeMillis();
    }
}
