package com.jimmy.lighthouse.apm.agent.trace;

import com.jimmy.lighthouse.apm.agent.trace.enumeration.SpanType;

import java.util.Objects;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 */
public class LightHouseContext {

    private static final ThreadLocal<TraceContext> traceContext = new ThreadLocal<>();

    /**
     * 开始方法，创建span
     */
    public static Span start(String operationName, String componentName, SpanType type) {
        TraceContext context = createOrGetContext();
        // 不存在segment则新建
        Segment currentSegment = context.getCurrentSegment();
        if (Objects.isNull(currentSegment)) {
            currentSegment = context.createSegment();
        }
        return currentSegment.createSpan(operationName, componentName, type);
    }

    /**
     * 结束方法，结束span
     */
    public static void end(String spanId) {
        TraceContext context = createOrGetContext();
        if (Objects.isNull(context)) {
            return;
        }
        Segment currentSegment = context.getCurrentSegment();
        if (Objects.isNull(currentSegment)){
            return;
        }
        context.end(spanId);
    }

    /**
     * 获取当前上下文，不存在则创建
     */
    private static TraceContext createOrGetContext() {
        TraceContext context = traceContext.get();
        // 上下文不存在时，创建新的context并且生成traceId
        if (Objects.isNull(context)) {
            context = new TraceContext();
            context.setTraceId(TraceIdGenerator.generateTraceId());
            traceContext.set(context);
        }
        return context;
    }

}
