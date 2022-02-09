package com.jimmy.lighthouse.apm.agent.service.impl;

import com.jimmy.lighthouse.apm.agent.dependency.event.BaseEventListener;
import com.jimmy.lighthouse.apm.agent.dependency.event.TraceEndEvent;
import com.jimmy.lighthouse.apm.agent.dependency.queue.ConcurrentCircleQueue;
import com.jimmy.lighthouse.apm.agent.service.LighthouseService;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 */
public class TraceCollectService extends BaseEventListener<TraceEndEvent> implements LighthouseService {


    @Override
    public void init() {
        register();
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public void listen(TraceEndEvent event) {
        ConcurrentCircleQueue.getInstance().offer(event.getTraceContext());
    }

    @Override
    public boolean support(Object event) {
        return event instanceof TraceEndEvent;
    }
}
