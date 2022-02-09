package com.jimmy.lighthouse.server.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
@Data
public class SegmentDO {

    private Long id;

    private String segmentId;

    private String traceId;

    private String parentSegmentId;

    private String parentSpanId;

    private String spans;

    private String serviceName;

    private String instanceName;

    private String ipAddress;

    private Integer type;

    private Date startTime;

    private Date endTime;

    private String description;

    private Date gmtCreate;
}
