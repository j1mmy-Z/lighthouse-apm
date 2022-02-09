package com.jimmy.lighthouse.apm.agent.dependency.event;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import java.util.Objects;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
public abstract class BaseEventListener<T> {

    public void register() {
        LighthouseEventBus.register(this);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void subscribe(Object event) {
        if (Objects.nonNull(event) && support(event)) {
            listen((T) event);
        }
    }

    /**
     * 监听方法
     */
    public abstract void listen(T event);

    /**
     * 过滤事件
     */
    public abstract boolean support(Object event);
}
