package com.jimmy.lighthouse.apm.common.enumeration;

import lombok.Getter;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-04
 */
public enum SegmentType {

    CROSS_PROCESS(0),
    CROSS_THREAD(1);

    @Getter
    private final int code;

    SegmentType(int code) {
        this.code = code;
    }
}
