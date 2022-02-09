package com.jimmy.lighthouse.apm.agent.service;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
public interface LighthouseService {

    public void init();

    public void start();

    public void shutdown();

    /**
     * 启动优先级，越大优先级越高
     */
    default int priority() {
        return 0;
    }
}
