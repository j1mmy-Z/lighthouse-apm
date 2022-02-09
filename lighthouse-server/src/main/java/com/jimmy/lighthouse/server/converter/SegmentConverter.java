package com.jimmy.lighthouse.server.converter;


import com.alibaba.fastjson.JSON;
import com.jimmy.lighthouse.apm.common.domain.dto.SegmentDTO;
import com.jimmy.lighthouse.apm.common.domain.dto.SpanDTO;
import com.jimmy.lighthouse.server.domain.SegmentDO;
import com.jimmy.lighthouse.server.util.CommonUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-08
 */
@Mapper(componentModel = "spring")
public abstract class SegmentConverter {

    @Mappings({
            @Mapping(target = "type", source = "segmentType")
    })
    public abstract SegmentDO convertToSegmentDO(SegmentDTO segmentDTO);

    public List<SegmentDO> batchConvertToSegmentDO(List<SegmentDTO> segmentDTOS) {
        return segmentDTOS.stream().map(this::convertToSegmentDO).collect(Collectors.toList());
    }

    public String convertSpansToJsonStr(List<SpanDTO> spanDTOS){
        return JSON.toJSONString(spanDTOS);
    }

    public Date convertMillsToDate(Long mills) {
        return CommonUtil.convertMillsToDate(mills);
    }
}
