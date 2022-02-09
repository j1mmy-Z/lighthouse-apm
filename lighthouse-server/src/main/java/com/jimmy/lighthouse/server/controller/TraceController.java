package com.jimmy.lighthouse.server.controller;

import com.jimmy.lighthouse.apm.common.domain.dto.SegmentDTO;
import com.jimmy.lighthouse.apm.common.domain.dto.TraceDTO;
import com.jimmy.lighthouse.apm.common.domain.request.TraceSaveRequest;
import com.jimmy.lighthouse.apm.common.domain.response.Result;
import com.jimmy.lighthouse.server.converter.SegmentConverter;
import com.jimmy.lighthouse.server.converter.TraceConverter;
import com.jimmy.lighthouse.server.domain.SegmentDO;
import com.jimmy.lighthouse.server.domain.TraceDO;
import com.jimmy.lighthouse.server.mapper.SegmentMapper;
import com.jimmy.lighthouse.server.mapper.TraceMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
@RestController
@RequestMapping("/trace")
@Slf4j
public class TraceController {

    @Resource
    private TraceMapper traceMapper;

    @Resource
    private SegmentMapper segmentMapper;

    @Resource
    private TraceConverter traceConverter;

    @Resource
    private SegmentConverter segmentConverter;

    @PostMapping(value = "/batch", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Boolean> batchSaveTrace(@RequestBody TraceSaveRequest request) {
        if (Objects.isNull(request) || CollectionUtils.isEmpty(request.getList())) {
            return Result.ok();
        }
        Result<Boolean> result = new Result<>();
        result.setSuccess(false);
        try {
            List<TraceDTO> traces = request.getList();
            List<TraceDO> traceDOS = traceConverter.batchConvertToTraceDO(traces);
            int saveTraceCount = traceMapper.batchSaveTrace(traceDOS);
            List<SegmentDTO> segments = new ArrayList<>();
            for (TraceDTO traceDTO : traces) {
                segments.addAll(traceDTO.getSegments());
            }
            List<SegmentDO> segmentDOS = segmentConverter.batchConvertToSegmentDO(segments);
            int saveSegmentCount = segmentMapper.batchSaveSegment(segmentDOS);
            result.setSuccess(saveSegmentCount > 0 && saveTraceCount > 0);
        } catch (Exception e) {
            log.error("batchSaveTrace error,request={}", request, e);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @GetMapping(value = "/{traceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<TraceDTO> queryByTraceId(@PathVariable("traceId") String traceId) {
        Result<TraceDTO> result = new Result<>();
        result.setSuccess(false);
        try {
            TraceDO traceDO = traceMapper.selectByPrimaryKey(traceId);
            TraceDTO traceDTO = traceConverter.convertToTraceDTO(traceDO);
            result.setData(traceDTO);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error("queryByTraceId error,traceId={}", traceId, e);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
