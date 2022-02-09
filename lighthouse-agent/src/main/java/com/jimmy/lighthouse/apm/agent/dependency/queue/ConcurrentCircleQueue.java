package com.jimmy.lighthouse.apm.agent.dependency.queue;

import com.jimmy.lighthouse.apm.agent.trace.TraceContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import static com.jimmy.lighthouse.apm.agent.config.LighthouseConfig.traceCollectMaxWaitTime;
import static com.jimmy.lighthouse.apm.agent.config.LighthouseConfig.traceCollectQueueSize;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
@Slf4j
public class ConcurrentCircleQueue {

    /**
     * 队列大小
     */
    @Getter
    private final int queueSize;

    /**
     * 队列满时最大重试时间
     */
    private final int maxWaitMills;

    /**
     * 队列
     */
    @Getter
    private final TraceContext[] entries;

    /**
     * 唤醒消费者最小任务阈值
     */
    private final int notifyThreshold;

    @Getter
    private final AtomicLong putIndex;

    @Getter
    private final AtomicLong discardCount;

    @Getter
    private final AtomicLong takeIndex;

    @Getter
    private final AtomicBoolean running;

    @Getter
    private final ReentrantLock lock;

    @Getter
    private final Condition notEmpty;

    @Getter
    private final int indexMask;

    private static final int DEFAULT_NOTIFY_THRESHOLD = 16;

    private static final ConcurrentCircleQueue instance
            = new ConcurrentCircleQueue(traceCollectQueueSize, traceCollectMaxWaitTime);

    public static ConcurrentCircleQueue getInstance() {
        return instance;
    }

    private ConcurrentCircleQueue(int queueSize, int maxWaitMills) {
        queueSize = 1 << (32 - Integer.numberOfLeadingZeros(queueSize - 1));
        this.queueSize = queueSize;
        this.maxWaitMills = maxWaitMills;
        this.entries = new TraceContext[queueSize];
        this.indexMask = queueSize - 1;
        this.notifyThreshold = Math.min(queueSize, DEFAULT_NOTIFY_THRESHOLD);
        putIndex = new AtomicLong(0);
        discardCount = new AtomicLong(0);
        takeIndex = new AtomicLong(0);
        running = new AtomicBoolean(false);
        lock = new ReentrantLock(false);
        notEmpty = lock.newCondition();
    }

    /**
     * 向队列中添加trace
     */
    public boolean offer(TraceContext context) {
        int queueSize = this.queueSize;

        long startTime = 0;

        while (true) {
            long put = putIndex.get();
            long size = put - takeIndex.get();
            if (size > queueSize) {
                boolean wait;

                if (this.maxWaitMills <= 0) {
                    wait = false;
                } else {
                    long now = System.currentTimeMillis();
                    if (0 == startTime) {
                        startTime = now;
                        wait = true;
                    } else {
                        wait = now - startTime < this.maxWaitMills;
                    }
                }

                if (wait) {
                    LockSupport.parkNanos(1000);
                    continue;
                } else {
                    discardCount.incrementAndGet();
                    return false;
                }
            }
            if (putIndex.compareAndSet(put, put + 1)) {
                entries[(int) put & indexMask] = context;
                if (size >= notifyThreshold && !running.get() && lock.tryLock()) {
                    try {
                        notEmpty.signal();
                    } catch (Exception e) {
                        log.error("notify traceContext consumer fail", e);
                    } finally {
                        if (lock.isLocked()) {
                            lock.unlock();
                        }
                    }
                }
                return true;
            }
        }
    }
}
