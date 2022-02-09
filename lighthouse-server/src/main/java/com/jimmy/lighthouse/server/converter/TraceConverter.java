package com.jimmy.lighthouse.server.converter;


import com.jimmy.lighthouse.apm.common.domain.dto.TraceDTO;
import com.jimmy.lighthouse.server.domain.TraceDO;
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
public abstract class TraceConverter {

    @Mappings({
            @Mapping(target = "id", source = "traceId")
    })
    public abstract TraceDO convertToTraceDO(TraceDTO traceDTO);

    @Mappings({
            @Mapping(target = "traceId", source = "id")
    })
    public abstract TraceDTO convertToTraceDTO(TraceDO traceDO);

    public List<TraceDO> batchConvertToTraceDO(List<TraceDTO> traceDTOS) {
        return traceDTOS.stream().map(this::convertToTraceDO).collect(Collectors.toList());
    }

    public Date convertMillsToDate(Long mills) {
        return CommonUtil.convertMillsToDate(mills);
    }

    public Long convertDateToMills(Date date) {
        return CommonUtil.convertDateToMills(date);
    }


}
