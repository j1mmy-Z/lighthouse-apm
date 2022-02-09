package com.jimmy.lighthouse.apm.common.domain.request;

import com.jimmy.lighthouse.apm.common.domain.dto.TraceDTO;
import lombok.Data;

import java.util.List;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
@Data
public class TraceSaveRequest {

    private List<TraceDTO> list;
}
