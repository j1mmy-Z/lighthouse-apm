package com.jimmy.lighthouse.apm.agent.trace;

import com.jimmy.lighthouse.apm.agent.dependency.event.LighthouseEventBus;
import com.jimmy.lighthouse.apm.agent.dependency.event.TraceEndEvent;
import com.jimmy.lighthouse.apm.common.constant.LighthouseConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.Objects;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 */
public class TraceContext {

    /**
     * segment集合
     */
    @Getter
    private LinkedList<Segment> segments;

    private String currentSegmentId;

    private Segment currentSegment;

    @Getter
    @Setter
    private String traceId;

    @Getter
    private final Long startTime;

    @Getter
    private Long endTime;

    public TraceContext() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 创建Segment
     */
    public Segment createSegment() {
        if (Objects.isNull(segments)) {
            segments = new LinkedList<>();
        }
        String nextSegmentId = nextSegmentId(getCurrentSegment());
        Segment segment = new Segment(traceId, nextSegmentId, currentSegmentId);
        currentSegmentId = nextSegmentId;
        currentSegment = segment;
        segments.addLast(segment);
        return segment;
    }


    private static String nextSegmentId(Segment segment) {
        if (Objects.isNull(segment)) {
            return LighthouseConstants.START_SEGMENT_ID;
        }
        return segment.generateChildSegmentId();
    }

    public Segment getCurrentSegment() {
        return currentSegment;
    }

    public void end(String spanId) {
        currentSegment.end(spanId);
        int index = segments.indexOf(currentSegment);
        if (index <= 0) {
            currentSegment = null;
            currentSegmentId = LighthouseConstants.EMPTY_SEGMENT_ID;
            endTime = System.currentTimeMillis();
            // trace结束后发布事件
            LighthouseEventBus.publish(new TraceEndEvent(this));
        } else {
            Segment segment = segments.get(index - 1);
            currentSegmentId = segment.getSegmentId();
            currentSegment = segment;
        }
    }
}
