package com.jimmy.lighthouse.apm.agent.service.converter;

import com.jimmy.lighthouse.apm.agent.trace.Segment;
import com.jimmy.lighthouse.apm.agent.trace.Span;
import com.jimmy.lighthouse.apm.agent.trace.TraceContext;
import com.jimmy.lighthouse.apm.common.domain.dto.SegmentDTO;
import com.jimmy.lighthouse.apm.common.domain.dto.SpanDTO;
import com.jimmy.lighthouse.apm.common.domain.dto.TraceDTO;

import java.util.stream.Collectors;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
public class TraceConverter {

    public static SpanDTO convertSpanDTO(Span span) {
        SpanDTO spanDTO = new SpanDTO();
        spanDTO.setSpanId(span.getSpanId());
        spanDTO.setSpanType(span.getSpanType().getCode());
        spanDTO.setParentSpanId(span.getParentSpanId());
        spanDTO.setComponentName(span.getComponentName());
        spanDTO.setStartTime(span.getStartTime());
        spanDTO.setEndTime(span.getEndTime());
        spanDTO.setProps(span.getProps());
        spanDTO.setOperationName(span.getOperationName());
        spanDTO.setSegmentId(span.getSegmentId());
        return spanDTO;
    }

    public static SegmentDTO convertSegmentDto(Segment segment) {
        SegmentDTO segmentDTO = new SegmentDTO();
        segmentDTO.setSegmentId(segment.getSegmentId());
        segmentDTO.setEndTime(segment.getEndTime());
        segmentDTO.setStartTime(segment.getStartTime());
        segmentDTO.setSegmentType(segment.getSegmentType().getCode());
        segmentDTO.setParentSegmentId(segment.getParentSegmentId());
        segmentDTO.setParentSpanId(segment.getParentSpanId());
        segmentDTO.setSpans(segment.getSpans().stream().map(TraceConverter::convertSpanDTO).collect(Collectors.toList()));
        segmentDTO.setInstanceName(segment.getInstanceName());
        segmentDTO.setServiceName(segment.getServiceName());
        segmentDTO.setIpAddress(segment.getIpAddress());
        return segmentDTO;
    }

    public static TraceDTO convertTraceDto(TraceContext traceContext) {
        TraceDTO traceDTO = new TraceDTO();
        traceDTO.setTraceId(traceContext.getTraceId());
        traceDTO.setStartTime(traceContext.getStartTime());
        traceDTO.setEndTime(traceContext.getEndTime());
        traceDTO.setSegments(traceContext.getSegments().stream().map(TraceConverter::convertSegmentDto).collect(Collectors.toList()));
        return traceDTO;
    }

}
