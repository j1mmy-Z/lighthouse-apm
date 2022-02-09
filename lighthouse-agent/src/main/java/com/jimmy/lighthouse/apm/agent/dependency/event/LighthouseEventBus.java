package com.jimmy.lighthouse.apm.agent.dependency.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;


/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
public class LighthouseEventBus {

    private static final EventBus eventBus;
    private static final GoogleEventBusExecutor executor;

    static {
        executor = new GoogleEventBusExecutor();
        eventBus = new AsyncEventBus(executor);
    }

    public static void publish(Object event) {
        eventBus.post(event);
    }

    public static void register(BaseEventListener<?> eventListener) {
        eventBus.register(eventListener);
    }

    public static void shutDownEvenBus() {
        try {
            executor.shutDown();
        }catch (Exception ignored){

        }
    }
}
