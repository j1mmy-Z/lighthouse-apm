package com.jimmy.lighthouse.apm.agent.trace;

import com.jimmy.lighthouse.apm.agent.config.LighthouseConfig;
import com.jimmy.lighthouse.apm.agent.trace.enumeration.SegmentType;
import com.jimmy.lighthouse.apm.agent.trace.enumeration.SpanType;
import com.jimmy.lighthouse.apm.agent.util.LighthouseUtil;
import com.jimmy.lighthouse.apm.common.constant.LighthouseConstants;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 */
public class Segment {

    private String traceId;

    @Getter
    private final String segmentId;

    @Getter
    private final String parentSegmentId;

    @Getter
    private LinkedList<Span> spans;

    @Getter
    private final Long startTime;

    @Getter
    private Long endTime;

    @Getter
    private Long parentSpanId;

    private final AtomicInteger nextChildId;

    private String currentSpanId;

    private Span currentSpan;

    @Getter
    private final String serviceName;

    @Getter
    private final String instanceName;

    @Getter
    private final SegmentType segmentType;

    @Getter
    private final String ipAddress;

    public Segment(String traceId, String segmentId, String parentSegmentId) {
        // todo 暂时设置为跨进程
        this.segmentType = SegmentType.CROSS_PROCESS;
        serviceName = LighthouseConfig.serviceName;
        instanceName = LighthouseConfig.instanceName;
        ipAddress = LighthouseUtil.getIP();
        this.traceId = traceId;
        this.segmentId = segmentId;
        this.parentSegmentId = parentSegmentId;
        this.spans = new LinkedList<>();
        this.startTime = System.currentTimeMillis();
        this.nextChildId = new AtomicInteger(0);
    }

    public String generateChildSegmentId() {
        return segmentId + "." + nextChildId.incrementAndGet();
    }


    /**
     * 创建一个span
     */
    public Span createSpan(String operationName, String componentName, SpanType type) {
        if (Objects.isNull(spans)) {
            spans = new LinkedList<>();
        }
        String nextSpanId = nextSpanId(getCurrentSpan());
        Span span = new Span(operationName, componentName, nextSpanId, currentSpanId, segmentId, type);
        currentSpanId = nextSpanId;
        currentSpan = span;
        spans.addLast(span);
        return span;
    }

    /**
     * 根据java的调用栈，end时的spanId和currentSpanId应该是相同的
     */
    public void end(String spanId) {
        if (!StringUtils.equals(spanId, currentSpanId)) {
            return;
        }
        currentSpan.end();
        int index = spans.indexOf(currentSpan);
        // 结束的span是最开始的，则表示segment结束
        if (index <= 0) {
            currentSpan = null;
            currentSpanId = LighthouseConstants.EMPTY_SPAN_ID;
            endTime = System.currentTimeMillis();
        } else {
            Span span = spans.get(index - 1);
            currentSpanId = span.getSpanId();
            currentSpan = span;
        }
    }


    private static String nextSpanId(Span span) {
        if (Objects.isNull(span)) {
            return LighthouseConstants.START_SPAN_ID;
        }
        return span.generateChildSpanId();
    }

    public Span getCurrentSpan() {
        return currentSpan;
    }

}
