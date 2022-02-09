package com.jimmy.lighthouse.apm.agent.config;

import java.util.Collections;
import java.util.List;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
public class LighthouseConfig {

    public static List<String> apmServerAddress = Collections.singletonList("127.0.0.1:9090");

    public static int traceCollectQueueSize = 1024;

    public static int traceCollectMaxWaitTime = 0;

    public static String serviceName = "UNKNOWN";

    public static String instanceName = "UNKNOWN";
}
