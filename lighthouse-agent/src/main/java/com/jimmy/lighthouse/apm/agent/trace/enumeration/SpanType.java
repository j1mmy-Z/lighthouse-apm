package com.jimmy.lighthouse.apm.agent.trace.enumeration;

import lombok.Getter;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 */
public enum SpanType {

    /**
     * 本地调用
     */
    LOCAL(1),

    /**
     * rpc调用
     */
    RPC(2),

    /**
     * 数据库读写
     */
    DB(3),

    /**
     * 缓存读写
     */
    CACHE(4),

    /**
     * 消息队列读写
     */
    MQ(5),

    /**
     * http请求
     */
    HTTP(6);

    @Getter
    private final int code;

    SpanType(int code) {
        this.code = code;
    }
}
