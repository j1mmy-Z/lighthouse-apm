package com.jimmy.lighthouse.apm.agent.dependency.event;

import com.jimmy.lighthouse.apm.agent.trace.TraceContext;
import lombok.Getter;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
public class TraceEndEvent {

    @Getter
    private final TraceContext traceContext;

    public TraceEndEvent(TraceContext traceContext) {
        this.traceContext = traceContext;
    }
}
