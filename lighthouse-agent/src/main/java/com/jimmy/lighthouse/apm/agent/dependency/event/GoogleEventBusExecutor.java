package com.jimmy.lighthouse.apm.agent.dependency.event;

import com.google.common.eventbus.AsyncEventBus;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jimmy.lighthouse.apm.common.constant.LighthouseConstants.SHUTDOWN_AWAIT_TIMEOUT;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
@Slf4j
public class GoogleEventBusExecutor implements Executor {


    private final AtomicInteger sequence;

    /**
     * Eventbus线程池
     */
    private final ThreadPoolExecutor executor;

    public GoogleEventBusExecutor() {
        sequence = new AtomicInteger(0);
        executor = new ThreadPoolExecutor(
                2, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("GoogleEventBusThread-" + sequence.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }, new ThreadPoolExecutor.DiscardPolicy()
        );
    }

    @Override
    public void execute(@NonNull Runnable command) {
        executor.execute(command);
    }

    public void shutDown() throws Exception {
        if (executor == null) {
            return;
        }

        executor.shutdown();

        if (!executor.awaitTermination(SHUTDOWN_AWAIT_TIMEOUT, TimeUnit.SECONDS)) {
            log.error("GoogleEventBusExecutor shutdown time out!");
            List<Runnable> tasks = executor.shutdownNow();
            log.error("GoogleEventBusExecutor was abruptly showdown, tasks will be dropped:{}", tasks);
        }
    }
}
