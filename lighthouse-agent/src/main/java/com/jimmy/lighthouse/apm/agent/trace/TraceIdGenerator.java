package com.jimmy.lighthouse.apm.agent.trace;

import com.jimmy.lighthouse.apm.agent.util.LighthouseUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 * 生成全局唯一id
 */
public class TraceIdGenerator {

    private static String IP_16 = "ffffffff";

    private static String IP_INT = "255255255255";

    private static String PID = "0000";

    private static char PID_FLAG = 'z';

    public static AtomicInteger counter = new AtomicInteger(1000);

    static {
        try {
            String address = LighthouseUtil.getIP();
            if (Objects.nonNull(address)) {
                IP_16 = LighthouseUtil.getIp16(address);
                IP_INT = LighthouseUtil.getIpInt(address);
            }
            PID = LighthouseUtil.getHexPid(LighthouseUtil.getCurrentPid());
        } catch (Exception ignored) {

        }
    }

    /**
     * 32位全局唯一id
     * ip+时间戳+计数+z+进程id
     */
    public static String generateTraceId() {
        return IP_16 + System.currentTimeMillis() + getNextId() + PID_FLAG + PID;
    }

    public static int getNextId() {
        while (true) {
            int current = counter.get();
            int next = (current > 9000) ? 1000 : current + 1;
            if (counter.compareAndSet(current, next)) {
                return next;
            }
        }
    }
}
