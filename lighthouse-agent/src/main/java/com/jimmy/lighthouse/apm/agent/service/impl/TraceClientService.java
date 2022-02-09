package com.jimmy.lighthouse.apm.agent.service.impl;

import com.google.common.collect.Lists;
import com.jimmy.lighthouse.apm.agent.dependency.http.ServerRestApi;
import com.jimmy.lighthouse.apm.agent.dependency.queue.ConcurrentCircleQueue;
import com.jimmy.lighthouse.apm.agent.service.LighthouseService;
import com.jimmy.lighthouse.apm.agent.service.converter.TraceConverter;
import com.jimmy.lighthouse.apm.agent.trace.TraceContext;
import com.jimmy.lighthouse.apm.common.domain.dto.TraceDTO;
import com.jimmy.lighthouse.apm.common.domain.request.TraceSaveRequest;
import com.jimmy.lighthouse.apm.common.domain.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
@Slf4j
public class TraceClientService implements LighthouseService, Runnable {

    ConcurrentCircleQueue queue;

    private ExecutorService traceContextConsumer;

    @Override
    public void init() {
        queue = ConcurrentCircleQueue.getInstance();
    }

    @Override
    public void start() {
        traceContextConsumer = Executors.newSingleThreadExecutor();
        traceContextConsumer.execute(this);
    }

    @Override
    public void shutdown() {
        traceContextConsumer.shutdown();
    }

    @Override
    public int priority() {
        return 80;
    }

    @Override
    public void run() {
        final int indexMask = queue.getIndexMask();
        final TraceContext[] entries = queue.getEntries();
        final AtomicLong putIndex = queue.getPutIndex();
        final AtomicLong takeIndex = queue.getTakeIndex();
        final AtomicBoolean running = queue.getRunning();
        final ReentrantLock lock = queue.getLock();
        final Condition notEmpty = queue.getNotEmpty();

        while (true) {
            try {
                running.set(true);
                long take = takeIndex.get();
                long size = putIndex.get() - take;
                if (size > 0) {
                    List<TraceContext> list = Lists.newArrayList();
                    do {
                        final int idx = (int) (take & indexMask);
                        TraceContext traceContext = entries[idx];
                        // 存在极小的可能性take动作正好在putIndex和真正放入context的间隙中
                        while (Objects.isNull(traceContext)) {
                            Thread.yield();
                            traceContext = entries[idx];
                        }
                        entries[idx] = null;
                        takeIndex.set(++take);
                        --size;
                        list.add(traceContext);
                    } while (size > 0);
                    batchProcessTraceContext(list);
                } else {
                    if (lock.tryLock()) {
                        try {
                            running.set(false);
                            notEmpty.await(1, TimeUnit.SECONDS);
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.warn("TraceClientService thread is interrupted while take traceContext");
                break;
            } catch (Throwable throwable) {
                log.error("TraceClientService thread fail to process traceContext", throwable);
            }
        }
        running.set(false);
        log.info("TraceClientService thread exit");
    }

    private void batchProcessTraceContext(List<TraceContext> list) {
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        List<TraceDTO> traces = list.stream().map(TraceConverter::convertTraceDto).collect(Collectors.toList());
        TraceSaveRequest request = new TraceSaveRequest();
        request.setList(traces);
        Result<Boolean> result = ServerRestApi.batchSaveTraceInfo(request);
    }
}