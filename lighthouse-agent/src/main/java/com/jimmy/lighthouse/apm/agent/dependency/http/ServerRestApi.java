package com.jimmy.lighthouse.apm.agent.dependency.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jimmy.lighthouse.apm.common.api.ServerRestApiUrl;
import com.jimmy.lighthouse.apm.common.domain.dto.TraceDTO;
import com.jimmy.lighthouse.apm.common.domain.request.TraceSaveRequest;
import com.jimmy.lighthouse.apm.common.domain.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
@Slf4j
public class ServerRestApi {

    private static final String HTTP = "http://";

    public static void main(String[] args) {
        ArrayList<TraceDTO> list = new ArrayList<>();
        TraceDTO traceDTO = new TraceDTO();
        traceDTO.setTraceId("123");
        list.add(traceDTO);
        TraceSaveRequest request = new TraceSaveRequest();
        request.setList(list);
        Result<Boolean> booleanResult = batchSaveTraceInfo(request);
        System.out.println(booleanResult);
    }

    public static Result<Boolean> batchSaveTraceInfo(TraceSaveRequest request) {
        String url = HTTP + RoundRobinLoadBalancer.select() + ServerRestApiUrl.BATCH_TRACE;
        String response = HttpClientUtil.doHttpPost(url, JSON.toJSONString(request));
        if (StringUtils.isBlank(response)) {
            log.error("http request [batchSaveTraceInfo] response is null,url={}", url);
            return null;
        }
        return JSON.parseObject(response, new TypeReference<Result<Boolean>>() {
        });
    }
}
