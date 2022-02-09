package com.jimmy.lighthouse.apm.common.enumeration;

import lombok.Getter;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-05
 */
public enum Components {


    SPRING_MVC(1, "spring-mvc"),

    ;

    @Getter
    private final int code;

    @Getter
    private final String name;

    Components(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
