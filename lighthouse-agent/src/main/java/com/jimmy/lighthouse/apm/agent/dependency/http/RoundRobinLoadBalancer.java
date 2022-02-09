package com.jimmy.lighthouse.apm.agent.dependency.http;

import com.jimmy.lighthouse.apm.agent.config.LighthouseConfig;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
public class RoundRobinLoadBalancer {

    public static AtomicInteger count = new AtomicInteger(0);

    public static String select() {
        List<String> address = LighthouseConfig.apmServerAddress;
        int indexMask = address.size();
        return address.get(count.getAndIncrement() % indexMask);
    }
}
